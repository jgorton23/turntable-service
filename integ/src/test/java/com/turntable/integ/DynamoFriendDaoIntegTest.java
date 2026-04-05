package com.turntable.integ;

import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import com.turntable.ddb.DynamoFriendDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamoFriendDaoIntegTest extends DynamoDbIntegBase {

    private static final String FRIENDS_TABLE = "integ-friends";
    private static DynamoFriendDao dao;

    @BeforeAll
    static void setUpTable() {
        createTable(FRIENDS_TABLE, "userId", "friendId");
        dao = new DynamoFriendDao(CLIENT, FRIENDS_TABLE);
    }

    @Test
    void create_storesBothDirections() {
        dao.create("friend-u1", "friend-u2");

        List<Friend> u1Sees = dao.findByUser("friend-u1", FriendStatus.INVITATION_SENT);
        List<Friend> u2Sees = dao.findByUser("friend-u2", FriendStatus.INVITATION_RECEIVED);

        assertEquals(1, u1Sees.size());
        assertEquals("friend-u2", u1Sees.get(0).getFriendId());
        assertEquals(FriendStatus.INVITATION_SENT, u1Sees.get(0).getStatus());

        assertEquals(1, u2Sees.size());
        assertEquals("friend-u1", u2Sees.get(0).getFriendId());
        assertEquals(FriendStatus.INVITATION_RECEIVED, u2Sees.get(0).getStatus());
    }

    @Test
    void updateStatus_toAccepted_setsFriendsSince() {
        dao.create("friend-u3", "friend-u4");

        dao.updateStatus("friend-u3", "friend-u4", FriendStatus.ACCEPTED);

        List<Friend> friends = dao.findByUser("friend-u3", FriendStatus.ACCEPTED);
        assertEquals(1, friends.size());
        assertNotNull(friends.get(0).getFriendsSince());
    }

    @Test
    void delete_removesBothDirections() {
        dao.create("friend-u5", "friend-u6");

        dao.delete("friend-u5", "friend-u6");

        assertTrue(dao.findByUser("friend-u5", FriendStatus.INVITATION_SENT).isEmpty());
        assertTrue(dao.findByUser("friend-u6", FriendStatus.INVITATION_RECEIVED).isEmpty());
    }

    @Test
    void findByUser_returnsEmptyList_whenNoFriends() {
        assertTrue(dao.findByUser("friend-u7", FriendStatus.ACCEPTED).isEmpty());
    }
}
