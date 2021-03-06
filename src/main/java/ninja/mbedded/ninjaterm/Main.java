package ninja.mbedded.ninjaterm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ninja.mbedded.ninjaterm.model.Model;
import ninja.mbedded.ninjaterm.util.comPort.ComPortFactory;
import ninja.mbedded.ninjaterm.util.javafx.exceptionPopup.ExceptionPopup;
import ninja.mbedded.ninjaterm.util.loggerUtils.LoggerUtils;
import ninja.mbedded.ninjaterm.util.stringUtils.StringUtils;
import ninja.mbedded.ninjaterm.view.mainWindow.MainWindowViewController;
import ninja.mbedded.ninjaterm.view.splashScreen.SplashScreenViewController;
import org.apache.commons.cli.*;
import org.controlsfx.glyphfont.GlyphFont;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Main class for application.
 *
 * Processes command-line arguments, checks Java version,
 * displays splash-screen, and displays main NinjaTerm window.
 *
 * @author Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @last-modified 2017-10-08
 * @since 2016-11-14
 */
public class Main extends Application {

    private GlyphFont glyphFont;

    /**
     * This can be set true if the --nosplash command line argument is
     * passed to the program.
     */
    private boolean disableSplashScreen = false;

    /**
     * Set to true when the first unhandled exception occurs, as to
     * stop spamming of multiple "Exception Occurred" message boxes.
     */
    private boolean hasUnhandledExceptionOccurred = false;

    private Stage splashScreenStage;
    private Stage mainStage;

    private Logger logger = LoggerUtils.createLoggerFor(getClass().getName());

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //==============================================//
        //================ LOGGING SETUP ===============//
        //==============================================//

        //===== LOAD LOGBACK CONFIG FILE =====//
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        loggerContext.reset();
//        JoranConfigurator configurator = new JoranConfigurator();
//
////        InputStream configStream = FileUtils.openInputStream();
//        InputStream configStream = getClass().getResourceAsStream("/logback.xml");
//
//        configurator.setContext(loggerContext);
//        configurator.doConfigure(configStream); // loads logback file
//        configStream.close();

        //===== MANUAL LOGGING ENABLING =====//
        // NOTE: We shouldn't need to enabled this anymore, it should be done
        // by passing --logc to the app on the command line (or through Gradle)
//        LoggerUtils.consoleLoggingEnabled.set(true);
//        LoggerUtils.fileLoggingEnabled.set(true);

