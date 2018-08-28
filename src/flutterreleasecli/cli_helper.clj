(ns flutterreleasecli.cli_helper)

(defn get-error! [arguments]
  (arguments :errors))

(defn get-summary! [arguments]
  (arguments :summary))

(defn get-options! [arguments]
  (arguments :options))

(defn help! [arguments]
  (println (arguments :summary)))

(defn read-password [prompt]
  ;; Based on https://groups.google.com/forum/#!topic/clojure/ymDZj7T35x4
  (if (= "user" (str (.getName *ns*)))
    (do
      (print (format "%s [will be echoed to the screen]" prompt))
      (flush)
      (read-line))
    (let [console (System/console)
          chars   (.readPassword console "%s" (into-array [prompt]))]
      (apply str chars))))