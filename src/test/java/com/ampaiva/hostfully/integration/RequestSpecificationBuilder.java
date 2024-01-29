package com.ampaiva.hostfully.integration;

import io.restassured.specification.RequestSpecification;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class RequestSpecificationBuilder {

    private final RequestSpecification spec;
    private final OperationRequestPreprocessor preprocessor;
    private final StringBuilder identifierBuilder = new StringBuilder();
    private RequestSpecificationBuilder(RequestSpecification spec, OperationRequestPreprocessor preprocessor) {
        this.spec = spec;
        this.preprocessor = preprocessor;
    }

    public static RequestSpecificationBuilder givenDoc(RequestSpecification spec, OperationRequestPreprocessor preprocessor) {
        return new RequestSpecificationBuilder(spec, preprocessor);
    }

    public RequestSpecificationBuilder identifier(String identifier) {
        identifierBuilder.append(identifier);
        return this;
    }

    public PathParametersBuilder path(PathParametersSnippet pathParameters) {
        return new PathParametersBuilder(pathParameters);
    }

    private RequestSpecification doc(PathParametersSnippet pathParameters) {
        return given(this.spec).filter(document(identifierBuilder.toString(), preprocessor, pathParameters));
    }

    private RequestSpecification doc(PathParametersSnippet pathParameters, ResponseFieldsSnippet responseFields) {
        return given(this.spec).filter(document(identifierBuilder.toString(), preprocessor, pathParameters, responseFields));
    }

    class PathParametersAndResponseFieldsBuilder {
        private final PathParametersSnippet pathParameters;
        private final ResponseFieldsSnippet responseFieldsSnippet;


        PathParametersAndResponseFieldsBuilder(PathParametersSnippet pathParameters, ResponseFieldsSnippet responseFieldsSnippet) {
            this.pathParameters = pathParameters;
            this.responseFieldsSnippet = responseFieldsSnippet;
        }

        public RequestSpecification doc() {
            return RequestSpecificationBuilder.this.doc(pathParameters, responseFieldsSnippet);
        }
    }

    class PathParametersBuilder {
        private final PathParametersSnippet pathParameters;

        PathParametersBuilder(PathParametersSnippet pathParameters) {
            this.pathParameters = pathParameters;
        }

        public PathParametersAndResponseFieldsBuilder response(ResponseFieldsSnippet responseFieldsSnippet) {
            return new PathParametersAndResponseFieldsBuilder(pathParameters, responseFieldsSnippet);
        }

        public RequestSpecification doc() {
            return RequestSpecificationBuilder.this.doc(pathParameters);
        }
    }
}
