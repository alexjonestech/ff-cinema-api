# FF Cinema API
Interface for FF Cinema, which plays only movies from the Fast & Furious franchise.

# Overview
This backend service was created as part of an interview coding project. The requirements are to build out a backend service with an API that can be consumed directly and by a (hypothetical) frontend service. The main features to be implemented are:

- an endpoint for reading and updating movie show times and ticket prices
- an endpoint for reading and updating user ratings (1-5)
- an endpoint for reading movie info via the OMDb API

# Setup

In order to get everything up and running, you will need Docker and some way of unpacking `.zip` archives.

## Unzip Data Volumes

First, unzip the `data.zip` volume to the root directory of the project. This will give you a `data` directory with subdirectories for `mysql` and `mongo`, which will be the volumes linked to the databse docker containers.

## Run Docker Containers

Then, from the root directory in a terminal, run the following commands:

`docker run --name ffcinema-mysql -p 3306:3306 -d -e MYSQL_ROOT_PASSWORD=test -v $PWD/data/mysql:/var/lib/mysql mysql`

`docker run --name ffcinema-mongo -p 27017:27017 -d -v $PWD/data/mongo:/data/db mongo`

This should run the two databases with the provided volumes, which include empty tables pre-configured for the service.

## Input API Key

In order to access the OMDb API, you will need an API key. There is no API key provided as part of this repository, but you can get one for yourself at https://www.omdbapi.com/apikey.aspx. Once you have a key, locate the `src/.../ffcinema/key/key.kt` file and edit it so that your API key is between the quotation marks. This is only required for the `/info` endpoint, so it can be omitted at the cost of this functionality.

## Run the Web Service

To run the service, from the root directory in a terminal, run:

`gradlew build`

and then

`java -jar ./build/libs/ff-cinema-0.0.1-SNAPSHOT.jar`

A simpler way to run the service would be to simply open in your preferred IDE (e.g. IntelliJ) and run from there.

# Run the Unit Tests

To run the test, from the root directory in a terminal, run:

`gradlew test --info`

which will provide a convenient html output in the build directory for reviewing test results.

## View the Documentation

This service doesn't include a SwaggerUI server. You can put up your own server to view the documentation located at `documentation/openapi.yaml`, or you can open that file with https://editor.swagger.io/.

# Design

## API Design
The API design is fairly straightforward, since we just have 2 CRUD endpoints. `POST` endpoints here are doing double duty as `PUT` endpoints, depending on what data is passed in and what data exists in the database, which simplifies the API. The OMDb call is a simple proxy, with a single passthrough method for getting the response. We can do this because we have no middle-processing here; if we wanted to add or transform the data coming from OMDb before emitting, we would need a more involved controller. API is documented in the OpenAPI Specification.

## Backend Service
The backend service is a Kotlin/Spring codebase using Spring Boot. It utilizes the Webflux reactive framework with Reactor and reactive MySQL (through R2DBC) and MongoDb databases. This fully reactive stack allows for an asyncronous API, as well as a highly functional and reactive style codebase, which I find especially elegant. The reactive database implementation through Spring Data gives us most of the requisite queries for free, reducing code complexity and boilerplate for simple database access.

The backend service is tested using JUnit 5 and Mockk. Testing is somewhat barebones here, since most of the API is a simple passthrough to the database access libraries. Wherever additional logic is being used to perform the operations, there are a few tests to make sure things are working as expected.

## Databases
There are two databases used in this project: MySQL and MongoDb. 

The MySQL table holds the data for the movie showings, which has fields for movie ID, show times, and ticket prices. Since this table only needs to hold as many showings as are planned in the near future, there's no way it ever gets big enough that it won't fit on a single machine. Further, the user base of this table is only the local customer base of the theater, so request volume should be especially low. On the other hand, MySQL is a very simple but effective database, so it's a good default if there's no reason not to use it. The table is designed under the assumption that there is only one theater; to avoid scaling issues as the number of theaters increases, we can use local database servers with region-specific tables, but this was not done for this project.

The MongoDb document holds the data for the user ratings, which has fields for user ID, movie ID, and score (1-5). Unlike the movie showings, user rating size and request volume is essentially unbounded, so scaling needs to be taken into consideration. To that end, a NoSQL database is a good choice. MongoDb was specifically chosen because the data model of nested object fits nicely into the document structure, and there is a significant overlap between the JSON used for API messages and the MongoDb document structure.

# Omissions
Due to the time contraints, there are many additional features that I would like to implement but have not. The main one is security, which is entirely absent from this application. There is no difference between the public and private API here, but the `POST` and `DELETE` endpoints for movie showings should be private. In that same vein, the API key and database credentials can be offboarded to a configuration service stored on a private repository, but I didn't do that for obvious reasons. As mentioned above, testing is somewhat barebones, and more in-depth testing could be beneficial even on this simple API. Lastly, there were some proper features which I considered implementing but ended up leaving out, such as getting showings by date, getting all ratings for a given movie, and tracking (and emitting) running averages for user ratings. None of the items omitted here would take especially long, but in the interest of keeping a tight scope for this project, they didn't make the cut.
