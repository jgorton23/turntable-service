package com.turntable.ddb;

import com.turntable.client.game.Game;
import com.turntable.client.game.GameResult;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoGameDaoTest {

    private static final String GAMES_TABLE = "games";
    private static final String PLAYER_GAMES_TABLE = "playerGames";
    private static final String MOVES_TABLE = "moves";

    private DynamoDbClient client;
    private DynamoGameDao dao;

    @BeforeEach
    void setUp() {
        client = mock(DynamoDbClient.class);
        dao = new DynamoGameDao(client, GAMES_TABLE, PLAYER_GAMES_TABLE, MOVES_TABLE);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_putsGameItemAndOnePlayerGameItemPerPlayer() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.PENDING)
                .createdTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
                .players(List.of("p1", "p2"))
                .build();

        dao.create(game);

        // 1 game put + 2 playerGame puts = 3 total
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client, times(3)).putItem(captor.capture());

        PutItemRequest gamesPut = captor.getAllValues().get(0);
        assertEquals(GAMES_TABLE, gamesPut.tableName());
        assertEquals("g1", gamesPut.item().get("gameId").s());
        assertEquals("PENDING", gamesPut.item().get("status").s());

        PutItemRequest p1Put = captor.getAllValues().get(1);
        assertEquals(PLAYER_GAMES_TABLE, p1Put.tableName());
        assertEquals("p1", p1Put.item().get("playerId").s());
        assertEquals("g1", p1Put.item().get("gameId").s());
    }

    @Test
    void create_returnsInputGame() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.PENDING)
                .createdTimestamp(Instant.now())
                .players(List.of("p1"))
                .build();

        assertEquals(game, dao.create(game));
    }

    @Test
    void create_includesCurrentPlayer_whenPresent() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.STARTED)
                .createdTimestamp(Instant.now())
                .players(List.of("p1", "p2"))
                .currentPlayer("p1")
                .build();

        dao.create(game);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client, times(3)).putItem(captor.capture());
        assertEquals("p1", captor.getAllValues().get(0).item().get("currentPlayer").s());
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_mapsAllFields() {
        Instant ts = Instant.parse("2025-01-01T00:00:00Z");
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().item(Map.of(
                        "gameId", AttributeValue.fromS("g1"),
                        "status", AttributeValue.fromS("STARTED"),
                        "createdTimestamp", AttributeValue.fromS(ts.toString()),
                        "players", AttributeValue.fromL(List.of(
                                AttributeValue.fromS("p1"), AttributeValue.fromS("p2"))),
                        "currentPlayer", AttributeValue.fromS("p1")
                )).build());

        Game game = dao.findById("g1");

        assertEquals("g1", game.getId());
        assertEquals(GameStatus.STARTED, game.getStatus());
        assertEquals(ts, game.getCreatedTimestamp());
        assertEquals(List.of("p1", "p2"), game.getPlayers());
        assertEquals("p1", game.getCurrentPlayer());
        assertNull(game.getResult());
    }

    @Test
    void findById_mapsResult_withWinner() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().item(Map.of(
                        "gameId", AttributeValue.fromS("g1"),
                        "status", AttributeValue.fromS("ENDED"),
                        "createdTimestamp", AttributeValue.fromS(Instant.now().toString()),
                        "players", AttributeValue.fromL(List.of(AttributeValue.fromS("p1"))),
                        "result", AttributeValue.fromM(Map.of(
                                "winnerId", AttributeValue.fromS("p1")
                        ))
                )).build());

        Game game = dao.findById("g1");

        assertNotNull(game.getResult());
        assertEquals("p1", game.getResult().getWinnerId());
    }

    @Test
    void findById_returnsNull_whenNotFound() {
        when(client.getItem(any(GetItemRequest.class))).thenReturn(
                GetItemResponse.builder().build());

        assertNull(dao.findById("g1"));
    }

    // ── findByPlayer ──────────────────────────────────────────────────────────

    @Test
    void findByPlayer_withStatusFilter_addsFilterExpression() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        dao.findByPlayer("p1", GameStatus.STARTED);

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());
        QueryRequest req = captor.getValue();

        assertEquals(PLAYER_GAMES_TABLE, req.tableName());
        assertEquals("p1", req.expressionAttributeValues().get(":playerId").s());
        assertNotNull(req.filterExpression());
        assertEquals("STARTED", req.expressionAttributeValues().get(":status").s());
    }

    @Test
    void findByPlayer_withNullStatus_omitsFilterExpression() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        dao.findByPlayer("p1", null);

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());
        assertNull(captor.getValue().filterExpression());
    }

    @Test
    void findByPlayer_looksupsEachGameById() {
        // Query returns two gameIds
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of(
                        Map.of("gameId", AttributeValue.fromS("g1"),
                                "playerId", AttributeValue.fromS("p1"),
                                "status", AttributeValue.fromS("PENDING")),
                        Map.of("gameId", AttributeValue.fromS("g2"),
                                "playerId", AttributeValue.fromS("p1"),
                                "status", AttributeValue.fromS("PENDING"))
                )).build());

        Instant ts = Instant.parse("2025-01-01T00:00:00Z");
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(Map.of(
                        "gameId", AttributeValue.fromS("g1"),
                        "status", AttributeValue.fromS("PENDING"),
                        "createdTimestamp", AttributeValue.fromS(ts.toString()),
                        "players", AttributeValue.fromL(List.of(AttributeValue.fromS("p1")))
                )).build())
                .thenReturn(GetItemResponse.builder().item(Map.of(
                        "gameId", AttributeValue.fromS("g2"),
                        "status", AttributeValue.fromS("PENDING"),
                        "createdTimestamp", AttributeValue.fromS(ts.toString()),
                        "players", AttributeValue.fromL(List.of(AttributeValue.fromS("p1")))
                )).build());

        List<Game> games = dao.findByPlayer("p1", null);

        assertEquals(2, games.size());
        verify(client, times(2)).getItem(any(GetItemRequest.class));
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void update_setsStatusPlayersAndTimestamp() {
        Instant ts = Instant.parse("2025-01-01T00:00:00Z");
        Game game = Game.builder()
                .id("g1").status(GameStatus.STARTED)
                .createdTimestamp(ts)
                .players(List.of("p1", "p2"))
                .currentPlayer("p1")
                .build();

        dao.update("g1", game);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        UpdateItemRequest req = captor.getValue();

        assertEquals(GAMES_TABLE, req.tableName());
        assertEquals("g1", req.key().get("gameId").s());
        assertEquals("STARTED", req.expressionAttributeValues().get(":status").s());
        assertEquals(ts.toString(), req.expressionAttributeValues().get(":createdTs").s());
    }

    @Test
    void update_removesCurrentPlayerAndResult_whenNull() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.ENDED)
                .createdTimestamp(Instant.now())
                .players(List.of("p1"))
                .build();

        dao.update("g1", game);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        String expr = captor.getValue().updateExpression();

        assertTrue(expr.contains("REMOVE"));
        assertTrue(expr.contains("currentPlayer"));
        assertTrue(expr.contains("result"));
    }

    @Test
    void update_setsResult_whenPresent() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.ENDED)
                .createdTimestamp(Instant.now())
                .players(List.of("p1"))
                .result(GameResult.builder().winnerId("p1").build())
                .build();

        dao.update("g1", game);

        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(client).updateItem(captor.capture());
        assertTrue(captor.getValue().expressionAttributeValues().containsKey(":result"));
    }

    @Test
    void update_putsPlayerGameItemForEachPlayer() {
        Game game = Game.builder()
                .id("g1").status(GameStatus.STARTED)
                .createdTimestamp(Instant.now())
                .players(List.of("p1", "p2"))
                .build();

        dao.update("g1", game);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client, times(2)).putItem(captor.capture());
        captor.getAllValues().forEach(req -> assertEquals(PLAYER_GAMES_TABLE, req.tableName()));
    }

    // ── findMoves ─────────────────────────────────────────────────────────────

    @Test
    void findMoves_queriesMovesTableByGameId() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        dao.findMoves("g1");

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());
        assertEquals(MOVES_TABLE, captor.getValue().tableName());
        assertEquals("g1", captor.getValue().expressionAttributeValues().get(":gameId").s());
    }

    @Test
    void findMoves_returnsMappedMoves() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of(Map.of(
                        "gameId", AttributeValue.fromS("g1"),
                        "userId", AttributeValue.fromS("p1"),
                        "move", AttributeValue.fromS("a4"),
                        "moveTimestamp", AttributeValue.fromS(ts.toString())
                ))).build());

        List<Move> moves = dao.findMoves("g1");

        assertEquals(1, moves.size());
        Move m = moves.get(0);
        assertEquals("g1", m.getGameId());
        assertEquals("p1", m.getUserId());
        assertEquals("a4", m.getMove());
        assertEquals(ts, m.getMoveTimestamp());
    }

    // ── createMove ────────────────────────────────────────────────────────────

    @Test
    void createMove_putsItemWithTimestampUserIdSortKey() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        Move move = Move.builder()
                .gameId("g1").userId("p1").move("a4").moveTimestamp(ts)
                .build();

        dao.createMove("g1", move);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        Map<String, AttributeValue> item = captor.getValue().item();

        assertEquals(MOVES_TABLE, captor.getValue().tableName());
        assertEquals("g1", item.get("gameId").s());
        assertEquals("p1", item.get("userId").s());
        assertEquals("a4", item.get("move").s());
        // sort key is timestamp#userId
        assertEquals(ts + "#p1", item.get("sk").s());
    }
}
