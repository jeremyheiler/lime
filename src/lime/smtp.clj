(ns lime.smtp
  "Low-level functions for communicating with an SMTP server."
  (:require [lime.io :as io]
            [lime.net :as net]))

(defn last-line?
  "Returns true if the given line identifies itself as the last line of a reply.
  The last line, which may also be the first, is the line that has a space
  character in the 3rd index position. All other lines will have a hyphen."
  [line]
  (= \space (nth line 3)))

(defn maybe-prepend-dot
  "For transparency, lines in a message that begin with a dot need to have
  another dot prepended to them. These will later be stripped after the
  messages has been transported. See section 4.5.2 in the RFC."
  [line]
  (if (= \. (first line))
    (str \. line)
    line))

(defn transparentize
  "The client needs to ensure the end-of-mail sequence doesn't appear in
  message data while it's being transported."
  [message]
  (map maybe-prepend-dot (line-seq (io/string-reader message))))

(defn read-reply
  "Reads an SMTP reply from the reader and returns a sequence containing
  each line from the reply. There will be at least one line in the sequence."
  [reader]
  (loop [reply []]
    (let [line (.readLine reader)]
      (if (last-line? line)
        (seq (conj reply line))
        (recur (conj reply line))))))

(defn parse-reply
  ""
  [reply-seq]
  (loop [[head & tail] reply-seq reply {:code (subs head 0 3) :text []}]
    (if (nil? head)
      reply
      (recur tail (update-in reply [:text] #(conj % (subs head 4)))))))

(defn send-command
  "Sends the given command to the server and returns the reply."
  [session command] ; & {:keys [expect]}]
  (let [reader (:reader session) writer (:writer session)]
    (io/write+flush writer command "\r\n")
    (parse-reply (read-reply reader))))

(defn write-message
  [writer message]
  (doseq [line message] (io/write writer line "\r\n"))
  (io/write+flush writer "\r\n.\r\n"))

(defn send-message
  ""
  [session message]
  (let [reader (:reader session) writer (:writer session)]
    (write-message writer (transparentize message))
    (parse-reply (read-reply reader))))

(defn connect
  "Connects to an SMTP server on the given host and port. Returns
  a session map that facilitates interaction with the server."
  [host port & {:keys [ssl tls]}]
  (let [socket (cond
                 ssl (throw (UnsupportedOperationException. "SSL not yet supported."))
                 tls (throw (UnsupportedOperationException. "TLS not yet supported."))
                 :else (net/open-socket host port))
        session {:host host
                 :port port
                 :reader (net/socket-reader socket)
                 :writer (net/socket-writer socket)
                 :socket socket}]
    (assoc session :opening-reply (parse-reply (read-reply (:reader session))))))
    

;(defn submit-all
;  "Opens a session with the server and sends the sequence of commands one
;   by one until all commands are processed or an irrecoverable error occurs."
;  [session commands])

;(defn with-session
;  ([host port]
;    (with-session (connect host port)))
;  ([session]
;    (doto session
;      (send-command! (HELO host))
;      (send-command! (QUIT)))))

