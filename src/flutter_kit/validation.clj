(ns flutter-kit.validation
  (:require [me.raynes.fs :refer [exists?]]
            [flutter-kit.path :refer [pubspec-path app-gradle-path android-gradle-path manifest-path]]))

(defn is-flutter? []
  (let [pubspec?        (exists? pubspec-path)
        android-gradle? (exists? android-gradle-path)
        app-gradle?     (exists? app-gradle-path)
        manifest?       (exists? manifest-path)]
    (and pubspec? android-gradle? app-gradle? manifest?)))
