(ns lime.test.core
  (:use [lime.core])
  (:use [clojure.test]))

(deftest simple
  (is (smtp-send
        {:to "to@example.com"
         :from "from@example.com"})))

