@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

echo ============================================
echo  Running Regression Tests
echo ============================================

call gradlew.bat clean test -DsuiteXmlFile=src/test/resources/suites/regression.xml -Denv=qa -Dbrowser=chrome -Dplatform=local

echo ============================================
echo  Generating Allure Report
echo ============================================

set "REPORT_NAME=OrangeHRM regression / chrome"
npx --yes allure generate target/allure-results --config ./allurerc.mjs

echo ============================================
echo  Opening Allure Report
echo ============================================

npx --yes allure open target/allure-report/awesome

pause
endlocal
