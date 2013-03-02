(ns lime.smtp.starttls
  (:require [lime.smtp :as smtp])
  (:import [javax.net.ssl SSLSocketFactory]))

(defn ^:private layer-socket
  [socket host port]
  (.createSocket (SSLSocketFactory/getDefault) socket host port true))

(defn negotiate-tls
  [{:keys [server-host server-port] :as session}]
  (-> session
      (update-in [:socket] layer-socket server-host server-port)
      (smtp/setup-io)))

(defn with-starttls
  [client]
  (fn [session]
    (-> session
        (update-in [:command-fns] assoc :STARTTLS (constantly "STARTTLS"))
        (update-in [:reply-fns] assoc :STARTTLS negotiate-tls))))
