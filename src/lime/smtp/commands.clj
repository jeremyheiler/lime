(ns lime.smtp.commands
  "A group of functions that return complete SMTP commands. There is no requirement
  to actually use these, but they might make developing SMTP programs easier. Also,
  terminating sequences are supplied by the send-* functions and not these commands.")

(defn HELO
  "Returns a complete HELO command using the given host as the local domain. Use this
  command to introduce yourself to the SMTP server, but only if they do not accept EHLO"
  [host]
  (str "HELO " host))

(defn EHLO
  "Returns a complete EHLO command using the given host as the local domain. Use this
  command to introduce yourself to the SMTP server. Prefer this over HELO."
  [host]
  (str "EHLO " host))

(defn MAIL
  "Returns a complete MAIL command using the given address as the FROM parameter. This
  command initiates a mail transaction and must be followed by one or more RCPT
  commands and finally DATA."
  [address]
  (str "MAIL FROM:<" address ">"))

(defn RCPT
  "Returns a complete RCPT command using the given address as the TO parameter. This
  command allows you to specifiy the recipients of the message. It can only be sent
  after initiating a mail trasnation with MAIL."
  [address]
  (str "RCPT TO:<" address ">"))

(defn DATA
  "Returns a complete DATA command. This command signals that the client is ready
  to send the message to the server. Use use the lime.smtp/send-message function
  to accomplish that. Once the message is sent, the transaction will be complete
  and result in a successful transmission or an error."
  []
  "DATA")

(defn RSET
  "Returns a complete RSET command. The reset command will cancel the current
  mail transaction if sent before the DATA command."
  []
  "RSET")

(defn VRFY
  "Returns a complete VRFY command using the given address (and user) as its parameter.
  This command is used to verify that a user exists on the remote system."
  ([address]
    (str "VRFY " address))
  ([address user]
    (str "VRFY " user " <" address ">")))

(defn EXPN
  "Returns a complete EXPN command using the given user as its parameter. The
  expand command takes a user and determines if the user is a mailing list. If
  so, the members of the mailing list are returned."
  [user]
  (str "EXPN " user))

(defn HELP
  "Returns a complete HELP command, possibly with the given arg as its parameter.
  With out a parameter, help will list all the acceptable commands. With the parameter,
  help will return useful information if it can."
  ([]
    "HELP")
  ([arg]
    (str "HELP " arg)))

(defn NOOP
  "Returns a complete NOOP command. This command simply requests for a successful
  reply from the SMTP server. The optional parameter should be ignored by the server."
  ([]
    "NOOP")
  ([arg]
    (str "NOOP " arg)))

(defn QUIT
  "Returns a complete QUIT command. This command tells the server that the client is
  quitting. The client should wait for the serve to reply."
  []
  "QUIT")

