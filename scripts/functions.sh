#!/bin/bash

CURRENT_PATH="$(pwd)"
PARENT_PROJECT_PATH="${PARENT_PROJECT_PATH:-$CURRENT_PATH}"
SCRIPTS_PARENT="${SCRIPTS_PARENT:-$PARENT_PROJECT_PATH}/scripts"

source "$SCRIPTS_PARENT/functions-common.sh"
source "$SCRIPTS_PARENT/functions-debug.sh"
source "$SCRIPTS_PARENT/functions-maven.sh"
source "$SCRIPTS_PARENT/functions-curl.sh"
