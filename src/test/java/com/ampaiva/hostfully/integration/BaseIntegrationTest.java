package com.ampaiva.hostfully.integration;


import com.ampaiva.hostfully.utils.DtoMetadata;
import com.ampaiva.hostfully.utils.DtoUtils;
import com.ampaiva.hostfully.utils.PayloadBuilder;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    public static final String API = "/api/";
    public static final String API_GUEST = "/api/guests";
    public static final String API_PROPERTY = "/api/properties";
    public static final String API_BLOCK = "/api/blocks";
    public static final String API_BOOKING = "/api/bookings";
    public static final String CANCEL = "/cancel";
    public static final String REBOOK = "/rebook";
    protected RequestSpecification spec;
    @LocalServerPort
    int randomServerPort;
    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    DtoUtils dtoUtils;

    @Autowired
    Faker faker;

    @Autowired
    PayloadBuilder payloadBuilder;

    private final Class<?> dtoClass;
    private final String dtoPlural;

    private List<DtoMetadata> listDtoMetadata;

    BaseIntegrationTest(Class<?> dtoClass, String dtoPlural) {
        this.dtoClass = dtoClass;
        this.dtoPlural = dtoPlural;
    }

    private List<DtoMetadata> getMetadata() {
        if (listDtoMetadata == null) {
            this.listDtoMetadata = dtoUtils.getDtoMetadata(dtoClass);
        }
        return listDtoMetadata;
    }

    static int getPropertyId() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"address\": \"Disney Road, 2024\", \"city\": \"Orlando\", \"state\": \"FL\", \"country\": \"USA\" }")
                .when()
                .post(API_PROPERTY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    protected static OperationRequestPreprocessor getPreprocessor() {
        return preprocessRequest(modifyUris()
                .scheme("https")
                .host("com.ampaiva.hostfully")
                .removePort());
    }

    private String getJsonRepr(Map.Entry<String, String> entry) {
        return String.format("\"%s\": \"%s\"", entry.getKey(), entry.getValue());
    }

    String generateBody(Map<String, String> map) {
        return "{" + map.entrySet().stream()
                .map(this::getJsonRepr)
                .collect(Collectors.joining(",")) + "}";
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = randomServerPort;
        RestAssured.basePath = contextPath;
    }

    @Test
    public void testCreate() {
        // Create
        int id = given(this.spec)
                .filter(document("api/" + dtoPlural + "/post/" + HttpStatus.CREATED.value(), getPreprocessor(),
                        requestFields(dtoUtils.generateCreateFieldDescriptors(getMetadata()))))
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


}
