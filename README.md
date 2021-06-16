# DRC Test Automation System

Documentregistratiecomponent (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is het project wat wordt gebruikt voor het testen van de DRC API comform referentieimplementatie van VNG Realisatie. De unit tests beschreven in dit project zijn een vertaling van de [python](https://www.python.org) tests van de [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

De volgende componenten worden in dit project gebruikt:

* [RestAssured](https://rest-assured.io); 
* [TestNG](https://testng.org);
* [Lombok](https://projectlombok.org).

Bij gebruik van Eclipse installeer Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Bezoek [project Lombok website](https://projectlombok.org/setup/eclipse) voor meer informatie.

## Tests

Overzicht van de unit tests die zijn geïmplementeerd zoals beschreven op [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

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

Run tests with different environment:

```
// DRC Gemma (external)
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=drc
// DRC Open Zaak (internal)
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=open-zaak
// DRC Alfresco (external)
mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=alfresco
```

Run build:

```
mvn clean install -DskipTests=true
```

## Docker Test

When running the test in docker, [change](src/main/resources/environments) `service.baseuri` value with the value of `service.dockeruri`. In the following example you can run the test with the configured environment (`-Denv=alfresco`).

```
docker run -it --network=dev_network -v "$(pwd)":/root -w /root adoptopenjdk/maven-openjdk11:latest mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=alfresco
```

## Reports

Open following files for test results:

```
target/extent-output/execution.html
target/surefire-reports/index.html
```