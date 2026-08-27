#!/usr/bin/env bash

set -euo pipefail

TEST_TMP_DIR="$(mktemp -d /tmp/biblioteca-postgres-test.XXXXXX)"
TEST_DATA_DIR="${TEST_TMP_DIR}/data"
TEST_SOCKET_DIR="${TEST_TMP_DIR}/socket"

cleanup() {
    if [[ -s "${TEST_DATA_DIR}/postmaster.pid" ]]; then
        pg_ctl -D "${TEST_DATA_DIR}" -m fast -w stop >/dev/null
    fi

    if [[ -n "${TEST_TMP_DIR}" && -d "${TEST_TMP_DIR}" ]]; then
        rm -rf -- "${TEST_TMP_DIR}"
    fi
}

trap cleanup EXIT

mkdir -p "${TEST_SOCKET_DIR}"

initdb \
    --pgdata="${TEST_DATA_DIR}" \
    --username=postgres \
    --auth=trust \
    --no-locale \
    --encoding=UTF8 >/dev/null

pg_ctl \
    -D "${TEST_DATA_DIR}" \
    -o "-F -k ${TEST_SOCKET_DIR} -c listen_addresses=''" \
    -w start >/dev/null

createdb \
    --host="${TEST_SOCKET_DIR}" \
    --username=postgres \
    biblioteca_teste

psql \
    --host="${TEST_SOCKET_DIR}" \
    --username=postgres \
    --dbname=biblioteca_teste \
    --set=ON_ERROR_STOP=1 \
    --file=database/schema.sql

psql \
    --host="${TEST_SOCKET_DIR}" \
    --username=postgres \
    --dbname=biblioteca_teste \
    --set=ON_ERROR_STOP=1 \
    --file=database/dados-iniciais.sql

psql \
    --host="${TEST_SOCKET_DIR}" \
    --username=postgres \
    --dbname=biblioteca_teste \
    --set=ON_ERROR_STOP=1 \
    --file=database/teste-schema.sql

echo "Teste de integração do banco concluído com sucesso."
