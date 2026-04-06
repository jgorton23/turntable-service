package com.turntable.ddb;

import com.turntable.client.game.Game;
import com.turntable.client.game.GameResult;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.IGameDao;
import com.turntable.client.game.Move;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DynamoDB implementation of {@link IGameDao}.
 *
 * <p>Table schemas:
 * <ul>
 *   <li><b>Games</b>: PK {@code gameId}. Stores all game state.</li>
 *   <li><b>PlayerGames</b>: PK {@code playerId}, SK {@code gameId}. Enables lookup of games by
 *       player. Denormalizes {@code status} for efficient filtering.</li>
 *   <li><b>Moves</b>: PK {@code gameId}, SK {@code sk} ({@code moveTimestamp#userId}).
 *       Items are returned in chronological order by the sort key.</li>
 * </ul>
 */
@RequiredArgsConstructor
public class DynamoGameDao implements IGameDao {

    private static final String GAME_ID = "gameId";
    private static final String STATUS = "status";
    private static final String CREATED_TIMESTAMP = "createdTimestamp";
    private static final String PLAYERS = "players";
    private static final String CURRENT_PLAYER = "currentPlayer";
    private static final String RESULT = "result";
    private static final String WINNER_ID = "winnerId";
    private static final String PLAYER_ID = "playerId";
    private static final String USER_ID = "userId";
    private static final String MOVE = "move";
    private static final String MOVE_TIMESTAMP = "moveTimestamp";
    private static final String SORT_KEY = "sk";

    private final DynamoDbClient dynamoDbClient;
    private final String gamesTableName;
    private final String playerGamesTableName;
    private final String movesTableName;

    @Override
    public Game create(Game game) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(gamesTableName)
                .item(gameToItem(game))
                .build());

        for (String playerId : game.getPlayers()) {
            putPlayerGameItem(playerId, game.getId(), game.getStatus());
        }

        return game;
    }

    @Override
    public Game findById(String gameId) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(gamesTableName)
                .key(Map.of(GAME_ID, AttributeValue.fromS(gameId)))
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return null;
        }

        return itemToGame(response.item());
    }

    @Override
    public List<Game> findByPlayer(String playerId, GameStatus status) {
        Map<String, String> nameMap = new HashMap<>();
        Map<String, AttributeValue> valueMap = new HashMap<>();

        nameMap.put("#pid", PLAYER_ID);
        valueMap.put(":playerId", AttributeValue.fromS(playerId));

        QueryRequest.Builder queryBuilder = QueryRequest.builder()
                .tableName(playerGamesTableName)
                .keyConditionExpression("#pid = :playerId");

        if (status != null) {
            nameMap.put("#s", STATUS);
            valueMap.put(":status", AttributeValue.fromS(status.name()));
            queryBuilder.filterExpression("#s = :status");
        }

        queryBuilder.expressionAttributeNames(nameMap).expressionAttributeValues(valueMap);

        QueryResponse queryResponse = dynamoDbClient.query(queryBuilder.build());

        return queryResponse.items().stream()
                .map(item -> item.get(GAME_ID).s())
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void update(String gameId, Game game) {
        StringBuilder setExpr = new StringBuilder(
                "SET #s = :status, #pl = :players, #ct = :createdTs");
        List<String> removeAttrs = new ArrayList<>();
        Map<String, String> nameMap = new HashMap<>();
        Map<String, AttributeValue> valueMap = new HashMap<>();

        nameMap.put("#s", STATUS);
        nameMap.put("#pl", PLAYERS);
        nameMap.put("#ct", CREATED_TIMESTAMP);
        valueMap.put(":status", AttributeValue.fromS(game.getStatus().name()));
        valueMap.put(":players", AttributeValue.fromL(game.getPlayers().stream()
                .map(AttributeValue::fromS)
                .collect(Collectors.toList())));
        valueMap.put(":createdTs", AttributeValue.fromS(game.getCreatedTimestamp().toString()));

        if (game.getCurrentPlayer() != null) {
            setExpr.append(", #cp = :currentPlayer");
            nameMap.put("#cp", CURRENT_PLAYER);
            valueMap.put(":currentPlayer", AttributeValue.fromS(game.getCurrentPlayer()));
        } else {
            nameMap.put("#cp", CURRENT_PLAYER);
            removeAttrs.add("#cp");
        }

        if (game.getResult() != null) {
            Map<String, AttributeValue> resultMap = new HashMap<>();
            if (game.getResult().getWinnerId() != null) {
                resultMap.put(WINNER_ID, AttributeValue.fromS(game.getResult().getWinnerId()));
            }
            setExpr.append(", #r = :result");
            nameMap.put("#r", RESULT);
            valueMap.put(":result", AttributeValue.fromM(resultMap));
        } else {
            nameMap.put("#r", RESULT);
            removeAttrs.add("#r");
        }

        String updateExpr = setExpr.toString();
        if (!removeAttrs.isEmpty()) {
            updateExpr += " REMOVE " + String.join(", ", removeAttrs);
        }

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(gamesTableName)
                .key(Map.of(GAME_ID, AttributeValue.fromS(gameId)))
                .updateExpression(updateExpr)
                .expressionAttributeNames(nameMap)
                .expressionAttributeValues(valueMap)
                .build());

        for (String playerId : game.getPlayers()) {
            putPlayerGameItem(playerId, gameId, game.getStatus());
        }
    }

    @Override
    public List<Move> findMoves(String gameId) {
        QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                .tableName(movesTableName)
                .keyConditionExpression("#gid = :gameId")
                .expressionAttributeNames(Map.of("#gid", GAME_ID))
                .expressionAttributeValues(Map.of(":gameId", AttributeValue.fromS(gameId)))
                .build());

        return response.items().stream()
                .map(this::itemToMove)
                .collect(Collectors.toList());
    }

    @Override
    public void createMove(String gameId, Move move) {
        // Sort key is timestamp#userId to ensure uniqueness while preserving chronological order
        String sk = move.getMoveTimestamp().toString() + "#" + move.getUserId();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(GAME_ID, AttributeValue.fromS(gameId));
        item.put(SORT_KEY, AttributeValue.fromS(sk));
        item.put(USER_ID, AttributeValue.fromS(move.getUserId()));
        item.put(MOVE, AttributeValue.fromS(move.getMove()));
        item.put(MOVE_TIMESTAMP, AttributeValue.fromS(move.getMoveTimestamp().toString()));

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(movesTableName)
                .item(item)
                .build());
    }

    private void putPlayerGameItem(String playerId, String gameId, GameStatus status) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(playerGamesTableName)
                .item(Map.of(
                        PLAYER_ID, AttributeValue.fromS(playerId),
                        GAME_ID, AttributeValue.fromS(gameId),
                        STATUS, AttributeValue.fromS(status.name())
                ))
                .build());
    }

    private Map<String, AttributeValue> gameToItem(Game game) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(GAME_ID, AttributeValue.fromS(game.getId()));
        item.put(STATUS, AttributeValue.fromS(game.getStatus().name()));
        item.put(CREATED_TIMESTAMP, AttributeValue.fromS(game.getCreatedTimestamp().toString()));
        item.put(PLAYERS, AttributeValue.fromL(game.getPlayers().stream()
                .map(AttributeValue::fromS)
                .collect(Collectors.toList())));

        if (game.getCurrentPlayer() != null) {
            item.put(CURRENT_PLAYER, AttributeValue.fromS(game.getCurrentPlayer()));
        }

        if (game.getResult() != null) {
            Map<String, AttributeValue> resultMap = new HashMap<>();
            if (game.getResult().getWinnerId() != null) {
                resultMap.put(WINNER_ID, AttributeValue.fromS(game.getResult().getWinnerId()));
            }
            item.put(RESULT, AttributeValue.fromM(resultMap));
        }

        return item;
    }

    private Game itemToGame(Map<String, AttributeValue> item) {
        GameResult result = null;
        if (item.containsKey(RESULT)) {
            Map<String, AttributeValue> resultMap = item.get(RESULT).m();
            String winnerId = resultMap.containsKey(WINNER_ID) ? resultMap.get(WINNER_ID).s() : null;
            result = GameResult.builder().winnerId(winnerId).build();
        }

        return Game.builder()
                .id(item.get(GAME_ID).s())
                .status(GameStatus.valueOf(item.get(STATUS).s()))
                .createdTimestamp(Instant.parse(item.get(CREATED_TIMESTAMP).s()))
                .players(item.get(PLAYERS).l().stream()
                        .map(AttributeValue::s)
                        .collect(Collectors.toList()))
                .currentPlayer(item.containsKey(CURRENT_PLAYER) ? item.get(CURRENT_PLAYER).s() : null)
                .result(result)
                .build();
    }

    private Move itemToMove(Map<String, AttributeValue> item) {
        return Move.builder()
                .gameId(item.get(GAME_ID).s())
                .userId(item.get(USER_ID).s())
                .move(item.get(MOVE).s())
                .moveTimestamp(Instant.parse(item.get(MOVE_TIMESTAMP).s()))
                .build();
    }
}
