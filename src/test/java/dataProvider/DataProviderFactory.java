package dataProvider;

import helpers.ExcelHelper;
import helpers.PropertiesHelper;
import org.testng.annotations.DataProvider;

public class DataProviderFactory {

      private static final String USER_DATA_SHEET = "LoginData";

      @DataProvider(name = "DataLogin", parallel = true)
      public Object[][] dataLogin() {
            ExcelHelper excelHelper = new ExcelHelper();
            return excelHelper.getExcelData(PropertiesHelper.getValue("USER_DATA_PATH"), USER_DATA_SHEET);
      }

      @DataProvider(name = "data_provider_login_excel_hashtable")
      public Object[][] dataLoginHRMFromExcelHashtable() {
            ExcelHelper excelHelper = new ExcelHelper();
            return excelHelper.getDataHashTable(PropertiesHelper.getValue("USER_DATA_PATH"), USER_DATA_SHEET, 1, 2);
      }
}