#!/bin/bash

CURRENT_PATH="$(pwd)"
PARENT_PROJECT_PATH="${PARENT_PROJECT_PATH:-$CURRENT_PATH}"
SCRIPTS_PARENT="${SCRIPTS_PARENT:-$PARENT_PROJECT_PATH}/scripts"

JAVA=$(/usr/bin/which java)
GREP=$(/usr/bin/which grep)
READLINK=$(/usr/bin/which readlink)
HEAD=$(/usr/bin/which head)
LS=$(/usr/bin/which ls)
FOLD=$(/usr/bin/which fold)
FIND=$(/usr/bin/which find)
CURL=$(/usr/bin/which curl)
SLEEP=$(/usr/bin/which sleep)
SH=$(/usr/bin/which sh)
SED=$(/usr/bin/which sed)

source "$SCRIPTS_PARENT/functions-common.sh"
source "$SCRIPTS_PARENT/functions-debug.sh"
source "$SCRIPTS_PARENT/functions-maven.sh"
source "$SCRIPTS_PARENT/functions-curl.sh"
source "$SCRIPTS_PARENT/functions-git.sh"
source "$SCRIPTS_PARENT/functions-docker.sh"
source "$SCRIPTS_PARENT/functions-aem.sh"



