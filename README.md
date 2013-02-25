# Lime

Lime is a mail library for Clojure. Development is currently focused
on designing an abstraction for scripting SMTP sessions as a client.
Future development will include further abstractions on top of SMTP,
and perhaps other mail protocols.

One key feature of Lime is that it does not depend on JavaMail. The
primary goal of the project is to give you full access to the
protocols and data structures, at the abstraction you want to use. For
example, if you want to extend SMTP, as allowed by the specification,
then the tools will be available for you to do so.

That said, Lime is still very young and in development. The focus is
on client-side SMTP, so keep that in mind when browsing the code. If
you're looking for a fully featured and production-ready SMTP client
library, please check out [Postal](https://github.com/drewr/postal).
It is an excellent wrapper to JavaMail.

## Motivation

Here is a snippet from the [Lime
blog](http://limemail.org/2012/01/20/a-better-way-to-mail.html) about
the motivation to start building Lime:

> At the time of this writing, Clojure doesn't have any built-in mail
>libraries. Also, all of the third party mail libraries that I have
>seen depend on JavaMail. This is fine for anyone who wants to simply
>send mail using the JVM. The problem is that the JavaMail API was
>built for Java, which leaves much to be desired in terms of
>expressiveness and extensibility in a Clojure environment. I believe
>there is an opportunity for a better and
>[simpler](http://www.infoq.com/presentations/Simple-Made-Easy)
>solution to send mail with Clojure. Sending mail should be as simple
>as a function call, but it should also be simple to dig into the
>protocols and use them to their fullest extent. There also is more
>than just the JVM to consider, and I believe having a common mail
>library that can easily be ported to other Clojure platforms would be
>incredibly useful.

## Contributing

Please report issues in the [issue
tracker](https://github.com/jeremyheiler/lime/issues). We encourage
contributions to be made in the form of pull requests, but since we
are still working to get a 1.0.0 release, please open discussion on
what you would like to work on. Other then the issue tracker, you may
join us on the [mailing list](http://groups.google.com/group/limemail)
or on IRC at #limemail in FreeNode.

## License

Copyright (c) 2011-2013 Jeremy Heiler. Distributed under the Eclipse
Public License, the same as Clojure. See COPYING.
