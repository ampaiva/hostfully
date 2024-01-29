package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.dto.BookingDto;
import com.ampaiva.hostfully.dto.GuestDto;
import com.ampaiva.hostfully.dto.PropertyDto;
import com.ampaiva.hostfully.utils.DtoMetadata;
import com.ampaiva.hostfully.utils.DtoUtils;
import com.ampaiva.hostfully.utils.PayloadBuilder;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class BaseIntegrationTest {

    public static final String API = "/api/";
    private final Class<?> dtoClass;
    private final String dtoPlural;
    private final String dtoSingular;
    protected RequestSpecification spec;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    DtoUtils dtoUtils;

    @Autowired
    Faker faker;

    @Autowired
    PayloadBuilder payloadBuilder;

    private List<DtoMetadata> listDtoMetadata;

    BaseIntegrationTest(Class<?> dtoClass, String dtoPlural, String dtoSingular) {
        this.dtoClass = dtoClass;
        this.dtoPlural = dtoPlural;
        this.dtoSingular = dtoSingular;
    }

    @PostConstruct
    public void init() {
        RestAssured.port = randomServerPort;
        payloadBuilder.getPropertyId = this::getPropertyId;
        payloadBuilder.getGuestId = this::getGuestId;
    }


    private OperationRequestPreprocessor getPreprocessor() {
        return preprocessRequest(modifyUris()
                .scheme("https")
                .host("com.ampaiva.hostfully")
                .removePort());
    }

    private List<DtoMetadata> getMetadata() {
        if (listDtoMetadata == null) {
            this.listDtoMetadata = dtoUtils.getDtoMetadata(dtoClass);
        }
        return listDtoMetadata;
    }

    private String getSingularCamelCase() {
        return dtoSingular.substring(0, 1).toUpperCase() + dtoSingular.substring(1);
    }


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    private int getId(String dtoPlural, List<DtoMetadata> dtoMetadata) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(dtoMetadata))
                .when()
                .post(API + dtoPlural)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    private int getId() {
        return getId(dtoPlural, getMetadata());
    }

    int getPropertyId() {
        return getId("properties", dtoUtils.getDtoMetadata(PropertyDto.class));
    }

    int getGuestId() {
        return getId("guests", dtoUtils.getDtoMetadata(GuestDto.class));
    }

    int getBookingId() {
        return getId("bookings", dtoUtils.getDtoMetadata(BookingDto.class));
    }

    private String getIdentifierPrefix() {
        return "api/" + dtoPlural;
    }

    private String getIdentifier(String method, int responseCode) {
        return getIdentifierPrefix() + "/" + method + "/" + responseCode;
    }

    String getPostIdentifier(int responseCode) {
        return getIdentifier("post", responseCode);
    }

    private String getGetIdentifier(int responseCode) {
        return getIdentifier("get", responseCode);
    }

    RestDocumentationFilter getDocument(String identifier) {
        return document(identifier, getPreprocessor());
    }

    private RestDocumentationFilter getDocument(String identifier, RequestFieldsSnippet requestFields) {
        return document(identifier, getPreprocessor(), requestFields);
    }

    private RestDocumentationFilter getDocument(String getIdentifier, PathParametersSnippet pathParametersSnippet) {
        return document(getIdentifier, getPreprocessor(), pathParametersSnippet);
    }

    private RestDocumentationFilter getDocument(String getIdentifier, PathParametersSnippet pathParametersSnippet, ResponseFieldsSnippet responseFieldsSnippet) {
        return document(getIdentifier, getPreprocessor(), pathParametersSnippet,
                responseFieldsSnippet);
    }

    RequestSpecification givenDoc(RestDocumentationFilter document) {
        return given(this.spec).filter(document);
    }

    RequestSpecification givenWithRequest(String identifier, RequestFieldsSnippet requestFields) {
        return givenDoc(getDocument(identifier, requestFields));
    }

    RequestSpecification givenWithPath(String identifier, PathParametersSnippet pathParameters) {
        return givenDoc(getDocument(identifier, pathParameters));
    }

    RequestSpecification givenWithPathAndResponse(String identifier, PathParametersSnippet pathParameters, ResponseFieldsSnippet responseFields) {
        return givenDoc(getDocument(identifier, pathParameters, responseFields));
    }

    RequestSpecification givenCreate(int responseCode, RequestFieldsSnippet requestFields) {
        return givenWithRequest(getPostIdentifier(responseCode), requestFields);
    }

    RequestSpecification givenCreate(int responseCode) {
        return givenCreate(responseCode, requestFields(dtoUtils.generateCreateFieldDescriptors(getMetadata())));
    }

    private RequestSpecification givenGet(int responseCode, PathParametersSnippet pathParametersSnippet) {
        return givenWithPath(getGetIdentifier(responseCode), pathParametersSnippet);
    }

    private RequestSpecification givenGet(int responseCode, PathParametersSnippet pathParametersSnippet, ResponseFieldsSnippet responseFieldsSnippet) {
        return givenWithPathAndResponse(getGetIdentifier(responseCode), pathParametersSnippet, responseFieldsSnippet);
    }

    private RequestSpecification givenGet(int responseCode) {
        return givenGet(responseCode, pathParameters(dtoUtils.generateIdParameter(getMetadata())), responseFields(dtoUtils.generateGetFieldDescriptors(getMetadata())));
    }

    private RequestSpecification givenGetWithPath(int responseCode) {
        return givenGet(responseCode, pathParameters(dtoUtils.generateIdParameter(getMetadata())));
    }

    @Test
    public void testCreate() {
        // Create
        int id = givenCreate(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(getMetadata()))
                .when()
                .post(API + dtoPlural)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        assertTrue(id > 0);
    }

    @Test
    public void testCreateWithMissingNonNullableFields() {
        givenCreate(HttpStatus.BAD_REQUEST.value(), requestFields(dtoUtils.generateMissingFieldDescriptors(getMetadata())))
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreateMissingNonNullablePayload(getMetadata()))
                .when()
                .post(API + dtoPlural)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.TEXT)
                .body(containsString("not-null property references a null or transient value : com.ampaiva.hostfully.model." + getSingularCamelCase()));
    }

    @Test
    public void testGetExisting() {
        // Create
        int id = getId();

        // Get
        givenGet(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .when()
                .get(API + dtoPlural + "/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id));
    }

    @Test
    public void testGetNonExisting() {
        int nonExistingId = Integer.MAX_VALUE;

        givenGetWithPath(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .when()
                .get(API + dtoPlural + "/{id}", nonExistingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + nonExistingId + " not found"));
    }

    @Test
    public void testGetAll() {
        // Create
        int id = getId();

        // Get All
        givenDoc(document("api/" + dtoPlural + "/get-all", getPreprocessor()))
                .contentType(ContentType.JSON)
                .when()
                .get(API + dtoPlural)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasItems(hasEntry("id", id)));

    }


    @Test
    public void testUpdate() {
        // Create
        int id = getId();

        var fakeValues = payloadBuilder.generateCreateFakeValues(getMetadata());

        // Update
        givenDoc(document("api/" + dtoPlural + "/put/" + HttpStatus.OK.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(fakeValues))
                .when()
                .put(API + dtoPlural + "/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value());
        //TODO: Implement the matcher
        //.body("", hasItems(hasEntries(fakeValues)));
    }


    private Matcher<String>[] hasEntries(Map<String, String> listDtoMetadata) {
        return listDtoMetadata.entrySet().stream()
                .map(dtoMetadata -> hasEntry(dtoMetadata.getKey(), dtoMetadata.getValue()))
                .toArray(Matcher[]::new);
    }


    @Test
    public void testUpdateNonExisting() {
        // Create
        int nonExistingId = Integer.MAX_VALUE;

        // Update
        givenDoc(document("api/" + dtoPlural + "/put/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(getMetadata()))
                .when()
                .put(API + dtoPlural + "/{id}", nonExistingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + nonExistingId + " not found"));
    }

    @Test
    public void testPatch() {
        // Create
        int id = getId();

        var fakeValues = payloadBuilder.generateCreateFakeValuesFilterRelations(getMetadata());

        // Update
        givenDoc(document("api/" + dtoPlural + "/patch/" + HttpStatus.OK.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(fakeValues))
                .when()
                .patch(API + dtoPlural + "/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value());
        //TODO: Implement the matcher
        //.body("", hasItems(hasEntries(fakeValues)));
    }

    @Test
    public void testPatchNonExisting() {
        // Create
        int nonExistingId = Integer.MAX_VALUE;

        // Update
        givenDoc(document("api/" + dtoPlural + "/patch/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .body(payloadBuilder.generateCreatePayload(getMetadata()))
                .when()
                .patch(API + dtoPlural + "/{id}", nonExistingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + nonExistingId + " not found"));
    }

    @Test
    public void testDelete() {
        // Create
        int id = getId();

        // Delete
        givenDoc(document("api/" + dtoPlural + "/delete/" + HttpStatus.NO_CONTENT.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .when()
                .delete(API + dtoPlural + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Retrieve Failed
        givenDoc(document("api/" + dtoPlural + "/delete/" + HttpStatus.NOT_FOUND.value(), getPreprocessor(),
                pathParameters(dtoUtils.generateIdParameter(getMetadata()))))
                .contentType(ContentType.JSON)
                .when()
                .delete(API + dtoPlural + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.TEXT)
                .body(containsString("Object with id=" + id + " not found"));
    }

}
