# Datavyu

[Datavyu](http://datavyu.org/) is an open-source research tool that integrates and displays all kinds of data, letting you discover the big picture while remaining connected with raw data. Datavyu will let you build and organize interpretations, and will assist with analysis.

## Download and Use Datavyu
You can find binaries of Datavyu available for Windows and OSX on [the Datavyu.org download page](http://datavyu.org/download/).

## Development Requirements

To get started with Datavyu development, you will need to download and install a few development tools. Datavyu is primarily written in Java, along with a little Ruby (via JRuby) for additional scripting tasks. So the list of toys you will need to download:

* [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
* [Maven 3.0.5](http://maven.apache.org/)
* [Git](http://git-scm.com/)

## Datavyu OSX And Windows builds

To build and package Datavyu, use the following commands:

	git clone https://github.com/databrary/datavyu.git
	cd datavyu
	export MAVEN_OPTS="-Xmx256M"
	mvn clean -U -Dmaven.test.skip=true jfx:native

## Running Datavyu in an IDE

To build and run Datavyu an in IDE like IntelliJ Idea or Netbeans set Datavyu up as a Maven project and run it with the following maven command line options:
	clean compile jfx:run

## More Information

See the [wiki](https://github.com/databrary/datavyu/wiki) for more information on how to code and contribute improvements to Datavyu.

A list of features and fixes that need implementing for Datavyu can be found [here](http://datavyu.org/bugs).

