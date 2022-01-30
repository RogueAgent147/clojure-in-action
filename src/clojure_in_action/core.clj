(ns clojure-in-action.core)

(def users {
            "yusuf" {:password "abinci" :pets 4}
            "halimat" {:password "alewa" :pets 5}
            "hamisu" {:password "maigida" :pets 6}
            "hassan" {:password "halimat" :pets 7}
            "ahmadu" {:password "rigia" :pets 8}
            })

(defn check-user [username password pets]
  (let [actual-password ((users username) :password)]
    (if (= actual-password password)
      (let [petss ((users username) :pets)]
                (println (str "The user : " username pets )))
      (println (str "Please sign up")))))

(comment
  (defn this-is-not-working [x y]
    (+ x y)))

;; Program Structure

(def addition-function (fn [x y]
                          (+ x y)))
;; Too complex
(defn average-pets []
  (/ (apply + (map :pets (vals users))) (count users)))

;; let form introduced

(defn average-pets-p []
  (let [user-data (vals users)
        pets (map :pets user-data)
        total (apply + pets)]
    (/ total (count users))))

(let [x 1
      y 2
      z (+ x y)]
  (println z))

;; Before moving on, it’s worth discussing the situation where you might not care about
;; the return value of an expression. Typically, such an expression is called purely for its
;; side effect
;; A trivial example is println

(defn average-pets-pp []
  (let [user-data (vals users)
        pets (map :pets user-data)
        value-from-println (println "Well i guess am out" pets)
        total (apply + pets)]
    (/ total (count users))))

;; average-pets how dare you call me and do that if you don't
;; want to use a value please use an underscore! okay
;; I think i broke something ouch, i deserve my room

(defn average-pets-ppp []
  (let [user-data (vals users)
        pets (map :pets user-data)
        _ (println "total pets: " pets)
        total (apply + :pets)]
    (/ total (count users))))

;; Side effects with do
(comment
  (defn do-many-things []
    (do-first-thing)
    (do-second-thing)
    (return-final-value)))

;; in a world without state and side effects the do-many things
;; function will be equivalent to
(comment
  (defn do-many-things-equivalent []
    (return-final-value)))

;; In order to combine multiple s-expressions into a single form, Clojure provides the
;; do form. can be used where some side effect is required

(comment
  (if (is-something-true?)
    (do
      (log-message "in true branch")
      (store-somthing-in-db)
      (return-useful-value))))
;; The do form is a convenient
;; way to combine multiple s-expressions into one.

;; try/catch/finally/and throw

;; if a average-pets-r trys to access an empty
;; map it throws an error

(defn average-pets-r [users]
  (let [user-data (vals users)
        number-pets (map :number-pets user-data)
        total (apply + number-pets)]
    (/ total (count users))))

(def no-users {})

;; we input a try catch block

(defn average-pets-t [users]
  ((try
    (let [user-data (vals users)
          number-pets (map :number-pets user-data)
          total (apply + number-pets)]
      (/ total (count users)))
     (catch Exception e
       (println "Error empty map")
       0))))

;; Exceptions can be thrown as easily using the throw form. In any place where you
;; wish to throw an exception, you can do something like the following:

(throw (Exception. "This is a nightmare"))

;; Reader macros converts program text into clojure data structure

;;; Program Flow
;; if if-not cond
;; (cond & clauses)

(defn range-info [x]
  (cond
    (< x 0) (println "negative")
    (= x 0) (println "Zero")
    :default (println "Positive")))

;; when
;; (when test & do)

(comment
  (when (some-cond)
    (do
      (do-this-first)
      (do-this-second)
      (and-return-this))))

;; when-not
;; The opposite of not
;; (when-not test & body)

;; Logical functions
; and (accepts more than one or more forms)
; (and x & next)

(comment
  (if (and (is-member? user)
           (has-specaial-status? user))
    (welcome-warmly user)))

;; or works in the opposite way it accepts one or more
;; forms and evaluates them one by one if any return true
;; then the value of that last one is returned

(comment (or (never-logged-in? user) (has-no-expenses? user))
         (email-encouragement user))

;; and and or are both macros
;; yea this calls for celebration, oh yes fried chicken!

