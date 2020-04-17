# Home Test 

The application has been designed following the DDD (Domain Driven Design principles):

    CQRS event driven and event store exercise composed of 4 components:

a. Catalog microservice which manage catalogs of products
b. Order microservice which manage orders of products
c. EventBus implemented by AxonServer
d. A relational DB 

The tecnologies adopted are:

    Axon framework
    Spring Boot
    Swagger
    H2 Embedded SQL database
    Axon Server  <--- EventStore
    Vaadin 
    Docker

Users by 2 RESTful API can manage a catalog of products and prepare Orders. 

# How to build

We need to build 2 microservices : catalog-service and order-service

This project requires JDK 8 and maven for building and testing it.

Install Java on linux using this command:

    sudo apt-get install openjdk-8-jdk
    sudo apt-get install maven

## Build the microservices

    cd catalog-service
    ./mvnw clean package

    cd order-service
    ./mvnw clean package
    

## How to run

To run we need to run the Axon Server that is the EventBus we are going to use in this example

## Axon Server 

    docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver

## RUN Axon Dashboard 

    http://localhost:8024/
    
If the services are up and running you should be able to see screen as in the attached pic:

    EventStoreDashboard.png
    
You should see the 2 microservices connected to the EventStore: the Axon Dashboard

Under the command screen, you can also see the history of the command/events executed.

    CommandEventsHistory.png

#### Useful Docker command 

    docker ps -a       <-- show current status of docker
    docker stop <id>>  <--  stop an image
    docker rm axonserver <-- rm an image

# Start the services

    cd catalog-service
    ./mvnw spring-boot:run

    cd order-service
    ./mvnw spring-boot:run

##### IntelliJ Editor settings

For Intellij users please make sure you add the annotation processor for Lombok 

    File/Settings/Annotation processor  -->  Make sure the checkbox: Enable annotation processor is selected.

The services can be executed from the IDE. For Ultimate users by the Spring Boot plugin of Intellij

## CLIENTS - Swagger and Vaadin

In order to access the REST API we run the Swagger UI for both services

    http://localhost:8080/swagger-ui.html
    
    http://localhost:8081/swagger-ui.html
    
    http://localhost:8081     <---- Vaadin
    
If succesfully started you should be able to see the screen as in the pic:  
    
        VaadingScreen.png    
    
## Software Architecture

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
be expected which is closer to a BDD approach to testing: the developer can test the sequence of event to set
up the stage for testing a particular command: 

    @Test
    public void test_update_product() {
        testFixture.given(
                new CatalogCreatedEvent(1, "me"),
                new ProductCreatedEvent(1, "me", "pomodoro"))
                .when(new ProductUpdateCommand(1, "me_2", "big pomodoro"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new ProductUpdatedEvent(1, "me_2","big pomodoro"));
    }

In this example for example we test the Update operation of a product forcing a replay of the events to rebuild the 
status of creating a catalog, adding a product and then finally issuing a ProductUpdateEvent. 

## Aggregates

In the order-service microservice (also called Bounded Context in DDD lingo) the aggregate is defined at:

    com/signore/demo/order/aggregate/OrderAggregate.java
    
In the catalog-service microservice (also called Bounded Context in DDD lingo) the aggregate is defined at:

    org/signore/axon/catalog/aggregate/Catalog.java
    

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

# DISCLAIMER

I have decided not to use the traditional stacked layer architecture because not only it is obsolete and not adapt 
to cloud apps but in full honesty, I'm currently implementing a pet application with Axon and so any chance I have to 
play with it, I take it. 

And an home assignment is a perfect chance to do so. If the contract does not working out at least is a learning experience.

Test coverage is not huge and so the application is not ready per production at all. Even if the coverage was 100% still it 
is not read for showtime not being validate by proper QA. In any case I hope the testing approach came accross.

Because of time limit and the complexity of the solution I did not go through all the corner cases so the app may have glitches
here and there. But the principle and the idea I hope came accross.

I hope you enjoyed the read as I did enjoy the coding.

Regard
Antonio 

