javaast
=======

clojure utility for reading java source AST

the javaast.analyze function takes
- sequence of strings for the collection of files to be parsed/analyzed
- sequence of strings for classpath entries to be passed to the compiler
and returns a map with two keys
:compunits - a sequence of the CompilationUnitTree objects returned from the parsing
:elements - a sequence of the javax.lang.model.element.Element objects returned from the analyze

Each of the CompilationUnitTree values can then be depth first navigated with the javaast.ast-seq function (as per tree-seq).  Each node in the tree is an instance of com.sun.source.tree.Tree.
You can then apply filter/map and other sequence processing functions to find and manipulate the nodes you want.

The example function is a small program that can be used to search for objects that invoke a certain function on a certain class (e.g. who invokes java.lang.StringBuffer.append)

