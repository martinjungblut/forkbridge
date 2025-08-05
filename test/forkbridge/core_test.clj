(ns forkbridge.core-test
  (:require [clojure.test :refer [deftest is]]
            [forkbridge.core :refer [start-process]]))

(deftest alive?-and-exit-value
  (let [p (start-process ["clojure" "-M" "-e" "(do (Thread/sleep 1000) (System/exit 10))"])
        alive? (:alive? p)
        exit-value (:exit-value p)]
    (is (true? (alive?)))
    (is (nil? (exit-value)))
    (Thread/sleep 1500)
    (is (false? (alive?)))
    (is (= 10 (exit-value)))))

(deftest read-and-write
  (let [p (start-process ["clojure"])
        exit-value (:exit-value p)
        read (:read p)
        write (:write p)
        header (read)]
    (is (= "Clojure 1.12.0" header))

    (write "(+ 10 20)")
    (let [output (read)]
      (is (= "user=> 30" output)))

    (write "(System/exit 15)")
    (Thread/sleep 500)
    (is (= 15 (exit-value)))))
