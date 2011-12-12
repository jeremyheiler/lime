# Lime

Lime is a library for sending mail over SMTP with Clojure. At the moment, the goal is to provide a Clojure-friendly wrapper for JavaMail. Although, the ultimate goal is to have Lime be useful in any Clojure environment.

## Synopsis

The most basic usage:

    (smtp-send
      {:to "to@example.com"
       :from "from@example.com"
       :subject "Mail from Lime!"
       :text "This is a test message sent using Lime!"})

## License

Copyright (c) 2011 Jeremy Heiler and is released under an MIT license.

