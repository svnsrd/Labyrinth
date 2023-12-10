package logic;

import logic.path.PathNode;
import logic.player.AIMove;
import logic.player.Player;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.tile.TileRotation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Eine FakeGUI zur Nutzung für Tests.
 *
 * @author Suwendi Suriadi (WInf104177) [Repository 37]
 * @version 08.08.2021
 */
public class FakeGUI implements GUIConnector {
    @Override
    public void createTreasureImg() {

    }

    @Override
    public void setFreeWayCard(Tile tile) {

    }

    @Override
    public void showAlert(String message) {

    }

    @Override
    public void showErrorAlert(String message) {

    }

    @Override
    public void movePlayer(int player, List<Position> positions, Treasure collected, Treasure nextTreasure, Game game) {

    }

    @Override
    public void showWinner(String playername) {

    }

    @Override
    public void animateHumanShift(Shift shift, PathNode[][] possiblePositions) {

    }

    @Override
    public void disableField(boolean value) {

    }

    @Override
    public void resetPlayerInfo() {

    }

    @Override
    public void createPlayer(int playerIdx, Player player) {

    }

    @Override
    public void rotateFreeWayCard(TileRotation rotation, RotateDirection rotDir) {

    }

    @Override
    public void setPossiblePos(PathNode[][] nodes) {

    }

    @Override
    public void displayInsetArrow(int col, int row, Direction direction) {

    }

    @Override
    public void showPlayerWinHint(int playerIdx) {

    }

    @Override
    public void aiMove(Shift shift, AIMove aiMove, int playerIdx, Treasure collected, Treasure nextTreasure, List<Position> path, Game game) {

    }

    @Override
    public void initializeField(Tile[][] board, Map<Position, Direction> insetPositions, FreeWayCard freeWayCard) {

    }

    @Override
    public void highlightTreasureToFind(Position posToHighlight, int playerIdx) {

    }

    @Override
    public void nextPlayer(int playerIdxBefore, int playerIdx) {

    }

    @Override
    public void loadField(Tile[][] board, Map<Position, Direction> insetPositions, FreeWayCard freeWayCard) {

    }
}
