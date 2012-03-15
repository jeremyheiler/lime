(ns lime.message
  "Create Internet messages as defined by RFC 5322."
  (:require [clojure.string :as s])
  (:import (java.text DecimalFormat)))

(defn format-unicode-char
  "Returns a string representing the unicode escape sequence for the given character."
  [c]
  (format "\\u%04X" (int c)))

(defn build-header-name [n]
  (if-let [n (nil? n)]
    (throw (IllegalArgumentException. "Header names cannot be nil."))
    (let [n (s/trim n)]
      (if (or (nil? n) (empty? n))
        (throw (IllegalArgumentException. "Header names cannot be empty or contain only whitespace."))
        (let [invalid (re-find #"[^\x21-\x39\x3B-\x7E]" n)]
        ;(let [invalid (re-find #"[\x00-\x20\x3A\x7F]" n)]
          (if invalid
            (throw (IllegalArgumentException. (str "The character " (format-unicode-char (nth invalid 0)) " is not allowed in a header name.")))
            n))))))

(defn build-header-body [b]
  (let [invalid (re-find #"[\x00-\x08\x0B\x0C\x0E-\x1F]" b)]
    (if invalid
      (throw (IllegalArgumentException. (str "The character " (format-unicode-char (nth invalid 0)) " is not allowed in a header body.")))
      b)))

(defn- build-message-headers [sb headers]
  (doseq [h headers]
    (.append sb (build-header-name (:name h)))
    (.append sb ": ")
    (.append sb (build-header-body (:body h "")))
    (.append sb "\r\n")))

(defn- build-message-body [sb body]
  (.append sb "\r\n")
  (.append sb body))

(defn build-message
  "Build a structured Internet message from a map containing header and body information."
  [message-map]
  (let [sb (StringBuilder.)]
    (build-message-headers sb (get message-map :headers []))
    (build-message-body sb (get message-map :body ""))
    (.toString sb)))




(defn whitespace?
  "Returns true if the provided character is a space or horizontal tab."
  [c]
  (or (= \space c) (= \tab c)))






;(defn break-line [line len]
;  (cond
;    (zero? len) [line]
;    (whitespace? (str (nth line len))) [(subs line 0 len) (subs line len)]
;    :else (recur line (dec len))))

;(defn hard-wrap
;  [text len]
;  (let [out (StringBuilder.) all (s/split-lines text)]
;    (loop [[line (first all) lines (rest all)]
;      (if (> len (count line))
;        (cond
;          (whitespace? (nth line len)) (do (.append out line 0 (dec len)) (recur (subs line len) lines))
;          (whitespace? (nth line (dec len))) (do (.append out line 0 (dec len)) (recur (subs line len) lines))
;          (

