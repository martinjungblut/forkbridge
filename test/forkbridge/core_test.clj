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

(deftest read-line-stdout-and-write-line
  (let [p (start-process ["clojure"])
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

    ((:sigterm! p))))

(deftest read-line-stderr-and-write-line
  (let [p (start-process ["clojure"])
        read-line-stderr (:read-line-stderr p)
        write-line (:write-line p)]

    (write-line (str '(.println System/err "this goes to stderr")))
    (let [output (read-line-stderr)]
      (is (= "this goes to stderr" output)))

    ((:sigterm! p))))

(deftest read-line-stderr-blocks-until-output
  (let [p (start-process ["bash"])
        f1 (future ((:read-line-stdout p)))
        f2 (future ((:read-line-stderr p)))]
    ;; give it a chance to block
    (Thread/sleep 300)

    ;; should still be waiting
    (is (not (realized? f1)))
    (is (not (realized? f2)))

    ;; write to stdout and stderr
    ((:write-line p) "echo needle-stdout")
    ((:write-line p) "echo needle-stderr 1>&2")

    ;; make sure we found our "needle" in stdout
    (let [line (deref f1 1000 ::timeout)]
      (is (not= ::timeout line))
      (is (= "needle-stdout" line)))

    ;; make sure we found our "needle" in stderr
    (let [line (deref f2 1000 ::timeout)]
      (is (not= ::timeout line))
      (is (= "needle-stderr" line)))

    ((:sigterm! p))))

(deftest read-line-returns-nil-if-dead-process
  (let [p (start-process ["clojure"])
        sigterm! (:sigterm! p)
        alive? (:alive? p)
        read-line-stdout (:read-line-stdout p)
        read-line-stderr (:read-line-stderr p)]
    (sigterm!)
    (is (false? (alive?)))
    (is (nil? (read-line-stdout)))
    (is (nil? (read-line-stderr)))))

(deftest write-line-returns-nil-if-dead-process
  (let [p (start-process ["clojure"])
        sigterm! (:sigterm! p)
        alive? (:alive? p)
        write-line (:write-line p)]
    (sigterm!)
    (is (false? (alive?)))
    (is (nil? (write-line "anything")))))

(deftest write-line-returns-true-if-alive-process
  (let [p (start-process ["clojure"])
        sigterm! (:sigterm! p)
        write-line (:write-line p)]
    (is (true? (write-line "(+ 10 20)")))
    (sigterm!)))
