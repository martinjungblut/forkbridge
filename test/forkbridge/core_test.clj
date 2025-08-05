(ns forkbridge.core-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [forkbridge.core :refer [start-process]]))

(deftest alive?-and-exit-value
  (let [p (start-process ["clojure" "-M" "-e" "(do (Thread/sleep 1000) (System/exit 10))"])
        alive? (:alive? p)
        exit-value (:exit-value p)
        wait (:wait p)]
    (is (true? (alive?)))
    (is (nil? (exit-value)))
    (wait)
    (is (false? (alive?)))
    (is (= 10 (exit-value)))))

(deftest read-and-write
  (let [p (start-process ["clojure"])
        exit-value (:exit-value p)
        wait (:wait p)
        read-line-stdout (:read-line-stdout p)
        write-line (:write-line p)
        header (read-line-stdout)]
    (is (str/starts-with? header "Clojure"))

    (write-line "(+ 10 20)")
    (let [output (read-line-stdout)]
      (is (= "user=> 30" output)))

    (write-line "(* 200 3)")
    (let [output (read-line-stdout)]
      (is (= "user=> 600" output)))

    (write-line "(System/exit 15)")
    (wait)
    (is (= 15 (exit-value)))))

(deftest sigterm
  (let [p (start-process ["clojure"])
        alive? (:alive? p)
        exit-value (:exit-value p)
        sigterm! (:sigterm! p)
        wait (:wait p)]
    (sigterm!)
    (wait)
    (is (false? (alive?)))
    (is (= 15 (- (exit-value) 128)))))

(deftest sigkill
  (let [p (start-process ["clojure"])
        alive? (:alive? p)
        exit-value (:exit-value p)
        sigkill! (:sigkill! p)
        wait (:wait p)]
    (sigkill!)
    (wait)
    (is (false? (alive?)))
    (is (= 9 (- (exit-value) 128)))))
