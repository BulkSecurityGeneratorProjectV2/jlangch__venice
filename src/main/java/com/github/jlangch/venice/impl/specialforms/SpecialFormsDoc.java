/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.specialforms;

import static com.github.jlangch.venice.impl.types.Constants.Nil;

import java.util.Map;

import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;


/**
 * The special form pseudo functions just serve for the cheat-sheet generation 
 * and the 'doc' function!
 */
public class SpecialFormsDoc {

	public static VncFunction doc = 
		new SpecialFormsDocFunction(
				"doc",
				VncFunction
					.meta()
					.arglists("(doc x)")
					.doc(
						"Prints documentation for a var or special form given x as its name. " +
						"Prints the definition of custom types. \n\n" +
						"Displays the source of a module if x is a module: (doc :ansi)")
					.examples(
						"(doc +)",
						"(doc def)",
						"(do \n" +
						"   (deftype :complex [real :long, imaginary :long]) \n" +
						"   (doc :complex))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
	
	public static VncFunction modules = 
		new SpecialFormsDocFunction(
				"modules",
				VncFunction
					.meta()
					.arglists("(modules)")
					.doc("Lists the available modules")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
			
	public static VncFunction list = 
		new SpecialFormsDocFunction(
				"()",
				VncFunction
					.meta()
					.doc("Creates a list.")
					.examples("'(10 20 30)")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction vector = 
		new SpecialFormsDocFunction(
				"[]",
				VncFunction
					.meta()
					.doc("Creates a vector.")
					.examples("[10 20 30]")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction set = 
		new SpecialFormsDocFunction(
				"#{}",
				VncFunction
					.meta()
					.doc("Creates a set.")
					.examples("#{10 20 30}")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction map = 
		new SpecialFormsDocFunction(
				"{}",
				VncFunction
					.meta()
					.doc("Creates a hash map.")
					.examples("{:a 10 :b 20}")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction resolve = 
		new SpecialFormsDocFunction(
				"resolve",
				VncFunction
					.meta()
					.arglists("(resolve symbol)")
					.doc("Resolves a symbol.")
					.examples(
						"(resolve '+)", 
						"(resolve 'y)", 
						"(resolve (symbol \"+\"))",
						"((-> \"first\" symbol resolve) [1 2 3])")
					.build()
		) {
		   private static final long serialVersionUID = -1;
		};

	public static VncFunction var_get = 
		new SpecialFormsDocFunction(
				"var-get",
				VncFunction
					.meta()
					.arglists("(var-get v)")
					.doc("Returns a var's value.")
					.examples(
						"(var-get +)",
						"(var-get '+)",
						"(var-get (symbol \"+\"))",
						"((var-get +) 1 2)",
						"(do \n" +
						"  (def x 10) \n" +
						"  (var-get 'x))")
					.seeAlso("var-ns", "var-name", "var-local?", "var-global?", "var-thread-local?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction var_ns = 
		new SpecialFormsDocFunction(
				"var-ns",
				VncFunction
					.meta()
					.arglists("(var-ns v)")
					.doc("Returns the namespace of the var's symbol")
					.examples(
						"(var-ns +)",
						"(var-ns '+)",
						"(var-ns (symbol \"+\"))",
						";; aliased function \n" +
						"(do \n" +
						"  (ns foo) \n" +
						"  (def add +)\n" +
						"  (var-ns add))",
						"(do  \n" +
						"  (def x 10) \n" +
						"  (var-ns x))",
						"(let [x 10]\n" +
						"  (var-ns x))",
						";; compare with namespace \n" +
						"(do \n" +
						"  (ns foo) \n" +
						"  (def add +)\n" +
						"  (namespace add))",
						";; compare aliased function with namespace \n" +
						"(do \n" +
						"  (ns foo) \n" +
						"  (def add +)\n" +
						"  (namespace add))")
					.seeAlso("namespace", "var-get", "var-name", "var-local?", "var-global?", "var-thread-local?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction var_name = 
		new SpecialFormsDocFunction(
				"var-name",
				VncFunction
					.meta()
					.arglists("(var-name v)")
					.doc("Returns the name of the var's symbol")
					.examples(
							"(var-name +)",
							"(var-name '+)",
							"(var-name (symbol \"+\"))",
							";; aliased function \n" +
							"(do \n" +
							"  (ns foo) \n" +
							"  (def add +)\n" +
							"  (var-name add))",
							"(do \n" +
							"  (def x 10) \n" +
							"  (var-name x))",
							"(let [x 10] \n" +
							"  (var-name x))",
							";; compare with name \n" +
							"(do \n" +
							"  (ns foo) \n" +
							"  (def add +)\n" +
							"  (name add))",
							";; compare aliased function with name \n" +
							"(do \n" +
							"  (ns foo) \n" +
							"  (def add +)\n" +
							"  (name add))")
					.seeAlso("name", "var-get", "var-ns", "var-local?", "var-global?", "var-thread-local?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction var_local_QUESTION = 
		new SpecialFormsDocFunction(
				"var-local?",
				VncFunction
					.meta()
					.arglists("(var-local? v)")
					.doc("Returns true if the var is local else false")
					.examples(
							"(var-local? +)",
							"(var-local? '+)",
							"(var-local? (symbol \"+\"))",
							"(do               \n" +
							"  (def x 10)      \n" +
							"  (var-local? x))   ",
							"(let [x 10]       \n" +
							"  (var-local? x))   ")
					.seeAlso("var-get", "var-ns", "var-name", "var-global?", "var-thread-local?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction var_thread_local_QUESTION = 
		new SpecialFormsDocFunction(
				"var-thread-local?",
				VncFunction
					.meta()
					.arglists("(var-thread-local? v)")
					.doc("Returns true if the var is thread-local else false")
					.examples(
							"(binding [x 100] \n" +
							"  (var-local? x))")
					.seeAlso("var-get", "var-ns", "var-name", "var-local?", "var-global?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction var_global_QUESTION = 
		new SpecialFormsDocFunction(
				"var-global?",
				VncFunction
					.meta()
					.arglists("(var-global? v)")
					.doc("Returns true if the var is global else false")
					.examples(
							"(var-global? +)",
							"(var-global? '+)",
							"(var-global? (symbol \"+\"))",
							"(do                \n" +
							"  (def x 10)       \n" +
							"  (var-global? x))   ",
							"(let [x 10]        \n" +
							"  (var-global? x))   ")
					.seeAlso("var-get", "var-ns", "var-name", "var-local?", "var-thread-local?")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction fn = 
		new SpecialFormsDocFunction(
				"fn",
				VncFunction
					.meta()
					.arglists("(fn name? [params*] condition-map? expr*)")
					.doc("Defines an anonymous function.")
					.examples(
						"(do (def sum (fn [x y] (+ x y))) (sum 2 3))",
						
						"(map (fn double [x] (* 2 x)) (range 1 5))",
						
						"(map #(* 2 %) (range 1 5))",
						
						"(map #(* 2 %1) (range 1 5))",
						
						";; anonymous function with two params, the second is destructured\n" + 
						"(reduce (fn [m [k v]] (assoc m v k)) {} {:b 2 :a 1 :c 3})",
						
						";; defining a pre-condition                 \n" + 
						"(do                                         \n" +
						"   (def square-root                         \n" +
						"        (fn [x]                             \n" +
						"            { :pre [(>= x 0)] }             \n" +
						"            (. :java.lang.Math :sqrt x)))   \n" +
						"   (square-root 4))                           ",
						
						";; higher-order function                                           \n" + 
						"(do                                                                \n" +
						"   (def discount                                                   \n" +
						"        (fn [percentage]                                           \n" +
						"            { :pre [(and (>= percentage 0) (<= percentage 100))] } \n" +
						"            (fn [price] (- price (* price percentage 0.01)))))     \n" +
						"   ((discount 50) 300))                                              ")
					.seeAlso("defn", "defn-", "def")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction eval = 
		new SpecialFormsDocFunction(
				"eval",
				VncFunction
					.meta()
					.arglists("(eval form)")
					.doc("Evaluates the form data structure (not text!) and returns the result.")
					.examples(
						"(eval '(let [a 10] (+ 3 4 a)))",
						"(eval (list + 1 2 3))",
					 	"(let [s \"(+ 2 x)\" x 10]     \n" +
					 	"   (eval (read-string s))))     ")
					.seeAlso("read-string")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction def = 
		new SpecialFormsDocFunction(
				"def",
				VncFunction
					.meta()
					.arglists("(def name expr)")
					.doc("Creates a global variable.")
					.examples(
						 "(def x 5)",
						 "(def sum (fn [x y] (+ x y)))")
					.seeAlso("def", "defonce")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction defonce = 
		new SpecialFormsDocFunction(
				"defonce",
				VncFunction
					.meta()
					.arglists("(defonce name expr)")
					.doc("Creates a global variable that can not be overwritten")
					.examples("(defonce x 5)")
					.seeAlso("defonce", "def-dynamic")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction set_BANG = 
		new SpecialFormsDocFunction(
				"set!",
				VncFunction
					.meta()
					.arglists("(set! var-symbol expr)")
					.doc("Sets a global or thread-local variable to the value of the expression.")
					.examples(
						"(do                             \n" +
						"  (def x 10)                    \n" +
						"  (set! x 20)                   \n" +
						"  x)                              ",
						 
						"(do                             \n" +
						"   (def-dynamic x 100)          \n" +
						"   (set! x 200)                 \n" +
						"   x)                             ",
						
						"(do                             \n" +
						"   (def-dynamic x 100)          \n" +
						"   (with-out-str                \n" +
						"      (print x)                 \n" +
						"      (binding [x 200]          \n" +
						"        (print (str \"-\" x))   \n" +
						"        (set! x (inc x))        \n" +
						"        (print (str \"-\" x)))  \n" +
						"      (print (str \"-\" x))))     ")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction defmulti = 
		new SpecialFormsDocFunction(
				"defmulti",
				VncFunction
					.meta()
					.arglists("(defmulti name dispatch-fn)")
					.doc("Creates a new multimethod with the associated dispatch function.")
					.examples(
						"(do                                                                       \n" +
						"   ;;defmulti with dispatch function                                      \n" +
						"   (defmulti salary (fn[amount] (amount :t)))                             \n" +
						"                                                                          \n" +
						"   ;;defmethod provides a function implementation for a particular value  \n" +
						"   (defmethod salary \"com\" [amount] (+ (:b amount) (/ (:b amount) 2)))  \n" +
						"   (defmethod salary \"bon\" [amount] (+ (:b amount) 99))                 \n" +
						"   (defmethod salary :default  [amount] (:b amount))                      \n" +
						"                                                                          \n" +
						"   [(salary {:t \"com\" :b 1000})                                         \n" +
						"    (salary {:t \"bon\" :b 1000})                                         \n" +
						"    (salary {:t \"xxx\" :b 1000})]                                        \n" +
						")                                                                           ")
					.seeAlso("defmethod")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction defmethod = 
		new SpecialFormsDocFunction(
				"defmethod",
				VncFunction
					.meta()
					.arglists("(defmethod multifn-name dispatch-val & fn-tail)")		
					.doc("Creates a new method for a multimethod associated with a dispatch-value.")
					.examples(
							"(do                                                                       \n" +
							"   ;;defmulti with dispatch function                                      \n" +
							"   (defmulti salary (fn [amount] (amount :t)))                            \n" +
							"                                                                          \n" +
							"   ;;defmethod provides a function implementation for a particular value  \n" +
							"   (defmethod salary \"com\" [amount] (+ (:b amount) (/ (:b amount) 2)))  \n" +
							"   (defmethod salary \"bon\" [amount] (+ (:b amount) 99))                 \n" +
							"   (defmethod salary :default  [amount] (:b amount))                      \n" +
							"                                                                          \n" +
							"   [(salary {:t \"com\" :b 1000})                                         \n" +
							"    (salary {:t \"bon\" :b 1000})                                         \n" +
							"    (salary {:t \"xxx\" :b 1000})]                                        \n" +
							")                                                                           ")
					.seeAlso("defmulti")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction deftype = 
		new SpecialFormsDocFunction(
				"deftype",
				VncFunction
					.meta()
					.arglists(
						"(deftype name fields)",
						"(deftype name fields validator)")
					.doc(
						"Defines a new custom type for the name with the fields.")
					.examples(
						"(do                                                      \n" +
						"  (ns foo)                                               \n" +
						"  (deftype :complex [real :long, imaginary :long])       \n" +
						"  ; explicitly creating a custom type value              \n" +
						"  (def x (.: :complex 100 200))                          \n" +
						"  ; Venice implicitly creates a builder function         \n" +
						"  ; suffixed with a '.'                                  \n" +
						"  (def y (complex. 200 300))                             \n" +
						"  ; ... and a type check function                        \n" +
						"  (complex? y)                                           \n" +
						"  y)                                                       ",
						"(do                                                      \n" +
						"  (ns foo)                                               \n" +
						"  (deftype :complex [real :long, imaginary :long])       \n" +
						"  (def x (complex. 100 200))                             \n" +
						"  (type x))                                                ",
						"(do                                                      \n" +
						"  (ns foo)                                               \n" +
						"  (deftype :complex                                                           \n" +
						"           [real :long, imaginary :long]                                      \n" +
						"           (fn [t]                                                            \n" +
						"              (assert (pos? (:real t)) \"real must be positive\")             \n" +
						"              (assert (pos? (:imaginary t)) \"imaginary must be positive\"))) \n" +
						"  (def x (complex. 100 200))                                                  \n" +
						"  [(:real x) (:imaginary x)])                                                   ",
						"(do                                                      \n" +
						"  (ns foo)                                               \n" +
						"  (deftype :named [name :string, value :any])            \n" +
						"  (def x (named. \"count\" 200))                         \n" +
						"  (def y (named. \"seq\" [1 2]))                         \n" +
						"  [x y])                                                   ")
					.seeAlso("deftype?", "deftype-of", "deftype-or", ".:")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction deftypeQ = 
		new SpecialFormsDocFunction(
				"deftype?",
				VncFunction
					.meta()
					.arglists(
						"(deftype? type)")
					.doc(
						"Returns true if type is a custom type else false.")
					.examples(
						"(do                                                 \n" +
						"  (ns foo)                                          \n" +
						"  (deftype :complex [real :long, imaginary :long])  \n" +
						"  (deftype? :complex))                                ",
						"(do                                                 \n" +
						"  (ns foo)                                          \n" +
						"  (deftype-of :email-address :string)               \n" +
						"  (deftype? :email-address))                          ",
						"(do                                                 \n" +
						"  (ns foo)                                          \n" +
						"  (deftype :complex [real :long, imaginary :long])  \n" +
						"  (def x (complex. 100 200))                        \n" +
						"  (deftype? (type x)))                                ")
					.seeAlso("deftype", "deftype-of", "deftype-or", ".:")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction deftype_of = 
		new SpecialFormsDocFunction(
				"deftype-of",
				VncFunction
					.meta()
					.arglists(
						"(deftype-of name base-type)",
						"(deftype-of name base-type validator)")
					.doc(
						"Defines a new custom type wrapper based on a base type.")
					.examples(
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :email-address :string)                         \n" +
						"  ; explicitly creating a wrapper type value                  \n" +
						"  (def x (.: :email-address \"foo@foo.org\"))                 \n" +
						"  ; Venice implicitly creates a builder function              \n" +
						"  ; suffixed with a '.'                                       \n" +
						"  (def y (email-address. \"foo@foo.org\"))                    \n" +
						"  ; ... and a type check function                             \n" +
						"  (email-address? y)                                          \n" +
						"  y)                                                            ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :email-address :string)                         \n" +
						"  (str \"Email: \" (email-address. \"foo@foo.org\")))           ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :email-address :string)                         \n" +
						"  (def x (email-address. \"foo@foo.org\"))                    \n" +
						"  [(type x) (supertype x)])                                     ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :email-address                                  \n" +
						"              :string                                         \n" +
						"              str/valid-email-addr?)                          \n" +
						"  (email-address. \"foo@foo.org\"))                             ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :contract-id :long)                             \n" +
						"  (contract-id. 100000))                                        ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-of :my-long :long)                                 \n" +
						"  (+ 10 (my-long. 100000)))                                     ")
					.seeAlso("deftype", "deftype?", "deftype-or", ".:")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction deftype_or = 
		new SpecialFormsDocFunction(
				"deftype-or",
				VncFunction
					.meta()
					.arglists(
						"(deftype-or name val*)")
					.doc(
						"Defines a new custom or type.")
					.examples(
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-or :color :red :green :blue)                       \n" +
						"  ; explicitly creating a wrapper type value                  \n" +
						"  (def x (.: :color :red))                                    \n" +
						"  ; Venice implicitly creates a builder function              \n" +
						"  ; suffixed with a '.'                                       \n" +
						"  (def y (color. :red))                                       \n" +
						"  ; ... and a type check function                             \n" +
						"  (color? y)                                                  \n" +
						"  y)                                                            ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-or :digit 0 1 2 3 4 5 6 7 8 9)                     \n" +
						"  (digit. 1))                                                   ",
						"(do                                                           \n" +
						"  (ns foo)                                                    \n" +
						"  (deftype-or :long-or-double :long :double)                  \n" +
						"  (long-or-double. 1000))                                       ")
				.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction deftype_new = 
		new SpecialFormsDocFunction(
				".:",
				VncFunction
					.meta()
					.arglists("(.: type-name args*)")
					.doc("Instantiates a custom type.")
					.examples(
						"(do                                                      \n" +
						"  (ns foo)                                               \n" +
						"  (deftype :complex [real :long, imaginary :long])       \n" +
						"  (def x (.: :complex 100 200))                          \n" +
						"  [(:real x) (:imaginary x)])                              ")
					.seeAlso("deftype", "deftype?", "deftype-of", "deftype-or")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction def_dynamic = 
		new SpecialFormsDocFunction(
				"def-dynamic",
				VncFunction
					.meta()
					.arglists("(def-dynamic name expr)")
					.doc(
						"Creates a dynamic variable that starts off as a global variable " +
						"and can be bound with 'binding' to a new value on the local thread.")
					.examples(
						"(do                      \n" +
						"   (def-dynamic x 100)   \n" +
						"   (println x)           \n" +
						"   (binding [x 200]      \n" +
						"      (println x))       \n" +
						"   (println x)))           ")
					.seeAlso("def", "defonce")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction binding = 
		new SpecialFormsDocFunction(
				"binding",
				VncFunction
					.meta()
					.arglists("(binding [bindings*] exprs*)")
					.doc("Evaluates the expressions and binds the values to dynamic (thread-local) symbols")
					.examples(
						"(do                      \n" +
						"   (binding [x 100]      \n" +
						"      (println x)        \n" +
						"      (binding [x 200]   \n" +
						"         (println x))    \n" +
						"      (println x)))        ")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction bound_Q = 
		new SpecialFormsDocFunction(
				"bound?",
				VncFunction
					.meta()
					.arglists("(bound? s)")
					.doc("Returns true if the symbol is bound to a value else false")
					.examples(
						"(bound? 'test)",
						"(let [test 100] (bound? 'test))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction quote = 
		new SpecialFormsDocFunction(
				"quote",
				VncFunction
					.meta()
					.arglists("(quote form)")
					.doc(
						"There are two equivalent ways to quote a form either with " +
						"quote or with '. They prevent the quoted form from being " +
						"evaluated.\n\n" +
						"Regular quotes work recursively with any kind of forms and " +
						"types: strings, maps, lists, vectors...")
					.examples(
						"(quote (1 2 3))",
						"(quote (+ 1 2))",
						"'(1 2 3)",
						"'(+ 1 2)",
						"'(a (b (c d (+ 1 2))))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction quasiquote = 
		new SpecialFormsDocFunction(
				"quasiquote",
				VncFunction
					.meta()
					.arglists("(quasiquote form)")
					.doc(
						"Quasi quotes also called syntax quotes (a backquote) supress " +
						"evaluation of the form that follows it and all the nested forms." +
						"\n\n" +
						"unquote:\n" +
						"It is possible to unquote part of the form that is quoted with `~`. " +
						"Unquoting allows you to evaluate parts of the syntax quoted expression." +
						"\n\n" +
						"unquote-splicing:\n" +
						"Unquote evaluates to a collection of values and inserts the " +
						"collection into the quoted form. But sometimes you want to " +
						"unquote a list and insert its elements (not the list) inside " +
						"the quoted form. This is where `~@` (unquote-splicing) comes " +
						"to rescue.")
					.examples(
						"(quasiquote (16 17 (inc 17)))",
						"`(16 17 (inc 17))",
						"`(16 17 ~(inc 17))",
						"`(16 17 ~(map inc [16 17]))",
						"`(16 17 ~@(map inc [16 17]))",
						"`(1 2 ~@#{1 2 3})",
						"`(1 2 ~@{:a 1 :b 2 :c 3})")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction do_ = 
		new SpecialFormsDocFunction(
				"do",
				VncFunction
					.meta()
					.arglists("(do exprs)")
					.doc("Evaluates the expressions in order and returns the value of the last.")
					.examples("(do (println \"Test...\") (+ 1 1))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction dorun = 
		new SpecialFormsDocFunction(
				"dorun",
				VncFunction
					.meta()
					.arglists("(dorun count expr)")
					.doc(
						"Runs the expr count times in the most effective way. It's main purpose is " +
						"supporting benchmark test. Returns the expression result of the first " +
						"invocation.")
					.examples("(dorun 10 (+ 1 1))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction dobench = 
		new SpecialFormsDocFunction(
				"dobench",
				VncFunction
					.meta()
					.arglists("(dobench count expr)")
					.doc(
						"Runs the expr count times in the most effective way and returns a list of " +
						"elapsed nanoseconds for each invocation. It's main purpose is supporting " +
						"benchmark test.")
					.examples("(dobench 10 (+ 1 1))")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction prof = 
		new SpecialFormsDocFunction(
				"prof",
				VncFunction
					.meta()
					.arglists("(prof opts)")
					.doc(
						"Controls the code profiling. See the companion functions/macros " +
						"'dorun' and 'perf'. The perf macro is built on prof and dorun and " +
						"provides all for simple Venice profiling.\n\n" +
						"The profiler reports a function's elapsed time as \"time with children\"! \n\n" +
						"Profiling recursive functions: \n" +
						"Because the profiler reports \"time with children\" and accumulates the " +
						"elapsed time across all recursive calls the resulting time for a " +
						"particular recursive function is higher than the effective time.")
					.examples(
						"(do  \n" +
						"  (prof :on)   ; turn profiler on  \n" +
						"  (prof :off)   ; turn profiler off  \n" +
						"  (prof :status)   ; returns the profiler on/off staus  \n" +
						"  (prof :clear)   ; clear profiler data captured so far  \n" +
						"  (prof :data)   ; returns the profiler data as map  \n" +
						"  (prof :data-formatted)   ; returns the profiler data as formatted text  \n" +
						"  (prof :data-formatted \"Metrics test\")   ; returns the profiler data as formatted text with a title  \n" +
						"  nil)  ")
					.seeAlso("perf", "time")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction if_ = 
		new SpecialFormsDocFunction(
				"if",
				VncFunction
					.meta()
					.arglists("(if test then else)", "(if test then)")
					.doc("Evaluates test. If logical true, evaluates and returns then expression, " +
						 "otherwise else expression, if supplied, else nil.")
					.examples(
						"(if (< 10 20) \"yes\" \"no\")",
						"(if true \"yes\")",
						"(if false \"yes\")")
					.seeAlso("if-not", "when")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction let = 
		new SpecialFormsDocFunction(
				"let",
				VncFunction
					.meta()
					.arglists("(let [bindings*] exprs*)")
					.doc(
						"Evaluates the expressions and binds the values to symbols in " +
						"the new local context.")
					.examples(
						"(let [x 1] x)",
						
						";; destructured map                     \n" +
						"(let [{:keys [width height title ]      \n" +
						"       :or {width 640 height 500}       \n" +
						"       :as styles}                      \n" +
						"      {:width 1000 :title \"Title\"}]   \n" +
						"     (println \"width: \" width)        \n" +
						"     (println \"height: \" height)      \n" +
						"     (println \"title: \" title)        \n" +
						"     (println \"styles: \" styles))       ")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction loop = 
		new SpecialFormsDocFunction(
				"loop",
				VncFunction
					.meta()
					.arglists("(loop [bindings*] exprs*)")
					.doc(
						"Evaluates the exprs and binds the bindings. " + 
						"Creates a recursion point with the bindings.")
					.examples(
						";; tail recursion                                   \n" +
						"(loop [x 10]                                        \n" +
						"   (when (> x 1)                                    \n" +
						"      (println x)                                   \n" +
						"      (recur (- x 2))))                               ",
				
						";; tail recursion                                   \n" +
						"(do                                                 \n" +
						"   (defn sum [n]                                    \n" +
						"         (loop [cnt n acc 0]                        \n" +
						"            (if (zero? cnt)                         \n" +
						"                acc                                 \n" +
						"                (recur (dec cnt) (+ acc cnt)))))    \n" +
						"   (sum 10000))                                       ")
					.seeAlso("recur")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction recur = 
		new SpecialFormsDocFunction(
				"recur",
				VncFunction
					.meta()
					.arglists("(recur expr*)")
					.doc(
						"Evaluates the exprs and rebinds the bindings of the recursion " + 
						"point to the values of the exprs. The recur expression must be " +
						"at the tail position. The tail position is a postion which an " +
						"expression would return a value from.")
					.examples(
						";; tail recursion                                   \n" +
						"(loop [x 10]                                        \n" +
						"   (when (> x 1)                                    \n" +
						"      (println x)                                   \n" +
						"      (recur (- x 2))))                               ",
				
						";; tail recursion                                   \n" +
						"(do                                                 \n" +
						"   (defn sum [n]                                    \n" +
						"         (loop [cnt n acc 0]                        \n" +
						"            (if (zero? cnt)                         \n" +
						"                acc                                 \n" +
						"                (recur (dec cnt) (+ acc cnt)))))    \n" +
						"   (sum 10000))                                       ")
					.seeAlso("loop")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction try_ = 
		new SpecialFormsDocFunction(
				"try",
				VncFunction
					.meta()
					.arglists(
						"(try expr)",
						"(try expr (catch exClass exSym expr))",
						"(try expr (catch exClass exSym expr) (finally expr))")
					.doc("Exception handling: try - catch -finally ")
					.examples(
						"(try (throw))",
						
						"(try                                      \n" +
						"   (throw \"test message\"))                ",
						
						"(try                                       \n" +
						"   (throw 100)                             \n" +
						"   (catch :java.lang.Exception ex -100))    ",
						
						"(try                                       \n" +
						"   (throw 100)                             \n" +
						"   (finally (println \"...finally\")))       ",
						
						"(try                                       \n" +
						"   (throw 100)                             \n" +
						"   (catch :java.lang.Exception ex -100)    \n" +
						"   (finally (println \"...finally\")))       ",
						
						"(do                                                  \n" +
						"   (import :java.lang.RuntimeException)              \n" +
						"   (try                                              \n" +
						"      (throw (. :RuntimeException :new \"message\")) \n" +
						"      (catch :RuntimeException ex (:message ex))))   \n",
						
						"(do                                                   \n" +
						"   (try                                               \n" +
						"      (throw [1 2 3])                                 \n" +
						"      (catch :ValueException ex (str (:value ex)))    \n" +
						"      (catch :RuntimeException ex \"runtime ex\")     \n" +
						"      (finally (println \"...finally\"))))             ")
					.seeAlso("try-with")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction try_with = 
		new SpecialFormsDocFunction(
				"try-with",
				VncFunction
					.meta()
					.arglists(
						"(try-with [bindings*] expr)",
						"(try-with [bindings*] expr (catch :java.lang.Exception ex expr))",
						"(try-with [bindings*] expr (catch :java.lang.Exception ex expr) (finally expr))")		
					.doc("try-with resources allows the declaration of resources to be used in a try block "
							+ "with the assurance that the resources will be closed after execution "
							+ "of that block. The resources declared must implement the Closeable or ")
					.examples(
						"(do                                                   \n" +
						"   (import :java.io.FileInputStream)                  \n" +
						"   (let [file (io/temp-file \"test-\", \".txt\")]     \n" +
						"        (io/spit file \"123456789\" :append true)     \n" +
						"        (try-with [is (. :FileInputStream :new file)] \n" +
						"           (io/slurp-stream is :binary false))))        ")
					.seeAlso("try")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};


	public static VncFunction locking = 
		new SpecialFormsDocFunction(
				"locking",
				VncFunction
					.meta()
					.arglists("(locking x & exprs)")
					.doc(
						"Executes exprs in an implicit do, while holding the monitor of x. \n" + 
						"Will release the monitor of x in all circumstances. \n" +
						"Locking operates like the synchronized keyword in Java.")
					.examples(
						"(do                        \n" +
						"   (def x 1)               \n" +
						"   (locking x              \n" +
						"      (println 100)        \n" +
						"      (println 200)))        ",
						";; Locks are reentrant     \n" +
						"(do                        \n" +
						"   (def x 1)               \n" +
						"   (locking x              \n" +
						"      (locking x           \n" +
						"         (println \"in\")) \n" +
						"      (println \"out\")))    ")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction defmacro = 
		new SpecialFormsDocFunction(
				"defmacro",
				VncFunction
					.meta()
					.arglists("(defmacro name [params*] body)")
					.doc("Macro definition")
					.examples(
						"(defmacro unless [pred a b]   \n" + 
						"  `(if (not ~pred) ~a ~b))      ")
					.seeAlso("macroexpand", "macroexpand-all")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
	
	public static VncFunction macroexpand = 
		new SpecialFormsDocFunction(
				"macroexpand",
				VncFunction
					.meta()
					.arglists("(macroexpand form)")		
					.doc(
						"If form represents a macro form, returns its expansion, else " + 
						"returns form.\n\n" +
						"To recursively expand all macros in a form use (macroexpand-all form).")
					.examples("(macroexpand '(-> c (+ 3) (* 2)))")
					.seeAlso("defmacro", "macroexpand-all")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction import_ = 
		new SpecialFormsDocFunction(
				"import",
				VncFunction
					.meta()
					.arglists("(import class)")
					.doc(
						"Imports a Java class. Imports are bound to the current namespace.")
					.examples(
						"(do                        \n" +
						"  (import :java.lang.Math) \n" +
						"  (. :Math :max 2 10))      ",
						"(do                                                                \n" +
						"  (ns alpha)                                                       \n" +
						"  (import :java.lang.Math)                                         \n" +
						"  (println \"alpha:\" (any? #(== % :java.lang.Math) (imports)))    \n" +
						"                                                                   \n" +
						"  (ns beta)                                                        \n" +
						"  (println \"beta:\" (any? #(== % :java.lang.Math) (imports)))     \n" +
						"                                                                   \n" +
						"  (ns alpha)                                                       \n" +
						"  (println \"alpha:\" (any? #(== % :java.lang.Math) (imports)))    \n" +
						")")
					.seeAlso("imports")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction imports_ = 
		new SpecialFormsDocFunction(
				"imports",
				VncFunction
					.meta()
					.arglists("(imports)")
					.doc("List the registered imports for the current namespace.")
					.examples(
						"(do                        \n" +
						"  (import :java.lang.Math) \n" +
						"  (imports))                 ")
					.seeAlso("import")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction ns_new = 
		new SpecialFormsDocFunction(
				"ns",
				VncFunction
					.meta()
					.arglists("(ns sym)")
					.doc("Opens a namespace.")
					.examples(
						"(do                               \n" + 
						"  (ns xxx)                        \n" + 
						"  (def foo 1)                     \n" + 
						"  (ns yyy)                        \n" + 
						"  (def foo 5)                     \n" + 
						"  (println xxx/foo foo yyy/foo))    ")
					.seeAlso("ns-unmap", "ns-remove", "ns-list", "namespace", "var-ns")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction ns_unmap = 
		new SpecialFormsDocFunction(
				"ns-unmap",
				VncFunction
					.meta()
					.arglists("(ns-unmap ns sym)")
					.doc("Removes the mappings for the symbol from the namespace.")
					.examples(
						"(do                    \n" + 
						"  (ns xxx)             \n" + 
						"  (def foo 1)          \n" + 
						"  (ns-unmap xxx foo)   \n" + 
						"  (ns-unmap *ns* foo))   ")
					.seeAlso("ns", "ns-remove", "ns-list", "namespace", "var-ns")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction ns_remove = 
		new SpecialFormsDocFunction(
				"ns-remove",
				VncFunction
					.meta()
					.arglists("(ns-remove ns)")
					.doc("Removes the mappings for all symbols from the namespace.")
					.examples(
						"(do                                    \n" + 
						"  (ns xxx)                             \n" + 
						"  (def xoo 1)                          \n" + 
						"  (ns yyy)                             \n" + 
						"  (def yoo 1)                          \n" + 
						"  (ns-remove xxx)                      \n" + 
						"  (ns-remove *ns*)                     \n" +
						"  (println \"ns xxx:\" (ns-list xxx))  \n" + 
						"  (println \"ns yyy:\" (ns-list yyy)))   ")
					.seeAlso("ns", "ns-unmap", "ns-list", "namespace", "var-ns")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction ns_list = 
		new SpecialFormsDocFunction(
				"ns-list",
				VncFunction
					.meta()
					.arglists("(ns-list ns)")
					.doc("Lists all the symbols in the namespace ns.")
					.examples("(ns-list regex)")
					.seeAlso("ns", "ns-unmap", "ns-remove", "namespace", "var-ns")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction namespace = 
		new SpecialFormsDocFunction(
				"namespace",
				VncFunction
					.meta()
					.arglists("(namespace x)")
					.doc("Returns the namespace string of a symbol, keyword, or function.")
					.examples(
						"(namespace 'user/foo)",
						"(namespace :user/foo)",
						"(namespace +)",
						"(do \n" +
						"  (ns foo) \n" +
						"  (def add +) \n" +
						"  (namespace add))",
						";; compare with var-ns \n" +
						"(var-ns +)",
						";; compare alias def'd function with var-ns \n" +
						"(do \n" +
						"  (ns foo) \n" +
						"  (def add +)\n" +
						"  (var-ns add))")
					.seeAlso("ns", "var-ns")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
		
	public static VncFunction tail_pos = 
		new SpecialFormsDocFunction(
				"tail-pos",
				VncFunction
					.meta()
					.arglists("(tail-pos)", "(tail-pos name)")
					.doc(
						"Throws a NotInTailPositionException if the expr is not in " +
						"tail position otherwise returns nil. \n\n" +
						"Definition: \n" +
						"The tail position is a position which an expression would " +
						"return a value from. There are no more forms evaluated after " +
						"the form in the tail position is evaluated. ")
					.examples(
						";; in tail position \n" +
						"(do 1 (tail-pos))",
						";; not in tail position \n" +
						"(do (tail-pos) 1)")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
		
	public static VncFunction print_highlight = 
		new SpecialFormsDocFunction(
				"print-highlight",
				VncFunction
					.meta()
					.arglists("(print-highlight form)")
					.doc(
						"Prints the form highlighted to *out*")
					.examples(
						"(print-highlight \"(+ 1 2)\")")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_version = 
		new SpecialFormsDocFunction(
				"*version*",
				VncFunction
					.meta()
					.doc("The Venice version")
					.examples("*version*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_newline = 
		new SpecialFormsDocFunction(
				"*newline*",
				VncFunction
					.meta()
					.doc("The system newline")
					.examples("*newline*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_loaded_modules = 
		new SpecialFormsDocFunction(
				"*loaded-modules*",
				VncFunction
					.meta()
					.doc("The loaded modules")
					.examples("*loaded-modules*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_loaded_files = 
		new SpecialFormsDocFunction(
				"*loaded-files*",
				VncFunction
					.meta()
					.doc("The loaded files")
					.examples("*loaded-files*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_ns = 
		new SpecialFormsDocFunction(
				"*ns*",
				VncFunction
					.meta()
					.doc("The current namespace")
					.examples(
						"*ns*", 
						"(do \n" +
						"  (ns test) \n" +
						"  *ns*)")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};

	public static VncFunction global_var_run_mode = 
		new SpecialFormsDocFunction(
				"*run-mode*",
				VncFunction
					.meta()
					.doc("The current run-mode one of (:repl, :script, :app)")
					.examples(
						"*run-mode*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
	

	public static VncFunction global_var_ansi_term = 
		new SpecialFormsDocFunction(
				"*ansi-term*",
				VncFunction
					.meta()
					.doc("True if Venice runs in an ANSI terminal, otherwise false")
					.examples(
						"*ansi-term*")
					.build()
		) {
			private static final long serialVersionUID = -1;
		};
	
	private static class SpecialFormsDocFunction extends VncFunction {
		public SpecialFormsDocFunction(final String name, final VncVal meta) {
			super(name, meta);
		}
		
		public VncVal apply(final VncList args) {
			return Nil;
		}
		
		private static final long serialVersionUID = -1;
	};
	
	
	public static Map<VncVal, VncVal> ns = 
			new VncHashMap
					.Builder()
					.put(new VncSymbol("doc"),				doc)
					.put(new VncSymbol("modules"),			modules)
					.put(new VncSymbol("()"),				list)
					.put(new VncSymbol("[]"),				vector)
					.put(new VncSymbol("#{}"),				set)
					.put(new VncSymbol("{}"),				map)
					.put(new VncSymbol("fn"),				fn)
					.put(new VncSymbol("eval"),				eval)
					.put(new VncSymbol("resolve"),			resolve)
					.put(new VncSymbol("var-get"),			var_get)
					.put(new VncSymbol("var-name"),			var_name)
					.put(new VncSymbol("var-ns"),			var_ns)
					.put(new VncSymbol("var-local?"),		var_local_QUESTION)
					.put(new VncSymbol("var-thread-local?"),var_thread_local_QUESTION)
					.put(new VncSymbol("var-global?"),		var_global_QUESTION)
					.put(new VncSymbol("def"),				def)
					.put(new VncSymbol("defonce"),			defonce)
					.put(new VncSymbol("defmulti"),			defmulti)
					.put(new VncSymbol("defmethod"),		defmethod)
					.put(new VncSymbol("deftype"),			deftype)
					.put(new VncSymbol("deftype?"),			deftypeQ)
					.put(new VncSymbol("deftype-of"),		deftype_of)
					.put(new VncSymbol("deftype-or"),		deftype_or)
					.put(new VncSymbol(".:"),				deftype_new)
					.put(new VncSymbol("def-dynamic"),		def_dynamic)
					.put(new VncSymbol("binding"),			binding)
					.put(new VncSymbol("bound?"),			bound_Q)
					.put(new VncSymbol("set!"),				set_BANG)
					.put(new VncSymbol("quote"),			quote)
					.put(new VncSymbol("quasiquote"),		quasiquote)
					.put(new VncSymbol("do"),				do_)
					.put(new VncSymbol("if"),				if_)
					.put(new VncSymbol("let"),				let)
					.put(new VncSymbol("loop"),				loop)
					.put(new VncSymbol("recur"),			recur)
					.put(new VncSymbol("try"),				try_)
					.put(new VncSymbol("try-with"),			try_with)
					.put(new VncSymbol("locking"),			locking)
					.put(new VncSymbol("defmacro"),			defmacro)
					.put(new VncSymbol("macroexpand"),		macroexpand)
					.put(new VncSymbol("ns"),				ns_new)
					.put(new VncSymbol("ns-unmap"),			ns_unmap)
					.put(new VncSymbol("ns-remove"),		ns_remove)
					.put(new VncSymbol("ns-list"),			ns_list)
					.put(new VncSymbol("namespace"),		namespace)
					.put(new VncSymbol("tail-pos"),			tail_pos)					
					.put(new VncSymbol("print-highlight"),	print_highlight)					
					.put(new VncSymbol("import"),			import_)
					.put(new VncSymbol("imports"),			imports_)
					.put(new VncSymbol("dobench"),			dobench)
					.put(new VncSymbol("dorun"),			dorun)
					.put(new VncSymbol("prof"),				prof)
					.put(new VncSymbol("*version*"),		global_var_version)
					.put(new VncSymbol("*newline*"),		global_var_newline)
					.put(new VncSymbol("*loaded-modules*"),	global_var_loaded_modules)
					.put(new VncSymbol("*loaded-files*"),	global_var_loaded_files)
					.put(new VncSymbol("*ns*"),				global_var_ns)
					.put(new VncSymbol("*run-mode*"),		global_var_run_mode)
					.put(new VncSymbol("*ansi-term*"),		global_var_ansi_term)
					.toMap();
}
