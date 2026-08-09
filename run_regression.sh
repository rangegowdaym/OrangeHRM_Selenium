#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "============================================"
echo " Running Regression Tests"
echo "============================================"

set +e
./gradlew clean test -DsuiteXmlFile=src/test/resources/suites/regression.xml -Denv=qa -Dbrowser=chrome -Dplatform=local
TEST_EXIT_CODE=$?
set -e

echo "============================================"
echo " Generating Allure Report"
echo "============================================"

export REPORT_NAME="OrangeHRM regression / chrome"
npx --yes allure generate target/allure-results --report-name "$REPORT_NAME" --output target/allure-report

echo "============================================"
echo " Opening Allure Report"
echo "============================================"

npx --yes allure open target/allure-report

exit $TEST_EXIT_CODE
