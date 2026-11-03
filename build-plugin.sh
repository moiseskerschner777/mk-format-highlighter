#!/usr/bin/env bash
set -euo pipefail

readonly MINIMUM_JAVA_VERSION=17
readonly PROJECT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

java_major_version() {
    local java_executable="$1/bin/java"
    [[ -x "$java_executable" ]] || return 1
    "$java_executable" -version 2>&1 \
        | sed -nE '1s/.*version "([0-9]+)(\.([0-9]+))?.*/\1 \3/p' \
        | awk '{ print ($1 == 1) ? $2 : $1 }'
}

is_valid_java_home() {
    local candidate="$1"
    [[ -n "$candidate" ]] || return 1
    local major_version
    major_version="$(java_major_version "$candidate")" || return 1
    [[ -n "$major_version" ]] && (( major_version >= MINIMUM_JAVA_VERSION ))
}

ask_for_java_home() {
    local candidate
    while true; do
        read -r -p "Enter the path to a Java ${MINIMUM_JAVA_VERSION}+ home (the folder that contains bin/java): " candidate
        candidate="${candidate/#\~/$HOME}"
        candidate="${candidate%/}"
        candidate="${candidate%/bin/java}"
        if is_valid_java_home "$candidate"; then
            echo "$candidate"
            return
        fi
        echo "Not a valid Java ${MINIMUM_JAVA_VERSION}+ home: $candidate" >&2
    done
}

resolve_java_home() {
    if is_valid_java_home "${JAVA_HOME:-}"; then
        echo "$JAVA_HOME"
        return
    fi
    if [[ -n "${JAVA_HOME:-}" ]]; then
        echo "JAVA_HOME is not a valid Java ${MINIMUM_JAVA_VERSION}+ home: $JAVA_HOME" >&2
    else
        echo "JAVA_HOME is not set." >&2
    fi
    ask_for_java_home
}

JAVA_HOME="$(resolve_java_home)"
export JAVA_HOME
echo "Using Java $(java_major_version "$JAVA_HOME") at $JAVA_HOME"

cd "$PROJECT_DIRECTORY"
./gradlew buildPlugin "$@"
echo "Plugin built: $(ls "$PROJECT_DIRECTORY"/build/distributions/*.zip)"
