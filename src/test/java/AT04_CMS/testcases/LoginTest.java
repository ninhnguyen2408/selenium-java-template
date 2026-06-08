package AT04_CMS.testcases;

import AT04_CMS.pages.DashboardPage;
import AT04_CMS.pages.LoginPage;
import common.BaseTest;
import constants.ConfigData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
      LoginPage loginPage;
      DashboardPage dashboardPage;

      @BeforeMethod(alwaysRun = true)
      public void initPages() {
            loginPage = new LoginPage();
            dashboardPage = new DashboardPage();
      }

      @Test
      public void testLoginSuccess() {
            dashboardPage = loginPage.loginCMS();
            dashboardPage.verifyDashboardPage();
      }

      @Test
      public void testLoginWithEmailInvalid() {
            loginPage.loginCMS("admin@example111.com", ConfigData.PASSWORD);
            Assert.assertTrue(loginPage.isAlertMessageDisplayed(), "Alert message should be displayed");
            Assert.assertTrue(loginPage.isLoginPageUrl(), "Fail, NOT on the Login page");
            Assert.assertEquals(loginPage.getAlertMessageText(), "Invalid login credentials",
                        "Content of alert message not match");
      }

      @Test
      public void testLoginWithPasswordInvalid() {
            loginPage.loginCMS(ConfigData.EMAIL, "568690");
            Assert.assertTrue(loginPage.isAlertMessageDisplayed(), "Alert message should be displayed");
            Assert.assertTrue(loginPage.isLoginPageUrl(), "Fail, NOT on the Login page");
            Assert.assertEquals(loginPage.getAlertMessageText(), "Invalid login credentials",
                        "Content of alert message not match");
      }

      @Test
      public void testLoginWithEmptyEmail() {
            loginPage.loginCMS("", ConfigData.PASSWORD);
            Assert.assertTrue(loginPage.isEmailFieldRequired(), "Email is NOT a required field");
            Assert.assertEquals(loginPage.getEmailValidationMessage(), "Please fill out this field.",
                        "Validation message of Email not match");
      }

      @Test
      public void testIncorrectFormatEmail() {
            loginPage.loginCMS("abc", ConfigData.PASSWORD);
            Assert.assertTrue(loginPage.isEmailFieldRequired(),
                        "Validation message of incorrect format Email NOT exists");
            Assert.assertTrue(loginPage.getEmailValidationMessage().contains("@"),
                        "Validation message of incorrect format Email not match");
      }

      @Test
      public void testLoginWithEmptyPassword() {
            loginPage.loginCMS(ConfigData.EMAIL, "");
            Assert.assertTrue(loginPage.isPasswordFieldRequired(), "Password is NOT a required field");
            Assert.assertEquals(loginPage.getPasswordValidationMessage(), "Please fill out this field.",
                        "Validation message of Password not match");
      }

}
