(ns flutter-kit.validation
  (:use [flutter-kit.path :only [file-exists?]]))

(defn is-flutter? []
  (let [pubspec?        (file-exists? "pubspec.yaml")
        android-gradle? (file-exists? "android/build.gradle")
        app-gradle?     (file-exists? "android/app/build.gradle")
        manifest?       (file-exists? "android/app/src/main/AndroidManifest.xml")]
    (and pubspec? android-gradle? app-gradle? manifest?)))
