package com.turntable.ddb;

import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoFriendDaoTest {

    private static final String FRIENDS_TABLE = "friends";

    private DynamoDbClient client;
    private DynamoFriendDao dao;

    @BeforeEach
    void setUp() {
        client = mock(DynamoDbClient.class);
        dao = new DynamoFriendDao(client, FRIENDS_TABLE);
    }

    // ── findByUser ────────────────────────────────────────────────────────────

    @Test
    void findByUser_queriesWithUserIdAndStatusFilter() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        dao.findByUser("u1", FriendStatus.ACCEPTED);

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());
        QueryRequest req = captor.getValue();

        assertEquals(FRIENDS_TABLE, req.tableName());
        assertEquals("u1", req.expressionAttributeValues().get(":userId").s());
        assertEquals("ACCEPTED", req.expressionAttributeValues().get(":status").s());
        assertTrue(req.filterExpression().contains(":status"));
    }

    @Test
    void findByUser_returnsMappedFriends() {
        Instant friendsSince = Instant.parse("2025-01-01T00:00:00Z");
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of(Map.of(
                        "friendId", AttributeValue.fromS("user2"),
                        "status", AttributeValue.fromS("ACCEPTED"),
                        "friendsSince", AttributeValue.fromS(friendsSince.toString())
                ))).build());

        List<Friend> friends = dao.findByUser("u1", FriendStatus.ACCEPTED);

        assertEquals(1, friends.size());
        Friend f = friends.get(0);
        assertEquals("user2", f.getFriendId());
        assertEquals(FriendStatus.ACCEPTED, f.getStatus());
        assertEquals(friendsSince, f.getFriendsSince());
    }

    @Test
    void findByUser_mapsNullFriendsSince_whenAbsent() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of(Map.of(
                        "friendId", AttributeValue.fromS("user2"),
                        "status", AttributeValue.fromS("INVITATION_RECEIVED")
                ))).build());

        List<Friend> friends = dao.findByUser("u1", FriendStatus.INVITATION_RECEIVED);

        assertNull(friends.get(0).getFriendsSince());
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_putsBothDirectionsWithCorrectStatuses() {
        dao.create("user1", "user2");

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client, times(2)).putItem(captor.capture());
        List<PutItemRequest> puts = captor.getAllValues();

        // fromUser's record: INVITATION_SENT
        Map<String, AttributeValue> fromItem = puts.get(0).item();
        assertEquals("user1", fromItem.get("userId").s());
        assertEquals("user2", fromItem.get("friendId").s());
        assertEquals("INVITATION_SENT", fromItem.get("status").s());

        // toUser's record: INVITATION_RECEIVED
        Map<String, AttributeValue> toItem = puts.get(1).item();
        assertEquals("user2", toItem.get("userId").s());
        assertEquals("user1", toItem.get("friendId").s());
        assertEquals("INVITATION_RECEIVED", toItem.get("status").s());
    }

    @Test
    void create_usesFriendsTable() {
        dao.create("user1", "user2");

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client, times(2)).putItem(captor.capture());
        captor.getAllValues().forEach(req -> assertEquals(FRIENDS_TABLE, req.tableName()));
    }

    @Test
    void create_doesNotCallGetItem() {
        dao.create("user1", "user2");
        // no user table lookups
        verify(client, times(0)).getItem(any(GetItemRequest.class));
    }

    // ── updateStatus ──────────────────────────────────────────────────────────

    @Test
    void updateStatus_setsStatusOnly_forNonAccepted() {
        dao.updateStatus("user1", "user2", FriendStatus.INVITATION_SENT);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals("INVITATION_SENT", req.expressionAttributeValues().get(":status").s());
        assertFalse(req.expressionAttributeValues().containsKey(":friendsSince"));
    }

    @Test
    void updateStatus_setsFriendsSince_whenAccepted() {
        dao.updateStatus("user1", "user2", FriendStatus.ACCEPTED);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals("ACCEPTED", req.expressionAttributeValues().get(":status").s());
        assertTrue(req.expressionAttributeValues().containsKey(":friendsSince"));
        assertTrue(req.updateExpression().contains(":friendsSince"));
    }

    @Test
    void updateStatus_sendsCorrectKey() {
        dao.updateStatus("user1", "user2", FriendStatus.ACCEPTED);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals(FRIENDS_TABLE, req.tableName());
        assertEquals("user1", req.key().get("userId").s());
        assertEquals("user2", req.key().get("friendId").s());
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_removesBothDirections() {
        dao.delete("user1", "user2");

        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(client, times(2)).deleteItem(captor.capture());
        List<DeleteItemRequest> deletes = captor.getAllValues();

        assertEquals("user1", deletes.get(0).key().get("userId").s());
        assertEquals("user2", deletes.get(0).key().get("friendId").s());

        assertEquals("user2", deletes.get(1).key().get("userId").s());
        assertEquals("user1", deletes.get(1).key().get("friendId").s());
    }

    @Test
    void delete_usesFriendsTable() {
        dao.delete("user1", "user2");

        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(client, times(2)).deleteItem(captor.capture());
        captor.getAllValues().forEach(req -> assertEquals(FRIENDS_TABLE, req.tableName()));
    }
}
