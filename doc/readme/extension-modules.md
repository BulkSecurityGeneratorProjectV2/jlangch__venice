# Extension Modules

Through extension modules Venice provides specific functionality
that not all application require thus keeping load time and 
resource usage low if the modules are not used.

Extension Modules are plain Venice scripts and must be loaded 
explicitly `(load-module :name)`. Venice tracks the modules loaded
and loads a module only once and skips subsequent load attempts.

* [Kira Template](ext-kira.md)
* [XML](ext-xml.md)
* [Charts](ext-charts.md)
* [Apache Tomcat WEB Server](ext-tomcat.md)
* [Ring WEB App library](ext-ring.md)
* [WebDAV](ext-webdav.md)
* [Maven](ext-maven.md)
* [Jackson JSON](ext-jackson.md)
* [Cryptographic Functions](ext-crypt.md)
* [Semantic Versioning](ext-semver.md)
* [CIDR (Classless Inter-Domain Routing)](ext-cidr.md)
* [Mercator Maps](ext-mercator.md)
* [GEO IP](ext-geoip.md)

### Explicitly forcing a module reload

Venice can be forced to reload an already loaded module

```clojure
(load-module :math)

; use the module
(math/bigint-add (math/bigint "100") 
                 (math/bigint "200"))
             
; reload the module
(ns-remove math)
(load-module :math true)
```

