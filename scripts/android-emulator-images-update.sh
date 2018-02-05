#!/bin/bash

# This script will update
# Intel HAXM (required for accelerated emulators)
# Android emulator images from 4.2 (android-17) to 8.1 (android-27)
#
# Notes:
# This script will work on both Linux and macOS hosts!
# This script will install both arm and x86 emulators plus google api images!
# Emulator images require a lot of disk space!

set -e
set -x

echo "Update Intel HAXM"
echo y | "${ANDROID_HOME}/tools/bin/sdkmanager" "extras;intel;Hardware_Accelerated_Execution_Manager"
sudo sh "${ANDROID_HOME}/extras/intel/Hardware_Accelerated_Execution_Manager/silent_install.sh"

PACKAGES=()
PACKAGES+=("system-images;android-17;default;x86")
PACKAGES+=("system-images;android-17;google_apis;x86")
PACKAGES+=("system-images;android-18;default;x86")
PACKAGES+=("system-images;android-18;google_apis;x86")
PACKAGES+=("system-images;android-19;default;x86")
PACKAGES+=("system-images;android-19;google_apis;x86")
PACKAGES+=("system-images;android-21;default;x86")
PACKAGES+=("system-images;android-21;google_apis;x86")
PACKAGES+=("system-images;android-22;default;x86")
PACKAGES+=("system-images;android-22;google_apis;x86")
PACKAGES+=("system-images;android-23;default;x86")
PACKAGES+=("system-images;android-23;google_apis;x86")
PACKAGES+=("system-images;android-24;default;x86")
PACKAGES+=("system-images;android-24;google_apis;x86")
PACKAGES+=("system-images;android-25;google_apis;x86")
PACKAGES+=("system-images;android-26;google_apis;x86")
PACKAGES+=("system-images;android-27;google_apis;x86")

echo "Update Emulator Images"
yes | "${ANDROID_HOME}/tools/bin/sdkmanager" "${PACKAGES[@]}"

