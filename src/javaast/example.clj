(ns javaast.example
  (:require [javaast.fileutil :as files])
  (:require [javaast :as ast])
  (:require [javaast.filters :as filters])
  (:gen-class)
  (:import [com.sun.source.tree Tree Tree$Kind]))
           

(defn -main [& args]
  (let [[filelist dot-classpath search-class search-method & more] args
        ast (ast/analyze (files/file-list-contents filelist) (files/file-list-contents dot-classpath))]
    (println "Analyze complete, starting scan...")
    (doseq [e (:compunits ast)]
      (when
        (not (empty? (filter #(filters/invocation-matches? % search-class search-method) (ast/ast-seq e))))
        (println (.toUri (.getSourceFile e)))))))
