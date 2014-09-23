curl http://www.datavyu.org/docs/user-guide.pdf > packaged_docs/user-guide.pdf
mvn clean -U -Prelease -Dmaven.test.skip=true package osxappbundle:bundle
