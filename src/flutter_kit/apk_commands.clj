(ns flutter-kit.apk-commands
  (:require [clojure.java.shell :as shell]))

(defn build []
  (let [build-script (shell/sh "flutter" "build" "apk")]
    (println (:err build-script))
    (println (:out build-script))
    (System/exit (:exit build-script))))
