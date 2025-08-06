(defproject org.clojars.martinjungblut/forkbridge "0.1.0"
  :description "A small, well-tested Clojure library for spawning and interacting with subprocesses"
  :url "https://github.com/martinjungblut/forkbridge"
  :license {:name "MIT Licence"
            :url "https://opensource.org/licenses/MIT"}
  :repl-options {:init-ns forkbridge.core}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :sign-releases false}]])
