# Installation

## Install Java 8 and Maven 3 on Ubuntu

* Install Java 8 by running the following commands:

   ```
   sudo apt-add-repository ppa:webupd8team/java
   sudo apt-get update
   sudo apt-get install oracle-java8-installer
   sudo apt-get install oracle-java8-set-default
   ```

* Check the Java version.

   ```
   java -version
   ```

* Install Maven 3 by running the following commands:

   ```
   sudo apt-add-repository ppa:andrei-pozolotin/maven3
   sudo apt-get update
   sudo apt-get install maven3
   ```

* Check the Maven version.

   ```
   mvn -version
   ```
