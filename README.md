# URL Shortener Service
> Java project written by Ofir Baranes. This document provides installation and running instructions.

## Introduction

> Url shortener service that converts urls into short 8-digits hash codes.
> When a user will use the hash code, it will be automatically redirected to the original (long) url.
> Main technologies used in this project
> Java, Spring Boot, jUnit, Maven

## How to import

Import in your Java IDE or specific location from 
```
git clone https://github.com/OfirBaranes/url-shortener.git
```


## How to start the service

The service could be started by pressing run in your IDE, or from the 
command line by executing 
```
cd url-shortener
```
then
```
./mvnw package
java -jar target/*.jar
```
or
```
./mvnw spring-boot:run
```
 You can then access the service from http://localhost:8080/.

## System API

| Method | Path | Parameters | Return
| ------ | ------ | ------ | ------ |
| POST | "/" |{ originalUrl (String) } JSON object  | { hashURL (String), new (boolean) } JSON object
| GET | "/{urlHash}"  | String | If url hash exists - redirect to the original url

## How to use
The application is running on localhost port 8080. 
Example:

Post to localhost:8080 with the following json  - 
```
curl -H "Content-type: application/json" -X POST -d '{"originalUrl":"https://www.optimove.com/about-us"}' http://localhost:8080
```

(or use Postman)

This request will put the url https://www.optimove.com/about-us in the service url database and map it to the new url hash by the application.
Assuming the url hash you got is 63729d49 - 
If you then put localhost:8080/63729d49 in your browser, you will be automatically redirected to https://www.optimove.com/about-us.

## Shortening Algorithm

The Url hash codes were made using the hashing algorithm murmur3.
It is simple, has good performance and has good collision resistance.
You can read more here - 
https://guava.dev/releases/31.0-jre/api/docs/com/google/common/hash/Hashing.html
## More documentation
There are more examples of the behaviour of the service in the jUnit tests.

## Suggestion of some future improvements of the service
* Replacing the H2 database to a NoSQL database.
* Add a caching layer such as Redis to improve the latency and reading time.
* Allow customised aliases of the urls.
* Adding an expriation time to the url hash codes.
