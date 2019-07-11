#!/bin/bash

DOCKER=$(/usr/bin/which docker)
AWK=$(/usr/bin/which awk)

function doRestartContainerUsingPort() {
    local CONTAINER_PORT=${1?Need container port}
    local CONTAINER_ID=$(docker ps | grep "$CONTAINER_PORT->" | awk '{print $1}')
    $DOCKER restart $CONTAINER_ID
}