Param(
  [string]$LOG_PATH = "${PWD}\logs",
  [string]$LOG_PEFIX = "${LOG_PATH}\deploy",
  [string]$LOG_SUFFIX = ".log",
  [string]$DOCKER_LOGS_FOLDER = "${PWD}\logs\docker",
  [string]$AEM_SCHEME = "http",
  [string]$AEM_HOST = "localhost",
  [string]$AEM_PORT = "4502",
  [string]$AEM_USERNAME = "admin",
  [string]$AEM_PASSWORD = "admin",
  [string]$MVN_COMMAND = "mvn -D""vault.useProxy=false"" -DskipTests clean install -P autoInstallPackage -D""aem.port=$AEM_PORT"" -D""aem.host=$AEM_HOST"" -D""aem.username=$AEM_USERNAME"" -D""aem.password=$AEM_PASSWORD"" -D""aem.scheme=$AEM_SCHEME"" "

)

. "..\scripts\functions.ps1"

printSectionBanner "Deploying:" "warn"
printSectionLine ("$MVN_COMMAND" -replace "$AEM_PASSWORD", "***")


#update host
if ( $TEST_HOST -eq "localhost" )
{
  debug "Test host is set as localhost, updating to use local ip" "info"
  $script:TEST_HOST="${LOCAL_IP}"
}

$script:AEM_AVAILABLE=$(testServer "${AEM_SCHEME}://${AEM_HOST}:${AEM_PORT}")

printSectionLine "Is AEM at ${AEM_SCHEME}://${AEM_HOST}:${AEM_PORT} available? ${AEM_AVAILABLE}"

if ( $AEM_AVAILABLE )
{
  printSectionLine "AEM host is available!" "info"
  printSectionLine "Deploying:" "info"
  Invoke-Expression -Command "$MVN_COMMAND" | Tee-Object -Append -FilePath "${LOG_FILE}"

} else {
  printSectionLine "AEM is not currently available!" "error"
  exit 1
}

