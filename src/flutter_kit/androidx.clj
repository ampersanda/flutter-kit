(ns flutter-kit.androidx
  (:require [flutter-kit.path :refer [app-gradle-path write-file]]))

(defn- set-compile-sdk-version [version]
  "change compileSdkVersion"
  (write-file app-gradle-path
              (clojure.string/replace-first (slurp app-gradle-path) #"compileSdkVersion\s*(\d+)" (str "compileSdkVersion " version))))

(defn- set-target-sdk-version [version]
  "change targetSdkVersion"
  (write-file app-gradle-path
              (clojure.string/replace-first (slurp app-gradle-path) #"targetSdkVersion\s*(\d+)" (str "targetSdkVersion " version))))

(defn- get-dependencies []
;  (dependencies\s*\{(([^{}]++|\{\})++)\})
;  (dependencies\s*\{[^{}]++|\{\})++\}
  (let [dependencies (re-find (re-matcher #"(dependencies\s*\{[^{}]++|\{\})++\}" (slurp app-gradle-path)))]
    (doseq [x dependencies]
      (println x))))


(defn install! []
  (println "Migrating to AndroidX...")
  (set-compile-sdk-version 28)
  (set-target-sdk-version 28)
  (get-dependencies))

;rgx-flutter-fragment                   #"implementation\s*[\'\"]com\.android\.support:support-fragment:(.*)[\'\"]"
;    dependencies\s*\{(\s*implementation\s*[\'\"].*[\'\"]\s*)*\s*(implementation\s*[\'\"]com\.android\.support:support-fragment:(.*)[\'\"])\s*(implementation\s*[\'\"].*[\'\"]\s*)*
