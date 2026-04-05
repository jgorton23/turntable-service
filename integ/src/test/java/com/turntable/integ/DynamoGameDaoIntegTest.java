package com.turntable.integ;

import com.turntable.client.game.Game;
import com.turntable.client.game.GameResult;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.Move;
import com.turntable.ddb.DynamoGameDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamoGameDaoIntegTest extends DynamoDbIntegBase {

    private static final String GAMES_TABLE = "integ-games";
    private static final String PLAYER_GAMES_TABLE = "integ-player-games";
    private static final String MOVES_TABLE = "integ-moves";
    private static DynamoGameDao dao;

    @BeforeAll
    static void setUpTables() {
        createTable(GAMES_TABLE, "gameId");
        createTable(PLAYER_GAMES_TABLE, "playerId", "gameId");
        createTable(MOVES_TABLE, "gameId", "sk");
        dao = new DynamoGameDao(CLIENT, GAMES_TABLE, PLAYER_GAMES_TABLE, MOVES_TABLE);
    }

    private Game pendingGame(String gameId, String... playerIds) {
        return Game.builder()
                .id(gameId)
                .status(GameStatus.PENDING)
                .createdTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
                .players(List.of(playerIds))
                .build();
    }

    @Test
    void createAndFindById_roundTrip() {
        Game game = pendingGame("integ-g1", "p1", "p2");
        dao.create(game);

        Game found = dao.findById("integ-g1");

        assertNotNull(found);
        assertEquals("integ-g1", found.getId());
        assertEquals(GameStatus.PENDING, found.getStatus());
        assertEquals(List.of("p1", "p2"), found.getPlayers());
        assertNull(found.getCurrentPlayer());
        assertNull(found.getResult());
    }

    @Test
    void findById_returnsNull_whenNotFound() {
        assertNull(dao.findById("integ-does-not-exist"));
    }

    @Test
    void findByPlayer_returnsGamesForPlayer() {
        dao.create(pendingGame("integ-g2", "integ-p10", "integ-p11"));
        dao.create(pendingGame("integ-g3", "integ-p10", "integ-p12"));

        List<Game> games = dao.findByPlayer("integ-p10", null);

        assertTrue(games.stream().anyMatch(g -> g.getId().equals("integ-g2")));
        assertTrue(games.stream().anyMatch(g -> g.getId().equals("integ-g3")));
    }

    @Test
    void findByPlayer_withStatusFilter_returnsOnlyMatchingGames() {
        Game pending = pendingGame("integ-g4", "integ-p20");
        dao.create(pending);

        Game started = Game.builder()
                .id("integ-g5").status(GameStatus.STARTED)
                .createdTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
                .players(List.of("integ-p20"))
                .currentPlayer("integ-p20")
                .build();
        dao.create(started);

        List<Game> pendingGames = dao.findByPlayer("integ-p20", GameStatus.PENDING);
        List<Game> startedGames = dao.findByPlayer("integ-p20", GameStatus.STARTED);

        assertTrue(pendingGames.stream().allMatch(g -> g.getStatus() == GameStatus.PENDING));
        assertTrue(startedGames.stream().allMatch(g -> g.getStatus() == GameStatus.STARTED));
    }

    @Test
    void update_changesGameStatus() {
        dao.create(pendingGame("integ-g6", "p1", "p2"));

        Game updated = Game.builder()
                .id("integ-g6").status(GameStatus.STARTED)
                .createdTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
                .players(List.of("p1", "p2"))
                .currentPlayer("p1")
                .build();
        dao.update("integ-g6", updated);

        Game found = dao.findById("integ-g6");
        assertEquals(GameStatus.STARTED, found.getStatus());
        assertEquals("p1", found.getCurrentPlayer());
    }

    @Test
    void update_setsResult_onGameEnd() {
        dao.create(pendingGame("integ-g7", "p1", "p2"));

        Game ended = Game.builder()
                .id("integ-g7").status(GameStatus.ENDED)
                .createdTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
                .players(List.of("p1", "p2"))
                .result(GameResult.builder().winnerId("p1").build())
                .build();
        dao.update("integ-g7", ended);

        Game found = dao.findById("integ-g7");
        assertNotNull(found.getResult());
        assertEquals("p1", found.getResult().getWinnerId());
    }

    @Test
    void createAndFindMoves_roundTrip() {
        dao.create(pendingGame("integ-g8", "p1", "p2"));

        Instant ts1 = Instant.parse("2025-06-01T10:00:00Z");
        Instant ts2 = Instant.parse("2025-06-01T10:01:00Z");
        Move move1 = Move.builder().gameId("integ-g8").userId("p1").move("a4").moveTimestamp(ts1).build();
        Move move2 = Move.builder().gameId("integ-g8").userId("p2").move("e5").moveTimestamp(ts2).build();

        dao.createMove("integ-g8", move1);
        dao.createMove("integ-g8", move2);

        List<Move> moves = dao.findMoves("integ-g8");

        assertEquals(2, moves.size());
        // DynamoDB returns moves sorted by sk (timestamp#userId)
        assertEquals("a4", moves.get(0).getMove());
        assertEquals("e5", moves.get(1).getMove());
    }
}
