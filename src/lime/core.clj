(ns lime.core
  (:require [clojure.string :as string])
  (:import
    (java.util Date Properties)
    (javax.mail Message Message$RecipientType MessagingException PasswordAuthentication Session Transport)
    (javax.mail.internet InternetAddress MimeMessage)))

(defn- make-authenticator [{user :user pass :password} message]
  (if-not (and (nil? user) (nil? (pass)))
    (PasswordAuthentication. user pass)))

(defn- make-smtp-message [session message]
  (doto (MimeMessage. session)
    (.setFrom (InternetAddress. (:from message)))
    (.setRecipients (Message$RecipientType/TO) (InternetAddress/parse (string/join "," (:to message))))
    (.setSendDate (:send-date message (Date.)))
    (.setSubject (:subject message "(no subject)"))
    (.setText (:text message ""))))

(defn- make-properties [message]
  (doto (Properties.)
    (.put "mail.smtp.auth" "true")
    (.put "mail.smtp.host" (:host message "localhost"))
    (.put "mail.smtp.port" (:post message "25"))))

(defn- make-session [message]
  (if (:auth message)
    (Session/getDefaultInstance (make-properties message) (make-authenticator message))
    (Session/getDefaultInstance (make-properties message))))

(defn smtp-send
  "Send a message over SMTP."
  [message]
  (try
    (do
      (Transport/send (make-smtp-message (make-session message) message)) true)
  (catch MessagingException e false)))

(defn smtp-send [message callback]
  (try
    (Transport/send (make-smtp-message (make-session message) message))
  (catch Exception e
    (callback e))
  (finally
    (callback nil))))
