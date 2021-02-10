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
  [string]$MVN_COMMAND = "mvn -D""vault.useProxy=false"" -DskipTests clean package "
)

. "..\scripts\functions.ps1"

$script:LOG_PATH = $LOG_PATH
$script:TEST_SELENIUM_URL = $TEST_SELENIUM_URL

printSectionBanner "Creating Package"
printSectionLine ("$MVN_COMMAND" -replace "$AEM_PASSWORD", "***")

Invoke-Expression -Command "$MVN_COMMAND" | Tee-Object -Append -FilePath "${LOG_FILENAME}"

