# Micro-service architecture

Java/Spring Boot based applications with MySql database (used Docker container)

## Applications

* Eureka Server: discovery server, identifies and tracks all running applications and it's url'server
* Gateway Zuul: dynamic url (request) routing to appropriate services
* Config Service: application to provide application configuration information (database urls, username, passwords)
* Image Service: sample service to provide images 
* Gallery Service: sample service to provide collection of images

