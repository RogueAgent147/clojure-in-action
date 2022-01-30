(ns clojure-in-action.poly-with-multimethods
  (:import (java.io BufferedReader)))

;;MULTIMETHOD
;; without multimethod
;;Consider the situation where our expense-tracking service has become popular. We’ve
;started an affiliate program where we pay referrers if they get users to sign up for our
;service. Different affiliates have different fees.

;; mint.com and google.com
;; create a function that calculates the fee we pay to the affiliate.
;; affiliates a percentage of the annual salary the user makes.
;; We’ll pay Google 0.01%
;; we’ll pay Mint 0.03%
;; everyone else gets 0.02%

(defn fee-amount [percentage user]
  (float (* 0.01 percentage (:salary user))))

(defn affiliate-fee-cond [user]
  (cond
    (= :google.com (:referee user)) (fee-amount 0.01 user)
    (= :mint.com (:referee user)) (fee-amount 0.03 user)
    :default (fee-amount 0.02 user)))

 (defn school-fees [price student]
   (int (* 10 price (:salary student))))

(defn payment-price [student]
  (cond
    (= :computer (:course student)) (school-fees 10 student)
    (= :physics (:coursr student)) (school-fees 15 student)
    :default (school-fees 5 student)))

;; The trouble with this way of writing this function is that it’s painful to add new rules
;; about affiliates and percentages.

;; USING-MULTIMETHOD
;; (defmulti name dispatch-fn & option )
;; dispatch-fn function is a regular Clojure function that accepts the same
;; arguments that are passed in when the multimethod is called.

;; (defmethod multifn dispatch-value & fn-tail)

(def user-1 {:login "rob" :referrer :mint.com :salary 100000})
(def user-2 {:login "kyle" :referrer :google.com :salary 90000})
(def user-3 {:login "celeste" :referrer :yahoo.com :salary 70000})

(defmulti affiliate-fee :referee :default :else)

(defmethod affiliate-fee :else [user]
  (fee-amount 0.02 user))

;; It’s that simple! Now, to add new cases, you add new methods, which is far cleaner
;; than ending up with a long-winded cond form
;; Multiple dispatch



(defmethod affiliate-fee :mint.com [user]
  (fee-amount 0.03 user))
(defmethod affiliate-fee :google.com [user]
  (fee-amount 0.01 user))

(defmulti payment-price :course)
(defmethod payment-price :computer [student]
  (school-fees 10 student))
(defmethod payment-price :physics [student]
  (school-fees 11 student))

;; Multiple dispatch
;; So great, in fact, that we’d like to pay more profitable users a
;; higher fee.

(defn profit-rating [user ]
  (let [ratings [::bronze ::silver ::gold ::platinum]]
    (nth ratings (rand-int (count ratings)))))

(defn fee-category [user]
  [(:referee user) (profit-rating user)])

(defmulti profit-based-affiliate-fee fee-category)
(defmethod profit-based-affiliate-fee [:mint.com ::bronze] [user]
  (fee-amount 0.03 user ))
(defmethod profit-based-affiliate-fee [:mint.com ::silver] [user]
  (fee-amount 0.04 user))
(defmethod profit-based-affiliate-fee [:mint.com ::gold] [user]
  (fee-amount 0.05 user))
(defmethod profit-based-affiliate-fee [:mint.com ::platinum] [user]
  (fee-amount 0.05 user))
(defmethod profit-based-affiliate-fee [:google.com ::gold] [user]
  (fee-amount 0.03 user))
(defmethod profit-based-affiliate-fee [:google.com ::platinum] [user]
  (fee-amount 0.03 user))
(defmethod profit-based-affiliate-fee :default [user]
  (fee-amount 0.02 user))

(derive ::bronze ::basic)
(derive ::silver ::basic)
(derive ::gold   ::premier)
(derive ::platinum ::premier)

;; THE VISITOR PATTERN REVISITED

;; let’s rewrite the AST program using multimethods.

(def aNode {:type :assignment :expr "assignment"})
(def vNode {:type :variable-ref :expr "variableref"})

(defmulti checkValidity :type)
(defmethod checkValidity :assignment [node]
  (println "checkking :assignment, expression is" (:expr node)))
(defmethod checkValidity :variable-ref [node]
  (println "checking :variable-ref, expression is" (:expr node)))

(def aBus {:type :bsu-api-zone :expr "Bsu Api"})
(def vNas {:type :nas-api-zone :expr "Nas Api"})

(defmulti test-api :type)
(defmethod test-api :bsu-api-zone [node]
  (println "checking :api now:" (:expr node)))

(defmethod test-api :nas-api-zone [node]
  (println "checking :api now:" (:expr node)))

(defmulti generateASM :type)
(defmethod generateASM :assignment [node]
  (println "gen ASM for :assignment, expr is" (:expr node)))
(defmethod generateASM :variable-ref [node]
  (println "gen ASM for :variable-ref, expr is" (:expr node)))

;; Redis-clojure
;; Redis is a key-value database.
;; It is fast and persistent Redis-clojure is a Clojure client
;; for the Redis server written by Ragnar Dahlen.
;; The library is open source and is hosted on github.com.
;; It uses a multimethod to parse responses sent by the server as the client communicates with it.

(defmulti parse-reply reply-type :default :unknown)

(defmethod parse-reply :unknown
  [#^BufferedReader reader]
  (throw (Exception. (str "Unknown reply type:"))))

(defmethod parse-reply  \-
  [#^BufferedReader reader]
  (let [error (read-line-crlf reader)]
    (throw (Exception. (str "Server error: " error))))

  (defmethod parse-reply \+
    [#^BufferedReader reader]
    (read-line-crlf reader))
  (defmethod parse-reply \$
    [#^BufferedReader reader]
    (let [line (read-line-crlf reader)
          length (parse-int line)]
      (if (< length 0)
        nil
        (let [#^chars cbuf (char-array length)]
          (do
            (do-read reader cbuf 0 length)
            (read-crlf reader) ;; CRLF
            (String. cbuf))))))
  (defmethod parse-reply \*
    [#^BufferedReader reader]
    (let [line (read-line-crlf reader)
          count (parse-int line)]
      (if (< count 0)
        nil
        (loop [i count
               replies []]
          (if (zero? i)
            replies
            (recur (dec i) (conj replies (read-reply reader))))))))
  (defmethod parse-reply \:
    [#^BufferedReader reader]
    (let [line (trim (read-line-crlf reader))
          int (parse-int line)]
      int))

