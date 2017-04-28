This is a test.

# Functional Tests - Core Framework

General purpose automation framework for native mobile apps based on [Appium][Appium Web].

## Demo Project

[NativeScript/functional-tests-demo](https://github.com/NativeScript/functional-tests-demo)

## Features 

- Manage emulators, simulators and physical devices.
    - Create emulators and simulators
    - Install/Uninstall applications
    - Start/Stop/Restart applications
    - Get console logs
    - Get screenshots
    - Simulate environment (like geo location for example)
    - [Android] Get performance stats (memory usage, startup time, application size)
    - All of the above managed via [configs files](docs/mobileSettings.md)
- Interaction with native apps on iOS Simulators, Android Emulators as well as physical Android and iOS devices.
    - Locate elements via accessibility of the mobile platforms  
    - [Cross-platform locators](docs/cross-platform-locators.md) that work for both Android and iOS (and also with different backend automation)
    - [Locate elements via images and OCR](docs/find-by-image.md)
    - Perform gestures
- Flexible image verification.
    - Verify your page looks as expected
    - Verify particular element on screen looks as expected
    - Allow setting tolerance as % and number of pixels
    - Logic to exclude status bar from comparison
    - Logic that helps when you have shadows or pixels that looks almost the same, but RGB is different
- Locating elements by image and OCR (based on Sikuli).
- Logs, Reports, Artifacts
    - Console log from devices, emulators and simulators
    - Appium server logs
    - Test execution logs
    - Screenshots
    - Junit style xml reports with meta info that helps generating html reports with images.

## Supported Platforms

### Host 

Tests based on this framework can be executed from Windows, Linux and macOS machines.

However, there are some limitations:
- Testing on iOS Simulators and devices can be done only from macOS hosts.
- Android testing is possible from any hosts, however some features do not work on Windows:
    - Executing tests on multiple Android emulators/simulators simultaneously.
    - Create emulators feature. 

### Mobile 

Tests based on this framework can be executed against Android Emulators, iOS Simulators, Android physical devices and iOS physical devices.

Supported versions: 
- iOS: 8+
- Android: 4.2+ 

...and yes, we work on iOS 10 and Android 7.1 :)

Most likely we also work on Android < 4.2, but we can not confirm since we test only Andoird 4.2+
 
## Prerequisites and Setup

### Prerequisites

Prerequisites to run tests based on this framework.
 
* The latest [Node][Node] LTS release
* [JDK 1.8+][JDK 8]
* [Appium][Appium Package]
* (Android only) [Android SDK][Android SDK] 
* (iOS only) [Latest Xcode][Xcode]

### Environment Setup
    
Please read [Environment Setup](docs/setup.md) document.

## NativeScript Functional Tests

This framework is used for testing all sample apps @NativeScript.

Tests are available in [NativeScript/functional-tests](https://github.com/NativeScript/functional-tests) repository.

## Issues
All work-items (issues, features, questions) are handled in [GitHub][GitHub Issues].

If you find valid issue, please log it (as good is the report as good is the chance to fix it).

Clarification:

We do not guarantee all issues will be fixed, but we will be happy to see pull requests.


## Build and Contribute

### Build

Gradle is used as build system.

Project use [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) so global installation of Gradle is not required.

#### Run tasks

- Unix-like: `./gradlew <task>`
- Windows: `gradlew.bat <task>`

#### List of tasks:

- *tasks* - Displays the tasks runnable from root project.
- *build* - Assembles and tests this project.
- *check* - Runs all style checks in `config/checkstyle/checkstyle.xml`; outputs reports in `./build/reports/`.
- *fatJar* - Assembles a jar archive with dependencies; outputs a `.jar` file in `./build/libs/`.


### Contribute

If you see an area for improvement, want to fix some git issues or just have an idea for a new feature, we will appreciate your [ pull requests](https://help.github.com/articles/about-pull-requests/)

Just make sure *check* task passes successfully.


[GitHub Issues]: https://github.com/NativeScript/functional-tests-core/issues
[Node]: https://nodejs.org
[Appium Web]: http://appium.io
[Appium Package]: https://www.npmjs.com/package/appium
[JDK 8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[Android SDK]: http://developer.android.com/sdk/index.html
[Xcode]: https://developer.apple.com/xcode/downloads/
[ideviceinstaller]: https://github.com/libimobiledevice/ideviceinstaller
