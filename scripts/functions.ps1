# DEFAULTS set these before including this file
$script:PARENT_PROJECT_PATH = (&{If($PARENT_PROJECT_PATH -eq $null) {".."} else {$PARENT_PROJECT_PATH}})
$script:DEFAULT_POM_FILE = (&{If($DEFAULT_POM_FILE -eq $null) {"${PARENT_PROJECT_PATH}\pom.xml"} else {$DEFAULT_POM_FILE}})
$script:POM_FILE = (&{If($POM_FILE -eq $null) {"${DEFAULT_POM_FILE}"} else {$POM_FILE}})
$script:SKIP_PRINT_CONFIG = (&{If($SKIP_PRINT_CONFIG -eq $null) {$false} else {$SKIP_PRINT_CONFIG}})
$script:SKIP_CONFIG = (&{If($SKIP_CONFIG -eq $null) {$false} else {$SKIP_CONFIG}})
$script:LOG_PATH = (&{If($LOG_PATH -eq $null) {"${PWD}\logs"} else {$LOG_PATH}})
$script:TEST_SELENIUM_URL = (&{If($TEST_SELENIUM_URL -eq $null) {""} else {$TEST_SELENIUM_URL}})

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
    [string]$IPCONFIG_COMMAND_OUTPUT = "${LOG_PATH}\ipconfig.log"
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
    printSectionLine "DEFAULT MISSING IN POM: $PARAM_NAME"
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

  # Save previous colors
  $previousForegroundColor = $host.UI.RawUI.ForegroundColor
  $previousBackgroundColor = $host.UI.RawUI.BackgroundColor

  if ( -not ([string]::IsNullOrEmpty(${LOG_FILE})) )
  {
    Write-Output "${TEXT}" | Add-Content -Path "${LOG_FILE}"
  }

  $TEXT_COLOR = $host.ui.rawui.ForegroundColor
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
  } else {
    $TEXT_COLOR = "gray"
  }

  $host.UI.RawUI.ForegroundColor = $TEXT_COLOR

  If ($MyInvocation.Line -like "*debug*")
  {
    Write-Output "${TEXT}"
  } elseif ($MyInvocation.Line -like "*printSectionLine *") {
    Write-Output $TEXT
  } elseif ($MyInvocation.Line -like "*printSectionStart *") {
    Write-Output "$("=" * 100)"
    Write-Output $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}","||"," ",$TEXT))
    Write-Output "$("=" * 100)"
  } elseif ($MyInvocation.Line -like "*printSectionEnd *") {
    Write-Output "$("^" * 100)"
    Write-Output $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}","||"," ",$TEXT))
    Write-Output "$("=" * 100)"
  } elseif ($MyInvocation.Line -like "*printSectionBanner *") {
    Write-Output "$("@" * 100)"
    Write-Output $([string]::Format("{0}{1,15}{2,-75}{1,8}{0}","@"," ",$TEXT))
    Write-Output "$("@" * 100)"
  } elseif ($MyInvocation.Line -like "*printSubSectionStart *") {
    Write-Output "$("~" * 100)"
    Write-Output $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}"," ~"," ",$TEXT))
    Write-Output "$("~" * 100)"
  } elseif ($MyInvocation.Line -like "*printSubSectionEnd *") {
    Write-Output "$("^" * 100)"
    Write-Output $([string]::Format("{0}{1,15}{2,-75}{1,6}{0}"," ~"," ",$TEXT))
    Write-Output "$("~" * 100)"
  } else {
    Write-Output "${TEXT}"
  }

}


Function Do-DebugOn
{
  [Cmdletbinding()]
  [Alias("debugOn")]
  param()
  $script:DEBUG = $true

}


