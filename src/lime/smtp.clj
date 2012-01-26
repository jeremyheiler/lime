(ns lime.smtp
  "Low-level functions for communicating with an SMTP server."
  (:require [lime.net :as net]))

(defn last-line?
  "Returns true if the line provided signals the last line of an SMTP reply.
  The last line, which may also be the first, is the line that has a space
  character in the 3rd index position. All other lines will have a dash."
  [line]
  (= \space (nth line 3)))

(defn write-command 
  "Writes the given SMTP command to the provided writer. No effort is made
  to ensure the SMTP command is valid. Returns the provided writer."
  [writer command]
  (doto writer
    (.write command)
    (.flush)))

(defn read-reply
  "Reads an SMTP reply from the reader and returns a sequence containing
  each line from the reply. There will be at least one line in the sequence."
  [reader]
  (loop [reply []]
    (let [line (.readLine reader)]
      (if (last-line? line)
        (seq (conj reply line))
        (recur (conj reply line))))))

(defn submit
  "Submits the given command within the context of the session and returns
  a reply which is a sequence of lines."
  [session command]
  (write-command (:writer session) command)
  (read-reply (:reader session)))

(defn connect
  "Creates a mail session by opening a socket to the SMTP server
  on the given host and port."
  [host port]
  (let [socket (net/open-socket host port)
        reader (net/socket-reader socket)
        writer (net/socket-writer socket)]
    {:host host
     :port port
     :reader reader
     :writer writer
     :socket socket
     :initial-reply (read-reply reader)}))

;(defn parse-reply
;  "Breaks a response into a map containing the :code and :message."
;  [res]
;  (let [r (string/split res #" " 2)]
;    {:code (nth r 0) :message (nth r 1)}))

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

