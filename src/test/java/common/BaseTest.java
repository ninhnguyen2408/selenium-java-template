package common;

import drivers.DriverManager;
import listeners.TestListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;

import java.time.Duration;

@Listeners(TestListener.class)
public class BaseTest {

      @BeforeMethod
      @Parameters({ "BROWSER" })
      public void createDriver(@Optional("chrome") String browserName) {
            WebDriver driver = setupBrowser(browserName);
            // khởi tạo loại browser và gán vào driver
            // WebDriver driver = setupBrowser(PropertiesHelper.getValue("browser"));

            DriverManager.setDriver(driver); // mang giá trị driver đã khởi tạo vào trong ThreadLocal
      }

      // Viết hàm trung gian để lựa chọn Browser cần chạy với giá trị tham số
      // "browser" bên trên truyền vào
      public WebDriver setupBrowser(String browserName) {
            WebDriver driver;
            switch (browserName.trim().toLowerCase()) {
                  case "chrome":
                        driver = initChromeDriver();
                        break;
                  case "firefox":
                        driver = initFirefoxDriver();
                        break;
                  case "edge":
                        driver = initEdgeDriver();
                        break;
                  default:
                        System.out.println("Browser: " + browserName
                                    + " is invalid, Launching Chrome as browser of choice...");
                        driver = initChromeDriver();
            }
            return driver;
      }

      // Viết các hàm khởi chạy cho từng Browser đó
      private WebDriver initChromeDriver() {
            System.out.println("\n  \uD83D\uDE80 Launching Chrome browser...");
            ChromeOptions options = new ChromeOptions();
            if (isCI()) {
                  options.addArguments("--headless");
                  options.addArguments("--no-sandbox");
                  options.addArguments("--disable-dev-shm-usage");
                  options.addArguments("--disable-gpu");
            }
            WebDriver driver = new ChromeDriver(options);
            configureDriver(driver);
            return driver;
      }

      private WebDriver initEdgeDriver() {
            System.out.println("\uD83D\uDE80 Launching Edge browser...");
            EdgeOptions options = new EdgeOptions();
            if (isCI()) {
                  options.addArguments("--headless");
                  options.addArguments("--no-sandbox");
                  options.addArguments("--disable-dev-shm-usage");
                  options.addArguments("--disable-gpu");
            }
            WebDriver driver = new EdgeDriver(options);
            configureDriver(driver);
            return driver;
      }

      private WebDriver initFirefoxDriver() {
            System.out.println("\uD83D\uDE80 Launching Firefox browser...");
            FirefoxOptions options = new FirefoxOptions();
            if (isCI()) {
                  options.addArguments("--headless");
            }
            WebDriver driver = new FirefoxDriver(options);
            configureDriver(driver);
            return driver;
      }

      // GitHub Actions tự động set biến môi trường CI=true
      private boolean isCI() {
            return "true".equalsIgnoreCase(System.getenv("CI"));
      }

      private void configureDriver(WebDriver driver) {
            driver.manage().window().maximize();
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
      }

      @AfterMethod
      public void closeDriver() {

            // Screenshot and Record video in TestListener
            DriverManager.quit();
      }

}
