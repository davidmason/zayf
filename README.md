Zayf - Zanata at your Fingertips
================================

A Zanata client for unstable connections.



Overview
--------

Zayf is a client for Zanata servers. Zanata is a web-based
translation management system, see http://zanata.org/ to learn about Zanata.

Zayf is a GUI tool for translators who cannot or do not wish to use Zanata's web
editor for translation work, particularly targeted at translators with an
unstable connection to the Zanata server. Zayf aims to help translators to fetch
documents from a Zanata server, and to upload modified documents to the server.



Philosophy
----------

Zayf is Free software, licensed under the
[LGPL](http://www.gnu.org/licenses/lgpl-2.1.html).

### Preamble

Working online is great for concurrency, but connectivity issues can make it
frustrating. It is frustrating when:-

 * You have to work slowly because the connection is slow.
 * Your work is interrupted whenever the network drops out.
 * You lose work because of connection problems.

Working offline can be frustrating too. It is frustrating if:-

 * You do the same work as someone else because you could not see their input.
 * You can not refer to other work that could help you work faster or to a
   higher standard of quality or consistency.

### Reasoning

Zayf’s sole reason to exist is to let translators avoid the frustrations of
working online. Zayf would be doing a poor job if it substituted some
frustrations for others, so it should also help translators avoid the
frustrations of working offline.

***Guiding Principle: Zayf should not be frustrating***

From this principle, a few guidelines arise:-

 * Installation should be straightforward.
 * All functionality should be possible through the GUI. Users should not have
   to touch the command line.
 * No other tools should be required for the intended workflow of Zayf. This
   does not preclude the requirement for a web browser to set up a Zanata
   account, and a desktop translation editor to perform the actual translation
   work.
 * Zayf should be responsive and accept commands regardless of connectivity.
   e.g. if a translator wants to submit their translations, Zayf should accept
   the input to do so, and do it as soon as possible.



Build requirements
------------------

Zayf is written in Java 1.6, and is built against both OpenJDK and Oracle JDK so
either should work. Zayf requires Maven for build and dependency management. I
use Maven 3.0.3 - other versions may work (no guarantees).

### Basic builds

Build and package distributable application in .zip and .tar.gz formats

    $ mvn package

To run Zayf, find the .tar.gz or .zip archive in target directory, untar/unzip
somewhere, then run bin/zayf or bin/zayf.bat.


Build, package and install to local Maven repository

    $ mvn install

Run Zayf directly from Maven

    $ mvn exec:java



Coding Style
------------

This project uses a standard coding style to ensure that diffs for changesets
usually show only the actual code changes, and no formatting changes. This
policy is designed to save developer time, both by making changesets easier to
grok, and by reducing unnecessary conflicts in version control that can consume
vast amounts of time and interfere with workflow.

### Automated formatting
Automatic formatting is preferred, since it can give more consistent results.
Formatter settings for Eclipse are provided in `eclipse/eclipse-formatter.xml`
and the main formatting decisions are shown below. If you use a different
formatting tool, feel free to submit the settings for inclusion in the project.

### Style guide

 * Never use tabs. Indent 3 spaces per block level.
 * No spaces on blank lines.
 * No lines longer than 100 characters.
 * Opening braces `{` always go on the next line except for array initializers,
   in line with parent expression.
 * Closing braces `}` always on a new line, in line with matching opening brace.
 * Annotations on their own line except field annotations.
 * Control statements (if, for, while, else, etc.) always use a block, even if
   they only have 1 statement. The block should follow the brace rules above.

If in doubt, look at existing code.

If still in doubt, ask.

