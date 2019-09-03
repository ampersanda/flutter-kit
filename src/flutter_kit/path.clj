(ns flutter-kit.path
  (:require [clojure.java.io :as io]))

(defn write-file [filename spitted-string]
  (io/make-parents filename)
  (spit filename spitted-string))
