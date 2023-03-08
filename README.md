# Synantisi Project

F2F scheduler that creates a timetable for events avoiding preferred and required attendance conflicts.



## Getting Started with development server

To run synantisi you will need Java SDK installed

### Install Java SDK by sdkman

Install sdkman by the [installation guide](https://sdkman.io/install) 

After sdkman installation run:
```shell script
sdk install java
```
## Usage

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```
The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

http://localhost:8080

![Synantisi home](https://github.com/dupliaka/synantisi/blob/main/src/main/resources/synantisi.jpg)

To get the calculation done you would need to provide a filled out 
excel file. See for example [50meetings-160timegrains-5rooms.xlsx](50meetings-160timegrains-5rooms.xlsx)

There you should fill up Tabs:
- Days
- Rooms
- Persons
- Meetings
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Contributing

Contribution is the best way to support and get involved in community !

- [Raise an issue](https://github.com/dupliaka/synantisi/issues)
- [Feature request](https://github.com/dupliaka/synantisi/issues)
- [Code submission](https://github.com/dupliaka/synantisi/pulls)

Consider giving the project a star on GitHub if you find it useful.


