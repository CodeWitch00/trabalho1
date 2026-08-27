#!/usr/bin/env bash

set -euo pipefail

APP_PORT="${APP_PORT:-8080}"
APP_SHUTDOWN_PORT="${APP_SHUTDOWN_PORT:-8005}"

exigir_variavel() {
    local nome="$1"
    local valor="${!nome:-}"
    if [[ -z "${valor}" ]]; then
        echo "Variavel obrigatoria ausente: ${nome}" >&2
        echo "Configure o arquivo .env na raiz do repositorio." >&2
        exit 1
    fi
}

exigir_variavel DB_URL
exigir_variavel DB_USER
exigir_variavel DB_PASSWORD

APP_TMP_DIR="$(mktemp -d /tmp/biblioteca-supabase.XXXXXX)"
APP_CATALINA_BASE="${APP_TMP_DIR}/tomcat"
APP_TOMCAT_PID=""

cleanup() {
    if [[ -n "${APP_TOMCAT_PID}" ]]; then
        kill "${APP_TOMCAT_PID}" >/dev/null 2>&1 || true
        wait "${APP_TOMCAT_PID}" >/dev/null 2>&1 || true
    fi

    if [[ -s "${APP_CATALINA_BASE}/conf/server.xml" ]]; then
        CATALINA_BASE="${APP_CATALINA_BASE}" catalina.sh stop >/dev/null 2>&1 || true
    fi

    if [[ -n "${APP_TMP_DIR}" && -d "${APP_TMP_DIR}" ]]; then
        rm -rf -- "${APP_TMP_DIR}"
    fi
}

trap cleanup EXIT
trap 'cleanup; exit 0' INT TERM

CATALINA_BIN="$(dirname "$(command -v catalina.sh)")"
CATALINA_HOME="$(cd "${CATALINA_BIN}/.." && pwd)"

mvn package

mkdir -p "${APP_CATALINA_BASE}"
cp -R "${CATALINA_HOME}/conf" "${APP_CATALINA_BASE}/conf"
mkdir -p "${APP_CATALINA_BASE}/logs" \
    "${APP_CATALINA_BASE}/temp" \
    "${APP_CATALINA_BASE}/webapps" \
    "${APP_CATALINA_BASE}/work"

sed -i \
    -e "s/port=\"8005\"/port=\"${APP_SHUTDOWN_PORT}\"/" \
    -e "s/port=\"8080\"/port=\"${APP_PORT}\"/" \
    "${APP_CATALINA_BASE}/conf/server.xml"

cp target/biblioteca.war "${APP_CATALINA_BASE}/webapps/biblioteca.war"

export CATALINA_BASE="${APP_CATALINA_BASE}"
export CATALINA_HOME

echo "Aplicacao com Supabase: http://localhost:${APP_PORT}/biblioteca/livros"
echo "Banco: Supabase PostgreSQL via DB_URL do .env"
echo "Pressione Ctrl+C para encerrar o Tomcat."

catalina.sh run &
APP_TOMCAT_PID="$!"
wait "${APP_TOMCAT_PID}" || true
