(ns flutter-kit.errors
  (:require [clojure.string :as string]))

(defn error-msg [errors]
  (str "🚫 The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))
