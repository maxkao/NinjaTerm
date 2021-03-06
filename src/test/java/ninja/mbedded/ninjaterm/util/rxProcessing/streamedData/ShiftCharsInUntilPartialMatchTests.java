package ninja.mbedded.ninjaterm.util.rxProcessing.streamedData;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the <code>shiftCharsInUntilAPartialMatch()</code> method of <code>StreamedData</code> class.
 *
 * @author          Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @since           2016-10-15
 * @last-modified   2016-10-15
 */
public class ShiftCharsInUntilPartialMatchTests {

    private StreamedData input;
    private StreamedData output;

    private Pattern pattern;

    @Before
    public void setUp() throws Exception {
        input = new StreamedData();
        output = new StreamedData();

        pattern = Pattern.compile("EOL");
    }

    @Test
    public void basicTest() throws Exception {

        input.append("123EO");

        pattern = Pattern.compile("EOL");
        output.shiftCharsInUntilPartialMatch(input, pattern);

        assertEquals("EO", input.getText());
        assertEquals(0, input.getColourMarkers().size());

        assertEquals("123", output.getText());
        assertEquals(0, output.getColourMarkers().size());
    }

    @Test
    public void matchComesOnSecondCallTest() throws Exception {

        input.append("123EO");

        output.shiftCharsInUntilPartialMatch(input, pattern);

        assertEquals("EO", input.getText());
        assertEquals(0, input.getColourMarkers().size());

        assertEquals("123", output.getText());
        assertEquals(0, output.getColourMarkers().size());

        input.append("L456");

        output.shiftCharsInUntilPartialMatch(input, pattern);

        assertEquals("", input.getText());
        assertEquals(0, input.getColourMarkers().size());

        assertEquals("123EOL456", output.getText());
        assertEquals(0, output.getColourMarkers().size());
    }

    @Test
    public void fullMatchTest() throws Exception {

        input.append("123EOL456EO");

        output.shiftCharsInUntilPartialMatch(input, pattern);

        assertEquals("EO", input.getText());
        assertEquals(0, input.getColourMarkers().size());

        assertEquals("123EOL456", output.getText());
        assertEquals(0, output.getColourMarkers().size());

        input.append("L789");

        output.shiftCharsInUntilPartialMatch(input, pattern);

        assertEquals("", input.getText());
        assertEquals(0, input.getColourMarkers().size());

        assertEquals("123EOL456EOL789", output.getText());
        assertEquals(0, output.getColourMarkers().size());
    }
}