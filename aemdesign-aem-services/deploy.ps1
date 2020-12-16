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
  [string]$MVN_COMMAND = "mvn -Dvault.useProxy=false -DskipTests clean deploy -P autoInstallBundle -Dmaven.deploy.skip=true -DskipNexusStagingDeployMojo=true"

)

. ".\scripts\functions.ps1"

$script:LOG_PATH = $LOG_PATH
$script:TEST_SELENIUM_URL = $TEST_SELENIUM_URL

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
  Invoke-Expression -Command "$MVN_COMMAND" | Tee-Object -Append -FilePath "${LOG_FILENAME}"

} else {
  printSectionLine "AEM is not currently available!" "error"
  exit 1
}

