package logic;

import com.google.gson.JsonSyntaxException;
import logic.data.Data;
import logic.data.InvalidGameDataException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Datenverarbeitungstests.
 *
 * @author Suwendi Suriadi (WInf104177)
 */
public class DataTest {

    @Test (expected = InvalidGameDataException.class)
    public void invalidCurrentPlayer() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/invalidCurrentPlayer.json");
        Data data = new Data(new File(path), 7, 7);
    }

    @Test (expected = InvalidGameDataException.class)
    public void invalidField() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/invalidField.json");
        Data data = new Data(new File(path), 7, 7);
    }


    @Test (expected = InvalidGameDataException.class)
    public void duplicateTreasureOnField() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/duplicateTreasure.json");
        Data data = new Data(new File(path), 7, 7);
    }


    @Test (expected = InvalidGameDataException.class)
    public void invalidFreeWayCard_null() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/nullFreeWayCard.json");
        Data data = new Data(new File(path), 7, 7);
    }

    @Test (expected = JsonSyntaxException.class)
    public void invalidJsonSyntax() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/invalidJsonSyntax.json");
        Data data = new Data(new File(path), 7, 7);
    }

    @Test (expected = NullPointerException.class)
    public void invalidTreasureAtPlayerStack() throws FileNotFoundException, InvalidGameDataException {
        String path = new File("").getAbsolutePath();
        path = path.concat("/test/logic/dataTestFiles/invalidTreasure.json");
        Data data = new Data(new File(path), 7, 7);
    }
}
