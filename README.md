# OpenSHAPA

[OpenSHAPA](http://openshapa.org/) is an open-source research tool that integrates and displays all kinds of data, letting you discover the big picture while remaining connected with raw data. OpenSHAPA will let you build and organize interpretations, and will assist with analysis.

## Development Requirements

To get started with OpenSHAPA development, you will need to download and install a few development tools. OpenSHAPA is primarily written in Java, along with a little Ruby (via JRuby) for additional scripting tasks. So the list of toys you will need to download:

* [Netbeans](http://www.netbeans.org/)
* [Maven](http://maven.apache.org/)
* [Git](http://git-scm.com/)

## OpenSHAPA OSX Builds

To build and package OpenSHAPA to run on OSX, use the following commands:

	git clone https://github.com/OpenSHAPA/openshapa.git
	cd openshapa
	export MAVEN_OPTS="-Xmx256M"
	mvn -Prelease clean -Dmaven.test.skip=true package osxappbundle:bundle
	
## OpenSHAPA Windows Builds

To build and package OpenSHAPA to run on windows, use the following commands:

	git clone https://github.com/OpenSHAPA/openshapa.git
	cd openshapa
	mvn -Prelease,win-package -Dmaven.test.skip=true clean package jar:jar launch4j:launch4j assembly:assembly
  
## More Information

See the [wiki](https://github.com/OpenSHAPA/openshapa/wiki) for more information on how to code and contribute improvements to OpenSHAPA.

A list of features and fixes that need implementing for OpenSHAPA can be found [here](https://www.pivotaltracker.com/projects/495691#).
	