        //==============================================//
        //========== UN-CAUGHT EXCEPTION SETUP =========//
        //==============================================//
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            showError(thread, throwable);
        });


        //==============================================//
        //======== COMMAND-LINE ARGUMENT PARSING =======//
        //==============================================//

        Options options = new Options();

        Option logc = Option.builder(null).longOpt("logc").numberOfArgs(0).desc("log to console").build();
        options.addOption(logc);

        Option logf = Option.builder(null).longOpt("logf").numberOfArgs(0).desc("log to file").build();
        options.addOption(logf);

        Option noSplash = Option.builder(null).longOpt("nosplash").numberOfArgs(0).desc("disable splashscreen").build();
        options.addOption(noSplash);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;


        try {
            cmd = parser.parse(options, getParameters().getRaw().toArray(new String[0]));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        if(cmd.hasOption(logc.getLongOpt())) {
            LoggerUtils.consoleLoggingEnabled.set(true);
        }

        if(cmd.hasOption(logf.getLongOpt())) {
            LoggerUtils.fileLoggingEnabled.set(true);
        }

        if(cmd.hasOption(noSplash.getLongOpt())) {
            disableSplashScreen = true;
        }

        this.primaryStage = primaryStage;

        //==============================================//
        //============= CHECK JAVA VERSION =============//
        //==============================================//

        // The install4j-generated install process should also perform a version check before
        // the user is allowed to install the app. However, it seemed that the installed allowed
        // NinjaTerm to be installed even though Java was only at v1.6 on Mac OSX, hence this
        // extra check at runtime (obviously some version of Java has to be present to get to this
        // point, but it seems v1.6 will let this code run, but will produce exceptions at other
        // points in the program, e.g. when the Macro Manager window is opened).

        double version = Double.parseDouble(System.getProperty("java.specification.version"));
        logger.debug("java.specification.version (as double) = " + version);
        if (version < 1.8) {
            // User's version of Java is not compatible with NinjaTerm, show error message and exit.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Incorrect Java version!");
            alert.setContentText(
                    "NinjaCalc requires Java v1.8 (also known as Java 8) or higher to run.\n" +
                    "\n" +
                    "Your version of Java was v" + System.getProperty("java.specification.version"));

            alert.showAndWait();
            return;
        }

        if (disableSplashScreen) {
            // Skip this function, and go straight to loading the main window.
            loadMainWindow();
            return;
        }

        loadSplashScreen();

    }

    private void loadSplashScreen() {
        this.splashScreenStage = primaryStage;

        // Load splashscreen FXML file and get controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/splashScreen/SplashScreenView.fxml"));
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            return;
        }

        SplashScreenViewController splashScreenViewController = loader.getController();
        splashScreenViewController.init();

        Scene splashScreenScene = new Scene(loader.getRoot(), 800, 600, Color.TRANSPARENT);
        splashScreenViewController.isFinished.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                loadMainWindow();
            }
        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(splashScreenScene);
        primaryStage.show();

        splashScreenScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            //event.getCharacter();
            if (event.getCode().isWhitespaceKey()) {
                splashScreenViewController.speedUpSplashScreen();
            }
        });

        splashScreenViewController.startNameVersionInfoMsg();
    }

    public void loadMainWindow() {


        // Initialise fontAwesome glyths (these are downloaded from CDN)
        // (this old method required the internet, new method uses local font in resources)
        //glyphFont = GlyphFontRegistry.font("FontAwesome");
        logger.debug("Loading font \"FontAwesome\"...");
        glyphFont = new GlyphFont("FontAwesome", 12, "resources/fontawesome-webfont.ttf");

        // Create application model (data/state)
        Model model = new Model(new ComPortFactory());

        mainStage = new Stage();
        mainStage.setTitle("NinjaTerm");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/mainWindow/MainWindowView.fxml"));
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
            //return;
        }
        MainWindowViewController mainWindowViewController = loader.getController();

        mainWindowViewController.init(model, glyphFont, mainStage);

        // If the splashscreen was skipped, splashScreenStage will be null
        if (!disableSplashScreen)
            splashScreenStage.close();

        Scene scene = new Scene(mainWindowViewController.mainVBox, 1000, 800);

        // Apply dark theme
        //scene.getStylesheets().add("ninja/mbedded/ninjaterm/resources/dark.css");

        mainStage.setScene(scene);

        mainStage.initStyle(StageStyle.DECORATED);
        //mainStage.centerOnScreen();
        mainStage.show();

        // Maximise (enlarge to full screen size)
        mainStage.setMaximized(true);

        // Make sure the main stage has focus (is in front of all other windows)
        mainStage.toFront();

        // Call event handler in model for app closing
        mainStage.setOnCloseRequest(event -> {
            model.handleAppClosing();
        });

        model.createTerminal();
    }

    /**
     * This should only be called if an uncaught exception occurs in the
     * UI thread. Will not do anything the second+ time it is called.
     * @param t
     * @param e
     */
    private void showError(Thread t, Throwable e) {
//        System.err.println("***Default exception handler***");

        // Check if this method has already been called, and if so,
        // don't do anything the second time around!
        if(hasUnhandledExceptionOccurred)
            return;

        hasUnhandledExceptionOccurred = true;

        // Stop all threads except this one
        // @todo Improve the way that the other threads are stopped, the current way is dangerous and unpredictable. Should we try and disconnect from the serial port instead?
        for (Thread thread : Thread.getAllStackTraces().keySet())
        {  if (thread != Thread.currentThread() && thread.getState() == Thread.State.RUNNABLE)
            thread.stop();
        }

        logger.error(StringUtils.throwableToString(e));

        if (Platform.isFxApplicationThread()) {
            // Write the exception to the logger, and also show pop-up
            ExceptionPopup.showAndWait(e);
        }

        // After an unhandled exception occurs, exit
        System.exit(1);
    }

    /**
     * Entry point for application. This calls <code>launch(args)</code> which starts the JavaFX UI.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
