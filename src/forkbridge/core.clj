(ns forkbridge.core
  (:import
   [java.lang ProcessBuilder]
   [java.io BufferedReader InputStreamReader OutputStreamWriter]))

(defn start-process
  [cmd-vec]
  (let [procbuilder (ProcessBuilder. cmd-vec)
        proc (.start procbuilder)
        writer-stdin  (-> proc .getOutputStream OutputStreamWriter.)
        reader-stdout (-> proc .getInputStream InputStreamReader. BufferedReader.)
        reader-stderr (-> proc .getErrorStream InputStreamReader. BufferedReader.)
        alive? #(.isAlive proc)
        exit-value #(try
                      (.exitValue proc)
                      (catch java.lang.IllegalThreadStateException _ nil))
        write-line #(try (do (.write writer-stdin (str % "\n"))
                             (.flush writer-stdin))
                         (catch java.io.IOException _ nil))
        read-line-stdout #(try (.readLine reader-stdout)
                               (catch java.io.IOException _ nil))
        read-line-stderr #(try (.readLine reader-stderr)
                               (catch java.io.IOException _ nil))]
    {:alive? alive?
     :exit-value exit-value
     :write-line write-line
     :read-line-stdout read-line-stdout
     :read-line-stderr read-line-stderr
     :sigterm!     #(.destroy proc)
     :sigkill!     #(.destroyForcibly proc)
     :wait         #(.waitFor proc)}))
