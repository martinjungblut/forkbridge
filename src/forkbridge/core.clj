(ns forkbridge.core
  (:import [java.lang ProcessBuilder]
           [java.io BufferedReader InputStreamReader OutputStreamWriter]))

(defn start-process
  [cmd-vec]
  (let [pb (ProcessBuilder. cmd-vec)
        proc (.start pb)
        stdin-writer  (-> proc .getOutputStream OutputStreamWriter.)
        stdout-reader (-> proc .getInputStream InputStreamReader. BufferedReader.)
        stderr-reader (-> proc .getErrorStream InputStreamReader. BufferedReader.)
        alive? #(.isAlive proc)
        exit-value #(try
                      (.exitValue proc)
                      (catch Exception _ nil))
        write #(do (.write stdin-writer (str % "\n"))
                   (.flush stdin-writer))
        read #(.readLine stdout-reader)]
    {:alive? alive?
     :exit-value exit-value
     :write write
     :read read
     :stdin   stdin-writer
     :stdout  stdout-reader
     :stderr  stderr-reader}))
