(ns flutter-kit.keystore
  (:require [flutter-kit.path :refer [expand-home]]
            [me.raynes.fs :refer [split-ext]]))

(defn- create-key-properties [])

(defn- set-release-mode [])

(defn- copy-jks [])

(defn- keystore? [path]
  (let [keystore-path-expanded (expand-home path)
        file-extension         (peek (split-ext keystore-path-expanded))]
    (or (= file-extension ".jks") (= file-extension ".keystore"))))

(defn install! [path keystore-password alias alias-password]
  "install keystore")
