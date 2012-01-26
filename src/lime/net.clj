(ns lime.net
  (:require [clojure.java.io :as io])
  (:import [java.net Socket]))

(defn open-socket
  "Opens and returns a client socket."
  [host port]
  (Socket. host port))

(defn socket-reader
  "Wraps the socket's input stream with a BufferedReader."
  [socket]
  (io/reader (.getInputStream socket)))

(defn socket-writer
  "Wraps the socket's output stream with a BufferedWriter."
  [socket]
  (io/writer (.getOutputStream socket)))

