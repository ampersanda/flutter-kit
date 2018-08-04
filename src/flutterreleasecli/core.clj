(ns flutterreleasecli.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [flutterreleasecli.path :as path-helper]
            [flutterreleasecli.cli_helper :as cli-helper])
  (:gen-class))

(def app-name "flutter-kit")

(defn input-keystore-password []
  (println "Keystore Password : (will be seen)")
  (read-line))

(defn input-alias []
  (println "Alias Name : ")
  (read-line))

(defn input-alias-password []
  (println "Alias Password : (will be seen)")
  (read-line))

(defn process-keystore! [keystore-path]
  (let [keystore-password (input-keystore-password)
        alias             (input-alias)
        alias-password    (input-alias-password)
        filename          "android/key.properties"]
    (path-helper/write-file filename
                            (str "storePassword=" keystore-password "\nkeyPassword=" alias "\nkeyAlias=" alias "\nstoreFile=" keystore-path))))

(defn ask-for-keystore [keystore-path]
  (if (nil? keystore-path)
    (println "Enter where the keystore is! (ex : ~/development/flutter/keystore.jks)"))

  (let [keystore-path-ask      (if (nil? keystore-path) (read-line) keystore-path)
        keystore-path-expanded (path-helper/expand-home keystore-path-ask)]

    (if (nil? keystore-path) (println))

    ;; check keystore file
    (if (.exists (io/as-file keystore-path-expanded))
      ;; when file exists
      (let [file-extension (peek (fs/split-ext keystore-path-expanded))
            is-jks?        (= file-extension ".jks")]

        (if is-jks?
          ;; when jks
          (process-keystore! keystore-path-expanded)
          ;; else
          (do
            (println "File is not keystore")
            (if (nil? keystore-path) (ask-for-keystore nil) (System/exit 0)))))
      ;; else
      (do
        (println "File is not exists")
        (if (nil? keystore-path) (ask-for-keystore nil) (System/exit 0))))))

(defn run!? [arguments]
  (let [keystore-path (:keystore-path (arguments :options))]
    (if (nil? keystore-path)
      ;; when keystore param is nil
      (ask-for-keystore nil)
      ;; when keystore param is available
      (ask-for-keystore (:keystore-path (cli-helper/get-options! arguments))))))

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
    (if (nil? (cli-helper/get-error! arguments))
      ;; error is nil - no error
      (run!? arguments)
      ;; when error
      (cli-helper/help! arguments))))