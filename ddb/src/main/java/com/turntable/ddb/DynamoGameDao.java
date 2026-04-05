package com.turntable.ddb;

import com.turntable.client.game.Game;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.IGameDao;
import com.turntable.client.game.Move;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

/** DynamoDB implementation of {@link IGameDao}. */
@RequiredArgsConstructor
public class DynamoGameDao implements IGameDao {

    private final DynamoDbClient dynamoDbClient;

    @Override
    public Game create(Game game) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Game findById(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<Game> findByPlayer(String playerId, GameStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'findByPlayer'");
    }

    @Override
    public void update(String gameId, Game game) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public List<Move> findMoves(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'findMoves'");
    }

    @Override
    public void createMove(String gameId, Move move) {
        throw new UnsupportedOperationException("Unimplemented method 'createMove'");
    }
}
