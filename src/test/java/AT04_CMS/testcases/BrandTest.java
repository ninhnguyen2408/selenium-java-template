package AT04_CMS.testcases;

import AT04_CMS.pages.BrandPage;
import AT04_CMS.pages.DashboardPage;
import AT04_CMS.pages.LoginPage;
import common.BaseTest;
import helpers.ExcelHelper;
import helpers.PropertiesHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BrandTest extends BaseTest {
      LoginPage loginPage;
      DashboardPage dashboardPage;
      BrandPage brandPage;
      ExcelHelper excelHelper;

      @BeforeMethod(alwaysRun = true)
      public void initPagesAndLogin() {
            loginPage = new LoginPage();
            excelHelper = new ExcelHelper();
            excelHelper.setExcelFile(PropertiesHelper.getValue("EXCEL_PATH"), "BrandData");
            dashboardPage = loginPage.loginCMS();
            brandPage = dashboardPage.navigateToBrandPage();
      }

      @Test
      public void testAddNewBrand() {
            brandPage.addBrand(excelHelper.getCellData(2, 1));
            brandPage.verifyAddBrandSuccess();
      }

      @Test(dependsOnMethods = "testAddNewBrand")
      public void testEditBrand() {
            brandPage.editBrand(excelHelper.getCellData(2, 1), excelHelper.getCellData(3, 1));
            brandPage.verifyEditBrandSuccess();
      }

      @Test(dependsOnMethods = "testEditBrand")
      public void testDeleteBrand() {
            brandPage.deleteBrand(excelHelper.getCellData(3, 1));
            brandPage.verifyDeleteBrandSuccess();
      }

}
