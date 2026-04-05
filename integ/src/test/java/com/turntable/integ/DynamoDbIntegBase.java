package com.turntable.integ;

import org.testcontainers.containers.GenericContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;

/**
 * Shared DynamoDB Local container and client for all integ tests.
 *
 * <p>The container is started once and reused across all subclasses (singleton pattern).
 * Each subclass creates its own tables in {@code @BeforeAll} using the helpers below.
 */
@SuppressWarnings("resource")
public abstract class DynamoDbIntegBase {

    // Singleton container — started once, shared across all integ test classes.
    static final GenericContainer<?> DYNAMO_DB;
    static final DynamoDbClient CLIENT;

    static {
        DYNAMO_DB = new GenericContainer<>("amazon/dynamodb-local:latest")
                .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
                .withExposedPorts(8000);
        DYNAMO_DB.start();

        CLIENT = DynamoDbClient.builder()
                .endpointOverride(URI.create(
                        "http://localhost:" + DYNAMO_DB.getMappedPort(8000)))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")))
                .build();
    }

    /** Creates a table with a single hash key (no sort key). */
    protected static void createTable(String tableName, String pkName) {
        CLIENT.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(pkName)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(pkName)
                        .keyType(KeyType.HASH)
                        .build())
                .build());
    }

    /** Creates a table with a hash key and a sort key. */
    protected static void createTable(String tableName, String pkName, String skName) {
        CLIENT.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName(pkName)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(skName)
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName(pkName)
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName(skName)
                                .keyType(KeyType.RANGE)
                                .build())
                .build());
    }
}
