package AT04_CMS.pages;

import drivers.DriverManager;
import helpers.ExcelHelper;
import helpers.PropertiesHelper;
import org.openqa.selenium.By;
import org.testng.Assert;

import static keywords.WebUI.*;

public class BrandPage {

      // Static locators
      private By headerBrand = By.xpath("//h1[normalize-space()='All Brands']");
      private By inputName = By.xpath("//input[@placeholder='Name']");
      private By uploadLogo = By.xpath("//div[@class='input-group-prepend']");
      private By searchImage = By.xpath("//input[@placeholder='Search your files']");
      private By linkImage = By.xpath("//img[@class='img-fit']");
      private By buttonAddFiles = By.xpath("//button[normalize-space()='Add Files']");
      private By inputMetaTitle = By.xpath("//input[@placeholder='Meta Title']");
      private By inputMetaDescription = By.xpath("//textarea[@name='meta_description']");
      private By buttonSave = By.xpath("//button[normalize-space()='Save']");
      private By notifyMessage = By.xpath("//span[@data-notify='message']");
      private By inputSearchBrand = By.xpath("//input[@id='search']");
      private By buttonDelete = By.xpath("//a[@id='delete-link']");
      private By headerDeleteForm = By.xpath("//h4[normalize-space()='Delete Confirmation']");
      private By confirmMessage = By.xpath("//p[@class='mt-1']");
      private By alertMessage = By.xpath("//div[@role='alert']");

      ExcelHelper excelHelper = new ExcelHelper();

      // Dynamic locators - tìm nút Edit/Delete theo tên brand trên bảng
      private By getEditButton(String brandName) {
            return By.xpath(String.format(
                        "//td[contains(text(),'%s')]/following-sibling::td//a[@title='Edit']", brandName));
      }

      private By getDeleteButton(String brandName) {
            return By.xpath(String.format(
                        "//td[contains(text(),'%s')]/following-sibling::td//a[@title='Delete']", brandName));
      }

      private void setFileExcel() {
            excelHelper.setExcelFile(PropertiesHelper.getValue("EXCEL_PATH"), "BrandData");
      }

      private void verifyBrandPage() {
            Assert.assertTrue(DriverManager.getDriver().getCurrentUrl().contains("brand"),
                        "NOT on the Brand page");
            Assert.assertTrue(checkElementDisplayed(headerBrand),
                        "Header 'All Brands' is NOT displayed");
      }

      // ==================== ADD BRAND ====================

      public void addBrand(String brandName) {
            setFileExcel();
            verifyBrandPage();
            sendKeys(inputName, brandName);
            getImage(uploadLogo, searchImage, excelHelper.getCellData(2, 2), linkImage, buttonAddFiles);
            sendKeys(inputMetaTitle, excelHelper.getCellData(2, 3));
            sendKeys(inputMetaDescription, excelHelper.getCellData(2, 4));
            clickElement(buttonSave);
      }

      public void verifyAddBrandSuccess() {
            setFileExcel();
            waitForElementVisible(notifyMessage);
            Assert.assertTrue(checkElementDisplayed(notifyMessage),
                        "Add brand success notification is NOT displayed");
            assertEquals(getTextElement(notifyMessage), excelHelper.getCellData(6, 3),
                        "Add brand notification message not match");
      }

      // ==================== EDIT BRAND ====================

      public void editBrand(String currentName, String newName) {
            setFileExcel();
            verifyBrandPage();
            setTextAndKeysENTER(inputSearchBrand, currentName);
            clickElement(getEditButton(currentName));
            waitForPageLoaded();
            clearAndSendKeys(inputName, newName);
            clearAndSendKeys(inputMetaTitle, excelHelper.getCellData(3, 3));
            clearAndSendKeys(inputMetaDescription, excelHelper.getCellData(3, 4));
            clickElement(buttonSave);
      }

      public void verifyEditBrandSuccess() {
            setFileExcel();
            waitForElementVisible(notifyMessage);
            Assert.assertTrue(checkElementDisplayed(notifyMessage),
                        "Edit brand success notification is NOT displayed");
            assertEquals(getTextElement(notifyMessage), excelHelper.getCellData(6, 7),
                        "Edit brand notification message not match");
      }

      // ==================== DELETE BRAND ====================

      public void deleteBrand(String brandName) {
            verifyBrandPage();
            setTextAndKeysENTER(inputSearchBrand, brandName);
            clickElement(getDeleteButton(brandName));
            verifyDeleteConfirmation();
            clickElement(buttonDelete);
            waitForPageLoaded();
      }

      public void verifyDeleteBrandSuccess() {
            setFileExcel();
            waitForElementVisible(alertMessage);
            assertEquals(getTextElement(alertMessage), excelHelper.getCellData(6, 6),
                        "Delete brand alert message not match");
      }

      // ==================== REQUIRED FIELD ====================

      public void verifyNameRequiredField() {
            setFileExcel();
            verifyHTML5RequiredField(inputName);
            assertEquals(getHTML5MessageField(inputName), excelHelper.getCellData(6, 4),
                        "Validation message name is required not match");
      }

      // ==================== PRIVATE ====================

      private void verifyDeleteConfirmation() {
            setFileExcel();
            waitForElementVisible(headerDeleteForm);
            Assert.assertTrue(checkElementDisplayed(headerDeleteForm),
                        "Delete confirmation dialog is NOT displayed");
            assertEquals(getTextElement(confirmMessage), excelHelper.getCellData(6, 5),
                        "Confirm message not match");
            Assert.assertTrue(checkElementEnable(buttonDelete),
                        "Button Delete is NOT enabled");
      }

}
