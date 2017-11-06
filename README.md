Under Armour programming exercise

The is implementation of Under Armour programming exercise to develop a Restful application. The project has a few requirements

* Java 8
* Apache Maven 3.x installed.

Design and implement a production-grade horizontally scalable REST service that implements a simple ephemeral text message service. The service should implement a hot/cold storage system where expired messages are stored separately from unexpired messages. 

Instructions
------------

There are two ways to deploy the web application. 

* As this is a Maven project, it can be imported in an IDE such as Eclipse or IntelliJ.
Create a **Run configuration** -> **Java Application**. Once done, select *UnderArmour*
as a main-class. Execute the main class, the application has been deployed to embedded tomcat
servlet container.

* Build and compile the project from command line. Navigate to the project root using the command
line, and execute the following command `mvn spring-boot:run`

The RESTful services can be invoked after either steps is performed. **IMPORTANT**: these two instructions are
mutually exclusive.


