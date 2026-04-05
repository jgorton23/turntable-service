package com.turntable.integ;

import com.turntable.client.user.User;
import com.turntable.ddb.DynamoUserDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DynamoUserDaoIntegTest extends DynamoDbIntegBase {

    private static final String TABLE = "integ-users";
    private static DynamoUserDao dao;

    @BeforeAll
    static void setUpTable() {
        createTable(TABLE, "userId");
        dao = new DynamoUserDao(CLIENT, TABLE);
    }

    @Test
    void createAndFindById_roundTrip_allFields() {
        User user = User.builder()
                .userId("integ-u1").username("alice")
                .email("alice@example.com").avatar("http://img/alice")
                .build();

        dao.create(user);
        User found = dao.findById("integ-u1");

        assertEquals(user, found);
    }

    @Test
    void createAndFindById_roundTrip_nullOptionalFields() {
        User user = User.builder().userId("integ-u2").username("bob").build();

        dao.create(user);
        User found = dao.findById("integ-u2");

        assertEquals("integ-u2", found.getUserId());
        assertEquals("bob", found.getUsername());
        assertNull(found.getEmail());
        assertNull(found.getAvatar());
    }

    @Test
    void findById_returnsNull_whenUserDoesNotExist() {
        assertNull(dao.findById("integ-does-not-exist"));
    }

    @Test
    void update_overwritesFields() {
        User original = User.builder()
                .userId("integ-u3").username("charlie")
                .email("charlie@example.com").avatar("http://img/charlie")
                .build();
        dao.create(original);

        User updated = User.builder()
                .userId("integ-u3").username("charlie-updated").build();
        dao.update("integ-u3", updated);

        User found = dao.findById("integ-u3");
        assertEquals("charlie-updated", found.getUsername());
        assertNull(found.getEmail());
        assertNull(found.getAvatar());
    }

    @Test
    void delete_removesUser() {
        User user = User.builder().userId("integ-u4").username("dana").build();
        dao.create(user);

        dao.delete("integ-u4");

        assertNull(dao.findById("integ-u4"));
    }
}
