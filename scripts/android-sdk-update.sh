#!/bin/bash

# This script will update
# Android tools, build tools and platform tools
# Android SDKs from 4.2 (android-17) to 8.1 (android-27)
#
# Notes: This script will work on both Linux and macOS hosts!

set -e
set -x

PACKAGES=()
PACKAGES+=("platform-tools")
PACKAGES+=("tools")
PACKAGES+=("build-tools;27.0.3")
PACKAGES+=("extras;android;m2repository")
PACKAGES+=("extras;google;m2repository")
PACKAGES+=("platforms;android-27")
PACKAGES+=("platforms;android-26")
PACKAGES+=("platforms;android-25")
PACKAGES+=("platforms;android-24")
PACKAGES+=("platforms;android-23")
PACKAGES+=("platforms;android-22")
PACKAGES+=("platforms;android-21")
PACKAGES+=("platforms;android-19")
PACKAGES+=("platforms;android-18")
PACKAGES+=("platforms;android-17")

echo "Update Android SDK"
yes | "${ANDROID_HOME}/tools/bin/sdkmanager" "${PACKAGES[@]}"

