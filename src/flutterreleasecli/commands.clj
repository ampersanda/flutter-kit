(ns flutterreleasecli.commands
  (:require [flutterreleasecli.path :as path-helper]
            [me.raynes.fs :as fs]))

(defn input-keystore-password []
  (println "Keystore Password : (will be seen)")
  (read-line))

(defn input-alias []
  (println "Alias Name : ")
  (read-line))

(defn input-alias-password []
  (println "Alias Password : (will be seen)")
  (read-line))

(def sign-gradle-1-str
  (str "def keystorePropertiesFile = rootProject.file(\"key.properties\")"))

(defn sign-gradle-1 []
  (let [filepath                "android/app/build.gradle"
        old                     (slurp filepath)
        addition-ex             #"def keystorePropertiesFile ?= ?rootProject.file\(\"key\.properties\"\)\ndef keystoreProperties ?= ?new Properties\(\)\nkeystoreProperties\.load\(new FileInputStream\(keystorePropertiesFile\)\)\n\nandroid ?\{"
        addition                (str "def keystorePropertiesFile = rootProject.file(\"key.properties\")" "\n"
                                     "def keystoreProperties = new Properties()" "\n"
                                     "keystoreProperties.load(new FileInputStream(keystorePropertiesFile))" "\n"
                                     "\n"
                                     "android {")
        addition-matcher        (re-matcher addition-ex old)
        addition-finder         (re-find addition-matcher)
        new-string              (clojure.string/replace-first old #"android ?\{" addition)]
    ;; check if gradle already has addition properties
    (if (nil? addition-finder)
      ;; when addition is not there yet
      (do
        ;; backup
        (println "Backing up build.gradle ..")
        (path-helper/write-file (str filepath ".bak") old)
        ;; create new file
        (println "Create new build.gradle ..")
        (path-helper/write-file filepath new-string))
      ;; else
      (println "build.gradle already configured. Skipping.."))))

(defn create-key-properties [keystore-path keystore-password alias alias-password filename]
  (path-helper/write-file filename
                          (str "storePassword=" keystore-password "\nkeyPassword=" alias-password "\nkeyAlias=" alias "\nstoreFile=" keystore-path)))

(defn process-keystore! [keystore-path]
  (create-key-properties keystore-path (input-keystore-password) (input-alias) (input-alias-password) "android/key.properties")
  (sign-gradle-1))

(defn ask-for-keystore [keystore-path]
  (if (nil? keystore-path)
    (println "Enter where the keystore is! (ex : ~/development/flutter/keystore.jks)"))

  (let [keystore-path-ask      (if (nil? keystore-path) (read-line) keystore-path)
        keystore-path-expanded (path-helper/expand-home keystore-path-ask)]

    (if (nil? keystore-path) (println))

    ;; check keystore file
    (if (path-helper/file-exists? keystore-path-expanded)
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