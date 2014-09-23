curl http://www.datavyu.org/docs/user-guide.pdf > packaged_docs/user-guide.pdf
mvn -Prelease,jar-package -Dmaven.test.skip=true clean package jar:jar assembly:assembly
