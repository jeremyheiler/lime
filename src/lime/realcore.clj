(ns lime.core
  (:require [clojure.string :as string])
  (:import
    (java.util Date Properties)
    (javax.mail Message Message$RecipientType MessagingException PasswordAuthentication Session Transport)
    (javax.mail.internet InternetAddress MimeMessage)))

(defn- mk-message [session message]
  (doto (MimeMessage. session)
    (.setFrom (InternetAddress. (:user message)))
    (.setRecipients (Message$RecipientType/TO) (InternetAddress/parse (string/join "," (:to message))))
    (.setReplyTo (:reply-to (string/join "," message)))
    (.setSendDate (:send-date message (Date.)))
    (.setSubject (:subject message))
    (.setText (:text message))))

(defn- mk-authenticator [msg]
  (PasswordAuthentication. (:user msg) (:password msg)))

(defn- mk-properties [mail]
  (doto (Properties.)
    (.put "mail.smtp.host" (:host mail))
    (.put "mail.smtp.port" (:post mail))
    (.put "mail.smtp.user" (:user mail))
    (.put "mail.smtp.socketFactory.port" (:port mail))
    (.put "mail.smtp.auth" "true")))

(defn- mk-session [mail]
  (Session/getInstance (mk-properties mail) (mk-authenticator mail)))

(defn- convert-message [mail]
  (mk-message (mk-session mail) mail))

(defn smtp-send [mail]
  (try
    (do (Transport/send (convert-message mail)) true)
    (catch MessagingException e false)))

(defn with-auth
  "Uses the given user and password for authentication unless the
   message map provides its own credentials."
  [message user password]
  (doto message
    (conj ["mail.smtp.user" (:user message user)])
    (conj ["mail.smtp.password" (:password message password)])
    (conj ["mail.smtp.auth" "true"])))

(defn with-ssl
  "Provides the necessary configuration to use TLS/SSL."
  [message]
  (doto message
    (conj ["mail.smtp.starttls.enable" "true"])
    (conj ["mail.smtp.socketFactory.class" "javax.net.ssl.SSLSocketFactory"])
    (conj ["mail.smtp.socketFactory.fallback" "false"])))

(defn send-email [message]
  (-> message (with-ssl) (with-auth "me" "secret") (smtp-send)))

