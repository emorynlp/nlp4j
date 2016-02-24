# Install

## With Maven

* Make sure [Java 8](http://www.oracle.com/technetwork/java/javase/) and [Maven 3](https://maven.apache.org) are installed on your machine.
* Add the following dependency to `pom.xml`:

	```xml
    <dependency>
      <groupId>edu.emory.mathcs.nlp</groupId>
      <artifactId>nlp4j</artifactId>
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

	If you see the following message, it is properly installed.

	```
	[INFO] Scanning for projects...
	[INFO]                                                                         
	[INFO] ------------------------------------------------------------------------
	[INFO] Building nlp4j x.x.x
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ nlp4j ---
	====================================
	Emory NLP Version x.x.x
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

## Without Maven

* Make sure [Java 8](http://www.oracle.com/technetwork/java/javase/) is installed on your machine.
* Download [`nlp4j-x.x.x.jar`](http://nlp.mathcs.emory.edu/nlp4j/nlp4j-1.0.0.jar) and add it to your classpath. If you are using [bash](https://www.gnu.org/software/bash/), export `CLASSPATH`:

	```bash
	export CLASSPATH=nlp4j-x.x.x.jar:.
	```


* Run the following command:

	```bash
	java edu.emory.mathcs.nlp.bin.Version
	```

	If you see the following message, it is properly installed.

	```
	====================================
	NLP4J Version x.x.x
	Webpage: http://nlp.mathcs.emory.edu
	Contact: jinho.choi@emory.edu
	====================================
	```