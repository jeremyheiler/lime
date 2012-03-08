(ns lime.smtp.starttls
  "Support for SMTP over TLS."
  (:require [lime.smtp :as smtp])
  (:import [javax.net.ssl SSLSocketFactory]))

(defn STARTTLS
  []
  "STARTTLS")

(defn negotiate-tls
  "Returns a session with a socket that uses TLS."
  [session]
  (smtp/new-session
    (:host session)
    (:port session)
    (.createSocket (SSLSocketFactory/getDefault) (:socket session) (:host session) (:port session) true)))

