#!/usr/bin/env bash
set -euo pipefail

readonly MINIMUM_JAVA_VERSION=17
readonly PROJECT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly JDK_SEARCH_DIRECTORIES=(
    "$HOME/.jdks"
    "$HOME/.sdkman/candidates/java"
    "/usr/lib/jvm"
    "/opt"
)

java_major_version() {
    local java_executable="$1/bin/java"
    [[ -x "$java_executable" ]] || return 1
    "$java_executable" -version 2>&1 \
        | sed -nE '1s/.*version "([0-9]+)(\.([0-9]+))?.*/\1 \3/p' \
        | awk '{ print ($1 == 1) ? $2 : $1 }'
}

java_home_problem() {
    local candidate="$1"
    if [[ -z "$candidate" ]]; then
        echo "no path was entered"
        return
    fi
    if [[ ! -d "$candidate" ]]; then
        echo "folder does not exist: $candidate"
        return
    fi
    if [[ ! -x "$candidate/bin/java" ]]; then
        echo "no bin/java inside: $candidate"
        return
    fi
    local major_version
    major_version="$(java_major_version "$candidate")"
    if [[ -z "$major_version" ]]; then
        echo "could not read the Java version of: $candidate"
        return
    fi
    if (( major_version < MINIMUM_JAVA_VERSION )); then
        echo "Java $major_version is too old (need ${MINIMUM_JAVA_VERSION}+): $candidate"
    fi
}

is_valid_java_home() {
    [[ -z "$(java_home_problem "$1")" ]]
}

normalize_java_home_path() {
    local candidate="$1"
    candidate="${candidate/#\~/$HOME}"
    candidate="${candidate%/}"
    candidate="${candidate%/bin/java}"
    candidate="${candidate%/bin}"
    echo "$candidate"
}

java_home_of_path_java() {
    local path_java
    path_java="$(command -v java 2>/dev/null)" || return 1
    dirname "$(dirname "$(readlink -f "$path_java")")"
}

list_installed_java_homes() {
    local search_directory candidate
    for search_directory in "${JDK_SEARCH_DIRECTORIES[@]}"; do
        [[ -d "$search_directory" ]] || continue
        for candidate in "$search_directory"/*/; do
            candidate="${candidate%/}"
            [[ -L "$candidate" ]] && continue
            is_valid_java_home "$candidate" && echo "$candidate"
        done
    done
}

print_installed_java_homes() {
    local installed_java_homes
    installed_java_homes="$(list_installed_java_homes)"
    if [[ -z "$installed_java_homes" ]]; then
        echo "No Java ${MINIMUM_JAVA_VERSION}+ installation was found on this machine." >&2
        return
    fi
    echo "Java ${MINIMUM_JAVA_VERSION}+ installations found on this machine:" >&2
    while read -r java_home; do
        echo "  $java_home  (Java $(java_major_version "$java_home"))" >&2
    done <<< "$installed_java_homes"
}

ask_for_java_home() {
    print_installed_java_homes
    local candidate problem
    while true; do
        read -e -r -p "Enter the path to a Java ${MINIMUM_JAVA_VERSION}+ home (Tab completes): " candidate
        candidate="$(normalize_java_home_path "$candidate")"
        problem="$(java_home_problem "$candidate")"
        if [[ -z "$problem" ]]; then
            echo "$candidate"
            return
        fi
        echo "Invalid: $problem" >&2
    done
}

resolve_java_home() {
    if [[ -n "${JAVA_HOME:-}" ]]; then
        if is_valid_java_home "$JAVA_HOME"; then
            echo "$JAVA_HOME"
            return
        fi
        echo "JAVA_HOME is unusable, $(java_home_problem "$JAVA_HOME")" >&2
    fi
    local path_java_home
    path_java_home="$(java_home_of_path_java)" || path_java_home=""
    if [[ -n "$path_java_home" ]] && is_valid_java_home "$path_java_home"; then
        echo "Using the java found on PATH instead." >&2
        echo "$path_java_home"
        return
    fi
    ask_for_java_home
}

JAVA_HOME="$(resolve_java_home)"
export JAVA_HOME
echo "Using Java $(java_major_version "$JAVA_HOME") at $JAVA_HOME"

cd "$PROJECT_DIRECTORY"
./gradlew buildPlugin "$@"
echo "Plugin built: $(ls "$PROJECT_DIRECTORY"/build/distributions/*.zip)"
