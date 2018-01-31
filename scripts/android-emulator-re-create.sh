#!/bin/bash

# This script will re-create Android emulators from 4.2 (android-17) to 7.1 (android-25)
#
# Notes:
# This script will work on both Linux and macOS hosts!
# Make sure emulator images exists (do not run before android-emulator-images-update.sh)!

echo "Default Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api17-Default -t android-17 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api18-Default -t android-18 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api19-Default -t android-19 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Default -t android-21 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Default -t android-22 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Default -t android-23 --abi default/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api24-Default -t android-24 --abi default/x86 -c 12M -f

echo "ARM Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api17-Arm -t android-17 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api18-Arm -t android-18 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api19-Arm -t android-19 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Arm -t android-21 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Arm -t android-22 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Arm -t android-23 --abi default/armeabi-v7a -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api24-Arm -t android-24 --abi default/armeabi-v7a -c 12M -f

echo "Google APIs x86 Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api25-Google -t android-25 --abi google_apis/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api24-Google -t android-24 --abi google_apis/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Google -t android-23 --abi google_apis/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Google -t android-22 --abi google_apis/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Google -t android-21 --abi google_apis/x86 -c 12M -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api19-Google -t android-19 --abi google_apis/x86 -c 12M -f
