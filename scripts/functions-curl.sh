#!/bin/bash

CURL=$(/usr/bin/which curl)
GREP=$(/usr/bin/which grep)

function doPost() {
    local LOGIN=${1?Need login}
    local ADDRESS=${2?Need address}
    local SERVICE=${3?Need service}
    local FIELDS=${4?Need fields}

#    echo $CURL -L -u "$LOGIN" --header Referer:${ADDRESS} -H User-Agent:curl -X POST --connect-timeout 1 --max-time 1 --silent -N "${FIELDS}" "${ADDRESS}${SERVICE}"

    local RESULT=$($CURL -L -u "$LOGIN" --header Referer:${ADDRESS} -H User-Agent:curl -X POST --connect-timeout 1 --max-time 1 --silent -N "${FIELDS}" "${ADDRESS}${SERVICE}" | $GREP -q "OK" && echo true || echo false)
    echo " -- URL:    ${ADDRESS}${SERVICE}"
    echo "    POST:   ${FIELDS}"
    echo "    RESULT: ${RESULT}"
}


function doPostFields() {
    local SERVICE=${1?Need service}
    local FIELDS=${2?Need fields}

    doPost "${AEM_USER}:${AEM_PASS}" "${AEM_SCHEMA}://${AEM_HOST}:${AEM_PORT}" "${SERVICE}" "${FIELDS}"

}

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