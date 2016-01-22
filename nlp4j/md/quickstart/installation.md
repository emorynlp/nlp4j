# Installation

* Make sure [Java 8](http://www.oracle.com/technetwork/java/javase/) and [Maven 3](https://maven.apache.org) are installed on your machine.
* Create a maven project: [maven in 5 minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).
* Move into the maven directory:

	```
	cd your_maven_project
	```

* Add the following dependency to `pom.xml`:

	```maven
    <dependency>
      <groupId>edu.emory.mathcs.nlp</groupId>
      <artifactId>emorynlp</artifactId>
      <version>RELEASE</version>
    </dependency>
	```
	
* Install the maven project:

	```bash
	mvn clean install
	```
	
* Run the following command:

	```bash
	mvn exec:java -Dexec.mainClass="edu.emory.mathcs.nlp.bin.Version"
	```

	If you see something like the following, it is properly installed.

	```bash
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