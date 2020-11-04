#!/bin/bash

function set_term_title() {
   echo -en "\033]0;$1\a"
}

function lowercase() {
    #echo "$1" | sed "y/ABCDEFGHIJKLMNOPQRSTUVWXYZ/abcdefghijklmnopqrstuvwxyz/"
    echo "$1" | awk '{print tolower($0)}'
}

function getOS() {
    OS=$(lowercase "$(uname -a | grep Microsoft -q && echo windowsnt || uname)")
    echo "OS=${OS}"
}

function fixPath() {
	local CHECK_PATH="${1?Need path}"
	if [[ "$OS" == "windows" ]]; then
		echo $(cygpath -wa $CHECK_PATH)
	elif [[ "$OS" == "windowsbash" ]]; then
#		local NEWPATH="$(wslpath -w $CHECK_PATH)"
		echo "${CHECK_PATH//\/mnt/}"
	else
		echo "$CHECK_PATH"
	fi
}

OS=osx
WINDOWS_UBUNTU_CONFIG_FILE="/etc/wsl.conf"

if [ "$(uname)" == "Darwin" ]; then
	OS=osx
elif [[ "$(expr substr $(uname -s) 1 5)" == "Linux" && "$(uname -a | grep -q Microsoft && echo "true")" != "true" ]]; then
    OS=linux
elif [[ "$(expr substr $(uname -s) 1 5)" == "Linux" && "$(uname -a | grep -q Microsoft && echo "true")" == "true" ]]; then
    OS=windowsbash
    if [[ ! -f "$WINDOWS_UBUNTU_CONFIG_FILE" ]]; then
        echo "Please create $WINDOWS_UBUNTU_CONFIG_FILE using following guide"
        echo "https://nickjanetakis.com/blog/setting-up-docker-for-windows-and-wsl-to-work-flawlessly#ensure-volume-mounts-work"
        exit 1
    fi
elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]; then
    OS=windows
elif [ "$(expr substr $(uname -s) 1 10)" == "MINGW64_NT" ]; then
    OS=windows
fi

#check current routable network interface
ROUTE=$(/usr/bin/which route 2>/dev/null)
if [[ "$ROUTE" != "" ]]; then
    NETWORK_INTERFACE=$($ROUTE -n get 0.0.0.0 2>/dev/null | awk '/interface: / {print $2}')
fi

#get ip of routable network interface
IFCONFIG="$(/usr/bin/which ifconfig 2>/dev/null)"
if [[ "$IFCONFIG" == "" && "$OS" == "windows" ]]; then
	LOCAL_IP="$(ipconfig | grep "(Default Switch)" -A 6 | grep "IPv4 Address" | head -n1 | awk -F ": " '/1/ {print $2}')"
else
	if [[ "$IFCONFIG" == "" ]]; then
		LOCAL_IP="$(hostname -i 2>/dev/null)"
	else
		LOCAL_IP="$($IFCONFIG $NETWORK_INTERFACE | grep 'inet ' | grep -v '127.0.0.1' | awk '{print $2}' | head -1)"
	fi
fi
