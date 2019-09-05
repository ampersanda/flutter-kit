# Flutter Kit

[![CircleCI](https://circleci.com/gh/ampersanda/flutter-kit.svg?style=svg)](https://circleci.com/gh/ampersanda/flutter-kit)

# How to use
1. Download `flutter-kit` (https://github.com/ampersanda/flutter-kit/releases)
2. Add to your `PATH` or just place it inside your flutter project

#### Proguard only
```
flutter-kit --proguard
```

#### Proguard and Signing
```
flutter-kit --proguard --keystore-path=keystore.jks --keystore-password=password --keystore-alias=alias --keystore-alias-password=aliasPassword
```
### To Do

- [x] Android Signing
- [ ] Revert Android Signing
- [x] Android Proguard setup
- [ ] Revert Android Proguard setup
- [ ] Migrate to AndroidX
- [ ] Migrate to AppCompat

Steps are from https://flutter.io/android-release/
