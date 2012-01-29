(ns lime.net
  "Provides useful fucntions that wrap Java's networking API. The
  purpose of these functions is to simplify platform portability."
  (:require [clojure.java.io :as io :only [reader writer]])
  (:import [java.net Socket]))

(defn open-socket
  "Opens and returns a socket bound to the given port on the host."
  [host port]
  (Socket. host port))

(defn socket-reader
  "Returns a buffered reader that wraps the socket's input stream."
  [socket]
  (io/reader (.getInputStream socket)))

(defn socket-writer
  "Returns a buffered writer that wraps the socket's output stream."
  [socket]
  (io/writer (.getOutputStream socket)))

