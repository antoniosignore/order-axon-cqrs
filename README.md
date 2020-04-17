# Home Test 

## Software Architecture

This is a CQRS exercise composed of 4 components:

a. Catalog microservice which manage catalogs of products
b. Order microservice which manage orders of products
c. EventBus implemented with AxonServer
d. A relational DB 

CQRS is one possible implementation of DDD (Domain Driven Design)

The design is CQRS (Command Query Responsibility Segregation) well described by the milestone article by Martin Fowler:

    https://martinfowler.com/bliki/CQRS.html

In few words it consists in splitting the logic into a component the deals with state change operations (CREATE, UPDATE, DELETE) vs the 
READ-ONLY Query side that responds to GET operations. 

The models are separated and the QUERY side is optimized for the GET operation.

It is not the usual stacked-layer pattern of presentation/business/DAO/entities packaged as a monolith 
that can only vertically (multiple copies of the whole monolith).

Entities are modeled as anemic pojo (no business logic but just a mapping of the SQL row).

In CQRS, the state is represented by rich objects that encapsulate business logic. 

And it consists of serating the Command model by the Read-only model. It works well for domains where the 
frequency of GET is much higher then the Write operations.  

So it shines particularly in applications where there is an higher frequency of access with respect the write operations

The state managed is expressed by the concept of aggregate which is defined as a cluster of domain objects  that can be treated 
as a single transactional unit

Each aggregate has a RootAggregate which contains children that are not directly accessible but
always by the root father. 

This concept fits perfectly with the RESTFul API principles where the URL are built always in a path
fashion from the father down to the nested children object

For instance in the order management app the REST Url look like:
in our example the URL paths are:

    "{[/events/{aggregateId}]}" 
    "{[/api/order/{orderId}/add/{sku}],methods=[POST]}" 
    "{[/api/order/{orderId}/remove/{sku}],methods=[POST]}" 
    "{[/api/order],methods=[POST]}" 

The Order is the root and all the operation related to the products are always referred to the father order.

## Event driven and EventStore

The framework selected to help us is the Axon framework produced by a Dutch company called AxonIQ.

It has many properties that I find interesting:

* the developer must think in terms of the business use cases (Domain driven design) and select an language that is tailored and understood by expert domain and developers (ubiquitus language) 
* identify the bounded contexts in the business organization and identify the Root Aggregates of the data
* model the bounded contexts to microservices (i.e. one microservice per bounded context) sometimes also referred as Actor model 
* model the problem in terms of state change commands that can be sent to each microservices and publish the immutable events to an event sourcing DB 

It offers a rich set of Annotations that drive the developer towards the solution and most importantly developing starting with a monolith.

As Martin Fowler wrote: most of the failures in microservices projects are projects where the developers started immediately in distributed fashion.

Success happens when teams start with a monolith and then as the project evolves and new bounded contexts emerge the new microservices are added.

One of the key properties that Axon offers to achieve this goal is the location transarency: 

    the developer develop with specific java annotations without bothering about where the other components are located. 

Axon offers a vast choice of adapters/bus technologies that can be select to realize the distribution as well the the event sourcing (i.e. Kafka, ActiveMQ, RabbitMq) as well several dataSources SQL and/or NO_Sql for the Query model projections and/or the validation side of the CommandModel

## Features

The 2 apps are both CQRS but present some differences in order to demonstrate that they represent objects that even 
if represent the same thing, depending by the context they may be represented differently. 

For instance the product in the context of the order management system is represented as a key as a String while 
in the Catalog context it is represented by an integer. 

### Command/Events as messages.  

The order demonstrate some more advanced features like the use of kotlin to define in very compact for the api: command,events, queries.

    com/signore/demo/order/api/api.kt

### Command and Query models

    com/signore/demo/order/command  
    com/signore/demo/order/query

### Vaadin GUI 

    com/signore/demo/order/gui

### EventSourcing

For this particular exercise I have used the Axon Server (recently announced on the 18th of October) as event bus and events db to implement the CQRS Event Sourcing and the Event driven pattern.

For this particular test given the time limitation the service is one only (monolith) but separable by spring boot profiles:

    spring.profiles.active=command,query,gui,rest
    
By playing with the pom and Axon properties it is possible to split anytime in 4 classes of microservice each deployable in 
multiple copies and written in a way that the aggregate state can be always be reconstructed by playback of the events generated 
in the past (untested in this exercise because it takes a long time...)

This properties ensure maximum scalability.    

## TEST FIRST

Axon provide test fictures that allow the user to write tests in given()/when()/expect() fashion which is
formidable because allows the programmer to write the tests thinking about commands and events (or error) to
be expected.

## Aggregates

    com/signore/demo/order/aggregate/OrderAggregate.java

## IntelliJ Editor settings

For Intellij Add the annotation processor for Lombok 

    File/Settings/Annotation processor  -->  Make sure the checkbox: Enable annotation processor is selected.

### Building the app from the sources

To build the demo app, simply run the provided [Maven wrapper](https://www.baeldung.com/maven-wrapper):

### Get all events from the Event Store

The Axon Server being an eventstore offers itself a REST API that can be queryed to pull the events history for each aggregate

This is the code principle of CQRS: events are immutable objects of the past and can be played back to rebuild the 
latest state of the aggregate.

It becomes obvious that with the replay it is possible to pinpoint the state of any time of past with precision.

    curl --request GET  --url http://localhost:8080/events/34345678900666

    [
      {
        "type": "OrderAggregate",
        "aggregateIdentifier": "4",
        "sequenceNumber": 0,
        "identifier": "094e6099-d3cf-4cbd-a3b0-daf1f70625b7",
        "timestamp": "2020-04-16T23:23:17.940Z",
        "payload": {
          "orderId": "4",
          "email": "antonio.signore@gmail.com",
          "createdOn": "2020-04-16T23:23:17.927Z"
        },
        "metaData": {
          "traceId": "2f6cf380-eb47-401e-9188-8218efd759a2",
          "correlationId": "2f6cf380-eb47-401e-9188-8218efd759a2"
        },
        "payloadType": "com.signore.demo.order.api.CreatedOrderEvt"
      }
    ]








# Installation

This project requires JDK 8 and maven for building and testing it.

Install Java on linux using this command:

    sudo apt-get install openjdk-8-jdk

## Install maven

    sudo apt-get install maven

## Build

    mvn clean package

or    
    ```
    mvnw clean package
    ```
Note that for Mac OSX or Linux you probably have to add "`./`" in front of `mvnw`.


## Test

    mvn clean test

There was no time to go higher. 

## How to run

To run we need to run the Axon Server that is the EventBus we are going to use in this example

## Axon Server 

    docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver

## RUN Axon Dashboard 

    http://localhost:8024/

#### Useful Docker command 

    docker ps -a       <-- show current status of docker
    docker stop <id>>  <--  stop an image
    docker rm axonserver <-- rm an image

```
./mvnw spring-boot:run
```

A simple UX has been implemented in Vaadin and available at:

```
http://localhost:8080
```

The application has been implemented following the DDD (Domain Driven Design principles). Framework used:

    Axon framework
    Spring Boot
    Swagger
    H2 Embedded SQL database
    Axon Server (docke)

## SWAGGER access

    http://localhost:8080/swagger-ui.html

