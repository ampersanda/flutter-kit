(ns flutterreleasecli.cli_helper)

(defn get-error! [arguments]
  (arguments :errors))

(defn get-summary! [arguments]
  (arguments :summary))

(defn get-options! [arguments]
  (arguments :options))

(defn help! [arguments]
  (println (arguments :summary)))