(ns javaast.example
  (:require [javaast.fileutil :as files])
  (:require [javaast :as ast])
  (:gen-class)
  (:import [com.sun.source.tree Tree Tree$Kind]))
           

(defn is-method?
  [node method-name]
  (-> (.getMethodSelect node)
      (.getIdentifier)
      (.contentEquals method-name)))

(defn is-class?
  [node class-name]
  (-> (.getMethodSelect node)
      (.getExpression)
      (.type)
      (.asElement)
      (.getQualifiedName)
      (.contentEquals class-name)))

(defn invocation-matches?
  [node class-name method-name]
  (and
    ; for method invocations
    (= (.getKind node) Tree$Kind/METHOD_INVOCATION)
    ; on another class (don't care about classes using themselves - those would be of type IDENTIFIER - what about parent class?
    (= (.getKind (.getMethodSelect node)) Tree$Kind/MEMBER_SELECT)
    (is-class? node class-name)
    (is-method? node method-name)))


(defn -main [& args]
  (let [[filelist dot-classpath search-class search-method & more] args
        ast (ast/analyze (files/file-list-contents filelist) (files/file-list-contents dot-classpath))]
    (println "Analyze complete, starting scan...")
    (doseq [e (:compunits ast)]
      (when
        (not (empty? (filter #(invocation-matches? % search-class search-method) (ast/ast-seq e))))
        (println (.toUri (.getSourceFile e)))))))
