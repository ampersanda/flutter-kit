(ns flutter-kit.unsign
  (:require [flutter-kit.errors :refer [error-msg]]))

(defn- unsign-with-backup []
  (println "▶️ Unsigning...")
  (println "✅️ Done."))

(defn- unsign-deleting-regex []
  (println "⚠️ No backup files found. Try unsign...")
  (println "✅️ Done."))

(defn unsign []
  ;; TODO : restore backup files
  ;; TODO : delete "spitted" files
  (cond
    true (unsign-with-backup)
    true (unsign-deleting-regex)
    :else (println "🚫 Unsign failed.")))
