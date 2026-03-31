package AT04_CMS.pages;

import constants.ConfigData;
import drivers.DriverManager;
import keywords.WebUI;
import org.openqa.selenium.By;

public class LoginPage {

      private By headerPage = By.xpath("//p[normalize-space()='Login to your account.']");
      private By inputEmail = By.xpath("//input[@id='email']");
      private By inputPassword = By.xpath("//input[@id='password']");
      private By buttonLogin = By.xpath("//button[normalize-space()='Login']");
      private By alertMessage = By.xpath("//div[@role='alert']");

      private void navigateToLoginPage() {
            WebUI.openURL(ConfigData.URL);
            WebUI.waitForPageLoaded();
            WebUI.assertEquals(WebUI.getTextElement(headerPage), "Login to your account.", "NOT the Login page");
      }

      public DashboardPage loginCMS(String email, String password) {
            navigateToLoginPage();
            WebUI.sendKeys(inputEmail, email);
            WebUI.sendKeys(inputPassword, password);
            WebUI.clickElement(buttonLogin);
            WebUI.waitForPageLoaded();
            return new DashboardPage();
      }

      public DashboardPage loginCMS() {
            navigateToLoginPage();
            WebUI.sendKeys(inputEmail, ConfigData.EMAIL);
            WebUI.sendKeys(inputPassword, ConfigData.PASSWORD);
            WebUI.clickElement(buttonLogin);
            WebUI.waitForPageLoaded();
            return new DashboardPage();
      }

      public boolean isEmailFieldRequired() {
            return WebUI.verifyHTML5RequiredField(inputEmail);
      }

      public String getEmailValidationMessage() {
            return WebUI.getHTML5MessageField(inputEmail);
      }

      public boolean isPasswordFieldRequired() {
            return WebUI.verifyHTML5RequiredField(inputPassword);
      }

      public String getPasswordValidationMessage() {
            return WebUI.getHTML5MessageField(inputPassword);
      }

      public boolean isLoginPageUrl() {
            return DriverManager.getDriver().getCurrentUrl().contains("login");
      }

      public boolean isAlertMessageDisplayed() {
            return WebUI.checkElementDisplayed(alertMessage);
      }

      public String getAlertMessageText() {
            return WebUI.getTextElement(alertMessage);
      }
}
