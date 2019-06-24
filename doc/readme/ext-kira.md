# Kira Templating

Kira is a simple templating module for Venice.


## Syntax

The `<% %>` tags are used to embed a section of Clojure code with side-effects. 
This is commonly used for control structures like loops or conditionals.

For example:

```clojure
(do
  (load-module :kira)

  (kira/eval """<% (docoll #(print (str %>foo<% % " ")) xs)%>""" 
             {:xs [1 2 3]}))

;;=> "foo1 foo2 foo3 "
```

The `<%= %>` tags will be substituted for the value of the expression within them. 
This is used for inserting values into a template.

For example:

```clojure
(do
  (load-module :kira)

  (kira/eval "Hello <%= name %>" {:name "Alice"}))

;;=> "Hello Alice"
```

### Customized Delimiters

The delimiters can be customized:

```clojure
(do
  (load-module :kira)

  (kira/eval """Hello ${= name }$""" ["${" "}$"] {:name "Alice"}))
  
;;=> "Hello Alice"
```

## API Documentation

### kira/eval

```clojure
(kira/eval source)
(kira/eval source bindings)
(kira/eval source delimiters bindings)
```

Evaluate a template source using an optional map of bindings. The template source 
can be a string, or any I/O source understood by the standard slurp function.

Example of use:

```clojure
(kira/eval 
   "Hello <%= name %>" 
   {:name "Bob"})

(kira/eval "Hello <%= name1 %> and <%= name2 %>" 
           {:name1 "Bob" 
            :name2 "Alice"})

(kira/eval "Hello <% (kira/emit (first names)) %> and <% (kira/emit (second names)) %>" 
           {:names ["Bob" "Alice"]})

(kira/eval 
   "Hello $= name $" ["$" "$"] 
   {:name "Bob"})
```

### kira/fn

```clojure
(kira/fn args source)
(kira/fn args source delimiters)
```

Compile a template source into a anonymous function. This is a lot faster than `kira/eval` 
for repeated calls, as the template source is only parsed when the function is created.

Examples of use:

```clojure
(do
  (load-module :kira)
  
  (def hello
    (kira/fn [name] "Hello <%= name %>"))

  (println (hello "Alice"))
  (println (hello "Bob")))
```

Defining a template with two scalar parameters:

```clojure
(do
  (load-module :kira)
  
  (def hello
    (kira/fn [name1 name2] "Hello <%= name1 %> and <% (kira/emit name2) %>"))

  (hello "Alice" "Bob")
  (hello "Miss Piggy" "Kermit"))
```

Defining a template with parameters passed as vector:

```clojure
(do
  (load-module :kira)
  
  (def hello
    (kira/fn [names] "Hello <% (kira/emit (first names)) %> and <% (kira/emit (second names)) %>"))

  (hello ["Alice" "Bob"])
  (hello ["Miss Piggy" "Kermit"]))
```



## Building blocks

### Escape output

#### Kira has built-in support for escaping XML/HTML:

```text
<% (kira/escape-xml text) %>
```

Example: 

```clojure
(do
  (load-module :kira)
  
  (def template (str/strip-indent """\
       <formula><% (kira/escape-xml formula) %></formula>"""))

  (def data { :formula "x > 100" })
  
  (println (kira/eval template data)))
```

Output:

```xml
<formula>x &gt; 100</formula>
```

#### Data conversion/formatting prior to escape XML: 

```text
<% (kira/escape-xml text fmt-fn) %>
```

Example: 


```clojure
(do
  (load-module :kira)
  
  (defn format-ts [t] (time/format t "yyyy-MM-dd"))
  
  (def template (str/strip-indent """\
       <birthdate><% (kira/escape-xml (:birth-date data) format-ts) %></birthdate>"""))

  (def data { :birth-date (time/local-date 2000 8 1) })
  
  (println (kira/eval template data)))
```

Output:

```xml
<birthdate>2000-08-01</birthdate>
```



#### Custom conversion

