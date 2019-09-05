(ns flutter-kit.core
  (:require [clojure.tools.cli :refer [parse-opts cli]]
            [flutter-kit.proguard :as proguard!]
            [clojure.string :as string]
            [flutter-kit.keystore :as keystore!]
            [flutter-kit.errors :refer [error-msg]]
            [flutter-kit.unsign :refer [unsign]]
            [flutter-kit.validation :refer [is-flutter?]])

  (:gen-class))

(def cli-options
  [["-h" "--help"]
   ;   [nil "--unsign" "Delete project signing (not yet implemented, please help)"]
   [nil "--keystore-path=PATH" "Path to keystore/jks file"]
   [nil "--keystore-password=PASSWORD" "Keystore password"]
   [nil "--keystore-alias=ALIAS_NAME" "Keystore alias name"]
   [nil "--keystore-alias-password=ALIAS_PASSWORD" "Keystore alias password"]
   ["-p" "--proguard" "Enable Proguard"]
   ;   [nil "--appcompat" "Convert Flutter project to AppCompat"]
   ["-x" "--androidx" "Convert Flutter project to AndroidX"]])

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
  (if-not (is-flutter?)
    (do
      (println "🚫 This folder is not a valid Flutter Android project️"))
    (let [{:keys [message options arguments action]} (validate-args args)]
      (cond
        message (println message)
        action  (action)

        :else
        (let [{:keys [keystore-path
                      keystore-password
                      keystore-alias
                      keystore-alias-password
                      proguard
                      androidx]} options]

          (cond
            androidx
            (do
              (println "❗️ AndroidX migration needs proguard.")
              (proguard!/install! true))

            proguard
            (proguard!/install! androidx))

          (when
            (or keystore-path keystore-password keystore-alias keystore-alias-password)
            (cond
              (not keystore-path)           (println "🚫 Keystore path is required for signing")
              (not keystore-password)       (println "🚫 Keystore password is required for signing")
              (not keystore-alias)          (println "🚫 Keystore alias is required for signing")
              (not keystore-alias-password) (println "🚫 Keystore alias password is required for signing")

              :else                         (keystore!/install! keystore-path keystore-password keystore-alias keystore-alias-password))))))))
