(ns lime.smtp
  (:require [clojure.algo.monads :as m]
            [clojure.java.io :as io])
  (:import [java.net Socket]))

;; TODO Allow flushing to be optional (pipelining)
(defn send-command
  [session command-str]
  (let [w (:writer session)]
    (.write w command-str)
    (.write w "\r\n")
    (.flush w)))

(defn ^:private last-line?
  "Returns true if the given line is the last line of a reply."
  [line]
  (= \space (nth line 3)))
 
(defn read-reply
  "Reads the reply and returns a sequence of reply lines."
  [session]
  (loop [reply []]
    (let [line (.readLine (:reader session))]
      (if (last-line? line)
        (seq (conj reply line))
        (recur (conj reply line))))))

(defn parse-reply
  "Parses a sequence of reply lines into a map with :code mapped to
  the reply code, and :text mapped to a vector containing the values
  from each line."
  [reply-seq]
  (loop [[head & tail] reply-seq reply {:code (Integer/parseInt (subs head 0 3)) :text []}]
    (if head
      (update-in reply [:text] conj (subs head 4))
      (recur tail (update-in reply [:text] conj (subs head 4))))))

(defn open-socket
  [host port]
  (fn [session]
    (let [socket (Socket. host port)]
      [socket (assoc session
                :socket socket
                :server-host host
                :server-port port)])))

(defn setup-io
  [{:keys [socket] :as session}]
  [true (assoc session
          :reader (io/reader (.getInputStream socket))
          :writer (io/writer (.getOutputStream socket)))])

(def command-fns
  {:HELO (fn
           ([] "HELO")
           ([client-host] (str "HELO " client-host)))
   :EHLO (fn
           ([] "EHLO")
           ([client-host] (str "EHLO " client-host)))
   :QUIT (fn [] "QUIT")})

(def reply-fns
  {})

(defn default-reply-fn
  [session {:keys [code] :as reply}]
  (if (or (<= code 199) (>= code 400))
    session
    session))

(defn obtain-command-fn
  [command-key]
  (fn [session]
    [(get command-fns command-key) session]))

;; TODO log command in the session
;; TODO allow extensions to disable flushing the writer
;; TODO use the maybe monad when the io fails?
;; TODO move command-fns into the session
(defn handle-command
  [command-fn command-args]
  (fn [session]
    (let [command-str (apply command-fn command-args)]
      (send-command session command-str)
      [command-str session])))

;; TODO log reply in the session
;; TODO allow extensions to defer reading the reply
;; TODO use the maybe monad when the io fails?
;; TODO move reply-fns into the session
(defn handle-reply
  [command-key]
  (fn [session]
    (let [{:keys [code] :as reply} (parse-reply (read-reply session))
          reply-fn (get-in reply-fns [command-key code] default-reply-fn)]
      [reply (reply-fn session reply)])))

(def script-m (m/state-t m/maybe-m))

(defn script
  [& actions]
  (m/with-monad script-m (m/m-seq actions)))

(defmacro script-let
  [bindings body]
  `(m/domonad script-m ~bindings ~body))

(defn connect
  [server-host server-port]
  (script-let
    [_ (open-socket server-host server-port)
     _ setup-io
     reply (handle-reply nil)]
    reply))

(defn command
  [command-key & [command-args]]
  (script-let
    [command-fn (obtain-command-fn command-key)
     command-str (handle-command command-fn command-args)
     reply (handle-reply command-key)]
    reply))

(comment
  (defn message
    []
    (script-let)))

(comment
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
  (map maybe-prepend-dot (line-seq (string-reader message)))))
