(ns lime.smtp.client.pipelining)

(defn with-pipelining
  [client]
  (fn [session]
    (let []
      (-> session
          (assoc :pipelined-commands #{:RSET :MAIL :RCPT})
          client
          ))))