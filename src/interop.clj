(ns interop
  (:import (java.text SimpleDateFormat))
  (:import (java.util Calendar))
  (:import (java.util Random))
  (:import (java.util Calendar TimeZone)))

;;These two examples access the static fields JANUARY and FEBRUARY from the
;Calendar class.
Calendar/JANUARY
Calendar/FEBRUARY
;; Calling Java from Clojure
;; (import & import-symbols-or-lists)

(def sdf (SimpleDateFormat. "yyyy-MM-dd"))

;; MEMBER ACCESS
;; in java member access refers to accessing methods
;; and fields of object

(defn date-from-date-string [date-string]
  (let [sdf (SimpleDateFormat. "yyyy-MM-dd")]
    (.parse sdf date-string)))

;; Calling a static method in general has the following form:
;; (Classname/staticMethod args*)
;; parselong is a static method on the class Long which accepts a string containing a long
;; number
(Long/parseLong "12321")

;; The Dot Special Form
;; In clojure all underlying java access is done via dot operator
;; The Clojure documentation says that the dot operator can be read as “in the scope of.”
;; That means that the member access is happening in the scope of the value of the first symbol.
;; Example

(. Classname-symbol method-symbol args*)
(. Classname-symbol (method-symbol args*))

;; The above form allows static method to be called on class specified as the first args
;; another example

(. System gatenv "PATH")
(. System (gatenv "PATH"))

;; Typically in code, if you do use the dot operator directly, the first form is preferred.

;; Now let’s look at another example that’s similar but operates on instances of Java
;; classes (objects) as opposed to classes

(. instance-exprs method-symbol args*)
(. instances-expr (method-symbol args*))
;; The example below illustrates the point above.(i imported the java random

(def newrnd (Random. ))
(. newrnd nextInt 10)
(. newrnd (nextInt 10))


;;(DOT DOT)
(. (. (Calendar/getInstance) (getTimeZone)))
;; If we were using method signatures that accepted arguments, we’d do so as follows:
(..
  (Calendar/getInstance)
  (getTimeZone)
  (getDisplayName true TimeZone/SHORT))

;; DOTO
;; the doto macro helps write code where multiple methods are called on the same Java object.\
(defn the-past-midnight []
  (let [calender-obj (Calender/getInstance)]
    (.set calender-obj Calendar/AM_PM Calendar/AM)
    (.set calender-obj Calendar/HOUR 0)
    (.set calender-obj Calendar/MINUTE 0)
    (.set calender-obj Calendar/SECOND 0)
    (.set calender-obj Calender/MILLSECOND 0)
    (.getTime calender-obj)))

;; there’s tedious repetition of the symbol calendar-obj in this code.
;; The doto macro eliminates this sort of duplication.

(defn the-past-midnight-p []
  (let [calender-obj (Calendar/getInstance)]
    (doto calender-obj)
    (.set Calendar/AM_PM Calendar/AM)
    (.set Calendar/HOUR 0)
    (.set Calendar/MINUTE 0)
    (.set Calendar/SECOND 0)
    (.set Calendar/MILLISECOND 0)
    (.getTime calender-obj)))

;; MEMFN
;; is a convenient way  to convert Java instance methods into Clojure functions.

;; Byte Array that compose a few strings

(map (fn [x] (.getBytes x)) ["amit" "rob" "kyle"])

;; The above can be simplified using the reader macro for anonymous function.
(map #(.getBytes %) ["amit" "rob" "kyle"])

(memfn getBytes)
(map (memfn getBytes) ["amit" "rob" "kyle"])

(.subSequence "Clojure" 2 5)
;; The above can be represented in this manner
((memfn subSequence start end) "Clojure" 2 5)

;; BEAN
;; bean is another convenient macro that’s useful when dealing with Java code, especially
;; Java beans, which are classes that conform to a simple standard involving exposing
;; their data via getter and setter methods

;; This returns a Clojure map that contains all its bean properties. Being a Clojure data
;; structure, it’s immutable
(bean (Calendar/getInstance))
(:timeInMillis 1257466522295,
  :minimalDaysInFirstWeek 1,
  :lenient true,
  :firstDayOfWeek 1,
  :class java.util.GregorianCalendar
  ;; Other properties
  )



;; Array
;; A java array is a container that holds values of thesame type.
;; it's a random access data structures that uses integers as its keys
;; Clojure has native support for dealing with Java arrays. Consider the follow-
;; ing snippet:

(def tokens (.split "clojure.in.action" "\\."))
(alength tokens)                                            ;returns the size of the array
(aget tokens 2)                                             ;returns the element of array at index specified
(aset tokens 2 "actionable")                                ; this mutates the tokens array so that last toen is now actionable

;; you should limit their use to situations where they're absolutely needed


