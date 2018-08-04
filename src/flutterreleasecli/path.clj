(ns flutterreleasecli.path
  (:require [clojure.java.io :as io]))

(defn expand-home [s]
  (if (.startsWith s "~")
    (clojure.string/replace-first s "~" (System/getProperty "user.home"))
    s))

(defn write-file [filename spitted-string]
  (io/make-parents filename)
  (spit filename spitted-string))