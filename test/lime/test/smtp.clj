(ns lime.test.smtp
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [lime.smtp :as smtp])
  (:import [java.io StringReader StringWriter]))

(defn open-socket
  [host port]
  (fn [session]
    [true (assoc session
            :server-host host
            :server-port port)]))

(defn setup-io
  [reader-text session]
  (let [sr (StringReader. reader-text) sw (StringWriter.)]
    [true (assoc session
            :string-reader sr
            :string-writer sw
            :reader (io/reader sr)
            :writer (io/writer sw))]))

(defmacro with-fake-server
  [reply-str & body]
  `(with-redefs
     [smtp/open-socket open-socket
      smtp/setup-io (partial setup-io ~reply-str)]
     ~@body))

(comment
  (deftest test-read-reply
    (let [test-pairs
          [[["123 a"] "123 server-host"]
           [["123:a" "456 a"] "123:a\r\n456 a"]
           [["123:a" "456 a"] "123:a\r\n456 a\r\n789 b"]
           [["123"] "123 "]]]
      (doseq [[expected input] test-pairs]
        (is expected (smtp/read-reply (connect input)))))
    (is (thrown? StringIndexOutOfBoundsException
                 (smtp/read-reply (connect "123"))))))

(deftest test-helo-ehlo-quit
  (with-fake-server "220 a\r\n250 b\r\n250 c\r\n221 d"
    (let [script (smtp/script
                   (smtp/connect "a" 587)
                   (smtp/command :HELO ["b"])
                   (smtp/command :EHLO ["c"])
                   (smtp/command :QUIT))
          [replies session] (script {})]
      (is (= 4 (count replies)))
      (is (= [220 250 250 221] (map :code replies)))
      (is (= ["a" "b" "c" "d"] (map (comp first :text) replies)))
      (is (= "HELO b\r\nEHLO c\r\nQUIT\r\n" (str (:string-writer session)))))))

(comment
  (deftest test-bad-reply
    (with-fake-server "600 a\r\n221 b"
      (let [[replies session] ((smtp/script (smtp/connect "b" 1)) {})]
        (is (= 2 (count replies)))
        (is (= [600 221] (map :code replies)))
        (is (= ["a" "b"] (map (comp first :text) replies)))
        (is (= "QUIT\r\n" (str (:string-writer session))))))))
