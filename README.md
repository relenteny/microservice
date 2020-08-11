# Microservice Sample Project

This project demonstrates a number of use microservice development patterns using an example of a simple media server. Although not critical to the patterns, the project is based on [Quarkus](https://quarkus.io/).

## Prerequisites

To build and run the code, the prerequisites are as follows:

* The project is using Java 11. There are multiple locations from which Java 8 may be obtained including [AdoptOpenJDK](https://adoptopenjdk.net/).

* Maven is used as the project definition format. A recent version of Maven should be used. This code has been developed and tested using Maven 3.6.3. Maven can be downloaded from the [Apache Maven Project](https://maven.apache.org/download.cgi).

As the project evolves, the following prerequisites will be required. *NOTE: At this time, none of these tools are required, and the project has not been validated against them. However, in furure iterations, they will become requirements.*

* If you choose to build a native version of the microservice, a version of the GraalVM must be available. For its purpose, Quarkus defines the requirements for intalling and configuring the GraalVM. The documentation is available on the Quarkus web site at [Quarkus - Building a Native Executable](https://quarkus.io/guides/building-native-image).

* A Docker image build environment. To create and build a microservice Docker image, Docker must be available. There are various ways in which this can be done including the use of [Docker for Mac](https://docs.docker.com/docker-for-mac/) or [Docker for Windows](https://docs.docker.com/docker-for-windows/).

* A Kubernetes environment. To execute the microservice within Kubernetes, a Kubernetes cluster will need to be available. If Docker for Mac or Windows are used as the Docker environment, these tools also include a small Kubernetes cluster. Documentation for enabling Kubernetes using these tools is available at the links specified above.

* For uniformity across tools, this project uses [google-java-format](https://github.com/google/google-java-format), which has integrations with several Java tools. 

## Repository layout

This source code repository is laid out for convenience in exploring the microservice sample code. It's not necessairly how the source code would be more formally organized.

* `microservice-platform-bom` demonstrates a Maven bill of materials containing common dependencies and definitions that would be used across multiple microservice projects.

* `media-domain` demonstrates a suggested pattern for laying out code that represents the definition and implementation of the microservice. The modules produced by this code are intended to be cosumed by native Java applications including RESTful servers and client consumers.

* `media-server` demonstrates the use of the modules made availabe from `media-domain` as both a RESTful and gRPC service.

## Building the code

With Java and Maven installed, building the application follows the typical Maven build lifecycle. Quarkus-specific build customizations are outlined at [Quarkus - Building Applications with Maven](https://quarkus.io/guides/maven-tooling). However, Quarkus behavior, such as executing in debug mode or building native applications, only applies to the `media-server` project. The other two projects simply use the standard Maven build lifecyle.

There is an order in which the projects must be built:

* The project contains two implementations of the business service interface. The primary implementation uses JPA as its data store. All standard Quarkus profiles use this service implementation.
* In addition to the standard implementation, there's a mock implementation that does not require a database connection. This can be used with any Quarkus profile. However, a primary intent for use of the mock implementation is to build a native version without requiring a database connection. Including the mock implementation in the build is done by specifying the customer-define Quarkus `mock` profile.
  * `mvn clean install -Dquarkus.profile=mock`
* When using Quarkus' `dev` mode, there are multiple configurations supported:
  * `mvn compile quarkus:dev -Dquarkus.profile=dev` (JPA service implementation using H2 database with sample data)
  * `mvn compile quarkus:dev -Dquarkus.profile=mock` (mock service implementation with sample data without a database)
* All configurations work for either the `rest` or `grpc` services. If all goes well, upon completion, the server will be running and listening on port 8080. For the gRPC services, gRPC server listens on port 9000.
* The application's "production" configuration assumes a connection to PostgreSQL is available, and the sample data has been loaded in PostgreSQL.
* If a PostgreSQL environment is available, the the database can be loaded with sample data using the uber jar created in the `media-domain/implementation/database-init` module. Review the application.properties. Change them or use one of the Quarkus properties override mechanisms to load a specific instance of PostgreSQL.

_As mentioned above, this project is a work in progress. Future updates will provide support for Kubernetes deployments via Helm Stay tuned._