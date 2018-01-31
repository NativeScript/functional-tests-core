#!/bin/bash

# This script will re-create Android emulators from 4.2 (android-17) to 8.1 (android-27)
#
# Notes:
# This script will work on both Linux and macOS hosts!
# Make sure emulator images exists (do not run before android-emulator-images-update.sh)!

function recreate_emulator() {
	name="$1"
	image="$2"

    echo no | $ANDROID_HOME/tools/bin/avdmanager create avd -n "${name}" -k "${image}" -c 12M -f
}

echo "Default Emulators"
recreate_emulator Emulator-Api17-Default "system-images;android-17;default;x86"
recreate_emulator Emulator-Api18-Default "system-images;android-18;default;x86"
recreate_emulator Emulator-Api19-Default "system-images;android-19;default;x86"
recreate_emulator Emulator-Api21-Default "system-images;android-21;default;x86"
recreate_emulator Emulator-Api22-Default "system-images;android-22;default;x86"
recreate_emulator Emulator-Api23-Default "system-images;android-23;default;x86"
recreate_emulator Emulator-Api24-Default "system-images;android-24;default;x86"

echo "ARM Emulators"
recreate_emulator Emulator-Api17-Arm "system-images;android-17;default;armeabi-v7a"
recreate_emulator Emulator-Api18-Arm "system-images;android-18;default;armeabi-v7a"
recreate_emulator Emulator-Api19-Arm "system-images;android-19;default;armeabi-v7a"
recreate_emulator Emulator-Api21-Arm "system-images;android-21;default;armeabi-v7a"
recreate_emulator Emulator-Api22-Arm "system-images;android-22;default;armeabi-v7a"
recreate_emulator Emulator-Api23-Arm "system-images;android-23;default;armeabi-v7a"
recreate_emulator Emulator-Api24-Arm "system-images;android-24;default;armeabi-v7a"

echo "Google APIs x86 Emulators"
recreate_emulator Emulator-Api27-Google "system-images;android-27;google_apis;x86"
recreate_emulator Emulator-Api26-Google "system-images;android-26;google_apis;x86"
recreate_emulator Emulator-Api25-Google "system-images;android-25;google_apis;x86"
recreate_emulator Emulator-Api24-Google "system-images;android-24;google_apis;x86"
recreate_emulator Emulator-Api23-Google "system-images;android-23;google_apis;x86"
recreate_emulator Emulator-Api22-Google "system-images;android-22;google_apis;x86"
recreate_emulator Emulator-Api21-Google "system-images;android-21;google_apis;x86"
recreate_emulator Emulator-Api19-Google "system-images;android-19;google_apis;x86"
