#!/bin/bash

#DEBUG

function debugOn {
#    export FLAG_DEBUG=true
    FLAG_DEBUG="true"
}

function debugOff {
    #export FLAG_DEBUG=false
    FLAG_DEBUG="false"
}

function debugTest {

    debugOn
    FLAG_DEBUG_LABEL=true

    debug "1. DEBUG TEXT #w:DEBUG TEXT" "error"
    debug "2. DEBUG TEXT #e:DEBUG TEXT" "info"
    debug "3. DEBUG TEXT #r:DEBUG TEXT" "warn"
    debug "4. DEBUG TEXT #i:DEBUG TEXT"
    debugOff

}


#debug(message,type[error,info,warning],newlinesiffix)
function debug {

    local DEFAULT_COLOR_WARN="\033[0;31;93m" #light yellow
    local DEFAULT_COLOR_ERROR="\033[0;31;91m" #light red
    local DEFAULT_COLOR_INFO="\033[0;31;94m" #light blue
    local DEFAULT_COLOR_DEFAULT="\033[0;31;92m" #light green
    local DEFAULT_COLOR_RESET="\033[0m" #light green

    COLOR_WARN="${COLOR_WARN:-$DEFAULT_COLOR_WARN}" #light yellow
    COLOR_ERROR="${COLOR_ERROR:-$DEFAULT_COLOR_ERROR}" #light red
    COLOR_INFO="${COLOR_INFO:-$DEFAULT_COLOR_INFO}" #light blue
    COLOR_DEFAULT="${COLOR_DEFAULT:-$DEFAULT_COLOR_DEFAULT}" #light green
    COLOR_RESET="${COLOR_RESET:-$DEFAULT_COLOR_RESET}" #light green

    LABEL_WARN="${LABEL_WARN:-*WARN*}"
    LABEL_ERROR="${LABEL_ERROR:-*ERROR*}"
    LABEL_INFO="${LABEL_INFO:-*INFO*}"
    LABEL_DEFAULT="${LABEL_DEFAULT:-}"


    COLOR_START="$COLOR_DEFAULT"
    COLOR_END="$COLOR_RESET"

    local TEXT="${1:-}"
    local TYPE="${2:-}"
    local LABEL_TEXT=""
    local TEXT_SUFFUX=""
    local NEWLINESUFFUX=$3

    if [ ! "$NEWLINESUFFUX" == "" ]; then
        TEXT_SUFFUX="$NEWLINESUFFUX"
    fi

    case $TYPE in
        ("error") COLOR_START="$COLOR_ERROR" LABEL_TEXT="$LABEL_ERROR " COLOR_END="$COLOR_END";;
        ("info") COLOR_START="$COLOR_INFO" LABEL_TEXT="$LABEL_INFO ";;
        ("warn") COLOR_START="$COLOR_WARN" LABEL_TEXT="$LABEL_WARN ";;
    esac


    if [ "$FLAG_DEBUG" == "true" ]; then

        local LABEL=""
        if [ "$FLAG_DEBUG_LABEL" == "true" ]; then
            LABEL=${LABEL_TEXT:-}
        fi

        TEXT="${TEXT//#d:/$COLOR_DEFAULT}"
        TEXT="${TEXT//#w:/$COLOR_WARN}"
        TEXT="${TEXT//#e:/$COLOR_ERROR}"
        TEXT="${TEXT//#i:/$COLOR_INFO}"
        TEXT="${TEXT//#r:/$COLOR_INFO}"

        echo -e "$COLOR_START$LABEL$TEXT$TEXT_SUFFUX$COLOR_END"
#        printf "$COLOR_START%s%s$COLOR_END$TEXT_SUFFUX" "$LABEL" "$TEXT"
    fi
}

#debugIf(conditionResult,success,fail)
function debugIf
{
    case $1 in
        (true) echo -e " - " "$2";;
        (false) echo -e " - " "$3";;
    esac
}

function debugStatus {
    echo -n "."
}

function set_term_title() {
   echo -en "\033]0;$1\a"
}
