(ns clojure-in-action.namespaces)
(use 'org.danlarkin.json)
(use 'clojure.xml)
;; require makes functions available to the current namespace, as use does, but doesn’t
;; include them the same way. They must be referred to using the full namespace name
;; or the aliased namespace using the as clause
(require '(org.danlarkin [json :as json-lib]))
(require '(clojure [xml :as xml-core]))

(use 'clojure-in-action.namespaces :reload)
(use 'clojure-in-action.namespaces :reload-all)


;; writing an HTTP service that responds to queries
;; about a user’s expenses

(defn import-transactions-xml-from-bank [url]
  (let [xml-docs (parse url)]
    ;; more codes hers
    ))

;;  here parse above and encode-to-str are functions that come from
;; the clojure.xml and clojure-json

(defn totals-by-day [start-date end-date]
  (let [expenses-by-day (load-totals start-date end-date)]
    (encode-to-str expenses-by-day)))

;; the above often makes
;; the code a little less understandable in terms of seeing where such functions are
;; defined. "require" solves this problem, as shown in here

(defn import-transactions-xml-from-bank-p [url]
  (let [xml-docs (xml-core/parse url)]
    ;; more codes
    ))
(defn totals-by-day-p [start-date end-date]
  (let [expences-by-day (load-totals start-date end-date)]
    (json-lib/encode-to-str expences-by-day)))


;; Reload and Reload all
;; reload can be replaced with :reload-all to reload all libraries that are used either
;; directly or indirectly by the specified library

;; (use 'org.currylogic.damages.http.expenses :reload)
;; (require '(org.currylogic.damages.http [expenses :as exp]) :reload)

;; Working with namespace
; (create-ns)
;; create-ns is a function that accepts a symbol and creates a namespace named by it
;; if it doesn’t already exist.

;; (n-ns) is a function that accepts a single symbol as argu-
;; ment and switches the current namespace to the one named by it
;; ETC

;; #>S DESTRUCTURING
;; clojure has a somewhat less-general form of pattern matching called destructuring
;; destructing lets programmers bind names to only those parts of sequences that they care about

(defn describe-salary [person]
  (let [first (:first-name person)
        last (:last-name person)
        annual (:salary person)]
    (println first last "earns" annual)))

(defn describe-user [person]
  (let [user-name (:username person)
        likee (:likes person)
        hobbies (:hobby person)
        age (:age person)]
    (println user-name "likes: " likee " and his hobbies are: " hobbies " at age " age)))

;; by using destructuring such code clutter can be eliminated
(defn describe-salary-p [{first :first-name
                          last  :last-name
                          annual :salary}]
  (println first last "earns" annual))

(defn describe-user-p [{username :username
                        likes :likes
                        hobbies :hobby
                        age :age}]
  (println username "likes: " likes" and his hobbies are: " hobbies " at age " age))

;; Vector bindings
;; Vector destructuring supports any data structure that implements the nth function,
;; including vectors, lists, seqs, arrays, and strings.

(defn print-amounts [[amount-1 amount-2]]
  (println "amounts are: " amount-1 " and " amount-2))

(print-amounts [10.95 31.45])

;; using & and :as

(defn print-amounts-multiple [[amount-1 amount-2 & remaining]]
  (println "Amounts are: " amount-1 "," amount-2 "and" remaining))

(print-amounts-multiple [10.95 31.45 22.36 2.95])

(defn print-all-amounts [[amount-1 amount-2 & remaining :as all]]
  (println "Amounts are:" amount-1 "," amount-2 "and" remaining)
  (println "Also, all the amounts are:" all))

(print-all-amounts [10.95 31.45 22.36 2.95])

;; Destructuring vectors makes it easy to deal with the data inside them.
;; Nested Vectors
;; vector destructuring works for any data type that
;; supports the nth and nthnext functions.
(defn print-first-category [[[category amount] & _ ]]
  (println "First category was:" category)
  (println "First amount was:" amount))

(def expenses [[:books 49.95] [:coffee 4.95] [:caltrain 2.25]])
(print-first-category expenses)

(defn car-models [[[name models] & _]]
  (println name)
  (println models))
(def cars [[:toyota "honda" :vexa "prosx"]])

(defn expenses-h [exp]
  (lets [item (:item exp)
         price (:price exp)]
        (println item ", " price)))

(defn expenses-d [{item :item
                   price :price
                   date-of-pub :dop}]
  (println item price date-of-pub))


;; MAPPING BINDINGS

;;Clojure supports similar destructuring of maps.
;; Clojure supports similar destructuring of maps. To be specific, Clojure supports destructuring of any associative data structure, which
;; includes maps, strings, vectors, and arrays.

(defn describe-salary-2 [{
                          first  :first-name
                          last :last-name
                          annual :salary
                          }]
  (println first last annual))

;; lets say you want to bind a bonus percentage which may or may not exist

(defn describe-salary-3 [{first :first-name
                          last :last-name
                          annual :salary
                          bonus :bonus-percentage
                          :or (bonus 5)}]
  (println first last "earns" annual "with a" bonus "percent bonus"))

(def a-user {:first-name "pascal"
             :last-name "dylan"
             :salary 85000
             :bonus-percentage 20})
(describe-salary-3 a-user)

;; here we see the :or function work

(def another-user {:first-name "basic"
                   :last-name "groovy"
                   :salary 70000})
(describe-salary-3 another-user)

;; :as

(defn describe-person [{first :first-name
                        last :last-name
                        bonus :bonus-percentage
                        :or {bonus 5}
                        :as p}]
  (println "Info about" first last "is:" p)
  (println "Bonus is:" bonus "percent"))

(def third-user {:first-name "lambda"
                 :last-name "curry"
                 :salary 95000})
(describe-person third-user)

;; Clojure provides a couple of
;; options that make it even more easy to destructure maps: the :keys , :strs , and :syms
;; keywords.

;; When you run this, first-name and last-name get bound to values of :first-name
;; and :last-name from inside the argument map. Let’s try it
(defn greet-user [{:keys [first-name last-name]}]
  (println "welcome: " first-name last-name))

;; likewise the above
;; if keys were strings or symbols instead of :keyword i'd use this instead :strs (strings) syms (symbols)

(defn social-app [{:keys [username password hobbies personal-legend]}]
  (println "basic info" username password hobbies personal-legend))


;; METADATA
;; metadata means data about data.
;; Clojure supports tagging data (for example, objects like
;; maps and lists) with other data, which can be completely unrelated to the tagged data.


;; For instance, you might want to tag some data that was read over an insecure network
;; lets use the tags :safe and :io to determine if something is considered a secuirity threat and if it
;; came from an external I/0 source

;; Returns an object of the same type and value as obj, with
;; map m as its metadata
(def untrusted (with-meta {:command "clean-table" :subject "users"}
                          {:safe false :io true}))

(def untrusted-link (with-meta {:port 450 :route "eagles-wing" :design "hunter"}
                               {:safe false :io true}))


;; function and macros can also be defined with metadata

(defn testing-meta
  "testing metadata for functions"
  {:safe true :console true}
  []
  (println "hello from meta! "))


