(ns flutter-kit.keystore
  (:require [flutter-kit.path :refer [write-file]]
            [me.raynes.fs :refer [file? split-ext absolute base-name split-ext exists?]]
            [clojure.java.io :as io]))

(defn- add-signing-configs [gradle-filepath]
  (let [rgx-buildTypes       #"buildTypes\s*\{\s*release\s*\{"
        rgx-signingConfig    #"signingConfigs\s*\{\s*release\s*\{"
        str-buildTypes       (str "signingConfigs {" "\n"
                                  "    release {" "\n"
                                  "        keyAlias keystoreProperties['keyAlias']" "\n"
                                  "        keyPassword keystoreProperties['keyPassword']" "\n"
                                  "        storeFile file(keystoreProperties['storeFile'])" "\n"
                                  "        storePassword keystoreProperties['storePassword']" "\n"
                                  "    }" "\n"
                                  "}" "\n"
                                  "buildTypes {" "\n"
                                  "    release {")
        gradle               (slurp gradle-filepath)
        signingConfig-finder (re-find (re-matcher rgx-signingConfig gradle))]
    (if (nil? signingConfig-finder)
      ;; when signingConfig not found
      ;; add signingConfig
      (write-file gradle-filepath
                  (clojure.string/replace-first gradle rgx-buildTypes str-buildTypes))
      ;; when signingConfig found
      (println "❇️ app/build.gradle already configured. Skipping.."))))

(defn- configure-signing [gradle-filepath]
  (let [rgx-release    #"signingConfig\ssigningConfigs.release"
        str-release    "signingConfig signingConfigs.release"
        rgx-debug      #"signingConfig\ssigningConfigs.debug"
        gradle         (slurp gradle-filepath)

        release-finder (re-find (re-matcher rgx-release gradle))]
    (if (nil? release-finder)
      ;; when it's not release
      (let [debug-finder (re-find (re-matcher rgx-debug gradle))]
        (if (not (nil? debug-finder))
          ;; nil
          (write-file gradle-filepath (clojure.string/replace-first gradle rgx-debug str-release))))
      ;; when it's already release
      (add-signing-configs gradle-filepath))))

(defn- sign-gradle []
  "Sign gradle"
  (let [gradle-filepath "android/app/build.gradle"
        old-gradle      (slurp gradle-filepath)
        rgx-keyprops    #"def\s*keystorePropertiesFile\s*=\s*rootProject.file\(\"key\.properties\"\)\ndef\skeystoreProperties\s*=\s*new\sProperties\(\)\nkeystoreProperties\.load\(new FileInputStream\(keystorePropertiesFile\)\)\n\nandroid\s*\{"
        keyprops        (str "def keystorePropertiesFile = rootProject.file(\"key.properties\")" "\n"
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
        (println "🎁 Backing up build.gradle (build.gradle.bak) ..")
        (write-file (str gradle-filepath ".bak") old-gradle)
        ;; create new file
        (println "🔨 Create new build.gradle ..")
        (write-file gradle-filepath
                    (clojure.string/replace-first old-gradle #"android ?\{" keyprops))))
    (configure-signing gradle-filepath)
    (add-signing-configs gradle-filepath)))

(defn- set-release-mode [path]
  (sign-gradle))

(defn- keystore? [path]
  (let [file-extension         (last (split-ext path))]
    (and (file? path) (not (nil? (#{".jks" ".keystore"} file-extension))))))

(defn install! [path keystore-password alias alias-password]
  "install keystore"

  (cond
    (not (exists? path))   (println "🚫 Keystore file doesn't exists")
    (keystore? path) (do
                       ;; create file key props
                       (write-file "android/key.properties"
                                   (str "storePassword=" keystore-password "\nkeyPassword=" alias-password "\nkeyAlias=" alias "\nstoreFile=" (base-name path)))


                       (io/copy (io/file path) (io/file (str "android/app/" (base-name path))))
                       (set-release-mode path))

    :else            (do
                       (println "🚫 This file is not keystore")
                       (System/exit 0))))
