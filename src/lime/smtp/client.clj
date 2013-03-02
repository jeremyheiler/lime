(ns lime.smtp.client
  (:require [lime.smtp :as smtp]
            [lime.smtp.starttls :as starttls])
  (:refer-clojure :exclude [send]))

(defn send
  "The batteries included function for sending mail."
  [session  message]
  (let [f (-> sessionxo smtp/base-smtp starttls/with-starttls smtp/with-auth)]
    (f message)))
