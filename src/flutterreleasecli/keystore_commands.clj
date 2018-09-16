(ns flutterreleasecli.keystore_commands
  (:require [flutterreleasecli.path :as path-helper]
            [me.raynes.fs :as fs]
            [flutterreleasecli.signing-commands :as signing]
            [flutterreleasecli.cli_helper :as cli]))

(defn input-keystore-password []
  (cli/read-password "Keystore Password : "))

(defn input-alias []
  (print "Alias Name : ")
  (flush)
  (read-line))

(defn input-alias-password []
  (cli/read-password "Alias Password : "))

(defn create-key-properties [keystore-path keystore-password alias alias-password filename]
  (path-helper/write-file filename
                          (str "storePassword=" keystore-password "\nkeyPassword=" alias-password "\nkeyAlias=" alias "\nstoreFile=" keystore-path)))

(defn process-keystore! [keystore-path]
  (create-key-properties keystore-path (input-keystore-password) (input-alias) (input-alias-password) "android/key.properties")
  (signing/sign-gradle))

(defn ask-for-keystore [keystore-path]
  (if (nil? keystore-path)
    (print "Enter where the keystore is! (ex : ~/development/flutter/keystore.jks)"))

  (let [keystore-path-ask (if (nil? keystore-path) (read-line) keystore-path)
        keystore-path-expanded (path-helper/expand-home keystore-path-ask)]

    (if (nil? keystore-path) (println))

    ;; check keystore file
    (if (path-helper/file-exists? keystore-path-expanded)
      ;; when file exists
      (let [file-extension (peek (fs/split-ext keystore-path-expanded))
            is-jks? (or (= file-extension ".jks") (= file-extension ".keystore"))]

        (if is-jks?
          ;; when jks
          (process-keystore! keystore-path-expanded)
          ;; else
          (do
            (println "File is not keystore")
            (if (nil? keystore-path)
              (ask-for-keystore nil)
              ;; exit the app avoid to adding proguard
              (System/exit 0)))))
      ;; else
      (do
        (println "File is not exists")
        (if (nil? keystore-path)
          (ask-for-keystore nil)
          ;; exit the app avoid to adding proguard
          (System/exit 0))))))