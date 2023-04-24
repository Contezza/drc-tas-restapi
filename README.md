# DRC Test Automation System

Document registration component (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is the project used for testing the DRC API according to reference implementation of VNG Realisatie. The unit tests described in this project are translated [python](https://www.python.org) tests from the [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/1.3.0/src/drc/tests) repository.

The following components are used in this project:

- [RestAssured](https://rest-assured.io);
- [TestNG](https://testng.org);
- [Lombok](https://projectlombok.org).

If using Eclipse install Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Visit [project Lombok site](https://projectlombok.org/setup/eclipse) for more information.

## Tests

Overview of the unit tests as described on [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/1.3.0/src/drc/tests) repository. The following unit tests are available:

&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectCachingTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectCachingTest.java) \
&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectPaginationTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectPaginationTest.java) \
&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectTest.java) \
&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectTest.java) \
&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectVersionHistoryTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectVersionHistoryTest.java) \
&nbsp;&nbsp;&#10004; [EnkelvoudigInformatieObjectZoekTest](src/test/java/nl/contezza/drc/tests/EnkelvoudigInformatieObjectZoekTest.java) \
&nbsp;&nbsp;&#10004; [GebruiksrechtenCachingTest](src/test/java/nl/contezza/drc/tests/GebruiksrechtenCachingTest.java) \
&nbsp;&nbsp;&#10004; [GebruiksrechtenReadTest](src/test/java/nl/contezza/drc/tests/GebruiksrechtenReadTest.java) \
&nbsp;&nbsp;&#10004; [InformatieObjectReadCorrectScopeTest](src/test/java/nl/contezza/drc/tests/InformatieObjectReadCorrectScopeTest.java) \
&nbsp;&nbsp;&#10004; [InformatieObjectScopeForbiddenTest](src/test/java/nl/contezza/drc/tests/InformatieObjectScopeForbiddenTest.java) \
&nbsp;&nbsp;&#10004; [OioCachingTest](src/test/java/nl/contezza/drc/tests/OioCachingTest.java) \
&nbsp;&nbsp;&#10004; [OioReadTest](src/test/java/nl/contezza/drc/tests/OioReadTest.java) \
&nbsp;&nbsp;&#10004; [UploadTest](src/test/java/nl/contezza/drc/tests/UploadTest.java) \
&nbsp;&nbsp;&#10004; [VerzendingTest](src/test/java/nl/contezza/drc/tests/VerzendingTest.java)

There are also some [custom unit tests](src/test/java/nl/contezza/drc/tests/custom) described that are not available in the python tests.

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

- http://localhost:8000 (open-zaak)
- http://localhost:8001 (open-notificaties)
- http://localhost:8002 (drc-gemma)
- http://localhost:8080/alfresco (alfresco)
- http://localhost:8081/share (share client)

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

## License

Licensed under the [EUPL](LICENSE.md)
