# Functional Tests - Core Framework

General purpose automation framework for native, hybrid and (soon) mobile web apps.

Framework is based on [Appium] (http://appium.io/), [Java] (https://www.java.com/en/download/) and [TestNG] (http://testng.org/doc/index.html).

Core principles:
 - Do not modify app under test
 - Use open source tools
 - Run on emulators, simulators and real devices
 - Cross platform testing with same (or almost same) code
 - Support all gestures like tap, swipe, pinch, zoom
 - Provide image comparison options
 - Provide nice HTML reports with test steps, images and device logs

## Features and Issues
All work-items (issues, features, questions) are handled in GitHub [repo] (https://github.com/NativeScript/functional-tests-core/issues) of the Functional Tests Core.

## Milestones
Main focus by milestones:

### Alfa:
- Manage infrastructure (install Appium, create simulators and emulators)
- Base Find and Wait API
- Capture device console log
- Image comparison
- Html reporting

### Beta:
- Polish alfa features
- Initial version of find by images API

### Official:
- Publish to Maven central repository
- Polished find by images API
- Windows support (this will depend on tooling like [Winium] (https://github.com/2gis/Winium))

## Prerequisites
* The latest stable [Node.js] (https://nodejs.org/)
* [JDK 8][JDK 8]
* (Android only)[Android SDK][Android SDK] 
* (iOS only) [Latest Xcode][Xcode]
* (iOS only) [Xcode command-line tools][Xcode]
* (iOS real devices only)[ideviceinstaller] (https://github.com/libimobiledevice/ideviceinstaller)

## Environment Setup

### 1. Setup Environment Variables
If not present, create the following environment variables.

	```
	JAVA_HOME=Path to the jdk* install folder
	```

	For example: JAVA_HOME=C:\Program Files\Java\jdk1.8.0_66

	```
	ANDROID_HOME=Path to Android installation directory
	```

	For example: ANDROID_HOME=C:\Android\android-sdk
	
	
### 2. Install Appium (http://appium.io/)
Windows: 
```
npm install -g appium
```

Linux and OSX:
Appium version manager allows multiple Appium versions on same machines.

Install Appium version manager:
```
npm install -g appium-version-manager
```
Install Appium with appium-version-manager:
```
avm <version-you-need>
```
Note: Appium version specified in test config will be automatically installed if not present

### 3. (Android only) Setup Android Emulators

### 4. (iOS only) Setup iOS Simulators

### 5. (iOS real devices only) Install ideviceinstaller
```
	brew install ideviceinstaller
```

## Installing dependencies

### 1. OpenCV
The OpenCV java libs can be found from project_root/opencv-2.4.9. To install them in maven, run:
```
    mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
```
### 2. Other
All other dependencies are fetched by Maven


## Build and run tests

**Run from command line with Maven**

Run all tests: 

mvn clean test

Run individual test:

mvn clean test -Dtest=BugInvadersTest

Run tests that match a mask from class:
mvn clean -Dtest=GetUser#get*Returns* test

**Run tests in Eclipse or IntelliJ IDEA**

Place this in Vm options: -DappConfig=/resources/config/cuteness.emu.api19.properties.txt

## Test results

**HTML Report**
- Screenshots for each steps are stored at project_root/screenshot locally
- Screenshots for image matching are stored at project_root/target/reports/deviceName/screenshots locally
- The reports, json file and everything else can be found from <project root>/target/reports/deviceName/

**Logs**
- Screenshots for each steps are stored at project_root/screenshot locally
- Screenshots for image matching are stored at project_root/target/reports/deviceName/screenshots locally
- The reports, json file and everything else can be found from <project root>/target/reports/deviceName/


## Test results

http://www.appbrain.com/stats/top-android-phones
https://testobject.com/blog/2014/07/top-10-android-devices-to-test-your-app-on-2.html

## Tips and Hints

**Clean image history**
git filter-branch --index-filter 'git rm --ignore-unmatch --cached res/images/*'
git push origin -f

**Screenshot modes**
git filter-branch --index-filter 'git rm --ignore-unmatch --cached res/images/*'
git push origin -f

[JDK 8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[Android SDK]: http://developer.android.com/sdk/index.html
[Xcode]: https://developer.apple.com/xcode/downloads/
