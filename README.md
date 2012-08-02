# Lime

Lime is a mail library for Clojure that provides a modular API that was inspired by [Ring](https://github.com/mmcgrana/ring). By abstracting the mail protocols and providing the basic building blocks to use them, Lime aims to make it simple to incorporate mail into your programs. One key component of Lime is that it is a re-implementation of the mail protocols in Clojure. It does **not** depend on JavaMail in any way.

The development of Lime has just begun, so much of it is still in the planning stages. The first task is to implement basic client-side SMTP, which includes being able to send plain text mail with basic authentication. Read on for more information regarding the current state of Lime.

## Motivation

Here is a snippet from the [Lime blog](http://limemail.org/2012/01/20/a-better-way-to-mail.html) about the motivation to start building Lime:

> At the time of this writing, Clojure doesn't have any built-in mail libraries. Also, all of the third party mail libraries that I have seen depend on JavaMail. This is fine for anyone who wants to simply send mail using the JVM. The problem is that the JavaMail API was built for Java, which leaves much to be desired in terms of expressiveness and extensibility in a Clojure environment.
>
>  I believe there is an opportunity for a better and [simpler](http://www.infoq.com/presentations/Simple-Made-Easy) solution to send mail with Clojure. Sending mail should be as simple as a function call, but it should also be simple to dig into the protocols and use them to their fullest extent. There also is more than just the JVM to consider, and I believe having a common mail library that can easily be ported to other Clojure platforms would be incredibly useful.

## Supported Protocols

### SMTP

The first protocol Lime will support is client-side SMTP.

### What Next?

What will be the next protocol to implement? It's too early to tell, but if you would like to see a protocol supported, [find or open an issue](https://github.com/jeremyheiler/lime/issues) to discuss it. Pull requests are always encouraged, but if it involves implementing an protocol that isn't supported yet, please discuss it first.

## FAQ

**Q:** Does Lime depend on JavaMail?  
**A:** No. Lime is meant to replace it within Clojure programs. If you do need a JavaMail wrapper, I recommend [Postal](https://github.com/drewr/postal).

## Contributing

Please report issues in the [issue tracker](https://github.com/jeremyheiler/lime/issues). We encourage contributions to be made in the form of pull requests, but since we are still working to get a 1.0.0 release, please open discussion on what you would like to work on. Other then the issue tracker, you may join us on the [mailing list](http://groups.google.com/group/limemail) or on IRC at #limemail in FreeNode.

## License

Copyright (c) 2011-2012 Jeremy Heiler. Distributed under the Eclipse Public License, the same as Clojure. See COPYING.
