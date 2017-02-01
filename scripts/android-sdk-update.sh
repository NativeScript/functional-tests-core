#!/bin/bash

# This script will update
# Android tools, build tools and platform tools
# Android SDKs from 4.2 (android-17) to 7.1 (android-25)
#
# Notes: This script will work on both Linux and macOS hosts!

echo "Update Android SDK"
echo y | $ANDROID_HOME/tools/android update sdk --filter platform-tools --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter tools --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter build-tools-25.0.0 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-25 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-24 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-23 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-22 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-21 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-19 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-18 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter android-17 --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter extra-android-m2repository --all --no-ui
echo y | $ANDROID_HOME/tools/android update sdk --filter extra-google-m2repository --all --no-ui