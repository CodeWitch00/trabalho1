#!/usr/bin/env bash

set -euo pipefail

REQUIRED_COMMANDS=(
    java
    mvn
    catalina.sh
    initdb
    pg_ctl
    createdb
    psql
)

missing_commands=()

for command_name in "${REQUIRED_COMMANDS[@]}"; do
    if ! command -v "${command_name}" >/dev/null 2>&1; then
        missing_commands+=("${command_name}")
    fi
done

if (( ${#missing_commands[@]} > 0 )); then
    echo "Nao foi possivel iniciar a aplicacao."
    echo
    echo "Comandos ausentes no PATH:"
    printf ' - %s\n' "${missing_commands[@]}"
    echo
    echo "Instale Java 17, Maven, Tomcat 10.1 e PostgreSQL 17 antes de executar."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"
exec "${SCRIPT_DIR}/rodar-local.sh"
