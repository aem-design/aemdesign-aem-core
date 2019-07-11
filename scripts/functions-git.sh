#!/bin/bash

TR=$(/usr/bin/which tr)
GIT=$(/usr/bin/which git)

function getCurrentProjectVersion() {
    local GIT_DESCRIBE=($(echo $(git describe) | tr "-" "\n"))
    echo "${GIT_DESCRIBE[0]}.${GIT_DESCRIBE[1]}"
}