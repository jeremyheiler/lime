(ns lime.test.core
  (:use [lime.core])
  (:use [clojure.test]))

(deftest simple
  (is (thrown? UnsupportedOperationException (smtp-send {}))))

