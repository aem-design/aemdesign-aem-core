<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="org.apache.jackrabbit.commons.flat.TreeTraverser" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="org.apache.sling.jcr.resource.JcrResourceConstants" %>
<%@ page import="org.apache.sling.api.SlingHttpServletResponse" %>
<%@ page import="com.day.cq.wcm.foundation.TextFormat" %>
<%@ page import="com.day.cq.wcm.foundation.forms.LayoutHelper" %>
<%@ page import="com.day.cq.wcm.foundation.forms.FormResourceEdit" %>
<%@ page import="com.day.cq.wcm.foundation.forms.ValidationInfo" %>
<%@ page import="com.day.cq.wcm.foundation.forms.FormsConstants" %>
<%@ page import="com.day.cq.wcm.foundation.forms.FormsHelper" %>

<%!

    //public static final String NODE_PAR = "./article/par";
    public static final String RESOURCE_TYPE_FORMS_ACTION = "aemdesign/components/forms/actions/formRedirect";
    public static final String RESOURCE_TYPE_FORMS_START = "aemdesign/components/forms/form";




    public  void renderFormStart(Page redirectPage , String actionType, String formid, SlingHttpServletRequest _slingRequest, SlingHttpServletResponse _slingResponse){

        if (RESOURCE_TYPE_FORMS_ACTION.equals(actionType)){

            String postToURL = StringUtils.EMPTY;

            try{

                if (redirectPage != null){

                    postToURL = mappedUrl(_slingRequest.getResourceResolver(), redirectPage.getPath().concat(DEFAULT_EXTENTION));

                    Node node = findFormStartNode(redirectPage);

                    String startNodePath = node.getPath();

                    PrintWriter out = _slingResponse.getWriter();

                    out.print("<form method=\"POST\" action=\"");
                    out.print(postToURL);
                    out.print("\">");

                    out.print("<input type=\"hidden\" name=\":formid\" value=\"");
                    out.print(formid);
                    out.print("\">");

                    out.print("<input type=\"hidden\" name=\":formstart\" value=\"");
                    out.print(startNodePath);
                    out.print("\">");
                }

            }catch (Exception e){
                //LoG
                LOG.error("Failed to perform ["+RESOURCE_TYPE_FORMS_ACTION+"] action " + e.getMessage(), e);
            }


        }else{
            try{
                FormsHelper.startForm(_slingRequest, _slingResponse);
            }catch(Exception e){
                LOG.error("Failed to perform ["+actionType+"] action " + e.getMessage(), e);

            }

        }

    }


    /**
     *
     * @param page
     * @return
     * @throws RepositoryException
     */
    public Node findFormStartNode(Page page) throws RepositoryException{

        Node node = null;

        if (page != null){

            Resource parsysR = page.getContentResource(NODE_PAR);
            if (parsysR != null) {
                Node parsysN = parsysR.adaptTo(Node.class);
                Iterator nodeIt = TreeTraverser.nodeIterator(parsysN);
                while (nodeIt.hasNext()) {
                    node = (Node)nodeIt.next();

                    if (node.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) && node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getValue().getString().equals(RESOURCE_TYPE_FORMS_START)){
                        break;
                    }
                }
            }
        }
        return node;
    }


    /**
     * Get Cross Field Error Message
     * @param _slingRequest
     * @param values
     * @param name
     * @return
     * @throws IOException
     */
    public String [] getCrossFieldErrorMessages(SlingHttpServletRequest _slingRequest, List<String> values, String name) {

        String [] errorList = new String[0];

        //validation logic
        if (values != null) {

            ValidationInfo info = ValidationInfo.getValidationInfo(_slingRequest);

            for (int j = 0; j < values.size(); j++) {
                // constraints (e.g. "number") are checked per field (multiple fields when multi value)
                if (info != null) {
                    errorList = info.getErrorMessages(name, j);
                }

            }
        }

        return errorList;
    }

    /**
     * Get Single Field Error Message
     * @param _slingRequest
     * @param name
     * @return
     * @throws IOException
     */
    public String [] getSingleFieldErrorMessages(SlingHttpServletRequest _slingRequest, String name) {

        String[] errorList = new String[0];

        //validation logic
        ValidationInfo info = ValidationInfo.getValidationInfo(_slingRequest);

        // check mandatory and single values constraints
        if (info != null) {
            errorList = info.getErrorMessages(name);
        }

        return errorList;
    }

    public ComponentProperties getComponentFormProperties(SlingHttpServletRequest _slingRequest, Resource _resource) throws IOException {

        ComponentProperties componentProperties = new ComponentProperties();

        String name = FormsHelper.getParameterName(_resource);
        String id = FormsHelper.getFieldId(_slingRequest, _resource);
        boolean required = FormsHelper.isRequired(_resource);

        List<String> values = FormsHelper.getValuesAsList(_slingRequest, _resource);

        String [] singleFieldErrorMessages = getSingleFieldErrorMessages(_slingRequest, name);

        String [] crossFieldErrorMessages = getCrossFieldErrorMessages(_slingRequest, values,  name);

        componentProperties.put("name", name);
        componentProperties.put("id", id);
        componentProperties.put("required", required);
        componentProperties.put("values", values);
        componentProperties.put("singleFieldErrorMessages", singleFieldErrorMessages);
        componentProperties.put("crossFieldErrorMessages", crossFieldErrorMessages);

        return componentProperties;
    }

%>