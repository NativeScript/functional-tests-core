Functional Tests - Core Framework
=======================

Sample Java Project to show use of OpenCV and Akaze algorithm to run image recognition tests using Appium.
Sample application can be found from https://github.com/bitbar/testdroid-samples/blob/master/apps/builds/Testdroid.apk


Info on OpencCV and Akaze
=======================

The Akaze algorithm (https://github.com/pablofdezalc/akaze) is used to find the matching keypoints from two images and save them to a json file. 
The opencv (http://opencv.org/) java bindings to process the json file with keypoints and find the homography of the wanted image in a scene (screenshot).

The ImageFinder.java class will run "akaze_match", then use the keypoints from json to try to identify a given image inside a scene and return the corners of the image found. 

Prerequisites
=======================
* The latest stable [Node.js] (https://nodejs.org/)
* [JDK 8][JDK 8]
* [Android SDK][Android SDK] 
	Note: Make sure 
* [Latest Xcode][Xcode]
* [Xcode command-line tools][Xcode]


Environment Setup
=======================
0. Setup Environment Variables
If not present, create the following environment variables.

	```
	JAVA_HOME=Path to the jdk* install folder
	```

	For example: JAVA_HOME=C:\Program Files\Java\jdk1.8.0_66

	```
	ANDROID_HOME=Path to Android installation directory
	```

	For example: ANDROID_HOME=C:\Android\android-sdk
	
	
1. Install Appium (http://appium.io/)
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

2. Setup Android Emulators

3. Setup iOS Simulators

Installing dependencies
=======================

1. OpenCV
The OpenCV java libs can be found from project_root/opencv-2.4.9. To install them in maven, run:
```
    mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
```
2. Other
All other dependencies are fetched by Maven


Build and run tests
=======================

**Run from command line with Maven**

Run all tests: 

mvn clean test

Run individual test:

mvn clean test -Dtest=BugInvadersTest

Run tests that match a mask from class:
mvn clean -Dtest=GetUser#get*Returns* test

**Run tests in Eclipse or IntelliJ IDEA**

Place this in Vm options: -DappConfig=/resources/config/cuteness.emu.api19.properties.txt

Test results
=======================
**HTML Report**
- Screenshots for each steps are stored at project_root/screenshot locally
- Screenshots for image matching are stored at project_root/target/reports/deviceName/screenshots locally
- The reports, json file and everything else can be found from <project root>/target/reports/deviceName/

**Logs**
- Screenshots for each steps are stored at project_root/screenshot locally
- Screenshots for image matching are stored at project_root/target/reports/deviceName/screenshots locally
- The reports, json file and everything else can be found from <project root>/target/reports/deviceName/


Test results
=======================

http://www.appbrain.com/stats/top-android-phones
https://testobject.com/blog/2014/07/top-10-android-devices-to-test-your-app-on-2.html

Tips and Hints
=======================
**Clean image history**
git filter-branch --index-filter 'git rm --ignore-unmatch --cached res/images/*'
git push origin -f

**Screenshot modes**
git filter-branch --index-filter 'git rm --ignore-unmatch --cached res/images/*'
git push origin -f

[JDK 8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[Android SDK]: http://developer.android.com/sdk/index.html
[Xcode]: https://developer.apple.com/xcode/downloads/