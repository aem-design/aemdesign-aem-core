Param(
  [string]$LOG_PATH = "..\logs",
  [string]$LOG_PEFIX = "package",
  [string]$LOG_SUFFIX = ".log",
  [string]$DOCKER_LOGS_FOLDER = "..\logs\docker",
  [string]$AEM_SCHEME = "http",
  [string]$AEM_HOST = "localhost",
  [string]$AEM_PORT = "4502",
  [string]$AEM_USERNAME = "admin",
  [string]$AEM_PASSWORD = "admin",
  [string]$MVN_COMMAND = "mvn -D""vault.useProxy=false"" -DskipTests clean package ",
  [string]$FUNCTIONS_URI = "https://github.com/aem-design/aemdesign-docker/releases/latest/download/functions.ps1"
)

$SKIP_CONFIG = $true
$PARENT_PROJECT_PATH = ".."

. ([Scriptblock]::Create((([System.Text.Encoding]::ASCII).getString((Invoke-WebRequest -Uri "${FUNCTIONS_URI}").Content))))

$script:LOG_PATH = $LOG_PATH
$script:TEST_SELENIUM_URL = $TEST_SELENIUM_URL

printSectionBanner "Creating Package"
printSectionLine ("$MVN_COMMAND" -replace "$AEM_PASSWORD", "***")

Invoke-Expression -Command "$MVN_COMMAND" | Tee-Object -Append -FilePath "${LOG_FILE}"

