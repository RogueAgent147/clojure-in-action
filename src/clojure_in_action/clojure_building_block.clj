(ns clojure-in-action.clojure-building-block)

;; clojure functions are object
;; How to use the do function to allow side effect
(comment

  (defn function-name doc-string? attr-map? [parameter-list]
    conditions-map?
    (expression)))

;; total-cost is the name of the
;; new function object we defined. It accepts two parameters:
;; item-cost and number-of-items.
;; There is no explicit return keyword in clojure
;; you could also add a doc string
(defn total-cost
  "return line-item total of the item and quantity provided"
  [item-cost number-of-items]
  (* item-cost number-of-items))

;; item-total behaves as a normal function that multiplies its two arguments and
;; returns a result. But at runtime,
;; it runs additional checks as specified by the hash with
;; the two keys :pre and :post.
;; preconditions and postcondition
(defn item-total [price quantity]
  (:pre [(> price 0) (> quantity 0)]
    :post [#(> % 0)])
  (* price quantity))

;; Basic line item is defined without any condition
;; and clearly focused on business logic of calculating
;;  the line item total
(defn basic-item-total [price quantity]
  (* price quantity))

;; with-line-item-condition is a higher order function that
;; accepts a function and thesame two arguments

(defn with-line-item-conditions [f price quantity]
  (:pre [(> price 0) (> quantity 0)]
    :post [#(> % 1)])
  (apply f price quantity))

;; call function
(with-line-item-conditions basic-item-total 20 1)

(def item-total (partial with-line-item-conditions basic-item-total))

;; Multiple Arity
;; Clojure functions can be overloaded on arity.
(comment
  (defn function-name
    ([parameter-list-1]
     ;;body
     )
    ([parameter-list-2]
     ;;body
     )
    ;;more case
    ))
(defn total-cost
  ([item-cost number-of-items]
   (* item-cost number-of-items))
  ([item-cost]
   (total-cost item-cost 1)))

;; Variadic functions
;; A variadic function is a function of variable arity.

;; total-all-numbers is a function that can be called
;; with any number of arguments
;; all args are packaged into a single list called numbers
;; which is available to the body of the function

(defn total-all-numbers [& numbers]
  (apply + numbers))

(defn whole [f & numbers]
  (:pre [(> numbers 0) (list? numbers)]
    :post [#(> % 1)])
  (apply f numbers ))

(comment
  (defn name-of-variadic-function [param-1 param-2 & rest-ars]
    (body-of-function)))

;; Recursive Functions
;; Recursive functions are those that either directly or indirectly call themselves.

;; using such recursion throws a StackOverflowError
;; instead of this route we use recur which is used at the
;; tail end of the function and it prevents stackoverflow error

(comment
  (defn count-down [n]
    (if-not (zero? n)
      (do
        (if (= 0 (rem n 100))
          (println "count-down:" n))
        (count-down (dec n))))))

;; A more suitable version of recur
;; As you can see, writing self-recursive functions is straightforward.
;; Writing mutually recursive functions is a bit more involved,
;;  and we’ll look at that next.
(defn count-downr [n]
  (if-not (zero? n)
    (do
      (if (= (rem n 100))
       (println "count-down: " n))
      (recur (dec n )))))

;; Mutually Recursive Functions
;; Mutually recursive functions are those that
;; either directly or indirectly call each other.


;; shows a contrived example of two functions, cat and hat,
;; that call each other.
;; When given a large enough argument,
;; they’ll throw the same StackOverflowError

;; declare macro calls def on each of its arguments

(comment
  (declare hat)
  (defn cat [n]
    (if-not (zero? n)
      (do
        (if (= 0 (rem n 100)))
        (hat (dec n)))))
  (defn hat [n]
    (if-not (zero? n)
      (do
        (if (= 0 (rem n 100))
          (println "hat: " n))
        (cat (dec n))))))

;; a more better version with the advent of trampoline
;; you now return an anony- mous function
;; that when called makes the call to hatt .
(declare hatt)

(defn catt [n]
  (if-not [zero? n]
    (do
      (if (= 0 (rem n 100))
        (println "catt:"  n))
      #(hatt (dec n)))))
(defn hatt [n]
  (if-not (zero? n)
    (do
      (if (= 0 (rem n 100))
        (println "hatt:" n))
      #(catt (dec n)))))

;; Because these functions no longer perform their recursion directly, you have to
;; use a special higher-order function to use them. That’s the job of trampoline , and
;; here’s an example of using it:

(trampoline catt 100000)

;;Although using
;; recur and trampoline is the correct and safe way to write such functions, if you’re
;; sure that your code isn’t in danger of consuming the stack, it’s OK to write them with-
;; out using these

;; Calling Functions
;; Higher-order-functions

;; Every?, Some
;; is a function that accept a predicate function and a sequence
;; it then calls the predicate on each element

(def bools [true true true false false])
(every? true? bools)

;; some has thesame interface as every; that is accept a predicate
;; and a sequence

(some (fn [p] (= "rob" p)) ["kyle" "siva" "rob" "celeste"])

;; Constantly
;; constantly accepts a value v and returns a
;; variadic function that always returns the
;; same value v no matter what the arguments.

;; Compliment
;; Takes a fn f and returns a fn that takes the same arguments as f,
;; has the same effects, if any, and returns the opposite truth value.

;; partial
;; you write a basic comparison function
;; and pass it on to filter
(defn above-threshold? [threshold number]
  (> number threshold))

(filter (fn [x] (above-threshold? 5 x)) [1 2 3 4 5 6])

;; The above can be handled neatly
(filter (partial above-threshold? 5) [1 2 3 4 5])

(defn personal-try [first last]
  (> first last))
(filter (fn [c] (personal-try 8 c)) [3 4 8 9 10 11 12])
;; better handled with partial
(filter (partial personal-try 8) [1 2 3 5 7 19])

;; MEMOIZE
;; Memoization is a technique that prevents functions from
;; computing results for arguments that have already been processed.
;; Instead, return values are looked up from a cache

(defn slow-calc [n m]
  (Thread/sleep 1000)
  (* n m))

;; let's make this fast, by using the built-in memoize function
(def fast-calc (memoize slow-calc))

;; Writing Higher-Order function

(def users [
            {:username "kyle"
             :balance 175.00
             :member-since "2009-04-16"}
            {
             :username "zak"
             :balance 12.95
             :member-since "2009-02-01"
             }
            {:username "rob"
             :balance 98.50
             :member-since "2009-03-30"}])
(defn username [user]
  (user :username))
(defn balance
  "balance of user"
  [user]
  (user :balance))
(defn member-since
  "tells you date became member"
  [user]
  (user :member-since))

(defn sorter-using
  "sorts the list of users"
  [ordering-fn]
  (fn [user]
    (sort-by ordering-fn users)))

(def poorest-first (sorter-using balance))
(def alphabetically (sorter-using username))
(def date-of-entry (sorter-using member-since))

;; anonymous functions

(def total-cost (fn [item-cost number-of-items]
                  (* item-cost number-of-items)))

(map (fn [user] (user :member-since)) users)

;; A shortcut for anonymous functions

(map #(% :member-since) users)
(map :member-since users)


;; Keywords and symbols
;; keywords and symbols are function
;; Keyword functions take either one or two parameters.
;; The first parameter is a hash
;; map, and the keyword looks itself up in the given hash map

(def person {:username "zak"
             :balance 12.95
             :member-since "2009-02-01"})

(person :member-since)
(person :member-since :not-found)

(def expence {'name "Snow Leonard"
              'cost 29.95})
(expence 'name)
('name expence)
('vendor expence)
('vendor expence :absent)

(def names ["kyle" "zak" "rob"])
(names 1)

;; Scope
;; Vars and binding

;; this initial binding is called root binding

(def MAX-CONNECTIONS 10)

;; A var can be defined without any initial binding

(def RABBITMQ-CONNECTION)

;; to set value for an unbound var or change the value
;; bound to a var clojure provides binding

(binding [MAX-CONNECTIONS 20
          RABBITMQ-CONNECTION (new-connection)]
  (
    ;; do something here
    ))

;; Special Variables
;; vars are dynamically scoped
(def *db-host* "localhost")

(defn expense-report [start-date end-date]
  (println *db-host*))                                      ;; can do real work

;; now we have tested the above we can connect to our production dbase
;; this is called action at a distance
(binding [*db-host* "production"]
  (expence-report "2010-01-10" "2010-10-07"))

;; aspect-oriented programming
;; specifically to add a log statement to functions when
;; they are called
;; Scope determines which names are visible at certain points in the code and which
;; names shadow which other ones.

(def *eval-me* 10)

(defn print-the-var [label]
  (println label *eval-me*))

(print-the-var "A: ")

(binding [*eval-me* 20]                                     ;; the first bindin
  (print-the-var "B: ")
  (binding [*eval-me* 30]                                   ;; the second binding
    (print-the-var "C: "))
  (print-the-var "D: "))

(print-the-var "E:")

(defn twice [x]
  (println "original function: ")
  (* 2 x))

(defn call-twice [y]
  (twice y))

(defn with-log [function-to-call log-statement]
  (fn [& args]
    (println log-statement)
    (apply function-to-call args)))


;; Thread-Local-State

;; Laziness and special variables

(def *factor* 10)

(defn multiply [x]
  (* x *factor*))

(map multiply [1 2 3 4 5])

(binding [*factor* 20]
  (map multiply [1 2 3 4 5 6]))

(binding [*factor* 320]
  (doall [map multiply [1 2 3 4 5]]))

;; The let form revisited

(let [x 10
      y 20]
  (println "x, y:" x "," y))

(defn upcased-names [names]
  (let [up-case (fn [name]
                  (.toUpperCase name))]
    (map up-case names)))

(defn lowercase-names [names]
  (let [low-case (fn [x]
                   (.toLowerCase))]
    (map lowercase-names names)))

(def *factor* 10)
(binding [*factor* 20]
  (println *factor*)
  (doall (map multiply [1 2 3])))

;; Lexical clojures
;; A variable is said to be free inside a given form if there’s no binding occurrence
;; of that variable in the lexical scope of that form.

(defn create-scaler [scale]
  (fn [x]
    (* x scale )))

(def percent-scaler (create-scaler 100))

;; Namespaces
;; When a program becomes larger than a few functions,
;; computer languages allow the programmer to break it up into parts.
;; an exmple is the package system in java
;; Another reason why namespaces are useful is to avoid name collisions in different
;; parts of programs.