# SGIS - SERVER

## About The Project

This project is a RESTful API used for managing instrumensts and spares, by communication with SGIS Database and NASPH API.
On this way, this project is responsible for create and update instruments, also emit reports and get datas about it.

### Built With

* [Java 11](https://www.oracle.com/br/java/technologies/javase/jdk11-archive-downloads.html) :coffee:
* [Maven](https://maven.apache.org/)
* [OracleDB](https://www.oracle.com/br/database/)
* [Spring](https://spring.io/) :leaves:
* [Flyway](https://flywaydb.org/)
* [OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
* [JasperSoft 6.18.1](https://community.jaspersoft.com/project/jaspersoft-studio)


<p align="right">(<a href="#top">back to top</a>)</p>

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

**1.JAVA**

First of all, to compile the server, you need to install Java 11 JDK, according to your OS specifications.
You can get the JDK at this link: [Java 11 JDK](https://www.oracle.com/br/java/technologies/javase/jdk11-archive-downloads.html)

    PS: If you use Windows OS, you need to put the JAVA_HOME as an evironment variable.

**2.Maven**

You will need Maven to get the project dependencies. You can download it at this link: [Maven](https://maven.apache.org/download.cgi).
The Maven will be responsible to get all the other technologies we will use on this project.

<p align="right">(<a href="#top">back to top</a>)</p>

### Installation

1 Clone the repo
   ```sh
   git clone https://gitlab.squadra.com.br/waltenes.junior/sgis-server.git
   ```
3. On the project path, open the terminal and install the dependencies
   ```sh
   mvn clean install
   ```

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

Now you installed the dependencies, the project will be ready to run.
When it be running, you could  access the API by the swagger documentation at this link: http://localhost:8080/swagger-ui.html#/


<p align="right">(<a href="#top">back to top</a>)</p>

## Database

### Flyway

At the time of writing this documentation, we still do not have communication with the Oracle database in a more current version, which must be provided by the customer. Thus, as a means of circumventing the problem and proceeding with the implementation, Flyway was used in order to create and insert data in the tables that we will use, in a local H2 database, following the mapping and insertion style of the legacy database.

