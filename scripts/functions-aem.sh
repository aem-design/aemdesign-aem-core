#!/bin/bash

CURL=$(/usr/bin/which curl)
GREP=$(/usr/bin/which grep)

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