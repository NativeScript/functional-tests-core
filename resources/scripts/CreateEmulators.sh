#!/bin/sh

echo "Default Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api17-Default -t android-17 --abi default/x86 -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api18-Default -t android-18 --abi default/x86 -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api19-Default -t android-19 --abi default/x86 -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Default -t android-21 --abi default/x86 -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Default -t android-22 --abi default/x86 -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Default -t android-23 --abi default/x86 -f

echo "Nexus 5 Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api19-Nexus5 -t android-19 --abi default/x86 --skin "Nexus 5" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Nexus5 -t android-21 --abi default/x86 --skin "Nexus 5" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Nexus5 -t android-22 --abi default/x86 --skin "Nexus 5" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Nexus5 -t android-23 --abi default/x86 --skin "Nexus 5" -f

echo "Nexus 6 Emulators"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Nexus6 -t android-21 --abi default/x86 --skin "Nexus 6" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Nexus6 -t android-22 --abi default/x86 --skin "Nexus 6" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Nexus6 -t android-23 --abi default/x86 --skin "Nexus 6" -f

echo "Nexus 7 Emulators (Asus Tablets)"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Nexus7 -t android-21 --abi default/x86 --skin "Nexus 7" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Nexus7 -t android-22 --abi default/x86 --skin "Nexus 7" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Nexus7 -t android-23 --abi default/x86 --skin "Nexus 7" -f

echo "Nexus 10 Emulators (Samsung Tablets)"
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api21-Nexus10 -t android-21 --abi default/x86 --skin "Nexus 10" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api22-Nexus10 -t android-22 --abi default/x86 --skin "Nexus 10" -f
echo no | $ANDROID_HOME/tools/android create avd -n Emulator-Api23-Nexus10 -t android-23 --abi default/x86 --skin "Nexus 10" -f