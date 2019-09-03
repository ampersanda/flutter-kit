(ns flutter-kit.validation
  (:use [me.raynes.fs :only [exists?]]))

(defn is-flutter? []
  (let [pubspec?        (exists? "pubspec.yaml")
        android-gradle? (exists? "android/build.gradle")
        app-gradle?     (exists? "android/app/build.gradle")
        manifest?       (exists? "android/app/src/main/AndroidManifest.xml")]
    (and pubspec? android-gradle? app-gradle? manifest?)))
