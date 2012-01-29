(ns lime.test.smtp
  (:use [lime.smtp] [lime.smtp.commands] [clojure.test]))

(deftest test-last-line?
  (is (last-line? "000 This is a last line"))
  (is (not (last-line? "000-This is not a last line"))))

(deftest test-HELO
  (is (= "HELO domain" (HELO "domain"))))

(deftest test-EHLO
  (is (= "EHLO domain" (EHLO "domain"))))

(deftest test-MAIL
  (is (= "MAIL FROM:<sophie@example.com>" (MAIL "sophie@example.com"))))

(deftest test-RCPT
  (is (= "RCPT TO:<sophie@example.com>" (RCPT "sophie@example.com"))))

(deftest test-DATA
  (is (= "DATA" (DATA))))

(deftest test-RSET
  (is (= "RSET" (RSET))))

(deftest test-VRFY
  (is (= "VRFY sophie@example.com" (VRFY "sophie@example.com")))
  (is (= "VRFY Sophie <sophie@example.com>" (VRFY "sophie@example.com" "Sophie"))))

(deftest test-EXPN
  (is (= "EXPN sophie-news" (EXPN "sophie-news"))))

(deftest test-HELP
  (is (= "HELP" (HELP)))
  (is (= "HELP RSET" (HELP "RSET"))))

(deftest test-NOOP
  (is (= "NOOP" (NOOP)))
  (is (= "NOOP RSET" (NOOP "RSET"))))

(deftest test-QUIT
  (is (= "QUIT" (QUIT))))

