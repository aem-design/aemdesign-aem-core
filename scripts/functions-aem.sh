#!/bin/bash

CURL=$(/usr/bin/which curl)
CAT=$(/usr/bin/which cat)
GREP=$(/usr/bin/which grep)
HEAD=$(/usr/bin/which head)
TAIL=$(/usr/bin/which tail)
SED=$(/usr/bin/which sed)

SERVER_ADDRESS="${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}"
LOGIN_DEFAULT="${AEM_USER}:${AEM_PASS}"

SERVICE_CHECK="/system/console/bundles"
INSTALL_CHECK="/system/console/bundles.json"
PACKAGE_MANAGER_CHECK="/crx/packmgr/service.jsp"


[ "ide" == "$CHECK_IDE_INTELLIJ" ] && source "./functions-debug.sh"
[ "ide" == "$CHECK_IDE_INTELLIJ" ] && source "./functions-curl.sh"


function doWorkflowsTurnOff() {
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_create" "-F enabled=false"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_create_without_DM" "-F enabled=false"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod" "-F enabled=false"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod_without_DM" "-F enabled=false"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod_without_DM_reupload" "-F enabled=false"
}

function doWorkflowsTurnOn() {
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_create" "-F enabled=true"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_create_without_DM" "-F enabled=true"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod" "-F enabled=true"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod_without_DM" "-F enabled=true"
    doPostFields "/libs/settings/workflow/launcher/config/update_asset_mod_without_DM_reupload" "-F enabled=true"
}

function doCacheClear() {
    doPost "${AEM_USER}:${AEM_PASS}" "${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}" "/system/console/slingjsp" " "

}


#(address,login)
function doWaitForStartup {

    local ADDRESS=${1:-$SERVER_ADDRESS}
    local LOGIN=${2:-$LOGIN_DEFAULT}
    local CURL_HEADER
    CURL_HEADER=$(compileCurlHeader "$LOGIN" "$ADDRESS")

    printSectionLine "Waiting for Instance startup on: $ADDRESS"
    printSectionLine "$CURL -L -u $LOGIN --header Referer:${ADDRESS} --silent --connect-timeout 5 --max-time 5 ${ADDRESS}${SERVICE_CHECK} | $GREP -q \"Configuration\" && echo true || echo false"

    printSectionInLine "Wait: "
    printSectionInLine "." ""


    while [[ "false" == "$($CURL -L -u "$LOGIN" --header "Referer:${ADDRESS}" --silent -N --connect-timeout 5 --max-time 5 "${ADDRESS}${SERVICE_CHECK}" | $GREP -q "Configuration" && echo true || echo false)" ]]; do
      printSectionInLine "." ""
      $SLEEP 1
    done
    printSectionInLineReset

    printSectionLine "Service is up.."
}


#doWaitForPackageManagerStartup(address,login)
function doWaitForPackageManagerStartup {
    local ADDRESS=${1:-$SERVER_ADDRESS}
    local LOGIN=${2:-$LOGIN_DEFAULT}
    local CURL_HEADER
    CURL_HEADER=$(compileCurlHeader "$LOGIN" "$ADDRESS")

    printSectionLine "Waiting for Package Manager to startup on: $ADDRESS"
    printSectionLine "$CURL -u $LOGIN --header Referer:${ADDRESS} --silent --connect-timeout 5 --max-time 5 ${ADDRESS}${PACKAGE_MANAGER_CHECK} | $GREP -q \"ok\" && echo true || echo false"

    printSectionInLine "Wait: "
    printSectionInLine "." ""
    while [[ "false" == "$($CURL -L -u "$LOGIN" --header "Referer:${ADDRESS}" --silent -N --connect-timeout 5 --max-time 5 "${ADDRESS}${PACKAGE_MANAGER_CHECK}" | $GREP -q "ok" && echo true || echo false)" ]]; do
      printSectionInLine "." ""
      $SLEEP 1
    done
    printSectionInLineReset

    printSectionLine "Package Manager Service is up.."
}



#doWaitForBundlesToInstall(address,login)
function doWaitForBundlesToInstall {
    local ADDRESS=${1:-$SERVER_ADDRESS}
    local LOGIN=${2:-$LOGIN_DEFAULT}

    printSectionLine "Waiting for bundles to be installed"
    printSectionLine "$CURL -u $LOGIN --header Referer:${ADDRESS} --silent --connect-timeout 5 --max-time 5 ${ADDRESS}${INSTALL_CHECK} | $GREP -q \"state\":\"Installed\" && echo true || echo false"

    printSectionInLine "Wait: "
    printSectionInLine "." ""
    while [[ "true" == "$($CURL -L -u "$LOGIN" --header "Referer:${ADDRESS}" --silent -N --connect-timeout 5 --max-time 5 "${ADDRESS}${INSTALL_CHECK}" | $GREP -q "\"state\":\"Installed\"" && echo true || echo false)" ]]; do
        printSectionInLine "." ""
        $SLEEP 1
    done
    printSectionInLineReset

    printSectionLine "Bundles are installed"

}


function doGetCheck() {
    local LOGIN=${1?Need login}
    local ADDRESS=${2?Need address}
    local PATH=${3?Need path}

    local RESULT=$($CURL -L -u "$LOGIN" --header Referer:${ADDRESS} -H User-Agent:curl -X GET --connect-timeout 1 --max-time 1 -w "%{http_code}" -o /dev/null --silent -N "${ADDRESS}${PATH}" | $GREP -q "200" && echo true || echo false)
    echo ${RESULT}
}

function doCheckPathExist() {
    local PATH=${1?Need path}

    doGetCheck "${AEM_USER}:${AEM_PASS}" "${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}" "${PATH}"

}

function restart_aem_docker() {
    echo "Finding AEM Docker Container on port: ${AEM_PORT}"
    AEM_CONTAINER_NAME=$(getContainerNameByPort "${AEM_PORT}")
    echo "Restarting AEM Docker Container: ${AEM_CONTAINER_NAME}"
    doRestartContainerUsingPort "${AEM_PORT}"
    #doPostFields "/system/console/vmstat" "-F shutdown_type=Restart"

    set_term_title "Wait for AEM Ready"
    echo "Waiting for AEM to be ready"
    doWaitForStartup
    doWaitForPackageManagerStartup
    doWaitForBundlesToInstall

    echo "AEM is ready!"

}


function delete_current_jar() {

    set_term_title "Get Project Version"

    PROJECT_VERSION=$(getParamOrDefault "" "version")
    BUNDLE_NAME=$(getParamOrDefault "" "bundleName")
    PROJECT_ARTIFACTID=$($CAT "$POM_FILE" | $GREP artifactId -A1 | $HEAD -n 2 | $TAIL -n 1 | $SED -e 's/.*>\(.*\)<.*/\1/')
    GIT_VERSION=$(getCurrentProjectVersion)

    echo "Current Project Version: $PROJECT_VERSION"
    echo "Current Git Version: $GIT_VERSION"
    echo "Project Artifact Id Version: $PROJECT_ARTIFACTID"

    PROJECT_JAR="/apps/aemdesign/install/${BUNDLE_NAME}-${PROJECT_VERSION}.jar"
    echo "Project Jar: $PROJECT_JAR"

    JAR_EXIST=$(doCheckPathExist "$PROJECT_JAR")

    echo "Check if Jar exists: $JAR_EXIST"

    echo "Delete Current Jar: ${PROJECT_JAR}"
    doDeletePath "${PROJECT_JAR}"
    set_term_title "Services Deploy"

}
