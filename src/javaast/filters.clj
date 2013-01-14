(ns javaast.filters
    (:import [com.sun.source.tree Tree$Kind]
             [javax.lang.model.element ElementKind]
    ))

; TODO: rename to indicate this applies only to MethodInvocationNodes
(defn is-method?
  [node method-name]
  (-> (.getMethodSelect node)
      (.getIdentifier)
      (.contentEquals method-name)))

; TODO: rename to indicate this applies only to MethodInvocationNodes
(defn is-class?
  [node class-name]
  (-> (.getMethodSelect node)
      (.getExpression)
      (.type)
      (.asElement)
      (.getQualifiedName)
      (.contentEquals class-name)))

;TODO: rename to be more clear on what it does amidst more general filters
(defn invocation-matches?
  [node class-name method-name]
  (and
    ; for method invocations
    (= (.getKind node) Tree$Kind/METHOD_INVOCATION)
    ; on another class (don't care about classes using themselves - those would be of type IDENTIFIER - what about parent class?
    (= (.getKind (.getMethodSelect node)) Tree$Kind/MEMBER_SELECT)
    (is-class? node class-name)
    (is-method? node method-name)))

(defn get-annotation-element-value
  [mirror element-name]
  (let [matches (filter
                  #(.contentEquals (.getSimpleName (.getKey %)) element-name)
                  (.getElementValues mirror))]
                        ; String <- Attribute$Contant.getValue() <- AnnotationValue.getValue()
    (when (seq matches) (.getValue (.getValue (first matches))))))

(defn get-annotation-mirror
  [element annotation-name]
  (let [matches (filter 
                  #(.contentEquals (.getQualifiedName (.asElement (.getAnnotationType %))) annotation-name) 
                  (.getAnnotationMirrors element))]
    (when (seq matches) (first matches))))

(defn annotation-attributes-match?
  "Tests if the specified annotation mirror has the specified values for its attributes.
   attributes is a map of attribute names(strings) to attribute values (objects)"
  [mirror attributes]
  (every?
    (fn [[k v]]
      (let [ev (get-annotation-element-value mirror k)]
        (= ev v)))
    attributes))

(defn has-annotation?
  [element annotation-class annotation-attributes]
  (not (empty?
    (filter
      (fn [annotation-mirror]
        (and
          (-> (.getAnnotationType annotation-mirror)
              (.asElement)
              (.getQualifiedName)
              (.contentEquals annotation-class))
          (annotation-attributes-match? annotation-mirror annotation-attributes)))
      (.getAnnotationMirrors element)))))

(defn method-invocation-callee
  "Returns the element for the class of the object whose method is being invoked. only safe for member select"
  [method-invocation-node]
  (-> (.getMethodSelect method-invocation-node)
      (.getExpression)
      (.type)
      (.asElement)))


(defn invokes-annotated-type?
  "Finds all method invocations where the object that is being invoked on has the specified annotation with the specified values"
  [method-invocation-node annotation-class annotation-attributes]
  (and
    (= (.getKind method-invocation-node) Tree$Kind/METHOD_INVOCATION)
    (= (.getKind (.getMethodSelect method-invocation-node)) Tree$Kind/MEMBER_SELECT)
    (has-annotation? (method-invocation-callee method-invocation-node) annotation-class annotation-attributes)))

(defn element-package
  "Returns the element for the package that contains the specified element"
  [element]
  (cond
    (nil? element) nil
    (= (.getKind element) ElementKind/PACKAGE) element
    :else (recur (.getEnclosingElement element))))


(defn invokes-on-xml-type?
  [method-invocation-node xml-namespace type-name]
  (and
    (invokes-annotated-type? method-invocation-node "javax.xml.bind.annotation.XmlType" {"name" type-name})
    (has-annotation?
      ; the package of the invoked class
      (element-package (method-invocation-callee method-invocation-node))
      "javax.xml.bind.annotation.XmlSchema"
      {"namespace"  xml-namespace})))

