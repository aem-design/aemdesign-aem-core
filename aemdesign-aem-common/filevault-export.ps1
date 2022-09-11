Param(
    #equivalent of using localhost in docker container
    [string]$AEM_HOST = "localhost",
    # TCP port SOURCE_CQ listens on
    [string]$AEM_PORT = "4502",
    # AEM Admin user for AEM_HOST
    [string]$AEM_USER = "admin",
    # AEM Admin password for AEM_HOST
    [string]$AEM_PASSWORD = "admin",
    # Server WebDav Path
    #$AEM_WEBDAV_PATH = "/crx/server/crx.default/jcr:root/"
    [string]$AEM_WEBDAV_PATH = "/crx",
    [string]$AEM_SCHEMA = "http",
    #to set additional flags if required
    [string]$VLT_FLAGS = "--insecure -Xmx2g",
    [string]$VLT_CMD = "../tools/vault-cli/bin/vlt",
    # Root folder name for placing content
    [string]$CONTENT_DESTINATION = (Resolve-Path -Path ".\src\main\content" -Relative),
    [string]$FILTER_FILE = "${CONTENT_DESTINATION}\META-INF\vault\filter.xml",
    [string]$FILTER_FILE_LOCATION = "${CONTENT_DESTINATION}\META-INF",
    [switch]$Silent = $false,
    [string]$LOG_PATH = "..\logs",
    [Parameter(Position=0)]
    [string[]]$ROOT_PATHS
)

Function Format-XMLIndent
{
    [Cmdletbinding()]
    [Alias("IndentXML")]
    param
    (
        [Parameter(ValueFromPipeline)]
        [xml]$Content,
        [int]$Indent
    )

    # String Writer and XML Writer objects to write XML to string
    $StringWriter = New-Object System.IO.StringWriter
    $XmlWriter = New-Object System.XMl.XmlTextWriter $StringWriter

    # Default = None, change Formatting to Indented
    $xmlWriter.Formatting = "indented"

    # Gets or sets how many IndentChars to write for each level in
    # the hierarchy when Formatting is set to Formatting.Indented
    $xmlWriter.Indentation = $Indent

    $Content.WriteContentTo($XmlWriter)
    $XmlWriter.Flush();$StringWriter.Flush()
    $StringWriter.ToString()
}


Function GetFilterList
{
    [Cmdletbinding()]
    [Alias("filterList")]
    param
    (
        [Parameter(ValueFromPipeline)]
        [string]$FILTER_FILE = ".\src\main\content\META-INF\vault\filter.xml"
    )
    $FILTER_PATHS = [System.Collections.ArrayList]::new()

    $FILTER_XML = [xml](Get-Content $FILTER_FILE)
    $FILTER_XML_CONTENT = $FILTER_XML.SelectNodes("//workspaceFilter")
    $FILTER_XML_ITEMS = $FILTER_XML_CONTENT.SelectNodes('//filter')
    $FILTER_XML_ITEMS | ForEach-Object {
        [void]$FILTER_PATHS.Add($_.root)
    }

    return $FILTER_PATHS
}

if (-not($ROOT_PATHS)) {
    $ROOT_PATHS = GetFilterList
}

Write-Output "---------------------------------------------------"
Write-Output "------- EXPORT CONTENT FROM AN AEM INSTANCE ----------"
Write-Output "---------------------------------------------------"
Write-Output ""
Write-Output "------- CONFIG ----------"
Write-Output "AEM_SCHEMA: $AEM_SCHEMA"
Write-Output "AEM_HOST: $AEM_HOST"
Write-Output "AEM_PORT: $AEM_PORT"
Write-Output "AEM_USER: $AEM_USER"
Write-Output "CONTENT_DESTINATION: $CONTENT_DESTINATION"
Write-Output "ROOT_PATHS: $ROOT_PATHS"
Write-Output "FILTER_FILE: $FILTER_FILE"
Write-Output "Silent: $Silent"
Write-Output "VLT_FLAGS: $VLT_FLAGS"
Write-Output "VLT_CMD:"

