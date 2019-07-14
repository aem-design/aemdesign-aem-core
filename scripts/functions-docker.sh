#!/bin/bash

DOCKER=$(/usr/bin/which docker)
AWK=$(/usr/bin/which awk)

function getContainerNameByPort() {
    local CONTAINER_PORT=${1?Need container port}
    echo $(docker ps --format='table {{.ID}}\t{{.Names}}\t{{.Ports}}' | grep "$CONTAINER_PORT->" | awk '{print $2}')
}
function doRestartContainerUsingPort() {
    local CONTAINER_PORT=${1?Need container port}
    local CONTAINER_ID=$(getContainerNameByPort ${CONTAINER_PORT})
    $DOCKER restart $CONTAINER_ID
}
