package AT04_CMS.testcases;

import AT04_CMS.pages.DashboardPage;
import AT04_CMS.pages.LoginPage;
import common.BaseTest;
import constants.ConfigData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
      LoginPage loginPage = new LoginPage();
      DashboardPage dashboardPage = new DashboardPage();

      @Test
      public void testLoginSuccess() {
            dashboardPage = loginPage.loginCMS();
            Assert.assertFalse(loginPage.isLoginPageUrl(), "Login fail");
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
      public void testEmailNull() {
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
            Assert.assertEquals(loginPage.getEmailValidationMessage(),
                        "Please include an '@' in the email address. 'abc' is missing an '@'.",
                        "Validation message of incorrect format Email not match");
      }

      @Test
      public void testPasswordNull() {
            loginPage.loginCMS(ConfigData.EMAIL, "");
            Assert.assertTrue(loginPage.isPasswordFieldRequired(), "Password is NOT a required field");
            Assert.assertEquals(loginPage.getPasswordValidationMessage(), "Please fill out this field.",
                        "Validation message of Password not match");
      }

}
