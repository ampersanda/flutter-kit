(ns flutter-kit.path
  (:require [clojure.java.io :as io]))

(def app-gradle-path "android/app/build.gradle")
(def android-gradle-path "android/build.gradle")
(def pubspec-path "pubspec.yaml")
(def manifest-path "android/app/src/main/AndroidManifest.xml")

(defn write-file [filename spitted-string]
  (io/make-parents filename)
  (spit filename spitted-string))
