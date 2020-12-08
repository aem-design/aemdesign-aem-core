Param(
  [string]$PARENT_PROJECT_PATH = ".",
  [string]$DEFAULT_POM_FILE = "${PARENT_PROJECT_PATH}\pom.xml",
  [string]$POM_FILE = "${DEFAULT_POM_FILE}"
)

Function Get-DateStamp
{
    [Cmdletbinding()]
    [Alias("DateStamp")]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$Text
    )

    return "{0:yyyyMMdd}-{0:HHmmss}" -f (Get-Date)

}

Function Get-TimeStamp
{
    [Cmdletbinding()]
    [Alias("TimeStamp")]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$Text
    )

    return "{0:MM/dd/yy} {0:HH:mm:ss}" -f (Get-Date)

}

Function Get-LocalIP
{
  [Cmdletbinding()]
  [Alias("LocalIP")]
  param
  (
    [Parameter(ValueFromPipeline)]
    [string]$ITERFACE_NAME = "(Default Switch)",
    [string]$CONFIG_NAME = "IPv4 Address",
    [string]$IPCONFIG_COMMAND = "ipconfig",
    [string]$IPCONFIG_COMMAND_OUTPUT = ".\logs\ipconfig.log"
  )
  Invoke-Expression -Command ${IPCONFIG_COMMAND} | Set-Content ${IPCONFIG_COMMAND_OUTPUT}

  # GET SECTION LINES
  $RESULT_INTERFACE = (Get-Content "$IPCONFIG_COMMAND_OUTPUT") | Select-String -SimpleMatch -Pattern "$ITERFACE_NAME" -Context 0,6 | Set-Content ${IPCONFIG_COMMAND_OUTPUT}
  # GET IP LINE
  $RESULT_INTERFACE = (Get-Content "$IPCONFIG_COMMAND_OUTPUT") | Select-String -SimpleMatch -Pattern "$CONFIG_NAME" | Set-Content ${IPCONFIG_COMMAND_OUTPUT}
  $IP_ADDRESS = (Get-Content "$IPCONFIG_COMMAND_OUTPUT").Split(":")[1].Trim()


  if ( [string]::IsNullOrEmpty($IP_ADDRESS) )
  {
    printSectionLine "COULD NOT FIND CURRENT IP ADDRESS"
    $IP_ADDRESS = "127.0.0.1"
  }

  return "$IP_ADDRESS"

}



Function Get-DefaultFromPom
{
  [Cmdletbinding()]
  [Alias("getDefaultFromPom")]
  param
  (
    [Parameter(ValueFromPipeline)]
    [string]$PARAM_NAME = $args[0],
    [string]$POM_FILE = $args[1]
  )

  return $POM_FILE_XML.project.properties["${PARAM_NAME}"].InnerText

}

Function Get-ParamOrDefault
{
  [Cmdletbinding()]
  [Alias("getParamOrDefault")]
  param
  (
    [Parameter(ValueFromPipeline)]
    [string]$PARAM = $args[0],
    [string]$PARAM_NAME = $args[1],
    [string]$POM_FILE = $args[3],
    [string]$DEFAULT_VALUE = "$(getDefaultFromPom "${PARAM_NAME}" "${POM_FILE}")"
  )

  if ( [string]::IsNullOrEmpty(${DEFAULT_VALUE}) )
  {
    printSectionLine "DEFAULT MISSING IN POM"
  } else {
    if ( [string]::IsNullOrEmpty(${PARAM}) )
    {
      return "${DEFAULT_VALUE}"
    } else {
      return ${PARAM}
    }
  }

}

Function Get-EvalMaven
{
  [Cmdletbinding()]
  [Alias("evalMaven")]
  param
  (
    [Parameter(ValueFromPipeline)]
    [string]$PARAM
  )

  return $(mvn help:evaluate -q -DforceStdout -D"expression=$PARAM")

}

Function Get-GitDir
{
  [Cmdletbinding()]
  [Alias("getGitDir")]
  param()
  return Resolve-Path (git rev-parse --git-dir) | Split-Path -Parent

}

Function Do-Debug
{
  [Cmdletbinding()]
  [Alias("debug")]
  [Alias("printSectionLine")]
  [Alias("printSectionBanner")]
  [Alias("printSectionStart")]
  [Alias("printSectionEnd")]
  [Alias("printSubSectionStart")]
  [Alias("printSubSectionEnd")]
  param
  (
    [Parameter(ValueFromPipeline)]
    [string]$TEXT = $args[0],
    [string]$TYPE = $args[1]
  )

  if ( -not ([string]::IsNullOrEmpty(${LOG_FILENAME})) )
  {
    Write-Output "${TEXT}" | Add-Content -Path "${LOG_FILENAME}"
  }

  $TEXT_COLOR = (get-host).ui.rawui.ForegroundColor
  If ($TYPE -eq "error")
  {
    $TEXT_COLOR = "red"
  }
  elseif ($TYPE -eq "info")
  {
    $TEXT_COLOR = "blue"
  }
  elseif ($TYPE -eq "warn")
  {
    $TEXT_COLOR = "yellow"
  }

  If ($MyInvocation.Line -like "* debug *")
  {
    Write-Host "${TEXT}" -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSectionLine *") {
    Write-Host $TEXT -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSectionStart *") {
    Write-Host "$("=" * 100)" -ForegroundColor $TEXT_COLOR
    Write-Host $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}","||"," ",$TEXT)) -ForegroundColor $TEXT_COLOR
    Write-Host "$("=" * 100)" -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSectionEnd *") {
    Write-Host "$("^" * 100)" -ForegroundColor $TEXT_COLOR
    Write-Host $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}","||"," ",$TEXT)) -ForegroundColor $TEXT_COLOR
    Write-Host "$("=" * 100)" -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSectionBanner *") {
    Write-Host "$("@" * 100)" -ForegroundColor $TEXT_COLOR
    Write-Host $([string]::Format("{0}{1,15}{2,-75}{1,8}{0}","@"," ",$TEXT)) -ForegroundColor $TEXT_COLOR
    Write-Host "$("@" * 100)" -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSubSectionStart *") {
    Write-Host "$("~" * 100)" -ForegroundColor $TEXT_COLOR
    Write-Host $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}"," ~"," ",$TEXT)) -ForegroundColor $TEXT_COLOR
    Write-Host "$("~" * 100)" -ForegroundColor $TEXT_COLOR
  } elseif ($MyInvocation.Line -like "* printSubSectionEnd *") {
    Write-Host "$("^" * 100)" -ForegroundColor $TEXT_COLOR
    Write-Host $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}"," ~"," ",$TEXT)) -ForegroundColor $TEXT_COLOR
    Write-Host "$("~" * 100)" -ForegroundColor $TEXT_COLOR
  }

}


