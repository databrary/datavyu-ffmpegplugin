# Datavyu

[Datavyu](http://datavyu.org/) is an open-source research tool that integrates and displays all kinds of data, letting you discover the big picture while remaining connected with raw data. Datavyu will let you build and organize interpretations, and will assist with analysis.

## Download and Use Datavyu
You can find binaries of Datavyu available for Windows and OSX on [the Datavyu.org download page](http://datavyu.org/download/).

## Development Requirements

To get started with Datavyu development, you will need to download and install a few development tools. Datavyu is primarily written in Java, along with a little Ruby (via JRuby) for additional scripting tasks. So the list of toys you will need to download:

* [Netbeans](http://www.netbeans.org/)
* [Maven](http://maven.apache.org/)
* [Git](http://git-scm.com/)

## Datavyu OSX Builds

To build and package Datavyu to run on OSX, use the following commands:

	git clone https://github.com/databrary/datavyu.git
	cd datavyu
	export MAVEN_OPTS="-Xmx256M"
	mvn -Prelease clean -Dmaven.test.skip=true package osxappbundle:bundle

## Datavyu Windows Builds

To build and package Datavyu to run on windows, use the following commands:

	git clone https://github.com/databrary/datavyu.git
	cd datavyu
	mvn -Prelease,win-package -Dmaven.test.skip=true clean package jar:jar launch4j:launch4j assembly:assembly

## More Information

See the [wiki](https://github.com/databrary/datavyu/wiki) for more information on how to code and contribute improvements to Datavyu.

A list of features and fixes that need implementing for Datavyu can be found [here](https://www.pivotaltracker.com/projects/495691#).

