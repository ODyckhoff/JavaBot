all:
	echo Compiling
	javac -cp gson-2.2.4.jar -d bin/ -sourcepath src/ src/**/*.java
	echo Copying dependencies
	cp -R com bin/
	echo Running
	java -cp bin/ program.Start

clean:
	echo Deleting old files
	rm -r bin/**/*.class
	cp -R com bin/

compile:
	echo Deleting old files
	rm -r -v bin/**/*.class
	echo Compiling source
	javac -cp gson-2.2.4.jar -d bin/ -sourcepath src/ src/*.java
prep:
	wget http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip
	unzip google-gson-2.2.4-release.zip
	rm google-gson-2.2.4-release.zip
	mv google-gson-2.2.4/gson-2.2.4.jar .
	rm -R google-gson-2.2.4
	unzip gson-2.2.4.jar
	cp run.sh bin/
	chmod 777 bin/run.sh
