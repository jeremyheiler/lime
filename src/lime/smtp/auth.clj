(ns lime.smtp.auth
  "Support for SMTP authentication."
  (:require [clojure.string :as string]
            [clojure.data.codec.base64 :as base64]))

(defn encode
  [input]
  (String. (base64/encode (.getBytes input))))

(defn AUTH
  ([mechanism] (str "AUTH " mechanism))
  ([mechanism arg] (string/join " " (str "AUTH " mechanism " " (encode arg)))))

(defn cancel
  []
  "*")

