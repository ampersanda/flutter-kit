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

(defn add-signing-configs [gradle-filepath]
  (let [rgx-buildTypes #"buildTypes\s*\{\s*release\s*\{"
        rgx-signingConfig #"signingConfigs\s*\{\s*release\s*\{"
        str-buildTypes (str "signingConfigs {" "\n"
                            "    release {" "\n"
                            "        keyAlias keystoreProperties['keyAlias']" "\n"
                            "        keyPassword keystoreProperties['keyPassword']" "\n"
                            "        storeFile file(keystoreProperties['storeFile'])" "\n"
                            "        storePassword keystoreProperties['storePassword']" "\n"
                            "    }" "\n"
                            "}" "\n"
                            "buildTypes {" "\n"
                            "    release {")
        gradle (slurp gradle-filepath)
        signingConfig-finder (re-find (re-matcher rgx-signingConfig gradle))
        ]
    (if (nil? signingConfig-finder)
      ;; when signingConfig not found
      ;; add signingConfig
      (path-helper/write-file gradle-filepath (clojure.string/replace-first gradle rgx-buildTypes str-buildTypes))
      ;; when signingConfig found
      (println "app/build.gradle already configured. Skipping..")
      )
    ))

(defn configure-signing [gradle-filepath]
  (let [rgx-release #"signingConfig\ssigningConfigs.release"
        str-release "signingConfig signingConfigs.release"
        rgx-debug #"signingConfig\ssigningConfigs.debug"
        gradle (slurp gradle-filepath)

        release-finder (re-find (re-matcher rgx-release gradle))]
    (if (nil? release-finder)
      ;; when it's not release
      (let [debug-finder (re-find (re-matcher rgx-debug gradle))]
        (if (not (nil? debug-finder))
          ;; nil
          (path-helper/write-file gradle-filepath (clojure.string/replace-first gradle rgx-debug str-release))))
      ;; when it's already release
      (add-signing-configs gradle-filepath))
    )
  )

(defn sign-gradle []
  "Sign gradle"
  (let [gradle-filepath "android/app/build.gradle"
        old-gradle (slurp gradle-filepath)
        rgx-keyprops #"def\s*keystorePropertiesFile\s*=\s*rootProject.file\(\"key\.properties\"\)\ndef\skeystoreProperties\s*=\s*new\sProperties\(\)\nkeystoreProperties\.load\(new FileInputStream\(keystorePropertiesFile\)\)\n\nandroid\s*\{"
        keyprops (str "def keystorePropertiesFile = rootProject.file(\"key.properties\")" "\n"
                      "def keystoreProperties = new Properties()" "\n"
                      "keystoreProperties.load(new FileInputStream(keystorePropertiesFile))" "\n"
                      "\n"
                      "android {")
        keyprops-finder (re-find (re-matcher rgx-keyprops old-gradle))]
    ;; check if gradle already has addition properties
    (if (nil? keyprops-finder)
      ;; when keyprops is not there yet
      (do
        ;; backup
        (println "Backing up build.gradle (build.gradle.bak) ..")
        (path-helper/write-file (str gradle-filepath ".bak") old-gradle)
        ;; create new file
        (println "Create new build.gradle ..")
        (path-helper/write-file gradle-filepath (clojure.string/replace-first old-gradle #"android ?\{" keyprops))

        ;; change signing config from debug to release
        (configure-signing gradle-filepath))
      ;; else
      ;; change signing config from debug to release
      (configure-signing gradle-filepath))))

(defn create-key-properties [keystore-path keystore-password alias alias-password filename]
  (path-helper/write-file filename
                          (str "storePassword=" keystore-password "\nkeyPassword=" alias-password "\nkeyAlias=" alias "\nstoreFile=" keystore-path)))

(defn process-keystore! [keystore-path]
  (create-key-properties keystore-path (input-keystore-password) (input-alias) (input-alias-password) "android/key.properties")
  (sign-gradle))

(defn ask-for-keystore [keystore-path]
  (if (nil? keystore-path)
    (println "Enter where the keystore is! (ex : ~/development/flutter/keystore.jks)"))

  (let [keystore-path-ask (if (nil? keystore-path) (read-line) keystore-path)
        keystore-path-expanded (path-helper/expand-home keystore-path-ask)]

    (if (nil? keystore-path) (println))

    ;; check keystore file
    (if (path-helper/file-exists? keystore-path-expanded)
      ;; when file exists
      (let [file-extension (peek (fs/split-ext keystore-path-expanded))
            is-jks? (= file-extension ".jks")]

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