Any Venice functions can be used to escape/convert/format output:

```text
<% (print (format x)) %>
```

Example:

```clojure
(do
  (load-module :kira)
  
  (defn format-ts [t] (print (time/format t "yyyy-MM-dd HH:mm:ss")))
  
  (def template """timestamp: <% (format-ts timestamp) %>""")

  (def data { :timestamp (time/local-date-time) })
  
  (println (kira/eval template data)))
```

Output:

```text
timestamp: 2019-06-22 19:21:07
```


### Loops

```text
<% (kira/docoll coll (fn [item] (kira/emit %>
   ...
<% ))) %>
```

Loop over a collection of items:

```clojure
(do
  (load-module :kira)
  
  (def template (str/strip-indent """\
       <users>
         <% (kira/docoll users (fn [user] (kira/emit %>
         <user>
           <firstname><% (kira/escape-xml (:first user)) %></firstname>
           <lastname><% (kira/escape-xml (:last user)) %></lastname>
         </user>
         <% ))) %>
       </users>"""))

  (def data { :users [ {:first "Thomas" :last "Meier" }
                       {:first "Anna" :last "Steiger" } ]  })

  (println (kira/eval template data)))
```

Output:

```xml
<users>
  
  <user>
    <firstname>Thomas</firstname>
    <lastname>Meier</lastname>
  </user>
  
  <user>
    <firstname>Anna</firstname>
    <lastname>Steiger</lastname>
  </user>
  
</users>
```


### Conditionals

#### when

```text
<% (when predicate (kira/emit %>
   ...
<% )) %>
```

Example: 

```clojure
(do
  (load-module :kira)
  
  (def template (str/strip-indent """\
       <users>
         <% (kira/docoll users (fn [user] (kira/emit %>
         <user>
           <firstname><% (kira/escape-xml (:first user)) %></firstname>
           <lastname><% (kira/escape-xml (:last user)) %></lastname>
           <% (when add-email (kira/emit %>
           <email><% (kira/escape-xml (:email user)) %></email>
           <% )) %>
         </user>
         <% ))) %>
       </users>"""))

  (def data { :users [ {:first "Thomas"
                        :last "Meier"
                        :email "thomas.meier@foo.org" } ]
              :add-email true })

  (println (kira/eval template data)))
```

Output:

```xml
<users>
  
  <user>
    <firstname>Thomas</firstname>
    <lastname>Meier</lastname>
    <email>thomas.meier@foo.org</email>
  </user>
  
</users>
```


#### if - then - else with value

```text
<% (print (if (== font :large) 36 12)) %>
```

Example: 

```clojure
(do
  (load-module :kira)
  
  (def template (str/strip-indent """\
       body {
         background-color: white;
         font-family: 'Open Sans', sans-serif;
         color: #444;
         font-size: <% (print (if (== font :large) 36 12)) %>px;
         line-height: 1.5em;
         font-weight: <%= weight %>;
       }"""))

  (def data { :font :large 
              :weight "400" })
  
  (println (kira/eval template data)))
```

Output:

```css
body {
  background-color: white;
  font-family: 'Open Sans', sans-serif;
  color: #444;
  font-size: 36px;
  line-height: 1.5em;
  font-weight: 400;
}
```


#### if - then - else with blocks

```text
<% (if predicate (kira/emit %>
   ...
<% ) (kira/emit %>
   ...
<% )) %>
```

```clojure
(do
  (load-module :kira)
  
  (def template (str/strip-indent """\
       <% (if font-mono (kira/emit %>
       @font-face {
           font-family: 'Source Code Pro';
           src: url('SourceCodePro-Regular.ttf');
           font-style: normal;
           font-weight: normal;
           color: #888;
           font-size: 10px;
       }
       <% ) (kira/emit %>
       @font-face {
           font-family: 'Open Sans';
           src: url('OpenSans-Regular.ttf');
           font-style: normal;
           font-weight: normal;
           color: #444;
           font-size: 12px;
       }
       <% )) %>"""))

  (def data { :font-mono true })
  
  (println (kira/eval template data)))
```

