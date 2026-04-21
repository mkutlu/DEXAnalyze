@echo off
REM SonarQube Scanner Configuration for DEXAnalyze
REM Replace the token with your actual token from SonarQube UI

SET SONAR_HOST_URL=http://localhost:9000
SET SONAR_TOKEN=YOUR_SONARQUBE_TOKEN_HERE
SET SONAR_PROJECT_KEY=DEXAnalyze
SET SONAR_PROJECT_NAME=DEXAnalyze
SET SONAR_SOURCES=app/src/main
SET SONAR_TESTS=app/src/test,app/src/androidTest

echo Starting SonarQube Analysis...
echo Project: %SONAR_PROJECT_NAME%
echo URL: %SONAR_HOST_URL%

REM Option 1: Using Gradle (if plugin is compatible)
REM gradlew sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.host.url=%SONAR_HOST_URL% -Dsonar.token=%SONAR_TOKEN%

REM Option 2: Using sonar-scanner-cli (download from https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/)
REM sonar-scanner.bat ^
REM   -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
REM   -Dsonar.projectName=%SONAR_PROJECT_NAME% ^
REM   -Dsonar.host.url=%SONAR_HOST_URL% ^
REM   -Dsonar.token=%SONAR_TOKEN% ^
REM   -Dsonar.sources=%SONAR_SOURCES% ^
REM   -Dsonar.tests=%SONAR_TESTS% ^
REM   -Dsonar.sourceEncoding=UTF-8

echo.
echo Instructions:
echo 1. Download sonar-scanner from: https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/
echo 2. Extract and add to PATH, or use full path to sonar-scanner.bat
echo 3. Uncomment Option 2 above and run this script
echo.
