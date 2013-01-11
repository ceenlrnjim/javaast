(ns javaast
  (:import [javax.tools ToolProvider]))

; TODO: may want a has-children? to support tree-seq without calling children twice
(defprotocol ParentNode
  (children [n]))

(extend com.sun.source.tree.CompilationUnitTree
  ParentNode
  {:children (fn [this] (-> (seq (.getPackageAnnotations this))
                            (conj (.getPackageName this))
                            (concat (.getImports this))
                            (concat (.getTypeDecls this))))})

(extend com.sun.source.tree.ImportTree
  ParentNode
  {:children #(vector (.getQualifiedIdentifier %))})

(extend com.sun.source.tree.AnnotationTree
  ParentNode
  {:children #(seq (.getArguments %))})

(extend com.sun.source.tree.ArrayAccessTree
  ParentNode
  {:children #(vector (.getExpression %) (.getIndex %))})

(extend com.sun.source.tree.ArrayTypeTree
  ParentNode
  {:children #(vector (.getType %))})

(extend com.sun.source.tree.AssertTree
  ParentNode
  {:children #(vector (.getCondition %) (.getDetail %))})

(extend com.sun.source.tree.AssignmentTree
  ParentNode
  {:children #(vector (.getExpression %) (.getVariable %))})

(extend com.sun.source.tree.BinaryTree
  ParentNode
  {:children #(vector (.getLeftOperand %) (.getRightOperand %))})

(extend com.sun.source.tree.BlockTree
  ParentNode
  {:children #(seq (.getStatements %))})

(extend com.sun.source.tree.BreakTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.CaseTree
  ParentNode
  {:children #(conj (seq (.getStatements %)) (.getExpression %))})

(extend com.sun.source.tree.CatchTree
  ParentNode
  {:children #(vector (.getBlock %) (.getParameter %))})

(extend com.sun.source.tree.ClassTree
  ParentNode
  {:children #(-> (vector (.getExtendsClause %))
                  (concat (.getImplementsClause %)
                          (.getMembers %)
                          (.getTypeParameters %))
                  (conj (.getModifiers %)))})

(extend com.sun.source.tree.CompoundAssignmentTree
  ParentNode
  {:children #(vector (.getExpression %) (.getVariable %))})

(extend com.sun.source.tree.ConditionalExpressionTree
  ParentNode
  {:children #(vector (.getCondition %) (.getFalseExpression %) (.getTrueExpression %))})

(extend com.sun.source.tree.ContinueTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.DoWhileLoopTree
  ParentNode
  {:children #(vector (.getCondition %) (.getStatement %))})

(extend com.sun.source.tree.EmptyStatementTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.EnhancedForLoopTree
  ParentNode
  {:children #(vector (.getExpression %) (.getStatement %) (.getVariable %))})

(extend com.sun.source.tree.ErroneousTree
  ParentNode
  {:children #(seq (.getErrorTrees %))})

(extend com.sun.source.tree.ExpressionStatementTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.ForLoopTree
  ParentNode
  {:children #(-> (vector (.getCondition %))
                  (concat (.getInitializer %)
                          (.getUpdate %))
                  (conj (.getStatement %)))})

(extend com.sun.source.tree.IdentifierTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.IfTree
  ParentNode
  {:children #(vector (.getCondition %) (.getElseStatement %) (.getThenStatement %))})

(extend com.sun.source.tree.InstanceOfTree
  ParentNode
  {:children #(vector (.getExpression %) (.getType %))})

(extend com.sun.source.tree.LabeledStatementTree
  ParentNode
  {:children #(vector (.getStatement %))})

(extend com.sun.source.tree.LiteralTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.MemberSelectTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.MethodInvocationTree
  ParentNode
  {:children #(conj (concat (.getArguments %) (.getTypeArguments %)) (.getMethodSelect %))})


(extend com.sun.source.tree.MethodTree
  ParentNode
  {:children #(-> (vector (.getBody %)
                          (.getDefaultValue %)
                          (.getModifiers %)
                          (.getReturnType %))
                  (concat (.getParameters %)
                          (.getThrows %)
                          (.getTypeParameters %)))})

(extend com.sun.source.tree.ModifiersTree
  ParentNode
  {:children #(seq (.getAnnotations %))})

(extend com.sun.source.tree.NewArrayTree
  ParentNode
  {:children #(conj (concat (.getDimensions %) (.getInitializers %)) (.getType %))})

(extend com.sun.source.tree.NewClassTree
  ParentNode
  {:children #(concat (vector (.getClassBody %)
                              (.getEnclosingExpression %)
                              (.getIdentifier %))
                      (.getArguments %)
                      (.getTypeArguments %))})

(extend com.sun.source.tree.ParameterizedTypeTree
  ParentNode
  {:children #(conj (seq (.getTypeArguments %)) (.getType %))})

(extend com.sun.source.tree.ParenthesizedTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.PrimitiveTypeTree
  ParentNode
  {:children (fn [this] nil)})

(extend com.sun.source.tree.ReturnTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.SwitchTree
  ParentNode
  {:children #(conj (seq (.getCases %)) (.getExpression %))})

(extend com.sun.source.tree.SynchronizedTree
  ParentNode
  {:children #(vector (.getBlock %) (.getExpression %))})

(extend com.sun.source.tree.ThrowTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.TryTree
  ParentNode
  {:children #(concat [(.getBlock %)] (.getCatches %) [(.getFinallyBlock %)])})

(extend com.sun.source.tree.TypeCastTree
  ParentNode
  {:children #(vector (.getExpression %) (.getType %))})

(extend com.sun.source.tree.TypeParameterTree
  ParentNode
  {:children #(seq (.getBounds %))})

(extend com.sun.source.tree.UnaryTree
  ParentNode
  {:children #(vector (.getExpression %))})

(extend com.sun.source.tree.VariableTree
  ParentNode
  {:children #(vector (.getInitializer %) (.getModifiers %) (.getType %))})

(extend com.sun.source.tree.WhileLoopTree
  ParentNode
  {:children #(vector (.getCondition %) (.getStatement %))})

(extend com.sun.source.tree.WildcardTree
  ParentNode
  {:children #(vector (.getBound %))})

(defn ast-seq
  "returns a sequence on the nodes of the compilation unit tree"
  [node]
  (tree-seq
    #(and (not (nil? %)) (not (nil? (children %))))
    ; No null checking above in ParentNode implementations, so we can get nil in the children list
    (fn [n] (filter #(not (nil? %)) (children n)))
    node))

(defn- classpath-option
  [classpath-seq]
  (when (seq classpath-seq)
    ["-classpath" (reduce #(str %1 java.io.File/pathSeparator %2) "" classpath-seq)]))

(defn- get-task
  [compiler filemgr options target-seq]
  (.getTask compiler nil filemgr nil options nil (.getJavaFileObjectsFromStrings filemgr target-seq)))

; TODO: support for optionally passing other arguments to file manager and compiler
(defn analyze
  "Returns a map with keys :elements and :compunits.  
   takes a sequence of file names to be analyzed and a sequence of classpath items"
  [source-seq classpath-seq]
  (let [compiler (javax.tools.ToolProvider/getSystemJavaCompiler)
        filemgr (.getStandardFileManager compiler nil nil nil)
        comptask (get-task compiler filemgr (classpath-option classpath-seq) source-seq)
        cutvec (vec (.parse comptask))
        elemvec (vec (.analyze comptask))]
    {:elements elemvec
     :compunits cutvec}))

