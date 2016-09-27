package ninja.mbedded.ninjaterm.util.ansiEscapeCodes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ninja.mbedded.ninjaterm.JavaFXThreadingRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Processor class.
 *
 * @author          Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @since           2016-09-26
 * @last-modified   2016-09-26
 */
public class ParseStringTests {

    /**
     * Including this variable in class allows JavaFX objects to be created in tests.
     */
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void oneSeqTest() throws Exception {
        Processor processor = new Processor();

        ObservableList<Node> observableList = FXCollections.observableArrayList();

        Text text = new Text();
        text.setFill(Color.rgb(0, 0, 0));
        observableList.add(text);

        processor.parseString(observableList, "default\u001B[31mred");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(Color.rgb(0, 0, 0), ((Text)observableList.get(0)).getFill());

        assertEquals("red", ((Text)observableList.get(1)).getText());
        assertEquals(Color.rgb(170, 0, 0), ((Text)observableList.get(1)).getFill());
    }

    @Test
    public void twoSeqTest() throws Exception {
        Processor processor = new Processor();

        ObservableList<Node> observableList = FXCollections.observableArrayList();

        Text text = new Text();
        text.setFill(Color.rgb(0, 0, 0));
        observableList.add(text);

        processor.parseString(observableList, "default\u001B[31mred\u001B[32mgreen");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(Color.rgb(0, 0, 0), ((Text)observableList.get(0)).getFill());

        assertEquals("red", ((Text)observableList.get(1)).getText());
        assertEquals(Color.rgb(170, 0, 0), ((Text)observableList.get(1)).getFill());

        assertEquals("green", ((Text)observableList.get(2)).getText());
        assertEquals(Color.rgb(0, 170, 0), ((Text)observableList.get(2)).getFill());
    }

    @Test
    public void boldRedColourTest() throws Exception {
        Processor processor = new Processor();

        ObservableList<Node> observableList = FXCollections.observableArrayList();

        Text text = new Text();
        text.setFill(Color.rgb(0, 0, 0));
        observableList.add(text);

        processor.parseString(observableList, "default\u001B[31;1mred");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(Color.rgb(0, 0, 0), ((Text)observableList.get(0)).getFill());

        assertEquals("red", ((Text)observableList.get(1)).getText());
        assertEquals(Color.rgb(255, 85, 85), ((Text)observableList.get(1)).getFill());
    }

    @Test
    public void partialSeqTest() throws Exception {
        Processor processor = new Processor();

        ObservableList<Node> observableList = FXCollections.observableArrayList();

        Text text = new Text();
        text.setFill(Color.rgb(0, 0, 0));
        observableList.add(text);

        processor.parseString(observableList, "default\u001B");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(Color.rgb(0, 0, 0), ((Text)observableList.get(0)).getFill());

        processor.parseString(observableList, "[31mred");

        assertEquals("red", ((Text)observableList.get(1)).getText());
        assertEquals(Color.rgb(170, 0, 0), ((Text)observableList.get(1)).getFill());
    }

    @Test
    public void partialSeqTest2() throws Exception {
        Processor processor = new Processor();

        ObservableList<Node> observableList = FXCollections.observableArrayList();

        Text text = new Text();
        text.setFill(Color.rgb(0, 0, 0));
        observableList.add(text);

        processor.parseString(observableList, "default\u001B");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(Color.rgb(0, 0, 0), ((Text)observableList.get(0)).getFill());

        processor.parseString(observableList, "[");

        assertEquals("default", ((Text)observableList.get(0)).getText());
        assertEquals(1, observableList.size());

        processor.parseString(observableList, "31mred");

        assertEquals(2, observableList.size());
        assertEquals("red", ((Text)observableList.get(1)).getText());
        assertEquals(Color.rgb(170, 0, 0), ((Text)observableList.get(1)).getFill());
    }
}