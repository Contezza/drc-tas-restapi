# DRC Test Automation System

Document registration component (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is the project used for testing the DRC API according to reference implementation of VNG Realisatie. The unit tests described in this project are translated [python](https://www.python.org) tests from the [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

The following components are used in this project:

* [RestAssured](https://rest-assured.io); 
* [TestNG](https://testng.org);
* [Lombok](https://projectlombok.org).

If using Eclipse install Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Visit [project Lombok site](https://projectlombok.org/setup/eclipse) for more information.

## Tests

Overview of the unit tests as described on [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

&nbsp;&nbsp;&#10004; [test_enkelvoudiginformatieobject.py](https://github.com/VNG-Realisatie/documenten-api/blob/stable/1.0.x/src/drc/api/tests/test_enkelvoudiginformatieobject.py) (25) \
&nbsp;&nbsp;&#10004; [test_auth.py](https://github.com/VNG-Realisatie/documenten-api/blob/stable/1.0.x/src/drc/api/tests/test_auth.py) (9)

## Docker

Run docker before executing tests. Create `dev_network` if not already exists (`docker network create -d bridge dev_network`).

```
// Open Zaak and DRC Gemma
cd docker
docker-compose up -d

// Open Zaak, DRC Gemma and Alfresco
cd docker
docker-compose -f docker-compose.yml -f docker-compose.alfresco.yml up -d
```

* http://localhost:8000 (open-zaak)
* http://localhost:8001 (open-notificaties)
* http://localhost:8002 (drc-gemma)
* http://localhost:8080 (alfresco)

Login: admin/admin

## Maven Test

Run tests (default) with DRC gemma (external):

```
mvn clean install -Dnashorn.args=--no-deprecation-warning
```

When running the test wihout docker, [change](src/main/resources/environments) `service.baseuri` value with localhost. Run tests with different environment:

```
// DRC Gemma (reference implementation of VNG Realisatie)
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=drc
// DRC Open Zaak
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=open-zaak
// DRC Alfresco
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=alfresco
```

Run build:

```
mvn clean install -DskipTests=true
```

## Docker Test

In the following example you can run the test with the configured environment (`-Denv=alfresco`).

```
docker run -it --network=dev_network -v "$(pwd)":/root -w /root adoptopenjdk/maven-openjdk11:latest mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=alfresco
```

## Reports

Open following files for test results:

```
target/extent-output/execution.html
target/surefire-reports/index.html
```