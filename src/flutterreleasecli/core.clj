(ns flutterreleasecli.core
  (:use [flutterreleasecli.cli_helper])
  (:require [clojure.tools.cli :refer [parse-opts]]
            [me.raynes.fs :as fs]
            [flutterreleasecli.path :as path-helper]
            [flutterreleasecli.commands :as cmd])
  (:gen-class))

(def app-name "flutter-kit")

(defn run!? [arguments]
  (let [keystore-path (:keystore-path (arguments :options))]
    (if (nil? keystore-path)
      ;; when keystore param is nil
      (cmd/ask-for-keystore nil)
      ;; when keystore param is available
      (cmd/ask-for-keystore (:keystore-path (get-options! arguments))))))

(def cli-options
  ;; An option with a required argument
  [[nil "--keystore-path PATH" "Path to keystore/jks file"]

   ;; Proguard
   ["-p" "--proguard" "Enable Proguard"
    :default
    false]

   ;; help
   ["-h" "--help"]])

(defn -main [& args]
  (let [arguments  (parse-opts args cli-options)
        is-flutter? (path-helper/file-exists? "pubspec.yaml")]
    (if (nil? (get-error! arguments))
      ;; error is nil - no error
      (if is-flutter?
        ;; when valid flutter
        (run!? arguments)
        ;; else
        (do
          (println "Not a valid flutter project :(")
          (System/exit 0)))
      ;; when error
      (help! arguments))))