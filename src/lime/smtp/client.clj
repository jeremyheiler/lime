(ns lime.smtp.client
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:refer-clojure :exclude [send read-line]))

;; TODO Consider whether or not throwing an exception on an invalid
;; command parameter is a good idea. How could it make use of the map?

(defn ^:private ehlo-command
  ([] "EHLO") ;; Prefer specifying a domain or address literal.
  ([host]
     (if host
       (str "EHLO " host)
       (ehlo-command))))

(defn ^:private helo-command
  ([] "HELO") ;; Prefer specifying a domain.
  ([host]
     (if host
       (str "HELO " host)
       (helo-command))))

;; TODO Allow compiling complex parameters as per 4.1.2 in RFC 5321.

(defn ^:private mail-command
  ([] "MAIL FROM:<>") ;; Must provide a nil reverse-path to use parameters.
  ([reverse-path]
     (str "MAIL FROM:<" (or reverse-path "") ">"))
  ([reverse-path & parameters]
     (string/join " " (cons (mail-command reverse-path) parameters))))

(defn ^:private rcpt-command
  ([forward-path]
     (if forward-path
       (str "RCPT TO:<" forward-path ">")
       (throw (ex-info "RCPT command requires the recipient's address" {}))))
  ([forward-path & parameters]
     (string/join " " (cons (rcpt-command forward-path) parameters))))

(defn ^:private data-command
  []
  "DATA")

(defn ^:private rset-command
  []
  "RSET")

(defn ^:private vrfy-command ;; TODO Provide as an extension?
  [query]
  (if query
    (str "VRFY " query)
    (throw (ex-info "VRFY requires a query argument" {}))))

(defn ^:private help-command
  ([] "HELP")
  ([query]
     (if query
       (str "HELP " query)
       (help-command))))

(defn ^:private expn-command ;; TODO Provide as an extension?
  [mailing-list]
  (if mailing-list
    (str "EXPN " mailing-list)
    (throw (ex-info "EXPN requires a mailing list argument" {}))))

(defn ^:private noop-command
  ([] "NOOP")
  ([parameter]
     (if parameter
       (str "NOOP " parameter)
       (noop-command))))

(defn ^:private quit-command
  []
  "QUIT")

(defn ^:private connect-event
  [client [server-host server-port]]
  (assoc client :socket (.Socket. server-host server-port)))

(defn ^:private connect-reply
  [client]
  ;; TODO THIS.
  )

;; TODO Do I have to worry about flusing?

(defn ^:private command-event
  [client [command-key & command-args]]
  (let [command-fn (get-in client [:commands command-key])
        command (apply command command-args)]
    (doto (.getOutputStream (:socket client))
      (.write (.getBytes command))
      (.write (.getBytes "\r\n")))
    client))

(defn ^:private command-reply
  [client [command-key & _]]
  (let [{:keys [code text]} (parse-reply (read-reply (:socket client)))
        reply-handler (get-in client [:command-replies command-key code])]
    (if reply-handler
      (reply-handler client)
      (assoc client :quit true))))

;; TODO Escape lines in the message that start with a period.

;; TODO Ensure that the message ends with \r\n so it doesn't have to
;; be written before the final period.

(defn ^:private message-event
  [client [message]]
  (doto (.getOutputStream (:socket client))
    (.write (.getBytes message))
    (.write (.getBytes "\r\n.\r\n")))
  client)

(defn ^:private message-reply
  [client]
  ;; TODO THIS.
  )

(defn ^:private dispatch-event ;; FIX This needs a better name...
  [[event-type & event-args]]
  (fn [client]
    (let [handler (get-in client [:events event-type :handler])]
      (handler client event-args))))

(defn ^:private dispatch-reply ;; FIX This needs a better name, too...
  [event-type]
  (fn [client]
    (let [replier (get-in client [:events event-type :replies])]
      (replier client))))

;; TODO Have a "flow" function which determines event and reply
;; sequences. The default "flow" will alternate between them. The
;; pipelining extension will be able to provide its own "flow".

(defn basic-smtp
  "Returns a map which configures a basic SMTP client."
  []
  {:events {:CONNECT {:handler connect-event :replier connect-reply}
            :COMMAND {:handler command-event :replier command-reply}
            :MESSAGE {:handler message-event :replier message-reply}}
   :commands {:EHLO ehlo-command
              :HELO helo-command
              :MAIL mail-command
              :RCPT rcpt-command
              :DATA data-command
              :RSET rset-command
              :VRFY vrfy-command
              :EXPN expn-command
              :HELP help-command
              :NOOP noop-command
              :QUIT quit-command}
   :command-replies {:EHLO {}
                     :HELO {}
                     :MAIL {}
                     :RCPT {}
                     :DATA {}
                     :RSET {}
                     :VRFY {}
                     :EXPN {}
                     :HELP {}
                     :NOOP {}
                     :QUIT {}}})

;; TODO Wut iz dis send? omfg...

(comment (defn send
           "The batteries included function for sending mail."
           [session message]
           (let [f (-> session smtp/base-smtp starttls/with-starttls auth/with-auth)]
             (f message))))

;; TODO DO REPLIES GOOD.

(defn ^:private last-line?
  [line]
  (= \space (nth line 3)))

(defn ^:private read-reply
  [socket]
  ;; TODO Not sure about using a BufferedReader like this...
  (let [reader (io/reader (.getInputStream socket))]
    (loop [reply []]
      (let [line (.readLine reader) reply (conj reply line)]
        (if (last-line? line)
          (seq reply)
          (recur reply))))))

(defn ^:private parse-reply
  [reply-seq]
  (loop [[head & tail] reply-seq
         reply {:code (Integer/parseInt (subs head 0 3)) :text []}]
    (if head
      (update-in reply [:text] conj (subs head 4))
      (recur tail (update-in reply [:text] conj (subs head 4))))))

(defn reply
  [command-key]
  (fn [client]
    ))
