#!/bin/bash

SCRIPTS_PARENT=$(realpath ../../scripts)

if [[ ! -f "$SCRIPTS_PARENT/functions-debug.sh" ]]; then
    echo "Please ensure this project is located in following structure:"
    echo "  -> aemdesign-parent"
    echo "   |- aemdesign-aem-core"
    echo "   | |- $(basename $(realpath .))"
    exit 1
fi

source "$SCRIPTS_PARENT/functions-debug.sh"
source "$SCRIPTS_PARENT/functions-maven.sh"
source "$SCRIPTS_PARENT/functions-curl.sh"
