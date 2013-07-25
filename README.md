#Introduction

JavaBot is a lightweight Oracle Java based IRC bot for managing and manipulating IRC channels, 
Its goal is to make management of an IRC channel simple, along with being flexible enough to allow users to add new
plugins with ease.

It was decided from the start that JavaBot should be written in Java as it is the standard teaching language in most
UK universities, and therefore most users of the channels I use can add functionality to JavaBot.

JavaBot uses a plugin based architecture to allow new functionality to be easily added on at any time.
It also uses a Singleton approch to core functionality to allow it to be easily interacted with by the rest of the
program.


#Setup

To get all depedancies and set up default JSON file, run this command

`make prep`

To compile the project, run this

`make compile`

To execute the project, run this

`make run`


##Run without makefile

Make sure that the dependancies exist first and that the source has been compiled.

Dependancies needed, 

`Google GSON libary`
`javax mail libary`

`java -cp .:bin/:gson-2.2.4.jar:javax.mail.jar core.Start`


##Make commands

`Make prep` - Downloads depedancies and prepares the application to make it executable.

`Make clean` - Removes `bin/` and cleans other installation files.

`Make compile` - Compiles the code.

`Make run` - Starts the program.

##Plugin setup

###Issue

The Issue plugin requires an OAuth token to be saved in auth_token.
This can be generated by running
`python gen_auth.py`
and inputting your GitHub username and password when prompted.

#Notes for developers

All code should conform to [Oracle's code conventions](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html)
and should compile for Java6 and above.

Plugins - All plugins should be placed in `src/` and must use the Plugin interface, any additional classes must
be put under package `addon.*pluginname*`.

#Available commands

|Plugin|Command|Channel|Query|Needs Admin|
|:----:|:-----:|:-----:|:---:|:---------:|
|Admin|
|~|.join||X|X|
|~|.part||X|X|
|~|.quit||X|X|
|~|.nick||X|X|
|~|.exception||X|X|
|~|.reload||X|X|
|~|.cmd||X|X|
|~|.loaded|X|||
|Authentication|
|~|.login||X||
|~|.register||X||
|~|.logout||X||
|~|.recover||X||
|Imgur|
|~|\<URL\>|X|X||
|Issue|
|~|.bug|X|X|X|
|Quote|
|~|.quoteadd|X|||
|~|.quotes|X|||
|~|.quote|X|||
|~|.quotedel|X|||
|Reminder|
|~|.remind|X|||
|~|.reminder|X|||
|Rep|
|~|\<username\>[ ]\<modifier\> [\<value\>]|X|||
|~|.rep|X|||
|Sed|
|~|s/\<search\>/\<replacement\>/|X|X||
|~|\<username\>: s/\<search\>/\<replacement\>/|X|X||
|~|.seddropcache||X|X|
|~|.seddumpcache||X|X|
|Stats|
|~|.msgsent|X|||
|~|.lastonline|X|||
|~|.stats|X|||
|StringResponse|
|~|.response|X||X|
|Users|
|YouTube|
|~|\<URL\>|X|X||
