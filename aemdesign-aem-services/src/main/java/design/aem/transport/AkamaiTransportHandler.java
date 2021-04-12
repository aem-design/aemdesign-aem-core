package design.aem.transport;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.day.cq.replication.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.adobe.cq.social.enablement.constants.EnablementConstants.HTTPS_PREFIX;
import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_256;


@Component(
    immediate = true,
    service = TransportHandler.class,
    property = { "label=Akamai Replication Agent" }
)
@Service
@ServiceDescription("Akamai Replication Agent for clearing cache after publish event.")
@ServiceRanking(1001)
@ServiceVendor("AEM.Design")
public class AkamaiTransportHandler implements TransportHandler {
    private static ReplicationLog currentReplicationLog = null;

    private static AgentConfig agentConfig = null;

    public static final String AKAMAI_PROTOCOL = "akamai:///";
    public static final String HTTPS = "https";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String TIME_STAMP_FORMAT = "yyyyMMdd'T'HH:mm:ss+0000";
    public final static String EG_1_HMAC_SHA_256 = "EG1-HMAC-SHA256";

    @Reference
    private CryptoSupport cryptoSupport;

    private HttpClient client;

    public boolean canHandle(AgentConfig config) {
        agentConfig = config;
        String transportURI = config.getTransportURI();
        return (transportURI != null) ? transportURI.toLowerCase().startsWith(AKAMAI_PROTOCOL) : false;
    }

