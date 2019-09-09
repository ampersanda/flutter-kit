(ns flutter-kit.proguard
  (:require [flutter-kit.path :refer [write-file]]
            [me.raynes.fs :refer [exists?]]
            [flutter-kit.androidx :as androidx]
            [flutter-kit.path :refer [app-gradle-path]]))

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
    (if (exists? proguard-filepath)
      (write-file (str proguard-filepath ".bak") (slurp proguard-filepath))
      (write-file proguard-filepath str-proguard)))

  (println "✅️ Done."))

(defn- enable-obfuscation-and-or-minification [use-androidx?]
  (println "▶️ Enabling obfuscation and minification...")
  (let [gradle                                  (slurp app-gradle-path)

        rgx-proguard                            #"useProguard\s*true"

        rgx-signingConfig                       #"signingConfig\s*signingConfigs\.(\w+)"
        signinConfigMode                        (second (re-find (re-matcher rgx-signingConfig gradle)))

        str-obfuscation                         (str "signingConfig signingConfigs." signinConfigMode "\n\n"
                                                     "            minifyEnabled true" "\n"
                                                     "            useProguard true" "\n\n"
                                                     "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'")
        has-proguard?                           (re-find (re-matcher rgx-proguard gradle))]

    ;;; FIXME
;    (println (clojure.string/replace gradle #"[\/]{2,}.*" ""))
    ;;; FIXME - EOL

    (if (nil? has-proguard?)
      (do
        (write-file app-gradle-path
                    (clojure.string/replace-first gradle rgx-signingConfig str-obfuscation))
        (println "✅️ Done."))
      (println "❇️️ Obfuscation and minification already enabled. Skipping.."))))

(defn install! [use-androidx?]
  "install proguard"
  (configure-proguard)
  (enable-obfuscation-and-or-minification use-androidx?))

(defn uninstall! []
  "remove proguard for unsigning")
