package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

      private static final ExtentReports extentReports = new ExtentReports();
      private static boolean initialized = false;

      public synchronized static ExtentReports getExtentReports() {
            if (!initialized) {
                  ExtentSparkReporter reporter = new ExtentSparkReporter("report/ExtentReport/ExtentReport.html");
                  reporter.config().setReportName("Extent Report | ninhnguyen2408");
                  extentReports.attachReporter(reporter);
                  extentReports.setSystemInfo("Framework Name", "Selenium Java | ninhnguyen2408");
                  extentReports.setSystemInfo("Author", "ninhnguyen2408");
                  initialized = true;
            }
            return extentReports;
      }

}