;; not (inverts the logical value of whatever is passed
;; as an argument

(comment
  (if (not (thrifty? user))
    (email-saving user)))

;;; Functional Iteration

;; while (while test & body)
(comment
  (while (request-on-queue?)
    (handle-request (pop-request-queue))))

;; Here this request will continue to be processed as long
;; as they keep appearing on the request queue

;; loop/recur
;; loop sets up bindings that work exactly like the let form does.
;; In this example, [current n fact 1] works the same way if used with a let form:
;; current gets bound to the value of n , and fact gets bound to a value of 1 .


;; recur
;; [recur bindings]
;; In this example, recur has two binding values,
;; (dec current) and (* fact current), which are
;; computed and rebound to current and fact.
;; recur can be used only from tail positions of code,
(comment
  (defn fact-loop [n]
    (loop [current n fact 1]
      (if (= current 1)
        fact (recur (dec current) (* fact current))))))

(comment
  (loop bindings & body))

;; Doseq Dotimes
;; you have a list of users and you to generate expense
;; report for each user

;; The simplest form accepts a vector containing two
;; terms, where the first term is a new symbol,
;; which will be sequentially bound to each
;; element in the second term (which must be a sequence).

(dotimes [x 5]
  (println "Factorial of " x "is =" (factorial x)))

(comment
  (defn run-report [user]
    (println "Running report for" user)))
(comment
  (defn dispatch-reporting-jobs [all-users]
    (deseq [user all-users]
           (run-reports user))))

;; Map
;; The simple use of map accepts a unary function
;; and a sequence of data elements.
;; A unary function is a function that accepts only
;; one argument. maps applies this function to each element
;; of the sequence

(element
  (defn find-daily-totals [start-date end-date]
    (let [all-dates (dates-between start-date end-date)]
      (map find-total-expenses all-dates)))
  )

;; filter
;; filter does something similar to map —it collects values.
;; But it accepts a predicate function and a sequence
;; and returns only those elements of the sequence that return a
;; logically true value when the predicate function is called on them

(comment
  (defn non-zero-expenses [expenses]
    (let [non-zero? (fn [e] (not (zero? e)))]
      (filter non-zero? expenses))))

;; here is a more succint alternative

(comment
  (defn non-zero-expensess [expenses]
    (filter pos? expenses)))

;; Reduce
;; The simplest form of reduce is a high-level
;; function that accepts a function of arity 2
;; and a sequence of data elements
;; The function is applied to the first two elements of
;; the sequence, producing the first result. The same function is then called again with
;; this result and the next element of the sequence. This then repeats with the following
;; element, and so on.

(defn factorial [n]
  (let [numbers (range 1 (+ n 1))]
    (reduce * numbers)))

;; For
;; sq-exprs is a vector specifying one or more binding-for/collection-exprs pairs.
;; (for seq-exprs body-expr)
;; Consider the following example that generates a list of labels for each square on
;; a chessboard:

(defn chessboard-labels []
  (for [alpha "abcdefgh"
        num (range 1 9)]
    (str alpha num)))

;; let’s first consider a function
;; that checks to see if a number is prime

(defn prime? [x]
  (let [divisor (range 2 (inc (int (Math/sqrt x))))
        remainder (map #(rem x %) divisor)]))

(defn prime-less-than [n]
  (for [x (range 2 (inc n))
        :when (prime? x)]
    x))

(defn pairs-for-primes [n]
  (let [z (range 2 (inc n))]
    (for [x z y z :when (prime?  (+ x y))]
      (list x y))))


;; The threading macros

;; Threading first
;;calculate the savings that would be
;; available to a user several
;years from now based on some amount they
; invest today.

(defn final-amount [principle rate time-periods]
  (* (Math/pow (+ 1 (/ rate 1000)) time-periods) principle))

;; The above is dificult to read because it's written
;; inside out

(defn final-amount-p [principle rate time-periods]
  (-> rate
       (/ 10
          0)
       (+ 1)
       (Math/pow time-periods)
       (* principle)))

;; Thread-last

;; The thread-last macro (named ->> ) is a cousin of the thread-first macro.
;; Instead of taking the first expression and
;; moving it into the second position of the next expression,
;; it moves it into the last place.

;; The factorial function takes n and sums it with 1
;; which gives a range which is multiply through the apply
;; function to give us final value

(defn factorial [n]
  (apply *
         (range 1 (+ 1 n))))

(defn factorial->> [n]
  (->> n
    (+ 1)
       (range 1)
       (apply *)))

;; Keywords are symbolic
;; identifiers that always evaluate to themselves
;; A typical use of keywords is as keys inside
;; hash maps, but they can be used anywhere a unique value is needed.
;; A typical use of keywords is as keys inside
;; hash maps, but they can be used anywhere a
;; unique value is needed.

;; The ISeq interface provides three functions: first, rest, and cons.

(let [a-list (list 1 2 3 4 5 6)]
  (list? a-list))

;; Vector
;; Vectors are like lists, except for two things: they’re denoted using square brackets, and
;; they’re indexed by numbers.
;; Being indexed by numbers means that you have random access to the elements inside
;; a vector.

;; Maps
;; The keys can be pretty much any
;; kind of object, and a value can
;; be looked up inside a map with its key

(def the-map {:a 1 :b 2 :c 3})
(hash-map :a 1 :b 2 :c 3)

;; let’s look at what we want to accomplish.
;; Imagine you had an empty map,
;; and you wanted to store user details in it.

(def users {:key {
                  :data-joined "2001-01-01"
                  :summary {
                            :average {
                                      :monthly 1000
                                      :yearly 12000
                                      }
                            }
                  }})
;; Note the use of nested maps.
;; If you wanted to update Kyle’s monthly average,
;; you’d need to write some code, like this:

(defn set-average-in [users-map user type amount]
  (let [user-map (users-map user)
        summary-map (:summary user-map)
        average-map (:average summary-map)]
    (assoc users-map user
                     (assoc users-map :summary
                                      (assoc summary-map :average
                                                         (assoc average-map type amount))))))


(defn average-for [user type]
  (type (:average (:summary (user @users)))))

;; general form of assoc-map
;; (assoc-in map [key & more-keys] value)
