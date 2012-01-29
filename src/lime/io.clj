(ns lime.io
  "Functions that help facilitate IO."
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

