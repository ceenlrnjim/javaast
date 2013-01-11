(ns javaast.fileutil
  (:require [clojure.xml :as xml])
  (:import [java.io BufferedReader FileReader File IOException]))

(defn file-list-contents
  "returns a list of strings in the specified file. Expected to be file names"
  [list-file]
  (with-open [reader (BufferedReader. (FileReader. list-file))]
    (loop [result []]
      (let [nextline (.readLine reader)]
        (if (nil? nextline) result (recur (conj result nextline)))))))

(defn pathname
  [reference root]
  (if (.isAbsolute (java.io.File. reference)) reference (str root File/separator reference)))

(defn eclipse-classpath
  "returns a string containing the classpath option value for the compiler based on an eclipse .classpath file"
  [dot-classpath]
  (let [reldir (.getParent (.getAbsoluteFile (java.io.File. dot-classpath)))
        xmlseq (xml-seq (xml/parse dot-classpath))]
    (map #(pathname (:path (:attrs %)) reldir)
      (filter #(and
                 (= (:tag %) :classpathentry)
                 (= (:kind (:attrs %)) "lib"))
              xmlseq))))
