## Settings and Configs

### Settings in config files

For each test run you can specify mobileSettings in config files.

#### Short example:
```
appiumVersion=1.6.3
platformName=Android
deviceName=Emulator-Api23-Default
platformVersion=6.0
testAppName=nativeapp.apk
deviceType=Emulator
emulatorOptions=-wipe-data
emulatorCreateOptions=-t android-23 --abi default/x86
```

#### List of all the mobileSettings:

##### Mandatory

**appiumVersion**  
Version of Appium server.  
Tests will be not executed if specified version is not installed globally.

**deviceName**  
For emulators: Name of Android emulator image (avd).  
For simulators: Name of iOS Simulator  
For real devices: Just give a alias to your device. For example: Nexus5  

**platformName**  
Name of the mobile platform - Android or iOS.

**platformVersion**  
Version ot the mobile OS.
For example: 4.4, 7.0, 10.0

**testAppName**  
Android: Name of apk file (including file extension).  
iOS Device: Name of ipa file (including file extension).  
iOS Simulator: Name of tgz archive of the *.app folder (including file extension).  
Notes: Tests apps are supposed to be available at `${project.root}/testapp folder`.

**deviceType**  
Type of mobile device.  
Emulator, Simulator, Android (for real Android devices), iOS (for real iOS devices).

#### Mandatory for real devices
**udid**  
Unique device identifier.

How to get it:  
Android: `$ANDROID_HOME/platform-tools/adb devices`  
iOS: `instruments -s` 

Notes: Later, at runtime this is available as ***deviceId***  

##### Optional

**defaultTimeout**  
Timeout for find and wait elements.  
Default value: 30 (seconds)  

**deviceBootTimeout**  
Timeout for emulators and simulators to boot.  
Default value: 300 (seconds)  

**restartApp**  
Application will be restarted between tests if value is `true`.  
Default value: `false`  

**orientation**  
Orientation of the device.  
PORTRAIT or LANDSCAPE.  
If specified it will be applied before test execution.
If not specified device orientation will not be changed (will use current orientation).  

**takeScreenShotAfterTest**  
Take screenshot after test (even if test pass).  
Default value: `false` 

**ImageVerificationType**  
`Default`: Perform default image verification. Fail test if expected image is not available.  
`FirstTimeCapture`: Do NOT fail test if expected image is not available and save actual image with the expected name at the expected location.  
`Skip`: Do NOT perform image verification.  

**logImageVerificationStatus**
Log image verification status in the report template and screenshots folder while image comparison concludes.
Default value is `false`
    
**appiumLogLevel**  
Log level for Appium server.  
Default value: `warn`  

**emulatorOptions**  
Options passed to emulator when starting it.  
Those are actually Android things and you can read more [here](https://developer.android.com/studio/run/emulator-commandline.html).  
Example: `-wipe-data -gpu on` 

**emulatorCreateOptions**  
If specified emulator name is not available and if `emulatorCreateOptions` is specified then we will try to create emulator with specified options.  
Please, have in mind that the `emulatorCreateOptions` should comply with the Android SDK Tools version installed.
Example:
SDK Tools 25.2.5: `-t android-24 --abi default/x86`
SDK Tools 25.3.0: `-k "system-images;android-24;default;x86" -b default/x86`
Details: Please read [official Android docs](https://developer.android.com/studio/tools/help/android.html)

**automationName**   
Name of Appium backend automation.

Default Values:  
SELENDROID for Android <= 4.1  
UIAUTOMATOR2 for Android >= 7.0  
IOS_XCUI_TEST for iOS >= 10.0  
APPIUM in all other cases  

#### Android specific mobileSettings

Those mobileSettings are reached through mobileSettings.android.*, for example `mobileSettings.android.getDefaultActivity`  

**appWaitActivity**  
Activity that should be started when app is launched.  
Tests will wait until activity loaded.  
Default value is equal to `defaultActivity` which is auto-detected from installation files.  

**appWaitPackage**  
Application ID that should be started when app is launched.  
Tests will wait until app with specified package ID loads.  
Default value is equal to `packageId` which is auto-detected from installation files.  

**memoryMaxUsageLimit**  
Limit of memory usage for app under test (in kB).  
Tests will fail if usage is above the limit.  
If memoryMaxUsageLimit is not specified it will be ignored and huge memory usage will not make tests fail.  

**appLaunchTimeLimit**  
Limit of time for loading the app (in milliseconds).  
Tests will fail if app load slower than the limit.
If appLaunchTimeLimit is not specified it will be ignored and tests will not fail because app is slow.  

#### iOS specific mobileSettings

Those mobileSettings are reached through mobileSettings.ios.*, for example `mobileSettings.android.acceptAlerts`  

**testAppArchive**  
[Only for Simulators] Application package for simulators is actually a folder, so we took the decision to archive it in tgz file.
`testAppArchive` is name of archive, including extension.

**acceptAlerts**  
If `true` all iOS alerts will be automatically handled.  
Default value: `false`  

**simulatorType**  
Example: `simulatorType=iPhone 7`  
Usage:  
If simulator with specified *deviceName* is not available, then create new and use ***simulatorType*** to specify type of iOS device we want to simulate.  

### Settings @runtime

Some more mobileSettings are auto-detected and available in test mobileContext at runtime.
 
Sample usage in tests:
```
if (this.mobileSettings.platform == PlatformType.Android) {
    // TODO: Do something!
}
```

List of available properties:

**OSType os**  
Operating system of host machine
    
**packageId**  
Bundle identifier of app under test.  
It is auto-detected from installation files.  
Example: `org.nativescript.nativeapp`  

**isRealDevice**  
True for Android and iOS real devices.  
False for emulators and simulators.  

**shortTimeout**  
Calculated as `defaultTimeout / 5`  

**testAppFriendlyName**
Name of the label under icon that launch the application.  

**testAppImageFolder**  
Auto-generated based on application name.  
If `STORAGE` environment variable is not set this folder is relative to ${project.root}/resources/images.  
If `STORAGE` environment variable is set this folder is relative to $STORAGE/images.  

**debug**  
If `true` Appium session length will be increased to allow more time for debugging.   
Automatically set to true if debugger session is detected.
Also set to true if explicitly set to `true` in config file.  

#### Android specific mobileSettings @runtime

Those mobileSettings are reached through mobileSettings.android.*, for example `mobileSettings.android.defaultActivity`  

**defaultActivity**  
Default launchable activity of app under test.  
It is auto-detected from installation files.  

#### iOS specific mobileSettings @runtime

Those mobileSettings are reached through mobileSettings.ios.*, for example `mobileSettings.android.xCode8ConfigFile`  

**xCode8ConfigFile**    
This file will be auto-generated at `{$project.root}/resources/xcode/xcode8config.xcconfig`
File should contain those lines:  
````
DEVELOPMENT_TEAM = [Value of DEVELOPMENT variable]
CODE_SIGN_IDENTITY = iPhone Developer
````
Note that you should have environment variable `DEVELOPMENT_TEAM` with correct value in order to be able to automate on real iOS 10+ devices.  

#### Experimental

**reuseDevice**
If `true` tests will detect if appropriate emulator/simulator is up and running.  
If appropriate emulator/simulator is running tests will reuse it.
Also emulator/simulator will be not killed after test run in order to keep it available for next run.  

Default value: `false`  
Will be set to true if `reuseDevice=true` in config file or if there is environment variable `REUSE_DEVICE` with value `true`.  
