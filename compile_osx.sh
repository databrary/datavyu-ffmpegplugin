curl http://www.datavyu.org/docs/user-guide.pdf > packaged_docs/user-guide.pdf
mvn clean -U -Dmaven.test.skip=true jfx:native
