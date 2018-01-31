#!/bin/bash

# This script will update
# Android tools, build tools and platform tools
# Android SDKs from 4.2 (android-17) to 7.1 (android-25)
#
# Notes: This script will work on both Linux and macOS hosts!

set -e
set -x

echo "Update Android SDK"
yes | $ANDROID_HOME/tools/bin/sdkmanager platform-tools
yes | $ANDROID_HOME/tools/bin/sdkmanager tools
yes | $ANDROID_HOME/tools/bin/sdkmanager 'build-tools;25.0.0'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-25'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-24'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-23'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-22'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-21'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-19'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-18'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-17'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'extras;android;m2repository'
yes | $ANDROID_HOME/tools/bin/sdkmanager 'extras;google;m2repository'
