(ns lime.smtp
  "Low-level functions for communicating with an SMTP server."
  (:require [lime.net :as net])
  (:require [clojure.java.io :as io :only [reader writer]])
  (:import [java.io StringReader]))

(defn write
  "Writes the given strings"
  [writer & strings]
  (.write writer (apply str strings)))

(defn write+flush
  "Writes the given strings and flushes the buffer."
  [writer & strings]
  (.write writer (apply str strings))
  (.flush writer))

(defn string-reader
  "Wraps the given string in a StringReader."
  [string]
  (io/reader (StringReader. string)))

(defn get-line
  ""
  [reader]
  (.readLine reader))

(defn put-line
  ""
  [writer line]
  (.write writer line)
  (.write writer "\r\n")
  (.flush writer))

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
  (map maybe-prepend-dot (line-seq (string-reader message))))

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
  ""
  [session command]
  (put-line (:writer session) command)
  (parse-reply (read-reply (:reader session))))

(defn send-message
  ""
  [session message]
  ; split msg by line
  (doseq [line (transparentize message)]
    (put-line (:writer session) line))
  (put-line (:writer session) "\r\n."))

(defn new-session
  ""
  [host port socket]
  (let [reader (net/socket-reader socket)
        writer (net/socket-writer socket)]
    {:host host
     :port port
     :socket socket
     :reader reader
     :writer writer}))

(defn connect
  "Connects to an SMTP server on the given host and port. Returns
  a session map that facilitates interaction with the server."
  [host port]
  (new-session host port (net/open-socket host port)))
;  (let [session (new-session host port (net/open-socket host port))]
;    (update-in session [:log] #(conj % (parse-reply (read-reply (:channel session)))))))



;(defn issue
;  "Issues the arg to the server. It is up to the caller to ensure the arg is
;  a valid SMTP command or message, and is terminated properly. The reply from
;  the server is parsed into a map and returned."
;  [session arg]
;  (let [{:keys [channel log]} session]
;    (cond
;      (string? arg)
;        (do
;          (put-line channel arg)
;          (log arg))
;      (seq? arg)
;        (do
;          (doseq [line arg] (put-line channel line))
;          (log arg))
;      :else
;        (IllegalArgumentException. "arg is not a string or a seq"))
;    (parse-reply (read-reply channel))))

