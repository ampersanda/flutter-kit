(ns flutterreleasecli.core
  (:use [flutterreleasecli.cli_helper]
        [flutterreleasecli.path :only [file-exists?]]
        [flutterreleasecli.keystore_commands :only [ask-for-keystore]]
        [flutterreleasecli.validation :only [is-flutter?]])
  (:require [clojure.tools.cli :refer [parse-opts]]
            [flutterreleasecli.proguard-commands :as proguard])
  (:gen-class))

(def app-name "flutter-kit")

(defn run!? [arguments]
  (let [keystore-path (:keystore-path (arguments :options))]
    (if (nil? keystore-path)
      ;; when keystore param is nil
      (ask-for-keystore nil)
      ;; when keystore param is available
      (ask-for-keystore (:keystore-path (get-options! arguments))))
    (if (:proguard (get-options! arguments))
      (do (println "Processing proguard")
          (proguard/configure-proguard)
          (proguard/enable-obfuscation-and-or-minification)))))

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
  (let [arguments  (parse-opts args cli-options)]
    (if (nil? (get-error! arguments))
      ;; error is nil - no error
      (if (is-flutter?)
        ;; when valid flutter
        (run!? arguments)
        ;; else
        (println "Not a valid flutter project :("))
      ;; when error
      (help! arguments))))