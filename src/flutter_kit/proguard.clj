(ns flutter-kit.proguard
  (:require [flutter-kit.path :refer [write-file file-exists?]]))

(defn- configure-proguard []
  (println "▶️ Configuring proguard...")

  (let [proguard-filepath "android/app/proguard-rules.pro"
        str-proguard      (str "#Flutter Wrapper" "\n"
                               "-keep class io.flutter.app.** { *; }" "\n"
                               "-keep class io.flutter.plugin.**  { *; }" "\n"
                               "-keep class io.flutter.util.**  { *; }" "\n"
                               "-keep class io.flutter.view.**  { *; }" "\n"
                               "-keep class io.flutter.**  { *; }" "\n"
                               "-keep class io.flutter.plugins.**  { *; }")]
    (if (file-exists? proguard-filepath)
      ;; exists
      (write-file (str proguard-filepath ".bak") (slurp proguard-filepath))
      ;; else
      (write-file proguard-filepath str-proguard)))

  (println "✅️ Done."))

(defn- enable-obfuscation-and-or-minification []
  (println "▶️ Enabling obfuscation and minification...")
  (let [app-gradle-path   "android/app/build.gradle"
        gradle            (slurp app-gradle-path)

        rgx-proguard      #"useProguard\s*true"

        rgx-signingConfig #"signingConfig\s*signingConfigs\.(release|debug)"
        signinConfigMode  (second (re-find (re-matcher rgx-signingConfig gradle)))

        str-obfuscation   (str "signingConfig signingConfigs." signinConfigMode "\n\n"
                               "            minifyEnabled true" "\n"
                               "            useProguard true" "\n\n"
                               "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'")
        proguard-finder   (re-find (re-matcher rgx-proguard gradle))]

    (if (nil? proguard-finder)
      (write-file app-gradle-path
                  (clojure.string/replace-first gradle rgx-signingConfig str-obfuscation)))

    (println "✅️ Done.")))

(defn proguard []
  "install proguard"
  (configure-proguard)
  (enable-obfuscation-and-or-minification))

(defn remove-proguard []
  "remove proguard for unsigning")
