package helpers;

import drivers.DriverManager;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class CaptureHelper extends ScreenRecorder {

      // Record with Monte Media library
      public static ScreenRecorder screenRecorder;
      public String name;

      public CaptureHelper(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
            this.name = name;
      }

      @Override
      protected File createMovieFile(Format fileFormat) throws IOException {

            if (!movieFolder.exists()) {
                  movieFolder.mkdirs();
            } else if (!movieFolder.isDirectory()) {
                  throw new IOException("\"" + movieFolder + "\" is not a directory.");
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
            return new File(movieFolder, name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
      }

      // Start record video
      public static void startRecord(String recordName) {
            if (screenRecorder != null) {
                  stopRecord();
            }

            File file = new File(SystemHelper.getCurrentDir() + PropertiesHelper.getValue("VIDEO_RECORD_PATH"));
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;

            Rectangle captureSize = new Rectangle(0, 0, width, height);

            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            try {
                  screenRecorder = new CaptureHelper(gc, captureSize, new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI), new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60), new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)), null, file, recordName);
                  screenRecorder.start();
            } catch (IOException e) {
                  throw new RuntimeException(e);
            } catch (AWTException e) {
                  throw new RuntimeException(e);
            }
      }

      // Stop record video
      public static void stopRecord() {
            if (screenRecorder == null) {
                  return;
            }
            try {
                  screenRecorder.stop();
            } catch (IOException e) {
                  throw new RuntimeException(e);
            } finally {
                  screenRecorder = null;
            }
      }


      private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");

      public static void captureScreenshot(String screenshotName) {
            try {
                  TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
                  File source = ts.getScreenshotAs(OutputType.FILE);
                  File theDir = new File(SystemHelper.getCurrentDir() + PropertiesHelper.getValue("SCREENSHOT_PATH"));
                  if (!theDir.exists()) {
                        theDir.mkdirs();
                  }
                  FileHandler.copy(source, new File(SystemHelper.getCurrentDir() + PropertiesHelper.getValue("SCREENSHOT_PATH") + File.separator + screenshotName + "_" + dateFormat.format(new Date()) + ".png"));
                  System.out.println("Screenshot taken: " + screenshotName);
                  System.out.println("Screenshot taken current URL: " + DriverManager.getDriver().getCurrentUrl());
            } catch (Exception e) {
                  System.out.println("Exception while taking screenshot: " + e.getMessage());
            }
      }

      public static void takeScreenshot(String screenshotName) {
            TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            File theDir = new File(SystemHelper.getCurrentDir() + PropertiesHelper.getValue("SCREENSHOT_PATH"));
            if (!theDir.exists()) {
                  theDir.mkdirs();
            }
            try {
                  FileHandler.copy(source, new File(SystemHelper.getCurrentDir() + PropertiesHelper.getValue("SCREENSHOT_PATH") + screenshotName + ".png"));
                  System.out.println("Take screenshot " + screenshotName + " successfully.");
            } catch (IOException e) {
                  System.out.println("ERROR. Can not take screenshot " + screenshotName);
                  e.printStackTrace();
            }
      }

}
