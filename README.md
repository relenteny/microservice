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

* Start in `microservice-platform-bom` and execute `mvn clean install`.
* Next, in `media-domain`, execute `mvn clean install`.
* As described above, `media-server` contains two server implementations. To build and run the servers using the Quarkus `dev` mode, execute `mvn clean quarkus:dev "-Dservice.type=mock"` in either the `rest` or `grpc` directories. If all goes well, upon completion, the server will be running and listening on port 8080. For the gRPC services, gRPC server also listens on port 8888. 
