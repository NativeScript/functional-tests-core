#!/bin/sh

TARGETS="android-17 android-18 android-19 android-21 android-22 android-23 android-24"

for TARGET in $TARGETS
do
    if [ -d $ANDROID_HOME/platforms/$TARGET/skins ]
    then
        cp -R ../skins/* $ANDROID_HOME/platforms/$TARGET/skins/
        echo "Skins installed for target: $TARGET"
    else
        echo "$TARGET not available."
    fi
done