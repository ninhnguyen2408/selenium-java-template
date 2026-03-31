package constants;

import helpers.PropertiesHelper;

public class ConfigData {

      static {
            PropertiesHelper.loadAllFiles();
      }

      public static final String URL = PropertiesHelper.getValue("URL");
      public static final String EMAIL = PropertiesHelper.getValue("EMAIL");
      public static final String PASSWORD = PropertiesHelper.getValue("PASSWORD");
}