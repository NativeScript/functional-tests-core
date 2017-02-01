## Environment Setup

### 1. Install Java 1.8+

#### Windows:
Install JDK 8 or a later stable official release.

Set the JAVA_HOME system environment variable.

#### macOS:

Install Java:

```
brew update
brew cask install java
```

Then add following in your ~/.bash_profile:

`export JAVA_HOME=$(/usr/libexec/java_home)`

### 2. Install Android SDK

#### Windows:
Install Android SDK or a later stable official release.

Set the ANDROID_HOME system environment variable.

#### macOS:
Install Android SDK:

```
brew update
brew install android-sdk
```

Then add following in your ~/.bash_profile:

`export ANDROID_HOME=/usr/local/opt/android-sdk`

#### Update SDKs

See [android-sdk-update.sh](../scripts/android-sdk-update.sh)

#### Download Emulator images

See [android-emulator-images-update.sh](../scripts/android-emulator-images-update.sh)

#### Create Emulators

See [android-emulator-re-create.sh](../scripts/android-emulator-re-create.sh)

### 3. Install NodeJS and Appium (http://appium.io/)

#### NodeJS

Windows:

[Download](https://nodejs.org/en/download/) and install latest NodeJS LTS release

macOS:

```
brew update
brew tap homebrew/versions
brew search node
brew install homebrew/versions/node6-lts
```

Notes: Do NOT install node with sudo! 

#### Appium

Install Appium 1.6.3

```
npm install -g appium@1.6.3
```

#### 4. Xcode

Latest official release of [Xcode](https://developer.apple.com/xcode/downloads/) is required for testing iOS applications.

#### 5. Other packages

If you intend to test on iOS devices you will need following:

```
brew update
brew install --HEAD libimobiledevice
brew install --HEAD ideviceinstaller
brew install ios-webkit-debug-proxy
brew install carthage 

npm install -g deviceconsole
npm install -g ios-deploy
```
