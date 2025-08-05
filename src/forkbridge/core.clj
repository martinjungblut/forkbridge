(ns forkbridge.core
  (:import
   [java.lang ProcessBuilder]
   [java.io BufferedReader InputStreamReader OutputStreamWriter]))

(defn start-process
  [cmd-vec]
  (let [pb (ProcessBuilder. cmd-vec)
        proc (.start pb)
        writer-stdin  (-> proc .getOutputStream OutputStreamWriter.)
        reader-stdout (-> proc .getInputStream InputStreamReader. BufferedReader.)
        reader-stderr (-> proc .getErrorStream InputStreamReader. BufferedReader.)
        alive? #(.isAlive proc)
        exit-value #(try
                      (.exitValue proc)
                      (catch Exception _ nil))
        write-line #(do (.write writer-stdin (str % "\n"))
                        (.flush writer-stdin))
        read-line-stdout #(.readLine reader-stdout)
        read-line-stderr #(.readLine reader-stderr)]
    {:alive? alive?
     :exit-value exit-value
     :write-line write-line
     :read-line-stdout read-line-stdout
     :read-line-stderr read-line-stderr
     :sigterm!     #(.destroy proc)
     :sigkill!     #(.destroyForcibly proc)
     :wait         #(.waitFor proc)}))
