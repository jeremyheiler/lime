(ns lime.smtp.client.pipelining)

(defn ^:private defer-reply
  [session reply-fn]
  (update-in session [:reply-queue] conj reply-fn))

(defn with-pipelining
  [client]
  (fn [session]
    (let []
      (-> session
          (update-in [:pipelined-commands] concat [:RSET :MAIL :RCPT])
          (update-in [:reply-hooks] conj defer-reply)
          client))))
