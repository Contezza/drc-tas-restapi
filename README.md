# DRC Test Automation System

Documentregistratiecomponent (DRC) **T**est **A**utomation **S**ystem (TAS) **RESTAPI** is het project wat wordt gebruikt voor het testen van de DRC API comform referentieimplementatie van VNG Realisatie. De unit tests beschreven in dit project zijn een vertaling van de [python](https://www.python.org) tests van de [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

De volgende componenten worden in dit project gebruikt:

* [RestAssured](https://rest-assured.io); 
* [TestNG](https://testng.org);
* [Lombok](https://projectlombok.org).

Bij gebruik van Eclipse installeer Lombok (Help -> Install New Software -> https://projectlombok.org/p2). Bezoek [project Lombok website](https://projectlombok.org/setup/eclipse) voor meer informatie.

## Tests

Overzicht van de unit tests die zijn geïmplementeerd zoals beschreven op [documenten api](https://github.com/VNG-Realisatie/documenten-api/tree/stable/1.0.x/src/drc/api/tests) repository.

### test_enkelvoudiginformatieobject

#### EnkelvoudigInformatieObject

* test_create;
* test_read;
* test_eio_download_with_accept_application_octet_stream_header;
* test_download_non_existing_eio;
* test_bestandsomvang;
* test_integrity_empty;
* test_integrity_provided;
* test_filter_by_identification;
* test_destroy_no_relations_allowed;
* test_destroy_with_relations_not_allowed;
* test_validate_unknown_query_params;
* test_invalid_inhoud;

#### EnkelvoudigInformatieObjectVersionHistory

* test_eio_update;
* test_eio_partial_update;
* test_eio_delete;
* test_eio_detail_retrieves_latest_version;
* test_eio_list_shows_latest_versions;
* test_eio_detail_filter_by_version;
* test_eio_detail_filter_by_wrong_version_gives_404;
* test_eio_detail_filter_by_registratie_op;
* test_eio_detail_filter_by_wrong_registratie_op_gives_404;
* test_eio_download_content_filter_by_version;
* test_eio_download_content_filter_by_registratie;

#### EnkelvoudigInformatieObjectPagination

&nbsp;&nbsp;&#10004; test_pagination_default; \
&nbsp;&nbsp;&#10004; test_pagination_page_param;

### Auth

#### InformatieObjectScopeForbidden

&nbsp;&#10005; test_cannot_create_io_without_correct_scope; \
&nbsp;&#10005; test_cannot_read_without_correct_scope;

#### InformatieObjectReadCorrectScope

* test_io_list;
* test_io_retrieve;
* test_read_superuser;

#### GebruiksrechtenRead

* test_list_gebruiksrechten_limited_to_authorized_zaken;
* test_create_gebruiksrechten_limited_to_authorized_zaken;

#### OioRead

* test_list_oio_limited_to_authorized_zaken;
* test_list_oio_limited_to_authorized_zaken;

## Maven

Run tests:

```
mvn clean install -Dnashorn.args=--no-deprecation-warning
```

Run build:

```
mvn clean install -DskipTests=true
```