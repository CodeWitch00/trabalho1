#!/usr/bin/env bash

set -euo pipefail

APP_PORT="${APP_PORT:-8080}"
APP_SHUTDOWN_PORT="${APP_SHUTDOWN_PORT:-8005}"
APP_PG_PORT="${APP_PG_PORT:-55433}"

APP_TMP_DIR="$(mktemp -d /tmp/biblioteca-app.XXXXXX)"
APP_PG_DATA_DIR="${APP_TMP_DIR}/postgres-data"
APP_PG_SOCKET_DIR="${APP_TMP_DIR}/postgres-socket"
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

    if [[ -s "${APP_PG_DATA_DIR}/postmaster.pid" ]]; then
        pg_ctl -D "${APP_PG_DATA_DIR}" -m fast -w stop >/dev/null 2>&1 || true
    fi

    if [[ -n "${APP_TMP_DIR}" && -d "${APP_TMP_DIR}" ]]; then
        rm -rf -- "${APP_TMP_DIR}"
    fi
}

trap cleanup EXIT
trap 'cleanup; exit 0' INT TERM

CATALINA_BIN="$(dirname "$(command -v catalina.sh)")"
CATALINA_HOME="$(cd "${CATALINA_BIN}/.." && pwd)"

mkdir -p "${APP_PG_SOCKET_DIR}"

initdb \
    --pgdata="${APP_PG_DATA_DIR}" \
    --username=postgres \
    --auth=trust \
    --no-locale \
    --encoding=UTF8 >/dev/null

pg_ctl \
    -D "${APP_PG_DATA_DIR}" \
    -o "-F -k ${APP_PG_SOCKET_DIR} -h 127.0.0.1 -p ${APP_PG_PORT}" \
    -w start >/dev/null

createdb \
    --host="${APP_PG_SOCKET_DIR}" \
    --port="${APP_PG_PORT}" \
    --username=postgres \
    biblioteca_dev

psql \
    --host="${APP_PG_SOCKET_DIR}" \
    --port="${APP_PG_PORT}" \
    --username=postgres \
    --dbname=biblioteca_dev \
    --set=ON_ERROR_STOP=1 \
    --file=database/schema.sql

psql \
    --host="${APP_PG_SOCKET_DIR}" \
    --port="${APP_PG_PORT}" \
    --username=postgres \
    --dbname=biblioteca_dev \
    --set=ON_ERROR_STOP=1 \
    --file=database/dados-iniciais.sql

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

export DB_URL="jdbc:postgresql://127.0.0.1:${APP_PG_PORT}/biblioteca_dev"
export DB_USER="postgres"
export DB_PASSWORD="teste"
export CATALINA_BASE="${APP_CATALINA_BASE}"
export CATALINA_HOME

echo "Aplicacao local: http://localhost:${APP_PORT}/biblioteca/livros"
echo "Banco temporario: PostgreSQL em 127.0.0.1:${APP_PG_PORT}/biblioteca_dev"
echo "Pressione Ctrl+C para encerrar Tomcat e PostgreSQL."

catalina.sh run &
APP_TOMCAT_PID="$!"
wait "${APP_TOMCAT_PID}" || true
