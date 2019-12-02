1. Check the [Closed Alpha milestone](https://github.com/inscribemc/Inscribe/milestone/1) to see what features need implementing.   
Check the [bug label](https://github.com/inscribemc/Inscribe/issues?q=is%3Aopen+is%3Aissue+label%3Abug) to see what bugs need fixing.
2. If you've chosen to work on a milestone feature, register your interest by joining this [Discord](http://discord.gg/6DUqXDf). Use the #inscribe channel, link to the issue and to your fork. This channel is where you can ask any questions you have about Inscribe.
3. Fork, clone and setup the repository for development. It's done the same way as any other Fabric mod.
4. Make your changes. The [design documentation](https://drive.google.com/open?id=1MtVYJ2hsnCzaIEkFYQS2t3ZDfYJJGvrg) may be helpful in understanding the design of the codebase. Inscribe uses the following style conventions:

    * 4 spaces instead of the tab character, because tab width is inconsistent across applications.
    * Braces on newlines, unless there is only a single statement inside the braces, in which case both braces should be on the same line.
    * Where Java syntax allows it, braces should be omitted if the body is a single, unwrapped line.
    * Implicit modifiers are not allowed, except for implicit abstract on non-static interface methods. Remembering which modifiers are implicit where is additional mental load for a reader.
    * Obey standard Java naming conventions (PascalCase class names, camelCase member names, etc.).
    * Use base type suffixes, not base type prefixes, i.e. EntityDisplayWidget not WidgetEntityDisplay; DogAnimal not AnimalDog.

5. Run `./gradlew checkstyleMain` and fix any style errors.
6. Make a PR back to the master repository. Mention which issue your PR resolves.