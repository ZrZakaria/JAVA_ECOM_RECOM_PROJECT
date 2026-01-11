# Automated Testing Module (Module 06)

## Overview
This module contains the automated JUnit unit tests used to verify the core logic of the recommendation system. It ensures that the algorithmic components (TF-IDF), data parsing (DataLoader), and ranking logic (RecommendationEngine) are mathematically correct and robust against edge cases.

## Purpose
- **Validation**: Prove the correctness of the TF-IDF and Cosine Similarity implementations.
- **Regression Testing**: Ensure new changes do not break existing functionality.
- **Quality Assurance**: Verify that complex data shapes (e.g., prices with thousand separators) are parsed correctly.

## Key Components
- **TFIDFVectorizerTest.java**: 
  - Verifies vocabulary size calculation.
  - Test transformation of text into vectors.
  - Ensures IDF weights are correctly assigned.
- **DataLoaderTest.java**:
  - Verifies Regex patterns for price parsing (handles "1 250,50 €", "15€", etc.).
  - Tests ID generation logic (ensuring unique IDs for unique URLs).
  - Validates category extraction from filenames.
- **RecommendationEngineTest.java**:
  - Verifies full integration: query -> ranking.
  - Tests price and category filters.
  - Validates dataset statistics generation (min/max price).

## Technical Stack
- **JUnit 5 (Jupiter)**: Current testing framework.
- **Standalone Console Launcher**: Enables running tests from the command line without an IDE.
- **Automated Script**: `test_junit.bat` simplifies the build-and-test workflow.

## Usage
To run all tests and generate reports:
1. Run `.\test_junit.bat` from the project root.
2. View results in the terminal or check `06_tests/reports/` for detailed XML/Plaintext logs.
3. Check `06_tests/test_results.csv` for a consolidated summary of all test cases.
