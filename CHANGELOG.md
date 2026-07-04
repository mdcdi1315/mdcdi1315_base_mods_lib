### Now releasing 1.0.35:

-> Bugfixes for issues #2 and #3

-> Additional other bugfixes

-> Fixed several .NET Layer issues and added more classes again.

-> Added an annotation processor for validating several annotations in the library,
will be coming soon to the developers as well.

-> Command API: Added a way for registering new custom command argument types to Minecraft.

-> Added several additional default functions, and made all the Primitive*Function/Action interfaces inherit 
from the PrimitiveNumericAction and PrimitiveNumericFunction interfaces.

-> Made the `Pure` annotation to be also defined in class constructors.

-> Made the Fast Binary Format a bit faster by removing the pushback stream for reading, and instead reading from the data stream directly.

-> Collections: Added priority queue interface, and it's implementations.

-> Made the BML Logger `static final`.

-> Documentation additions and fixes.