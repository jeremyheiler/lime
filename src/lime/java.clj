(ns lime.java
  (:import
    (java.util Date Properties)
    (javax.mail Message Message$RecipientType MessagingException PasswordAuthentication Session Transport)
    (javax.mail.internet InternetAddress MimeMessage)))

(defn- make-authenticator [{user :user pass :password} message]
  (if-not (and (nil? user) (nil? (pass)))
    (PasswordAuthentication. user pass)))

(defn- make-mime-message [session message]
  (doto (MimeMessage. session)
    (.setFrom (InternetAddress. (:user message)))
    (.setRecipients (Message$RecipientType/TO) (InternetAddress/parse (string/join "," (:to message))))
    (.setSendDate (:send-date message (Date.)))
    (.setSubject (:subject message))
    (.setText (:text message))))

(defn- make-properties [message]
  (doto (Properties.)
    (.put "mail.smtp.auth" "true")
    (.put "mail.smtp.host" (:host message "localhost"))
    (.put "mail.smtp.port" (:post message "25"))))

(defn- make-session [message]
  (if (:auth message)
    (Session/getDefaultInstance (make-properties message) (make-authenticator message))
    (Session/getDefaultInstance (make-properties message))))

(defn encode-message [message]
  (make-mime-message (make-session message) message))