Function Do-DebugOn
{
  [Cmdletbinding()]
  [Alias("debugOn")]
  param()
  $global:DEBUG = $true

}


Function Do-DebugOff
{
  [Cmdletbinding()]
  [Alias("debugOff")]
  param()
  $global:DEBUG = $false

}


Function Do-TestServer
{
  [Cmdletbinding()]
  [Alias("testServer")]
  param(
    [string]$ADDRESS = $args[0]
  )

  try
  {
    $Response = Invoke-WebRequest -Uri "${ADDRESS}" -ErrorAction Stop
    # This will only execute if the Invoke-WebRequest is successful.
    $StatusCode = $Response.StatusCode
  }
  catch
  {
    $StatusCode = $_.Exception.Response.StatusCode.value__
  }


  if ( $StatusCode -eq "200" ) {
    return $true
  } else {
    return $false
  }
}

Function Main
{
#  printSectionBanner "printSectionBanner" "error"
#  printSectionLine "printSectionLine"
#  debug "debug"
#  debug "debug" "warn"
#  printSectionStart "printSectionStart" "warn"
#  printSectionEnd "printSectionEnd" "info"
#  printSubSectionStart "printSubSectionStart"
#  printSubSectionEnd "printSubSectionEnd"

  $LOG_PATH = $([System.IO.Directory]::CreateDirectory("${LOG_PATH}"))
  $DOCKER_LOGS_FOLDER = $([System.IO.Directory]::CreateDirectory("${DOCKER_LOGS_FOLDER}"))

  printSectionBanner "Config"

  # set logfile name
  $global:LOG_FILENAME_DATE = "$(DateStamp)"
  $global:LOG_FILENAME = "${LOG_PEFIX}-${DOCKER_NETWORK_NAME}-${LOG_FILENAME_DATE}${LOG_SUFFIX}"

  $global:LOCAL_IP = (Get-LocalIP)

  # load pom file
  [xml]$POM_FILE_XML = (Get-Content $POM_FILE)

  printSectionLine "AEM_USER: $AEM_USER"
  printSectionLine "LOG_FILENAME: ${LOG_FILENAME}"
  printSectionLine "PARENT_PROJECT_PATH: ${PARENT_PROJECT_PATH}"
  printSectionLine "DEFAULT_POM_FILE: ${DEFAULT_POM_FILE}"
  printSectionLine "POM_FILE: ${POM_FILE}"
  printSectionLine "SCRIPT_PARAMS: ${SCRIPT_PARAMS}"

  $global:AEM_USER=$(getParamOrDefault "${AEM_USER}" "crx.password" "${POM_FILE}")
  $global:AEM_PASS=$(getParamOrDefault "${AEM_PASS}" "crx.username" "${POM_FILE}")
  $global:AEM_SCHEME=$(getParamOrDefault "${AEM_SCHEME}" "crx.scheme" "${POM_FILE}")
  $global:AEM_HOST=$(getParamOrDefault "${AEM_HOST}" "crx.host" "${POM_FILE}")
  $global:AEM_PORT=$(getParamOrDefault "${AEM_PORT}" "crx.port" "${POM_FILE}")
  $global:AEM_SCHEMA=$(getParamOrDefault "${AEM_SCHEMA}" "package.uploadProtocol" "${POM_FILE}")

  if ( $AEM_HOST -eq "localhost" )
  {
    $global:AEM_HOST = $LOCAL_IP
  }
  if ( $SELENIUMHUB_HOST -eq "localhost" )
  {
    $global:SELENIUMHUB_HOST = $LOCAL_IP
  }

  if ( -not($SKIP_PRINT_CONFIG) )
  {

    printSectionLine "Params:     $SCRIPT_PARAMS"
    printSectionLine " - POM_FILE:   ${POM_FILE}"
    printSectionLine " - AEM_USER:   $AEM_USER"
    printSectionLine " - AEM_PASS:   $( "*" * $AEM_PASS.length )"
    printSectionLine " - AEM_SCHEME: $AEM_SCHEME"
    printSectionLine " - AEM_HOST:   $AEM_HOST"
    printSectionLine " - AEM_PORT:   $AEM_PORT"
    printSectionLine " - AEM_SCHEMA: $AEM_SCHEMA"

    $SKIP_PRINT_CONFIG = $false
  }

}

#run main
Main
