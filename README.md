# Lime

Lime is a mail library for Clojure. The goal is to provide both the high level functionality and the low level building blocks for the mail-related protocols. Currently, work is going on to fully implement the Mail User Agent aspect (or client-side) of SMTP.

## Motivation

> At the time of this writing, Clojure doesn't have any built-in mail libraries. Also, all of the third party mail libraries that I have seen depend on JavaMail. This is fine for anyone who wants to simply send mail using the JVM. The problem is that the JavaMail API was built for Java, which leaves much to be desired in terms of expressiveness and extensibility in a Clojure environment.
>
>  I believe there is an opportunity for a better and [simpler](http://www.infoq.com/presentations/Simple-Made-Easy) solution to send mail with Clojure. Sending mail whould be as simple as a function call, but it should also be simple to dig into the protocols and use them to their fullest extent. There also is more than just the JVM to consider, and I believe having a common mail library that can easily be ported to other Clojure platforms would be incredibly useful.

[read more](http://abitofclojure.com/)

## FAQ

**Q:** Does Lime depend on JavaMail?  
**A:** No. Lime does not depend on JavaMail, and is meant to replace it within Clojure programs. If you need a JavaMail wrapper, I recommend [Postal](https://github.com/drewr/postal).

**Q:** Why not just use JavaMail?  
**A:** The purpose of Lime is to be a mail library that makes the protocols accessible in a Clojure environment. You can still do the high level functions, but you can also create your own high level functions with the building blocks Limes provide for any given protocol.

## License

Copyright (c) 2011-2012 Jeremy Heiler and is released under an MIT license.