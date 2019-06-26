# Charts

Venice supports rendering charts if the [XChart](https://knowm.org/open-source/xchart/) library is on the runtime classpath:

 - org.knowm.xchart:xchart:3.5.4


#### Line Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/line-chart.png" width="300">


```clojure
(do
   (load-module :xchart)

   (xchart/write-to-file
      (xchart/xy-chart
         { "y(x)" { :x [0.0 1.0 2.0]
                    :y [0.0 0.8 2.0] } }
         { :title "Line Chart"
           :render-style :line   ; :step
           :x-axis { :title "X" :decimal-pattern "#0.0"}
           :y-axis { :title "Y" :decimal-pattern "#0.0"}
           :theme :xchart } )
      :png ; write as PNG
      120  ; render with 120 dpi
      (io/file "line-chart.png")))
```

#### Area Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/area-chart.png" width="300">

```clojure
(do
   (load-module :xchart)

   (xchart/write-to-file
      (xchart/xy-chart
         { "a" { :x [0.0  3.0  5.0  7.0  9.0]
                 :y [0.0  8.0 12.0  9.0  8.0] }
           "b" { :x [0.0  2.0  4.0  6.0  9.0]
                 :y [2.0  9.0  7.0  3.0  7.0] }
           "c" { :x [0.0  1.0  3.0  8.0  9.0]
                 :y [1.0  2.0  4.0  3.0  4.0] } }

         { :title "Area Chart"
           :render-style :area   ; :step-area
           :legend {:position :inside-ne}
           :x-axis { :title "X" :decimal-pattern "#0.#"}
           :y-axis { :title "Y" :decimal-pattern "#0.#"}
           :theme :xchart } )
      :png ; write as PNG
      120  ; render with 120 dpi
      (io/file "area-chart.png")))
```

#### Scatter Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/scatter-chart.png" width="300">

```clojure
(do
   (load-module :xchart)

   (defn rand-list [count max] (map (fn [x] (rand-long max)) (range count)))

   (xchart/write-to-file
      (xchart/xy-chart
         { "Rand 1" { :x (rand-list 8 10)
                      :y (rand-list 8 10) }
           "Rand 2" { :x (rand-list 8 10)
                      :y (rand-list 8 10) } }
         { :title "Scatter Chart"
           :render-style :scatter
           :marker { :size 20 }
           :x-axis { :title "X" :decimal-pattern "#0.0" :min 0.0 :max 10.0 }
           :y-axis { :title "Y" :decimal-pattern "#0.0" :min 0.0 :max 10.0 }
           :theme :xchart } )
      :png ; write as PNG
      120  ; render with 120 dpi
      (io/file "scatter-chart.png")))
```

#### Bubble Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/bubble-chart.png" width="300">

```clojure
(do
   (load-module :xchart)

   (def series1
        [ {:x  1 :y  2 :bubble 30}
          {:x 10 :y  8 :bubble 12}
          {:x 12 :y 16 :bubble 15}
          {:x 20 :y 25 :bubble 24} ])
   (def series2
        [ {:x 10 :y  4 :bubble 30}
          {:x  5 :y  5 :bubble 36}
          {:x  6 :y 20 :bubble 50}
          {:x 18 :y 20 :bubble  9} ])
   (defn bubblify [series]
         {:x (map (fn [t] (:x t)) series)
          :y (map (fn [t] (:y t)) series)
          :bubble (map (fn [t] (:bubble t)) series)})

   (xchart/write-to-file
      (xchart/bubble-chart
         {"Series 1" (bubblify series1)
          "Series 2" (bubblify series2) }
         { :title "Bubble Chart"
           :legend {:position :inside-sw}
           :x-axis {:title "Series 2"}
           :y-axis {:title "Series 1"}
           :theme :xchart } )
      :png ;; write as PNG
      120  ;; render with 120 dpi
      (io/file "bubble-chart.png")))
```

#### Bar Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/bar-chart.png" width="300">

```clojure
(do
   (load-module :xchart)

   (xchart/write-to-file
      (xchart/category-chart
         { "Bananas" {"Mon" 6, "Tue" 2, "Fri" 3, "Wed" 1, "Thu" 3}
           "Apples" {"Tue" 3, "Wed" 5, "Fri" 1, "Mon" 1}
           "Pears" {"Thu" 1, "Mon" 3, "Fri" 4, "Wed" 1} }           
         { :title "Weekly Fruit Sales"
           :theme :xchart 
           :x-axis {:order ["Mon" "Tue" "Wed" "Thu" "Fri"] } } )
      :png ;; write as PNG
      120  ;; render with 120 dpi
      (io/file "bar-chart.png")))
```

#### Pie Chart Example

<img src="https://github.com/jlangch/venice/blob/master/doc/charts/pie-chart.png" width="300">

```clojure
(do
   (load-module :xchart)

   (xchart/write-to-file
      (xchart/pie-chart
         { "A" 400
           "B" 310
           "C" 50 } 
         { :title "Pie Chart"
           :render-style :pie
           :theme :xchart } )
      :png ;; write as PNG
      120  ;; render with 120 dpi
      (io/file "pie-chart.png")))
```



#### Download required 3rd party libs

```clojure
(do
  (load-module :maven)
  
  (maven/download "org.knowm.xchart:xchart:3.5.4")
```

