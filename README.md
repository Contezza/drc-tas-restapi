# DRC Test Automation System

Documentregistratiecomponent (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is het project wat wordt gebruikt voor het testen van de DRC API comform referentieimplementatie van VNG Realisatie. De unit tests beschreven in dit project zijn een vertaling van de [python](https://www.python.org) tests van de [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

De volgende componenten worden in dit project gebruikt:

* [RestAssured](https://rest-assured.io); 
* [TestNG](https://testng.org);
* [Lombok](https://projectlombok.org).

Bij gebruik van Eclipse installeer Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Bezoek [project Lombok website](https://projectlombok.org/setup/eclipse) voor meer informatie.

## Tests

Overzicht van de unit tests die zijn geïmplementeerd zoals beschreven op [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

### test_enkelvoudiginformatieobject.py

&nbsp;&nbsp;&#10004; test_create;\
&nbsp;&nbsp;&#10004; test_read;\
&nbsp;&nbsp;&#10004; test_eio_download_with_accept_application_octet_stream_header;\
&nbsp;&nbsp;&#10004; test_download_non_existing_eio;\
&nbsp;&nbsp;&#10004; test_bestandsomvang;\
&nbsp;&nbsp;&#10004; test_integrity_empty;\
&nbsp;&nbsp;&#10004; test_integrity_provided;\
&nbsp;&nbsp;&#10004; test_filter_by_identification;\
&nbsp;&nbsp;&#10004; test_destroy_no_relations_allowed;\
&nbsp;&nbsp;&#10004; test_destroy_with_relations_not_allowed;\
&nbsp;&nbsp;&#10004; test_validate_unknown_query_params;\
&nbsp;&nbsp;&#10004; test_invalid_inhoud;\
&nbsp;&nbsp;&#10004; test_eio_update;\
&nbsp;&nbsp;&#10004; test_eio_partial_update;\
&nbsp;&nbsp;&#10004; test_eio_delete;\
&nbsp;&nbsp;&#10004; test_eio_detail_retrieves_latest_version;\
&nbsp;&nbsp;&#10004; test_eio_list_shows_latest_versions;\
&nbsp;&nbsp;&#10004; test_eio_detail_filter_by_version;\
&nbsp;&nbsp;&#10004; test_eio_detail_filter_by_wrong_version_gives_404;\
&nbsp;&nbsp;&#10004; test_eio_detail_filter_by_registratie_op;\
&nbsp;&nbsp;&#10004; test_eio_detail_filter_by_wrong_registratie_op_gives_404;\
&nbsp;&nbsp;&#10004; test_eio_download_content_filter_by_version;\
&nbsp;&nbsp;&#10004; test_eio_download_content_filter_by_registratie;\
&nbsp;&nbsp;&#10004; test_pagination_default;\
&nbsp;&nbsp;&#10004; test_pagination_page_param;

### test_auth.py

&nbsp;&nbsp;&#10004; test_cannot_create_io_without_correct_scope; \
&nbsp;&nbsp;&#10004; test_cannot_read_without_correct_scope; \
&nbsp;&nbsp;&#10004; test_io_list;\
&nbsp;&nbsp;&#10004; test_io_retrieve;\
&nbsp;&nbsp;&#10004; test_read_superuser;\
&nbsp;&nbsp;&#10004; test_list_gebruiksrechten_limited_to_authorized_zaken;\
&nbsp;&nbsp;&#10004; test_create_gebruiksrechten_limited_to_authorized_zaken;\
&nbsp;&nbsp;&#10004; test_list_oio_limited_to_authorized_zaken;\
&nbsp;&nbsp;&#10004; test_detail_oio_limited_to_authorized_zaken;

## Docker

Run docker before executing tests:

```
cd docker
docker-compose up -d
```

* http://localhost:8000 (open-zaak)
* http://localhost:8001 (open-notificaties)
* http://localhost:8002 (drc-gemma)

Login: admin/admin

## Maven

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

## Docker

When running the test in docker, change `service.baseuri` value with the value of `service.dockeruri`. In the following example you can run the test with the configured environment (`-Denv=alfresco`).

```
docker run -it --network=dev_network -v "$(pwd)":/root -w /root adoptopenjdk/maven-openjdk11:latest mvn clean install -Dnashorn.args=--no-deprecation-warning -Denv=alfresco
```

## Reports

Open following files for test results:

```
target/extent-output/execution.html
target/surefire-reports/index.html
```