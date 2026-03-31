package listeners;

import com.aventstack.extentreports.Status;
import helpers.CaptureHelper;
import helpers.PropertiesHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reports.AllureManager;
import reports.ExtentReportManager;
import reports.ExtentTestManager;
import utils.LogUtils;

public class TestListener implements ITestListener {
      public String getTestName(ITestResult result) {
            return result.getTestName() != null ? result.getTestName() : result.getMethod().getConstructorOrMethod().getName();
      }

      public String getTestDescription(ITestResult result) {
            return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
      }

      @Override
      public void onStart(ITestContext result) {
            PropertiesHelper.loadAllFiles();
      }

      @Override
      public void onFinish(ITestContext result) {
            // End and flush Extent report once per test context
            ExtentReportManager.getExtentReports().flush();
      }

      @Override
      public void onTestStart(ITestResult result) {
            LogUtils.info("Test start: " + result.getName());

            if (isVideoRecordEnabled()) {
                  CaptureHelper.startRecord(result.getName());
            }

            // Start a new test item in Extent report
            ExtentTestManager.saveToReport(getTestName(result), getTestDescription(result));
      }

      @Override
      public void onTestSuccess(ITestResult result) {
            LogUtils.info(result.getName() + " is successfully");

            if ("on".equalsIgnoreCase(PropertiesHelper.getValue("SCREENSHOT_PASS"))) {
                  CaptureHelper.takeScreenshot(result.getName());
            }

            stopRecordIfEnabled();

            // Extent report
            ExtentTestManager.logMessage(Status.PASS, result.getName() + " is passed");
      }

      @Override
      public void onTestFailure(ITestResult result) {
            LogUtils.error(result.getName() + " is FAIL");

            if ("on".equalsIgnoreCase(PropertiesHelper.getValue("SCREENSHOT_FAIL"))) {
                  CaptureHelper.takeScreenshot(result.getName());
            }

            stopRecordIfEnabled();

            // Extent report
            ExtentTestManager.addScreenShot(result.getName());
            ExtentTestManager.logMessage(Status.FAIL, result.getThrowable().toString());
            ExtentTestManager.logMessage(Status.FAIL, result.getName() + " is failed");

            // Allure report
            AllureManager.saveScreenshotPNG();
      }

      @Override
      public void onTestSkipped(ITestResult result) {
            LogUtils.warn(result.getName() + " is SKIPPED");

            stopRecordIfEnabled();

            // Extent report
            if (result.getThrowable() != null) {
                  ExtentTestManager.logMessage(Status.SKIP, result.getThrowable().toString());
            } else {
                  ExtentTestManager.logMessage(Status.SKIP, result.getName() + " is skipped");
            }
      }

      @Override
      public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
            LogUtils.info("onTestFailedButWithinSuccessPercentage: " + result.getName());
      }

      private boolean isVideoRecordEnabled() {
            return "on".equalsIgnoreCase(PropertiesHelper.getValue("VIDEO_RECORD"));
      }

      private void stopRecordIfEnabled() {
            if (isVideoRecordEnabled()) {
                  CaptureHelper.stopRecord();
            }
      }
}