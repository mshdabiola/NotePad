# Contributing to Notepad

Thank you for your interest in contributing to the HydraulicApp project! We appreciate your willingness to help improve and expand our application. By contributing, you help us make this project better for everyone.

Please take a moment to review these guidelines before making a contribution. Following these guidelines will help us ensure a smooth and efficient contribution process.

## Table of Contents

1.  [Getting Started](#getting-started)
2.  [How to Contribute](#how-to-contribute)
    *   [Reporting Bugs](#reporting-bugs)
    *   [Suggesting Features](#suggesting-features)
    *   [Making Code Contributions](#making-code-contributions)
3.  [Development Setup](#development-setup)
4.  [Code Style Guidelines](#code-style-guidelines)
5.  [Commit Message Guidelines](#commit-message-guidelines)
6.  [Pull Request Guidelines](#pull-request-guidelines)
7.  [Testing](#testing)
8.  [Code of Conduct](#code-of-conduct)
9.  [Getting Help](#getting-help)

---

## 1. Getting Started

*   **Fork the Repository:** Start by forking the main HydraulicApp repository to your GitHub account.
*   **Clone Your Fork:** Clone your forked repository to your local machine.
*   *   **Set Up Upstream Remote:** Add the original HydraulicApp repository as an "upstream" remote. This allows you to sync your fork with the latest changes from the main project.
    *   (Replace `PROJECT_ORGANIZATION` with the actual organization or username of the main repository).
*   **Sync Your Fork:** Before starting any work, sync your fork with the upstream repository to ensure you have the latest code.

*   ## 2. How to Contribute

There are several ways you can contribute to HydraulicApp:

*   **Reporting Bugs:** If you find a bug, please report it by opening a new [issue](https://github.com/PROJECT_ORGANIZATION/HydraulicApp/issues/new?assignees=&labels=&projects=&template=bug_report.md&title=) on the GitHub repository. Provide as much detail as possible, including steps to reproduce the bug, expected behavior, and the environment in which the bug occurred. (Consider creating a `bug_report.md` issue template).
*   **Suggesting Features:** Have an idea for a new feature or improvement? Open a new [issue](https://github.com/PROJECT_ORGANIZATION/HydraulicApp/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=) to discuss your suggestion. Explain the feature, its potential benefits, and how it might be implemented. (Consider creating a `feature_request.md` issue template).
*   **Making Code Contributions:** This is the most common way to contribute. It involves writing code to fix bugs, implement features, or improve existing code. Please follow the guidelines below for making code contributions.

## 3. Development Setup

To set up your development environment:

*   **Prerequisites:**
    *   **Java Development Kit (JDK):** Ensure you have JDK 21 or later installed.
    *   **Android Studio:** If you're working on the Android part of the project, Android Studio is highly recommended.
    *   **Gradle:** The project uses Gradle as its build tool. Gradle is usually bundled with Android Studio, or you can install it separately.
    *   **Git:** For version control.
*   **Import into Android Studio:** Open or import the project into Android Studio. Android Studio should automatically detect the Gradle project structure.
*   **Run the Application:** Once the project is imported, you should be able to build and run the application on an emulator or physical device (for the Android target) or run the JVM or Wasm targets depending on the specific module you are working on.

## 4. Code Style Guidelines

*   We follow the standard [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
*   Maintain consistent formatting throughout the codebase. Android Studio's default Kotlin formatter is generally acceptable.
*   Use meaningful variable and function names.
*   Write clear and concise comments where necessary to explain complex logic.
*   Keep lines reasonably short (ideally under 120 characters).
*   Ensure your code adheres to the project's Detekt configuration. Run Detekt locally before submitting a pull request using `./gradlew detekt check`.

## 5. Commit Message Guidelines

We follow a [conventional commit message format](https://www.conventionalcommits.org/en/v1.0.0/). This makes it easier to understand the purpose of each commit and helps with automated changelog generation.

A commit message should have the following structure:
*   **`type`:** Must be one of the following:
    *   `feat`: A new feature
    *   `fix`: A bug fix
    *   `docs`: Documentation changes
    *   `style`: Changes that do not affect the meaning of the code (whitespace, formatting, missing semicolons, etc.)
    *   `refactor`: A code change that neither fixes a bug nor adds a feature
    *   `perf`: A code change that improves performance
    *   `test`: Adding missing tests or correcting existing tests
    *   `build`: Changes that affect the build system or external dependencies (example scopes: `gradle`, `dependencies`)
    *   `ci`: Changes to our CI configuration files and scripts (example scopes: `travis`, `circle`, `browserstack`, `saucelabs`)
    *   `chore`: Other changes that don't modify src or test files
*   **`scope` (optional):** The part of the codebase affected by the commit (e.g., `app`, `feature/main`, `module/data`, `build-logic`).
*   **`description`:** A concise description of the change (max 50 characters). Start with a lowercase letter.
*   **`body` (optional):** A more detailed explanation of the change. Use the imperative mood.
*   **`footer` (optional):** Can contain breaking changes information or references to GitHub issues. Use keywords like `Closes #<issue_number>` or `Fixes #<issue_number>` to automatically link commits to issues and close them when the pull request is merged. [1]

**Example:**
## 6. Pull Request Guidelines

*   **One Pull Request Per Issue:** Ideally, each pull request should address a single bug fix or feature implementation.
*   **Create a New Branch:** Create a new branch for your changes based on the latest `main` branch of the upstream repository. Use a descriptive branch name (e.g., `fix/bug-description` or `feat/feature-name`).
*   *   **Write Code:** Implement your changes in your new branch.
*   **Test Your Changes:** Ensure your changes are well-tested. Write new tests or update existing ones as needed.
*   **Run Static Analysis:** Run Detekt and other static analysis tools to check for code style and potential issues.
*   *   **Write Clear Commit Messages:** Follow the commit message guidelines.
*   **Push Your Branch:** Push your branch to your fork on GitHub.
*   *   **Create a Pull Request:** Go to the HydraulicApp repository on GitHub and create a new pull request from your branch.
*   **Provide Details:** In the pull request description, explain your changes, the problem they solve, or the feature they implement. Reference the relevant issue number using `#<issue_number>` (e.g., "This PR addresses #123").
*   **Address Feedback:** Be prepared to address feedback from maintainers and other contributors.

## 7. Testing

*   Write unit tests for your code, especially for business logic and data layers.
*   Write integration tests or UI tests for key functionalities.
*   Ensure all tests pass before submitting a pull request using `./gradlew check`.

## 8. Code of Conduct

We strive to create a welcoming and inclusive community. Please adhere to our [Code of Conduct](CODE_OF_CONDUCT.md) (Replace `CODE_OF_CONDUCT.md` with the actual filename and path if it's different).

## 9. Getting Help

If you have questions, need help, or want to discuss anything related to contributing, you can:

*   Open a [GitHub issue](https://github.com/PROJECT_ORGANIZATION/HydraulicApp/issues/new) with your question.
*   [Mention other communication channels if available, e.g., a project Slack channel, Discord server, etc.]

Thank you again for your contribution! We look forward to your pull requests.