$ROOT_PATHS | ForEach-Object {
    Write-Output "${VLT_CMD} ${VLT_FLAGS} --credentials ${AEM_USER}:****** export -v ${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}${AEM_WEBDAV_PATH} $_ ${CONTENT_DESTINATION}"
}

if (-not($Silent))
{
    $START = Read-Host -Prompt 'Do you want to start export with these settings? (y/n)'

    if ($START -ne "y")
    {
        Write-Output "Quiting..."
        Exit
    }
}

Write-Output "------- START Exporting content ----------"
$ROOT_PATHS_LAST = $ROOT_PATHS | Select-Object -Last 1
$ROOT_PATHS | ForEach-Object {
    Write-Output "START Export $_"
    $LOG_FILENAME = "$_".Replace("/","-")

    Write-Output "Remove Filer..."
    Copy-Item ".\src\main\content\META-INF\vault\filter-blank.xml" -Destination "$FILTER_FILE"

    Write-Output "Create filter for: $_"
    $FILTER_XML = [xml](Get-Content $FILTER_FILE)
    $FILTER_XML_CONTENT = $FILTER_XML.SelectNodes("//workspaceFilter")
    $FILTER_XML_DELETE = $FILTER_XML_CONTENT.SelectNodes('//filter')
    $FILTER_XML_DELETE | ForEach-Object{
        $DELETE_STATUS = $FILTER_XML_CONTENT.RemoveChild($_)
    }
    $FILTER_XML_CONTENT_NEW = $FILTER_XML.CreateNode("element","filter","")
    $FILTER_XML_CONTENT_NEW.SetAttribute("root",$_)
    $FILTER_XML_CONTENT_NEW_ADD = $FILTER_XML_CONTENT.AppendChild($FILTER_XML_CONTENT_NEW)
    Write-Output "Saving..."
    $FILTER_XML.OuterXml | IndentXML -Indent 4 | Out-File $FILTER_FILE -encoding "UTF8"
    Write-Output "Done..."

    Write-Output "Running VLT..."
    Invoke-Expression -Command "${VLT_CMD} ${VLT_FLAGS} --credentials ${AEM_USER}:${AEM_PASSWORD} export -v ${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}${AEM_WEBDAV_PATH} $_ ${CONTENT_DESTINATION}" | Tee-Object -FilePath "${LOG_PATH}\filevailt-export-$LOG_FILENAME.log"

    Write-Output "END Export $_"
}

Write-Output "------- END Exporting content ----------"

Write-Output "------- START Updating ${FILTER_FILE} ----------"

$FILTER_XML = [xml](Get-Content $FILTER_FILE)
Write-Output "Removing Existing Filters..."
$FILTER_XML_CONTENT = $FILTER_XML.SelectNodes("//workspaceFilter")
$FILTER_XML_DELETE = $FILTER_XML_CONTENT.SelectNodes('//filter')
$FILTER_XML_DELETE | ForEach-Object{
    $DELETE_STATUS = $FILTER_XML_CONTENT.RemoveChild($_)
}
Write-Output "Adding Exported Filters..."
$ROOT_PATHS | ForEach-Object {
    $FILTER_XML_CONTENT_NEW = $FILTER_XML.CreateNode("element","filter","")
    $FILTER_XML_CONTENT_NEW.SetAttribute("root",$_)
    $FILTER_XML_CONTENT_NEW_ADD = $FILTER_XML_CONTENT.AppendChild($FILTER_XML_CONTENT_NEW)
}
Write-Output "Saving..."
$FILTER_XML.OuterXml | IndentXML -Indent 4 | Out-File $FILTER_FILE -encoding "UTF8"
Write-Output "Done."
Write-Output "------- DONE Updating ${FILTER_FILE} ----------"

Write-Output "------- Revert Filter.xml ----------"
git checkout HEAD src/main/content/META-INF/
git clean -fx src/main/content/META-INF
Write-Output "------- Revert Filter.xml ----------"
