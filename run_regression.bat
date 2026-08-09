@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

echo ============================================
echo  Running Regression Tests
echo ============================================

call gradlew.bat clean test -DsuiteXmlFile=regression.xml -Dgroups=regression -Denv=qa -Dbrowser=chrome -Dplatform=local

echo ============================================
echo  Generating Allure Report
echo ============================================

allure generate --report-name "Orange HRM Regression" --output target/allure-report target/allure-results

echo ============================================
echo  Opening Allure Report
echo ============================================

allure open target/allure-report

pause
endlocal
