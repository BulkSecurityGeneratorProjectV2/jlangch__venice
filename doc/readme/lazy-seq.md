# Lazy Sequences

Venice supports lazy evaluated sequences. Lazy sequences are sequences which elements
are produced only when needed and memorized for further access, thus lazy sequences
can be infinite. The evaluation of sequence elements is called realization.



## Producing Lazy Sequences

Lazy sequences are produced by an element generating function.


Lazy sequence with random numbers

```clojure
(lazy-seq rand-long) ; => (...)
 ```
 
 
Lazy sequence with positive numbers

```clojure
(lazy-seq 1 #(+ % 1)) ; => (...)
 ```


Lazy sequence with cons'ing a value

```clojure
(cons -1 (lazy-seq 0 #(+ % 1))) ; => (...)
 ```


### Functions working with Lazy Sequences

Functions that return lazy sequences when their input is a lazy sequence:
 
	- cons
	- map
	- filter
	- remove
	- take
	- take-while
	- drop
	- drop-while
	- rest

Functions that return evaluated elements from a lazy sequences:
	
	- first
	- second
	- third
	- fourth


## Realizing Lazy Sequences

Lazy sequences must be explicitly realized. At this moment the elements are
computed.

Single elements of a lazy sequence can be realized with one of the functions 
`first`, `second`, `third`, or `fourth`

```clojure
(first (lazy-seq 1 #(+ % 1))) ; => 1
 ```

A lazy sequence can be realized to a list by applying the `doall` function. 

**Be aware that your runtime system will not survive realizing an infinite sequence.**

This example will continuing realizing elements until the memory is exhausted:

```clojure
(doall (lazy-seq 1 #(+ % 1)))
 ```

Realizing a finite lazy sequence

```clojure
(->> (lazy-seq rand-long)
     (take 4)  ; finite lazy sequence with 4 elements not yet realized
     (doall))
     
; => (1818406514169153152 8927930230538774116 713188723202483350 1539851250757480188)
```

```clojure
(->> (lazy-seq 1 inc)
     (map #(* 10 %))
     (drop 2)
     (take 2)
     (doall))
     
; => (30 40)
```


Remember that elements are just realized once and then memorized for further access

Example 1:

```clojure
(do
  (def ls (lazy-seq 0 (fn [x] (let [n (+ x 1)]
                               (println "realized" n)
                               n))))

  (first ls)
  ; => 0, the first value is the passed seed value -> no evaluation

  (second ls)
  ;realized 1
  ;=> 1, the second value is accessed -> it is evaluated

  (second ls)
  ;=> 1, the second has already been evaluated -> the memorized value is returned
)
```


Example 2:

```clojure
(do
  (def ls (lazy-seq 0 (fn [x] (let [n (+ x 1)]
                               (println "  realized" n)
                               n))))

  (println "[1]:")
  (->> (map #(* 10 %) ls)
       (take 40)     
       (take 2)
       (doall)))

  ; [1]:
  ;   realized 1
  ; => (0 10)
     
  (println "[2]:")
  (->> (map #(* 10 %) ls)
       (take 40)
       (take 4)
       (doall))
       
  ; [2]:
  ;   realized 2
  ;   realized 3
  ; => (0 10 20 30)
)
```