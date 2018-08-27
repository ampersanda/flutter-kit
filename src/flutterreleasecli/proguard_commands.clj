(ns flutterreleasecli.proguard-commands
  (:use [flutterreleasecli.path]))

(defn configure-proguard []
  (let [proguard-filepath "android/app/proguard-rules.pro"
        str-proguard (str "#Flutter Wrapper" "\n"
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
      (write-file proguard-filepath str-proguard))))

(defn enable-obfuscation-and-or-minification []
  (let [rgx-proguard #"useProguard\s*true"
        rgx-signingConfig #"signingConfig\s*signingConfigs\.release"
        str-obfuscation (str "signingConfig signingConfigs.release" "\n\n"
                             "            minifyEnabled true" "\n"
                             "            useProguard true" "\n\n"
                             "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'")
        app-gradle-path "android/app/build.gradle"
        gradle (slurp app-gradle-path)
        proguard-finder (re-find (re-matcher rgx-proguard gradle))]
    (if (nil? proguard-finder)
      (write-file app-gradle-path (clojure.string/replace-first gradle rgx-signingConfig str-obfuscation)))))