#!/bin/bash

DEFAULT_POM_FILE="pom.xml"
POM_FILE="${POM_FILE:-./$DEFAULT_POM_FILE}"


function getDefaultFromPom() {
    local PARAM_NAME=${1:-}
    local POM_FILE=${2:-$DEFAULT_POM_FILE}
    echo $(cat $POM_FILE | grep "<${PARAM_NAME}>" | head -n 1 | sed -e 's/.*>\(.*\)<.*/\1/')

}

function getParamOrDefault() {
    local PARAMS="${1:-}"
    local PARAM_NAME="${2:-}"
    local POM_FILE="${3:-$DEFAULT_POM_FILE}"
    local DEFAULT_VALUE=$(getDefaultFromPom "${PARAM_NAME}" "${POM_FILE}")

    if [[ "" == "$DEFAULT_VALUE" ]]; then
        echo "DEFAULT MISSING IN POM"
    else
        PARAMS_CHECK=$(echo ${PARAMS} | grep "${PARAM_NAME}=" | sed "s/.*-D$PARAM_NAME=\([^,[:space:]]*\).*/\1/")
        if [[ "" == "$PARAMS_CHECK" ]]; then
            echo $DEFAULT_VALUE
        else
            echo $(echo ${PARAMS_CHECK} | sed -e "s/.*${PARAM_NAME}=\(.*\).*/\1/")
        fi
    fi
}

function evalMaven() {
    local PARAM=${1:-}
    echo $(mvn help:evaluate -q -DforceStdout -D"expression=$PARAM")

}



AEM_USER=$(getParamOrDefault "$SCRIPT_PARAMS" "aem.password" "$POM_FILE")
AEM_PASS=$(getParamOrDefault "$SCRIPT_PARAMS" "aem.username" "$POM_FILE")
AEM_HOST=$(getParamOrDefault "$SCRIPT_PARAMS" "aem.host" "$POM_FILE")
AEM_PORT=$(getParamOrDefault "$SCRIPT_PARAMS" "aem.port" "$POM_FILE")
AEM_SCHEMA=$(getParamOrDefault "$SCRIPT_PARAMS" "package.uploadProtocol" "$POM_FILE")

AEM_APP_FOLDER=$(getParamOrDefault "$SCRIPT_PARAMS" "package.appFolder" "$POM_FILE")

#if [[ "$SCRIPT_PARAMS" == *"localhost"* ]]; then
#    AEM_HOST = "localhost"
#fi

echo "Params:     $SCRIPT_PARAMS"
echo "AEM_USER:   $AEM_USER"
echo "AEM_PASS:   $(sed "s/\w/*/g" <<< ${AEM_PASS})"
echo "AEM_HOST:   $AEM_HOST"
echo "AEM_PORT:   $AEM_PORT"
echo "AEM_SCHEMA: $AEM_SCHEMA"
echo "POM_FILE:   $POM_FILE"
echo "APP_FOLDER:   $AEM_APP_FOLDER"
