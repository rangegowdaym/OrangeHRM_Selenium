#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "============================================"
echo " Running Regression Tests"
echo "============================================"

set +e
./gradlew clean test -DsuiteXmlFile=regression.xml -Dgroups=regression -Denv=qa -Dbrowser=chrome -Dplatform=local
TEST_EXIT_CODE=$?
set -e

echo "============================================"
echo " Generating Allure Report"
echo "============================================"

allure generate --report-name "Orange HRM Regression" --output target/allure-report target/allure-results

echo "============================================"
echo " Opening Allure Report"
echo "============================================"

allure open target/allure-report

exit $TEST_EXIT_CODE
