package com.turntable.ddb;

import com.turntable.client.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoUserDaoTest {

    private static final String TABLE = "users";

    private DynamoDbClient client;
    private DynamoUserDao dao;

    @BeforeEach
    void setUp() {
        client = mock(DynamoDbClient.class);
        dao = new DynamoUserDao(client, TABLE);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_putsAllFields_whenEmailAndAvatarPresent() {
        User user = User.builder()
                .userId("u1").username("alice")
                .email("alice@example.com").avatar("http://img")
                .build();

        dao.create(user);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        Map<String, AttributeValue> item = captor.getValue().item();

        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals("u1", item.get("userId").s());
        assertEquals("alice", item.get("username").s());
        assertEquals("alice@example.com", item.get("email").s());
        assertEquals("http://img", item.get("avatar").s());
    }

    @Test
    void create_omitsOptionalFields_whenNull() {
        User user = User.builder().userId("u1").username("alice").build();

        dao.create(user);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        Map<String, AttributeValue> item = captor.getValue().item();

        assertFalse(item.containsKey("email"));
        assertFalse(item.containsKey("avatar"));
    }

    @Test
    void create_returnsInputUser() {
        User user = User.builder().userId("u1").username("alice").build();
        assertEquals(user, dao.create(user));
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_sendsCorrectKey() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().item(Map.of(
                        "userId", AttributeValue.fromS("u1"),
                        "username", AttributeValue.fromS("alice")
                )).build());

        dao.findById("u1");

        ArgumentCaptor<GetItemRequest> captor = ArgumentCaptor.forClass(GetItemRequest.class);
        verify(client).getItem(captor.capture());
        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals("u1", captor.getValue().key().get("userId").s());
    }

    @Test
    void findById_mapsAllFields() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().item(Map.of(
                        "userId", AttributeValue.fromS("u1"),
                        "username", AttributeValue.fromS("alice"),
                        "email", AttributeValue.fromS("alice@example.com"),
                        "avatar", AttributeValue.fromS("http://img")
                )).build());

        User user = dao.findById("u1");

        assertEquals("u1", user.getUserId());
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("http://img", user.getAvatar());
    }

    @Test
    void findById_mapsNullOptionalFields_whenAbsent() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().item(Map.of(
                        "userId", AttributeValue.fromS("u1"),
                        "username", AttributeValue.fromS("alice")
                )).build());

        User user = dao.findById("u1");

        assertNull(user.getEmail());
        assertNull(user.getAvatar());
    }

    @Test
    void findById_returnsNull_whenItemNotFound() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().build());

        assertNull(dao.findById("u1"));
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void update_setsAllFields_whenEmailAndAvatarPresent() {
        User user = User.builder()
                .userId("u1").username("alice")
                .email("alice@example.com").avatar("http://img")
                .build();

        dao.update("u1", user);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals(TABLE, req.tableName());
        assertEquals("u1", req.key().get("userId").s());
        assertTrue(req.updateExpression().contains("SET"));
        assertFalse(req.updateExpression().contains("REMOVE"));
        assertTrue(req.expressionAttributeValues().containsKey(":email"));
        assertTrue(req.expressionAttributeValues().containsKey(":avatar"));
    }

    @Test
    void update_removesOptionalFields_whenNull() {
        User user = User.builder().userId("u1").username("alice").build();

        dao.update("u1", user);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertTrue(req.updateExpression().contains("REMOVE"));
        assertFalse(req.expressionAttributeValues().containsKey(":email"));
        assertFalse(req.expressionAttributeValues().containsKey(":avatar"));
    }

    @Test
    void update_removesOnlyAvatar_whenEmailPresentAvatarNull() {
        User user = User.builder().userId("u1").username("alice").email("alice@example.com").build();

        dao.update("u1", user);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertTrue(req.expressionAttributeValues().containsKey(":email"));
        assertFalse(req.expressionAttributeValues().containsKey(":avatar"));
        assertTrue(req.updateExpression().contains("REMOVE"));
    }

    @Test
    void update_setsUsernameAlways() {
        User user = User.builder().userId("u1").username("alice").build();

        dao.update("u1", user);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals("alice", req.expressionAttributeValues().get(":username").s());
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_deletesItemByKey() {
        dao.delete("u1");

        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(client).deleteItem(captor.capture());
        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals("u1", captor.getValue().key().get("userId").s());
    }
}
