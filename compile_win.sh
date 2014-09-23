curl http://www.datavyu.org/docs/user-guide.pdf > packaged_docs/user-guide.pdf
mvn -Prelease,win-package -Dmaven.test.skip=true clean package jar:jar launch4j:launch4j assembly:assembly
