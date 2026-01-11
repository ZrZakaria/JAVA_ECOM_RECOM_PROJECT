# Dependency Setup Guide

Since this project no longer uses Maven, you must manually download the required libraries (JAR files) and place them in the `lib/` directory.

## Required Libraries

### 1. Jsoup
- **Download**: [jsoup-1.17.2.jar](https://repo1.maven.org/maven2/org/jsoup/jsoup/1.17.2/jsoup-1.17.2.jar)
- **Place in**: `scrap_no_maven/lib/`

### 2. Selenium
Selenium is a bit more complex manually because it has many dependencies. The easiest way is to download the "Selenium Server" which is a single JAR with everything included:
- **Download**: [selenium-server-4.16.1.jar](https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.16.1/selenium-server-4.16.1.jar)
- **Place in**: `scrap_no_maven/lib/`

---

## How to use
1.  Place the JARs above into `scrap_no_maven/lib/`.
2.  Double-click `compile.bat` to compile.
3.  Double-click `run.bat` to run the scraper.

> [!NOTE]
> Make sure `chromedriver.exe` is in your PATH or in the same directory as the project.
