#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "============================================"
echo " Cleaning and Building the Project"
echo "============================================"

./gradlew clean

echo "============================================"
echo " Running Smoke Tests"
echo "============================================"

set +e
./gradlew test -DsuiteXmlFile=src/test/resources/suites/smoke.xml -Denv=dev -Dbrowser=chrome -Dplatform=local
TEST_EXIT_CODE=$?
set -e

echo "============================================"
echo " Generating Allure Report"
echo "============================================"

npx --yes allure generate target/allure-results --output target/allure-report

echo "============================================"
echo " Opening Allure Report"
echo "============================================"

npx --yes allure open target/allure-report

exit $TEST_EXIT_CODE