    public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx) throws ReplicationException {

        currentReplicationLog = tx.getLog();

        ReplicationActionType replicationType = tx.getAction().getType();

        // only runs the code if ReplicationAction type matches any of the following
        if (replicationType == ReplicationActionType.TEST || replicationType == ReplicationActionType.ACTIVATE || replicationType == ReplicationActionType.DEACTIVATE) {
            return handleRequest(tx);
        } else {
            throw new ReplicationException("Replication action type " + replicationType + " not supported.");
        }

    }


    /**
     * Send purge request to Akamai via a POST request
     * <p>
     * Akamai will respond with a 201 HTTP status code if the purge request was
     * successfully submitted.
     *
     * @param tx Replication Transaction
     * @return ReplicationResult OK if 201 response from Akamai
     * @throws ReplicationException
     */
    private ReplicationResult handleRequest(ReplicationTransaction tx) throws ReplicationException {

        if (agentConfig != null) {

            ValueMap props = agentConfig.getProperties();

            String domain = props.get("domain","");
            String baseurl = props.get("baseurl","");
            String purgeurlpath = props.get("purgeurlpath","");
            String token = props.get("token","");
            String accesstoken = props.get("accesstoken","");
            String secret = props.get("secret","");
            String protocol = props.get("protocol","https");
            String additionalTrimPath = props.get("additionaltrimpath","");
            String[] excludepaths = props.get("excludepaths",new String[]{});

            if (StringUtils.isNotEmpty(protocol) &&
                StringUtils.isNotEmpty(baseurl) &&
                StringUtils.isNotEmpty(purgeurlpath)) {
                HttpPost request = new HttpPost(MessageFormat.format("{0}://{1}{2}", protocol, baseurl, purgeurlpath));

                request.setEntity(createPostBody(tx, domain, protocol, additionalTrimPath, excludepaths));

                ReplicationActionType replicationType = tx.getAction().getType();

                if (replicationType == ReplicationActionType.TEST) {
                    logReplicationEventInfoStatement("AKAMAI CACHE PURGE TEST: " + replicationType);
                } else if (replicationType == ReplicationActionType.ACTIVATE || replicationType == ReplicationActionType.DEACTIVATE) {
                    logReplicationEventInfoStatement("AKAMAI CACHE PURGE REQUEST: " + replicationType);
                } else {
                    logReplicationEventInfoStatement("AKAMAI CACHE REQUEST: " + replicationType);
                }

                HttpResponse response = sendRequest(request, baseurl, purgeurlpath, token, accesstoken, secret, protocol);

                if (response != null) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    logReplicationEventInfoStatement("Response from Akamai " + response.toString());
                    logReplicationEventInfoStatement("StatusCode returned from Akamai " + statusCode);

                    if (statusCode == HttpStatus.SC_CREATED) {
                        logReplicationEventInfoStatement("Akamai accepted the purge request");
                        return ReplicationResult.OK;
                    }


                }
            } else {
                logReplicationEventInfoStatement("Replication failed, agent config is incomplete.");
                return new ReplicationResult(false, 0, "Replication failed, agent config is incomplete.");
            }
        }
        return new ReplicationResult(false, 0, "Replication failed, agent does not have any config.");
    }


    /***
     * log error to console based on log level selected in agent dialog.
     * @param message
     */
    public static void logReplicationEventInfoStatement(String message) {
        logReplicationEvent((Enum)ReplicationLog.Level.INFO, message);
    }


    public static boolean logReplicationEvent(Enum level, String message) {
        if (null != currentReplicationLog) {
            if (level == ReplicationLog.Level.INFO) {
                currentReplicationLog.info(message);
            } else if (level == ReplicationLog.Level.ERROR) {
                currentReplicationLog.error(message);
            } else if (level == ReplicationLog.Level.WARN) {
                currentReplicationLog.warn(message);
            }
            return true;
        }
        return false;
    }




    /**
     * Build the Akamai purge request body based on the replication agent
     * settings and append it to the POST request.
     *
     * @param tx      ReplicationTransaction
     * @param domain      target Domain
     * @param protocol      Protocol to use
     * @param additionalTrimPath      Include short paths for each path.
     * @throws ReplicationException if errors building the request body
     */
    private StringEntity createPostBody(ReplicationTransaction tx, String domain, String  protocol, String additionalTrimPath, String[] excludepaths) throws ReplicationException {

        JsonArray requestedJson = getPathsList(tx, domain, protocol, additionalTrimPath, excludepaths);

        if (requestedJson.size() > 0) {
            JsonObject json = new JsonObject();
            json.add("objects", requestedJson);
            return new StringEntity(json.toString(), CharEncoding.ISO_8859_1);
        } else {
            throw new ReplicationException("No paths to purge");
        }

    }

    /**
     * returns the requested urls for purging the akamai cache
     * these values are set from the content builders.
     *
     * @param tx
     * @return
     * @throws ReplicationException
     */
    private JsonArray getPathsList(ReplicationTransaction tx, String domain, String protocol, String additionalTrimPath, String[] excludepaths) throws ReplicationException {

        JsonArray jsonArray = new JsonArray();

        if (StringUtils.isNotEmpty(domain) &&
            StringUtils.isNotEmpty(protocol)) {
            String[] paths = tx.getAction().getPaths();

            try {

                List<String> excludepathsList = Arrays.stream(excludepaths).collect(Collectors.toList());

                for (String path : paths) {
                    //check if path matches exclude
                    List<String> matching = excludepathsList.stream()
                        .filter(path::startsWith)
                        .collect(Collectors.toList());

                    //if not excluded continue
                    if (matching.size() == 0) {
                        // adding full path of the page or replicated content path
                        jsonArray.add(MessageFormat.format("{0}://{1}{2}", protocol, domain, path));

                        //add trimmed path
                        if (StringUtils.isNotEmpty(additionalTrimPath)) {
                            String pathAfter = path.substring(path.indexOf(additionalTrimPath) + additionalTrimPath.length());
                            jsonArray.add(MessageFormat.format("{0}://{1}{2}", protocol, domain, pathAfter));
                        }
                    }
                }

            } catch (Exception e) {
                throw new ReplicationException("Could not retrieve content from content builder", e);
            }
        } else {
            throw new ReplicationException("Could not compile payload need to specify domain and protocol");
        }

        logReplicationEventInfoStatement("Requesting Akamai cache purge for the urls: " + jsonArray);

        return jsonArray;
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String read(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Please provide a valid input stream");
        }
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(inputStream));
        String output;
        StringBuilder outputString = new StringBuilder();
        while ((output = br.readLine()) != null) {
            outputString.append(output);
        }
        return outputString.toString();
    }

    private HttpResponse sendRequest(HttpPost request, String baseurl, String purgeurlpath, String token, String accesstoken, String secret, String protocol) throws ReplicationException {
        if (baseurl != null &&
            purgeurlpath != null &&
            token != null &&
            accesstoken != null &&
            secret != null &&
            protocol != null &&
            request != null) {
            String body = null;

            try {
                body = read(request.getEntity().getContent());
            } catch (IOException e) {
                logReplicationEventInfoStatement("IOException occurred while reading the tmp file created bu Akamai Content builder " + e);
            }

            String clientToken = "";
            try {
                clientToken = cryptoSupport.isProtected(token) ? cryptoSupport.unprotect(token): token;
            } catch (CryptoException e) {
                throw new ReplicationException("Could not unprotect token.", e);
            }
            String clientAccessToken = "";
            try {
                clientAccessToken = cryptoSupport.isProtected(accesstoken) ? cryptoSupport.unprotect(accesstoken): accesstoken;
            } catch (CryptoException e) {
                throw new ReplicationException("Could not unprotect accesstoken.", e);
            }
            String clientSecret = "";
            try {
                clientSecret = cryptoSupport.isProtected(secret) ? cryptoSupport.unprotect(secret): secret;
            } catch (CryptoException e) {
                throw new ReplicationException("Could not unprotect secret.", e);
            }

            String authHeader = generateAuthHeader(clientToken, clientAccessToken);
            String bodyHash = crypto(body, clientSecret);

            String data = StringUtils.join(getDataToSign(request, authHeader, bodyHash, baseurl, purgeurlpath, protocol), "\t");
            String authorizationHeader = authHeader + "signature=" + crypto(data, clientSecret);

            request.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader);
            request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

            HttpResponse response;

            if(client == null) {
                client = HttpClientBuilder.create()
                    .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(getClientCredential(clientAccessToken,clientToken,clientSecret,baseurl)))
                    .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(getClientCredential(clientAccessToken,clientToken,clientSecret,baseurl)))
                    .setConnectionTimeToLive(1000, TimeUnit.MILLISECONDS)
                    .build();
            }
            try {
                response = client.execute(request);
            } catch (IOException e) {
                throw new ReplicationException("Could not send replication request.", e);
            }

            return response;
        }
        return null;
    }

    /**
     * prepare the authentication header with the values from configs
     *
     * @return auth header
     */
    private String generateAuthHeader(String clientToken, String clientAccessToken) {
        UUID nonce = UUID.randomUUID();
        Date date = new Date();
        String timeStamp = getTimeStamp(date);
        String authHeader = EG_1_HMAC_SHA_256 + StringUtils.SPACE;

        authHeader += "client_token=" + clientToken + SEMICOLON;
        authHeader += "access_token=" + clientAccessToken + SEMICOLON;
        authHeader += "timestamp=" + timeStamp + SEMICOLON;
        authHeader += "nonce=" + nonce + SEMICOLON;
        return authHeader;
    }

    /***
     *
     * @param date
     * @return
     */
    private static String getTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(TIME_STAMP_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * creates ClientCredential object with Akamai configs which is required for POST and returns it.
     *
     * @return ClientCredential
     */
    private ClientCredential getClientCredential(String clientAccessToken, String clientToken, String clientSecret, String baseUrl) {
        return ClientCredential.builder()
            .accessToken(clientAccessToken)
            .clientToken(clientToken)
            .clientSecret(clientSecret)
            .host(baseUrl)
            .build();
    }

    /**
     * encrypts the secret values using hashHMacSha256 by encoding them.
     *
     * @param message
     * @param secret
     * @return
     */
    private String crypto(String message, String secret) {
        String hmacString = hashHMACSHA256(secret, message);
        if (StringUtils.isNotBlank(hmacString)) {
            return Base64.getEncoder().encodeToString(hmacString.getBytes());
        }
        return StringUtils.EMPTY;
    }

    /**
     * encrypts the secret values using hashHMacSha256 by encoding them.
     *
     * @return
     */
    private String hashHMACSHA256(String key, String data) {
        try {
            Mac sha256Hmac = Mac.getInstance(HMAC_SHA_256.name());
            SecretKeySpec secretkey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256.name());
            sha256Hmac.init(secretkey);

            return new String(Hex.encodeHex(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) {
            logReplicationEventInfoStatement("NoSuchAlgorithmException occurred while encrypting the keys " + e);
        } catch (InvalidKeyException e) {
            logReplicationEventInfoStatement("InvalidKeyException occurred while encrypting the keys " + e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * prepare the list with configured values and returns it.
     *
     * @param request
     * @param authHeader
     * @param bodyHash
     * @param <T>
     * @return
     */
    private <T extends HttpRequestBase> List<String> getDataToSign(T request, String authHeader, String bodyHash, String baseUrl, String PurgeUrl, String protocol) {
        List<String> dataToSign = new ArrayList<>();
        dataToSign.add(request.getMethod());
        dataToSign.add(protocol + COLON);
        dataToSign.add(baseUrl);
        dataToSign.add(PurgeUrl);
        dataToSign.add(bodyHash);
        dataToSign.add(authHeader);

        return dataToSign;
    }

}
