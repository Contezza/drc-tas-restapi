# DRC Test Automation System

Documentregistratiecomponent (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is het project wat wordt gebruikt voor het testen van de DRC API comform referentieimplementatie van VNG Realisatie. De unit tests beschreven in dit project zijn een vertaling van de [python](https://www.python.org) tests van de [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

De volgende componenten worden in dit project gebruikt:

* [RestAssured](https://rest-assured.io); 
* [TestNG](https://testng.org);
* [Lombok](https://projectlombok.org).

Bij gebruik van Eclipse installeer Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Bezoek [project Lombok website](https://projectlombok.org/setup/eclipse) voor meer informatie.

## Maven

Run tests:

```
mvn clean install -Dnashorn.args=--no-deprecation-warning
```

Run build:

```
mvn clean install -DskipTests=true
```