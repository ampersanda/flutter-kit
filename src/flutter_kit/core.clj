(ns flutter-kit.core
  (:use [flutter-kit.cli_helper]
        [flutter-kit.path :only [file-exists?]]
        [flutter-kit.keystore_commands :only [ask-for-keystore]])
  (:require [clojure.tools.cli :refer [parse-opts cli]]
            [flutter-kit.proguard :as proguard!]
            [flutter-kit.apk-commands :as apk]
            [clojure.string :as string]
            [flutter-kit.keystore :as keystore!]
            [flutter-kit.errors :refer [error-msg]]
            [flutter-kit.unsign :refer [unsign]]
            [flutter-kit.validation :refer [is-flutter?]])

  (:gen-class))

(defn run!? [arguments]
  (let [keystore-path (:keystore-path (arguments :options))
        proguard      (:proguard (get-options! arguments))
        apk           (:apk (get-options! arguments))]
    (if (nil? keystore-path)
      ;; when keystore param is nil
      (ask-for-keystore nil)
      ;; when keystore param is available
      (ask-for-keystore (:keystore-path (get-options! arguments))))

    (if apk
      (do (println "Building APK..")
        (apk/build)))))

(def cli-options
  [["-h" "--help"]
   [nil "--unsign" "Delete project signing"]
   [nil "--keystore-path=PATH" "Path to keystore/jks file"]
   [nil "--keystore-password=PASSWORD" "Keystore password"]
   [nil "--keystore-alias=ALIAS_NAME" "Keystore alias name"]
   [nil "--keystore-alias-password=ALIAS_PASSWORD" "Keystore alias password"]
   ["-p" "--proguard" "Enable Proguard"
    :default
    false]])

(defn usage [options-summary]
  (->>
   ["Easy android flutter signing."
    "example: flutter-android-signing --keystore-path=\"~/keystore.jks\" --proguard"
    ""
    "Usage: flutter-android-signing [options]"
    ""
    "Options:"
    options-summary]
   (string/join \newline)))

(defn validate-args [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]

    (cond
      (:help options)                       {:message (usage summary)}
      errors                                {:message (error-msg errors)}

      (:unsign options)                     {:action unsign}
      (or (> (count arguments) 0) options)  {:options options :arguments arguments}
      :else                                 {:message (usage summary)})))


(defn -main [& args]
  ;  (if-not (is-flutter?)
  ;    (println "Not a valid Flutter Android project ☹️")
  ;    )
  (let [{:keys [message options arguments action]} (validate-args args)]


    (println options)
    (println arguments)

    (cond
      message (println message)
      action  (action)

      :else
      (let [{:keys [keystore-path
                    keystore-password
                    keystore-alias
                    keystore-alias-password
                    proguard]} options]
        (when
          proguard (proguard!/install!))

        (when
          (or keystore-path keystore-password keystore-alias keystore-alias-password)
          (cond
            (not keystore-path)           (println "🚫 Keystore path is required for signing")
            (not keystore-password)       (println "🚫 Keystore password is required for signing")
            (not keystore-alias)          (println "🚫 Keystore alias is required for signing")
            (not keystore-alias-password) (println "🚫 Keystore alias password is required for signing")

            :else                         (keystore!/install! keystore-path keystore-password keystore-alias keystore-alias-password)))))))

;  (let [arguments  (parse-opts args cli-options)]
;    (if (nil? (get-error! arguments))
;      ;; error is nil - no error
;      (if (is-flutter?)
;        ;; when valid flutter
;        (run!? arguments)
;        ;; else
;        (println "Not a valid Flutter Android project :("))
;      ;; when error
;      (help! arguments))))
