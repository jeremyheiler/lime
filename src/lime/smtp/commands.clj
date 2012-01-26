(ns lime.smtp.commands)

(defn HELO
  ""
  [host]
  (str "HELO " host "\r\n"))

(defn EHLO
  ""
  [host]
  (str "EHLO " host "\r\n"))

(defn MAIL
  ""
  [address]
  (str "MAIL FROM:<" address ">\r\n"))

(defn RCPT
  ""
  [address]
  (str "RCPT TO:<" address ">\r\n"))

(defn DATA
  ""
  [data]
  "DATA\r\n")

(defn RSET
  ""
  [session]
  "RSET\r\n")

(defn VRFY
  ""
  [user]
  (str "VRFY " user "\r\n"))

(defn EXPN
  ""
  [user]
  (str "EXPN " user "\r\n"))

(defn HELP
  ""
  ([]
    "HELP\r\n")
  ([arg]
    (str "HELP " arg "\r\n")))

(defn NOOP
  ""
  ([]
    "NOOP\r\n")
  ([arg]
    (str "NOOP " arg "\r\n")))

(defn QUIT
  ""
  []
  "QUIT\r\n")

