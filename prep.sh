#!/bin/bash

set -e # Fail if subcommands fail

JSON="gson-2.2.4"
JAVA_MAIL="javax.mail.jar"

if [ ! -d libs/ ]; then
        mkdir libs
        echo libs/ directory created.
else
        echo libs/ directory already exists.
fi

# Get GSON dependancy unzip and move to correct location.
if [ ! -f ${JSON}.jar ]; then
	wget http://google-gson.googlecode.com/files/google-$JSON-release.zip
	unzip google-$JSON-release.zip google-$JSON/$JSON.jar
	rm google-$JSON-release.zip
	mv google-$JSON/$JSON.jar libs/
	rm -R google-$JSON
	echo JSON dependancy downloaded.
else
	echo JSON dependancy already exists.
fi

# Get Java mail dependancy
if [ ! -f $JAVA_MAIL ]; then
	wget http://java.net/projects/javamail/downloads/download/$JAVA_MAIL
	echo Email dependancy downloaded.
	mv $JAVA_MAIL libs/
else
	echo Email dependancy already exists.
fi

if [ ! -f Details.json ]; then
	cat Details.default > Details.json
	"${EDITOR:-vi}" Details.json
else
	echo Details already exists!
fi