Function Do-DebugOff
{
  [Cmdletbinding()]
  [Alias("debugOff")]
  param()
  $script:DEBUG = $false

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

Function Do-CreateDir {
  [Cmdletbinding()]
  [Alias("createDir")]
  param(
    [string]$NAME = $args[0]
  )
  if (-not([string]::IsNullOrWhitespace($NAME)))
  {
    return $([System.IO.Directory]::CreateDirectory("${NAME}") )
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

  # ensure default log path
  if ( [string]::IsNullOrEmpty(${LOG_PATH}) ) {
   $script:LOG_PATH = "${PWD}\logs"
  }

  $script:LOG_PATH = (createDir $LOG_PATH)
  $script:DOCKER_LOGS_FOLDER = (createDir $DOCKER_LOGS_FOLDER)
  $script:DRIVER_FOLDER = (createDir $DRIVER_FOLDER)

  Write-Output "${LOG_PATH}"

  # set logfile name
  $script:LOG_FILENAME_DATE = "$(DateStamp)"
  $script:LOG_FILENAME = "${LOG_PEFIX}-${DOCKER_NETWORK_NAME}-${LOG_FILENAME_DATE}${LOG_SUFFIX}"
  $script:LOG_FILE = "${LOG_PATH}\${LOG_FILENAME}"

  Write-Output "${LOG_FILE}"

  $script:LOCAL_IP = (Get-LocalIP)

  if (-not($SKIP_CONFIG))
  {
    printSectionBanner "Maven Config" "info"

    # load pom file
    [xml]$POM_FILE_XML = (Get-Content $POM_FILE)

    printSectionLine "LOG_FILE: ${LOG_FILE}"
    printSectionLine "PARENT_PROJECT_PATH: ${PARENT_PROJECT_PATH}"
    printSectionLine "DEFAULT_POM_FILE: ${DEFAULT_POM_FILE}"
    printSectionLine "POM_FILE: ${POM_FILE}"
    printSectionLine "SCRIPT_PARAMS: ${SCRIPT_PARAMS}"

    $script:AEM_USER = $( getParamOrDefault "${AEM_USER}" "aem.password" "${POM_FILE}" )
    $script:AEM_PASS = $( getParamOrDefault "${AEM_PASS}" "aem.username" "${POM_FILE}" )
    $script:AEM_SCHEME = $( getParamOrDefault "${AEM_SCHEME}" "aem.scheme" "${POM_FILE}" )
    $script:AEM_HOST = $( getParamOrDefault "${AEM_HOST}" "aem.host" "${POM_FILE}" )
    $script:AEM_PORT = $( getParamOrDefault "${AEM_PORT}" "aem.port" "${POM_FILE}" )
    $script:AEM_SCHEMA = $( getParamOrDefault "${AEM_SCHEMA}" "package.uploadProtocol" "${POM_FILE}" )
    $script:SELENIUMHUB_HOST = $( getParamOrDefault "${SELENIUMHUB_HOST}" "seleniumhubhost.host" "${POM_FILE}" )
    $script:SELENIUMHUB_PORT = $( getParamOrDefault "${SELENIUMHUB_PORT}" "seleniumhubhost.port" "${POM_FILE}" )
    $script:SELENIUMHUB_SCHEME = $( getParamOrDefault "${SELENIUMHUB_SCHEME}" "seleniumhubhost.scheme" "${POM_FILE}" )
    $script:SELENIUMHUB_SERVICE = $( getParamOrDefault "${SELENIUMHUB_SERVICE}" "seleniumhubhost.service" "${POM_FILE}" )

    if ($AEM_HOST -eq "localhost")
    {
      $script:AEM_HOST = $LOCAL_IP
    }
    if ($SELENIUMHUB_HOST -eq "localhost")
    {
      $script:SELENIUMHUB_HOST = $LOCAL_IP
    }

    if (-not($SKIP_PRINT_CONFIG))
    {

      printSectionLine "Params:     $SCRIPT_PARAMS"
      printSectionLine " - POM_FILE:   ${POM_FILE}"
      printSectionLine " - AEM_USER:   $AEM_USER"
      printSectionLine " - AEM_PASS:   $( "*" * $AEM_PASS.length )"
      printSectionLine " - AEM_SCHEME: $AEM_SCHEME"
      printSectionLine " - AEM_HOST:   $AEM_HOST"
      printSectionLine " - AEM_PORT:   $AEM_PORT"
      printSectionLine " - AEM_SCHEMA: $AEM_SCHEMA"
      printSectionLine " - SELENIUMHUB_HOST: $SELENIUMHUB_HOST"
      printSectionLine " - SELENIUMHUB_PORT: $SELENIUMHUB_PORT"
      printSectionLine " - SELENIUMHUB_SCHEME: $SELENIUMHUB_SCHEME"
      printSectionLine " - SELENIUMHUB_SERVICE: $SELENIUMHUB_SERVICE"

      $script:SKIP_PRINT_CONFIG = $false
    }

  }

}

### SLING POST - WORKFLOW

[HashTable]$script:BODY_SERVICE_TO_DISABLE = @{
    "action"="stop"
}
[HashTable]$script:BODY_SERVICE_TO_DISABLE_ENABLE = @{
    "action"="start"
}
[HashTable]$script:WORKFLOW_ASSET_ENABLE_UPDATE = @{
    "jcr:primaryType"= "cq:WorkflowLauncher"
    "description"= "Update Asset - Modification"
    "enabled"= "true"
    "conditions"= "jcr:content/jcr:mimeType!=video/.*"
    "glob"= "/content/dam(/((?!/subassets).)*/)renditions/original"
    "eventType"= "16"
    "workflow"= "/var/workflow/models/dam/update_asset"
    "runModes"= "author"
    "nodetype"= "nt:file"
    "excludeList"= "event-user-data:changedByWorkflowProcess"
    "enabled@TypeHint"="Boolean"
    "eventType@TypeHint"="Long"
    "conditions@TypeHint"="String[]"
}

[HashTable]$script:WORKFLOW_ASSET_DISABLE_UPDATE = @{
    "jcr:primaryType"= "cq:WorkflowLauncher"
    "description"= "Update Asset - Modification"
    "enabled"= "false"
    "conditions"= "jcr:content/jcr:mimeType!=video/.*"
    "glob"= "/content/dam(/((?!/subassets).)*/)renditions/original"
    "eventType"= "16"
    "workflow"= "/var/workflow/models/dam/update_asset"
    "runModes"= "author"
    "nodetype"= "nt:file"
    "excludeList"= "event-user-data:changedByWorkflowProcess"
    "enabled@TypeHint"="Boolean"
    "eventType@TypeHint"="Long"
    "conditions@TypeHint"="String[]"
}

[HashTable]$script:WORKFLOW_ASSET_ENABLE_CREATE = @{
    "jcr:primaryType"= "cq:WorkflowLauncher"
    "description"= "Update Asset - Create"
    "enabled"= "true"
    "conditions"= "jcr:content/jcr:mimeType!=video/.*"
    "glob"= "/content/dam(/((?!/subassets).)*/)renditions/original"
    "eventType"= "1"
    "workflow"= "/var/workflow/models/dam/update_asset"
    "runModes"= "author"
    "nodetype"= "nt:file"
    "excludeList"= "event-user-data:changedByWorkflowProcess"
    "enabled@TypeHint"="Boolean"
    "eventType@TypeHint"="Long"
    "conditions@TypeHint"="String[]"
}

[HashTable]$script:WORKFLOW_ASSET_DISABLE_CREATE = @{
    "jcr:primaryType"= "cq:WorkflowLauncher"
    "description"= "Update Asset - Create"
    "enabled"= "false"
    "conditions"= "jcr:content/jcr:mimeType!=video/.*"
    "glob"= "/content/dam(/((?!/subassets).)*/)renditions/original"
    "eventType"= "16"
    "workflow"= "/var/workflow/models/dam/update_asset"
    "runModes"= "author"
    "nodetype"= "nt:file"
    "excludeList"= "event-user-data:changedByWorkflowProcess"
    "enabled@TypeHint"="Boolean"
    "eventType@TypeHint"="Long"
    "conditions@TypeHint"="String[]"
}


function doSlingPost {
    [CmdletBinding()]
    Param (

        [Parameter(Mandatory=$true)]
        [string]$Url="http://localhost:4502",

        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [ValidateSet('Post','Delete')]
        [string]$Method,

        [Parameter(Mandatory=$false)]
        [HashTable]$Body,

        [Parameter(Mandatory=$false,
                HelpMessage="Provide Basic Auth Credentials in following format: <user>:<pass>")]
        [string]$BasicAuthCreds="",

        [Parameter(Mandatory=$false)]
        [string]$UserAgent="",

        [Parameter(Mandatory=$false)]
        [string]$Referer="",

        [Parameter(Mandatory=$false)]
        [string]$Timeout="5"

    )



    $HEADERS = @{
    }

    if (-not([string]::IsNullOrEmpty($UserAgent))) {
        $HEADERS.add("User-Agent",$UserAgent)
    }

    if (-not([string]::IsNullOrEmpty($Referer))) {
        $HEADERS.add("Referer",$Referer)
    }


    if (-not([string]::IsNullOrEmpty($BasicAuthCreds))) {
        $BASICAUTH = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($BasicAuthCreds))
        $HEADERS.add("Authorization","Basic $BASICAUTH")
    }


    Write-Output "Performing action $Method on $Url."

    $Response = Invoke-WebRequest -Method Post -Headers $HEADERS -TimeoutSec $Timeout -Uri "$Url" -Form $Body -ContentType "application/x-www-form-urlencoded"

}


#run main
Main
