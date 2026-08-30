# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## CS2103T coding standards

Apply these CS2103T Java standards to all source and test code in this repository.

### Naming

* Put every class in a lowercase package. For school projects, start the package name with the project
  or group name; do not use `edu.nus.comp.*`.
* Name classes and enums with English nouns in PascalCase. Name variables and methods in camelCase;
  method names must be verbs. Name constants in SCREAMING_SNAKE_CASE.
* Write all identifiers in English. Test methods may use underscores in the form
  `featureUnderTest_scenario_expectedBehavior`; omit the scenario or expected-behavior part when it
  does not add useful information.
* Write abbreviations as normal words within an identifier: use `getAsXml`, not `getAsXML`.
* Name booleans to read as booleans, preferably with prefixes such as `is`, `has`, or `was`.
  Use a boolean setter form such as `setFound(boolean isFound)`.
* Use plural names for collections. Use short iterator names such as `i`, `j`, and `k` only for loops.
  Use descriptive names for variables with a broad scope and short names only for small scopes.
* Give associated constants a common prefix.

### Layout and statements

* Indent with four spaces; never tabs. Keep lines at or below 120 characters (aim for 110 or fewer).
* Use K&R braces. Put spaces around binary and ternary operators, after Java keywords, and after commas.
  Keep a method or constructor name attached to its opening parenthesis.
* Wrap lines to improve readability: prefer breaks after commas, before operators (including `.`, `&`,
  and `|`), and at higher expression levels. Indent a wrapped line eight spaces more than its parent.
* Separate logical units within a block with one blank line.
* Follow the standard forms for method declarations, `if`/`else`, `for`, `while`, `do`/`while`,
  `switch`, and `try`/`catch`/`finally`. Put conditionals on their own line and use braces for every
  conditional and loop body, including a single statement.
* Include `// Fallthrough` immediately before any intentional fallthrough in a traditional `switch`.
* Declare variables in the smallest possible scope and initialize them at declaration where practical.
  Keep class variables non-public, except for constants and behavior-free data classes.
* Attach array specifiers to the type (`int[] values`), not the variable. Use explicit imports and keep
  their ordering consistent; never use wildcard imports.

### Comments and Javadocs

* Write all comments in American English and indent them to match the code they describe.
* Write descriptive header Javadocs for every public class and public method. They may be omitted for
  getters/setters, tests, and overrides whose inherited documentation applies exactly.
* Start a Javadoc with `/**` on its own line. Its first sentence is a short summary that begins with an
  appropriate third-person verb, such as `Returns`, `Sends`, `Adds`, `Creates`, or `Prints`, and ends
  with punctuation. Align later `*` lines, leave a blank line before tags, and do not leave a blank
  line between the closing `*/` and the declaration.
* Give each non-obvious parameter a punctuated `@param` description. Include `@return` and `@throws`
  tags when they add useful information; omit `@return` for `void` or an otherwise obvious return.
  Use `{@inheritDoc}` when an override needs inherited documentation with small additions.
* A simple one-line Javadoc is acceptable for a member. Trailing comments are allowed when useful.

## JUnit test coverage

Aim to cover the top half of the codebase by value with JUnit tests: roughly the 50% of methods
carrying the most complex, core, or otherwise critical logic. Parsing, task-list operations, and
reading and writing the data file are in that half. Trivial accessors, one-line overrides, and
methods that only print to the console are not, and are left to the UI test plan instead.

After every code update:

1. Add or update the JUnit tests so the target still holds. A new method in that top half needs
   tests of its own; a method whose behaviour changed needs its existing tests changed to assert
   the new behaviour, rather than deleted.
2. Cover the reasonable cases for each method under test, not just one: the ordinary result, the
   boundaries, and each way the method is meant to fail.
3. Run the tests from the repository root:

   ```bash
   ./gradlew test
   ```

   If a test fails, stop and report the failure rather than adjusting the test to match the new
   behaviour without saying so.

Follow the Gradle and JUnit conventions for placement and naming: a test class lives under
`src/test/java` in the same package as the class it tests and is named after it, such as
`happybot.Parser` being tested by `happybot.ParserTest` in
`src/test/java/happybot/ParserTest.java`. Name a test method for what it checks, using
`featureUnderTest_testScenario_expectedBehavior()` when a plain name would not be clear enough.

## UI test verification

After every code update:

1. Review `src/test/ui-test-plan.md` and update it when the change adds or alters console behaviour, commands, or expected output.
2. Run the UI test plan with the `test-ui` skill. In Codex, invoke it as `$test-ui`. In Claude Code, use
   the `test-ui` skill in `.claude/skills/`. Any agent that finds neither should run the shared runner
   directly from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py src/test/ui-test-plan.md
   ```

   All three routes run the same script. Do not substitute an ad-hoc test script for it. If the build or
   a test case fails, stop the test session immediately and report the failure.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
