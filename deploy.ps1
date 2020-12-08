Param(
  [string]$LOG_PATH = "${PWD}\logs",
  [string]$LOG_PEFIX = "${LOG_PATH}\deploy",
  [string]$LOG_SUFFIX = ".log",
  [string]$DOCKER_LOGS_FOLDER = "${PWD}\logs\docker",
  [string]$AEM_SCHEME = "http",
  [string]$AEM_HOST = "localhost",
  [string]$AEM_PORT = "4502",
  [string]$AEM_USERNAME = "admin",
  [string]$AEM_PASSWORD = "admin"

)

. ".\scripts\functions.ps1"

$global:LOG_PATH = $LOG_PATH
$global:TEST_SELENIUM_URL = $TEST_SELENIUM_URL

printSectionBanner "Deploying Monolith Package:" "warn"
printSectionLine "mvn -Dvault.useProxy=false -DskipTests -e -U -P installdeploymentpackage clean install"


#update host
if ( $TEST_HOST -eq "localhost" )
{
  debug "Test host is set as localhost, updating to use local ip" "info"
  $global:TEST_HOST="${LOCAL_IP}"
}

$global:AEM_AVAILABLE=$(testServer "${AEM_SCHEME}://${AEM_HOST}:${AEM_PORT}")

printSectionLine "Is AEM at ${AEM_SCHEME}://${AEM_HOST}:${AEM_PORT} available? ${AEM_AVAILABLE}"

if ( $AEM_AVAILABLE )
{
  printSectionLine "AEM host is available!" "info"
  printSectionLine "Deploying:" "info"
  Invoke-Expression -Command "mvn -DskipTests -e -U -P installdeploymentpackage clean install" | Tee-Object -Append -FilePath "${LOG_FILENAME}"

} else {
  printSectionLine "AEM is not currently available!" "error"
  exit 1
}

