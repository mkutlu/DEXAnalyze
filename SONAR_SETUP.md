# SonarQube Setup Instructions

## Quick Start

### Step 1: Download sonar-scanner
Download the Windows version from:
https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/

Extract it to a location, e.g., `C:\sonar-scanner`

### Step 2: Add to PATH (Optional)
Add the sonar-scanner bin directory to your system PATH, OR use the full path in commands.

### Step 3: Run Analysis

Replace `YOUR_TOKEN` with your actual SonarQube token from the UI:

```bash
sonar-scanner.bat ^
  -Dsonar.projectKey=DEXAnalyze ^
  -Dsonar.projectName=DEXAnalyze ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.token=YOUR_TOKEN ^
  -Dsonar.sources=app/src/main ^
  -Dsonar.tests=app/src/test,app/src/androidTest ^
  -Dsonar.sourceEncoding=UTF-8
```

Or if sonar-scanner is in PATH, just run:
```bash
sonar-scanner -Dsonar.projectKey=DEXAnalyze -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN
```

### Step 4: View Results
Go to: http://localhost:9000/projects/DEXAnalyze

---

## Getting Your SonarQube Token

1. Open http://localhost:9000
2. Login with admin credentials
3. Go to: Account > Security > Generate Tokens
4. Create a token and use it in the commands above

---

## What Gets Analyzed

✅ **Sources:** `app/src/main/` (Java, Kotlin, XML)  
✅ **Tests:** `app/src/test/` and `app/src/androidTest/`  
✅ **Code Quality:** Lint violations, code smells, bugs  
✅ **Coverage:** Test coverage (if enabled)  

---

## Current Project Status

- **Lint Warnings:** 13 (down from 50 - 74% reduction!)
- **Code Quality Commits:** 8 quality improvement commits
- **Dependencies:** All updated to latest versions
- **Gradle:** Modernized for AGP 9.0+

---

## Troubleshooting

**If sonar-scanner command not found:**
- Use full path: `C:\sonar-scanner\bin\sonar-scanner.bat`
- Or add to PATH and restart terminal

**If connection fails:**
- Ensure SonarQube is running: `http://localhost:9000`
- Check firewall settings
- Verify token is correct

**Invalid token:**
- Generate a new token from SonarQube UI
- Don't commit tokens to git (use .gitignore or environment variables)

