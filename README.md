# How to use
1. Download `flutter-android-signing`
2. Add to your `PATH` or just place it inside your flutter project
3. run `./flutter-android-signing -p -apk` for complete actions with proguard and building the apk or run `./flutter-android-signing` for just signing 

# DONE

1. Check is valid flutter project? 
2. Making key.properties
    1. Check ~ as home
    2. is file exists?
    3. is extension .jks?
    4. Input all info for make key.properties
    5. Spit key.properties
3. Add Sign Gradle
4. Proguard
5. Build release APK