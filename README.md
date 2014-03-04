trove-3.0.3
===========

Patched version of the Trove library - changes the Collections semantics to match proper java.util.Map semantics


This library has been patched by Palantir Technologies to make the following changes:

Branch [palantir-gotham-3.12.x](https://github.com/palantirtech/trove-3.0.3/tree/palantir-gotham-3.12.x) - used by Palantir Gotham 3.12.x:
* Trove implements of series of decorators that wrap their custom collections and implement the [standard Java Collections interfaces](http://docs.oracle.com/javase/6/docs/api/java/util/Collections.html). These decorators depart from the behavior specified in the [Map interface](http://docs.oracle.com/javase/6/docs/api/java/util/Map.html) by returning 0 when [put()][put] or [remove()][remove] is called with a key not in the map.  This version returns null as specified by Map.
* Trove collections use a magic primitive value to mean null when wrapped by a decorator. If this magic primitive value is actually added to the collection, decorators will treat it as null, the same way they would treat a missing value. This version distinguishes between these two cases and disallows adding null to decorators.

Branch [palantir-gotham-4.x](https://github.com/palantirtech/trove-3.0.3/tree/palantir-gotham-4.x) - used by Palantir Gotham 4.x:
* This version adds implementations of arrays, lists, sets, and maps that utilize offheap memory allocated by the sun.misc.Unsafe class.

Original source code for this library available at: 

[http://sourceforge.net/projects/trove4j/files/trove/3.0.3/trove-3.0.3.tar.gz/download](http://sourceforge.net/projects/trove4j/files/trove/3.0.3/trove-3.0.3.tar.gz/download)

[put]: http://docs.oracle.com/javase/6/docs/api/java/util/Map.html#put(K,%20V)
[remove]: http://docs.oracle.com/javase/6/docs/api/java/util/Map.html#remove(java.lang.Object)

This release made available under the LGPL version 2.1 - see [LICENSE](https://github.com/palantirtech/trove-3.0.3/blob/master/LICENSE)