Output:

```css
@font-face {
    font-family: 'Source Code Pro';
    src: url('SourceCodePro-Regular.ttf');
    font-style: normal;
    font-weight: normal;
    color: #888;
    font-size: 10px;
}
```



## Examples

### XML

The XML example demonstrates _Kira_ loops, nested loops and conditionals.

Template blueprint:

```text
<users>
  ${for u in users}$
  <user>
    <firstname>...</firstname>
    <lastname>...</lastname>
    <birthdate>...</birthdate>
    <address>
      <street>...</street>
      <zip>...</zip>
      <city>...</city>
    </address>
    ${if add-email}$
    <emails>
      ${for e in u.emails}$
      <email type="...">...</email>
      ${endfor}$
    </emails>
    ${endif}$
  </user>
  ${endfor}$
</users>
```

Venice template:

```clojure
(do
  (load-module :kira)
  
  (defn format-ts [t] (time/format t "yyyy-MM-dd"))
  
  (def template (str/strip-indent """\
       <users>
         <% (kira/docoll users (fn [user] (kira/emit %>
         <user>
           <firstname><% (kira/escape-xml (:first user)) %></firstname>
           <lastname><% (kira/escape-xml (:last user)) %></lastname>
           <birthdate><% (kira/escape-xml (:birth-date user) format-ts) %></birthdate>
           <address>
             <street><% (kira/escape-xml (-> user :location :street)) %></street>
             <zip><% (kira/escape-xml (-> user :location :zip)) %></zip>
             <city><% (kira/escape-xml (-> user :location :city)) %></city>
           </address>
           <% (when add-emails (kira/emit %>
           <emails>
             <% (kira/docoll (:emails user) (fn [[type email]] (kira/emit %>
             <email type="<% (kira/escape-xml (name type)) %>"> <% (kira/escape-xml email) %></email>
             <% ))) %>
           </emails>
           <% )) %>
         </user>
         <% ))) %>
       </users>"""))

  (def data { :users [ {:first "Thomas"
                        :last "Meier"
                        :birth-date (time/local-date 2000 8 1)
                        :location { :street "Aareweg 3"
                                    :zip "3000"
                                    :city "Bern" }
                        :emails { :private "thomas.meier@privat.ch"
                                  :business "thomas.meier@business.ch" } }
                       {:first "Anna"
                        :last "Steiger"
                        :birth-date (time/local-date 1987 10 15)
                        :location { :street "Auengasse 27"
                                    :zip "5000"
                                    :city "Aarau" }
                        :emails { :private "anna.steiger@privat.ch"
                                  :business "anna.steiger@business.ch" } } ]
              :add-emails true })

  ; runtime evaluation
  (println (kira/eval template data))
  
  ; pre-compiled evaluation
  (let [tf (kira/fn [users add-emails] template)]
    (println (tf (:users data) (:add-emails data))))
)
```

The produced output:

```xml
<users>
  
  <user>
    <firstname>Thomas</firstname>
    <lastname>Meier</lastname>
    <birthdate>2000-08-01</birthdate>
    <address>
      <street>Aareweg 3</street>
      <zip>3000</zip>
      <city>Bern</city>
    </address>
    <emails>
      <email type="business"> thomas.meier@business.ch</email>
      <email type="private"> thomas.meier@privat.ch</email>
    </emails>
  </user>
  
  <user>
    <firstname>Anna</firstname>
    <lastname>Steiger</lastname>
    <birthdate>1987-10-15</birthdate>
    <address>
      <street>Auengasse 27</street>
      <zip>5000</zip>
      <city>Aarau</city>
    </address>
    <emails>
      <email type="business"> anna.steiger@business.ch</email>
      <email type="private"> anna.steiger@privat.ch</email>
    </emails>
  </user>
  
</users>
```


To analyze the parsed template just print it:

```clojure
(println (kira/parse-string template))
```
