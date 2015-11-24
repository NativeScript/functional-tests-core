@ECHO OFF
FOR %%X IN (android-17 android-18 android-19 android-21 android-22 android-23 android-24) DO (
    ECHO %%X
    IF exist %ANDROID_HOME%\platforms\%%X\skins\ ( xcopy ..\skins\* %ANDROID_HOME%\platforms\%%X\skins\ /s /e /h /Y ) ELSE (echo Target %%X does not exist.)
)