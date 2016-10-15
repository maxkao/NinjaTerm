package ninja.mbedded.ninjaterm.util.streamedText;

import ninja.mbedded.ninjaterm.JavaFXThreadingRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the <code>{@link StreamedText}</code> class.
 *
 * @author          Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @since           2016-10-15
 * @last-modified   2016-10-15
 */
public class CopyOrShiftNewLinesTests {

    /**
     * Including this variable in class allows JavaFX objects to be created in tests.
     */
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    private StreamedText inputStreamedText;
    private StreamedText outputStreamedText;

    @Before
    public void setUp() throws Exception {
        inputStreamedText = new StreamedText();
        outputStreamedText = new StreamedText();
    }

    @Test
    public void shiftWithNewLineTest() throws Exception {

        Method method = StreamedText.class.getDeclaredMethod(
                "copyOrShiftNewLineMarkers",
                StreamedText.class, int.class, StreamedText.CopyOrShift.class);
        method.setAccessible(true);

        inputStreamedText.append("123456");
        inputStreamedText.addNewLineMarkerAt(2);

        outputStreamedText.shiftCharsIn(inputStreamedText, inputStreamedText.getText().length());

        // Check input
        assertEquals("", inputStreamedText.getText());
        assertEquals(0, inputStreamedText.getNewLineMarkers().size());

        // Check output
        assertEquals("123456", outputStreamedText.getText());
        assertEquals(1, outputStreamedText.getNewLineMarkers().size());
        assertEquals(2, outputStreamedText.getNewLineMarkers().get(0).intValue());
    }
}