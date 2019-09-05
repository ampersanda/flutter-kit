(ns flutter-kit.androidx
  (:require [flutter-kit.path :refer [app-gradle-path write-file]]))

(defn- set-compile-sdk-version [version]
  "change compileSdkVersion"
  (write-file app-gradle-path
              (clojure.string/replace-first (slurp app-gradle-path) #"compileSdkVersion\s*(\d+)" (str "compileSdkVersion " version))))

(defn install! []
  (set-compile-sdk-version 28))
