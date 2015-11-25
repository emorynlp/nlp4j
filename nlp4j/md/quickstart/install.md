# How To Install

1. Make sure the following tools are installed on your machine:
 * [Git](https://git-scm.com): version 2 or above.
 * [Java](http://www.oracle.com/technetwork/java/javase/): version 8 or above.
 * [Maven](https://maven.apache.org): version 3 or above.

1. Clone this repository:

	```bash
	git clone https://github.com/emorynlp/emorynlp.git
	```

1. Change to the `emorynlp` directory:

	```bash
	cd emorynlp
	```
	
1. Compile this project:

	```bash
	mvn compile
	```
	
1. Run the following command:

	```bash
	mvn exec:java -Dexec.mainClass="edu.emory.mathcs.nlp.bin.Version"
	```

	If you see something like the following, it is properly installed.

	```
	Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=128m; support was removed in 8.0
	[INFO] Scanning for projects...
	[INFO]                                                                         
	[INFO] ------------------------------------------------------------------------
	[INFO] Building emorynlp 1.0.0
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ emorynlp ---
	====================================
	Emory NLP Version 1.0.0
	Webpage: http://nlp.mathcs.emory.edu
	Contact: jinho.choi@emory.edu
	====================================
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 0.739s
	[INFO] Finished at: Tue Nov 24 20:08:59 EST 2015
	[INFO] Final Memory: 11M/247M
	[INFO] ------------------------------------------------------------------------
	```