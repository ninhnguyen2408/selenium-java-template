package helpers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ExcelHelper {

      private Workbook wb;
      private Sheet sh;
      private Cell cell;
      private Row row;
      private CellStyle cachedCellStyle;
      private String excelFilePath;
      private Map<String, Integer> columns = new HashMap<>();

      // Set file excel — fis có thể đóng ngay sau khi Workbook load vào memory
      public void setExcelFile(String excelPath, String sheetName) {
            try {
                  File f = new File(excelPath);
                  if (!f.exists()) {
                        System.out.println("File doesn't exist: " + excelPath);
                        return;
                  }
                  if (wb != null) {
                        wb.close();
                        wb = null;
                        cachedCellStyle = null;
                  }
                  try (FileInputStream fis = new FileInputStream(excelPath)) {
                        wb = WorkbookFactory.create(fis);
                  }
                  sh = wb.getSheet(sheetName);
                  if (sh == null) {
                        throw new Exception("Sheet name doesn't exist: " + sheetName);
                  }
                  this.excelFilePath = excelPath;
                  columns.clear();
                  sh.getRow(0).forEach(c -> columns.put(c.getStringCellValue(), c.getColumnIndex()));
            } catch (Exception e) {
                  System.out.println(e.getMessage());
            }
      }

      // Get cell data by column index and row index
      public String getCellData(int columnIndex, int rowIndex) {
            try {
                  cell = sh.getRow(rowIndex).getCell(columnIndex);
                  switch (cell.getCellType()) {
                        case STRING:
                              return cell.getStringCellValue();
                        case NUMERIC:
                              if (DateUtil.isCellDateFormatted(cell)) {
                                    return String.valueOf(cell.getDateCellValue());
                              }
                              return String.valueOf((long) cell.getNumericCellValue());
                        case BOOLEAN:
                              return Boolean.toString(cell.getBooleanCellValue());
                        case FORMULA:
                              return cell.getCellFormula();
                        case BLANK:
                        case ERROR:
                        case _NONE:
                        default:
                              return "";
                  }
            } catch (Exception e) {
                  return "";
            }
      }

      // Get cell data by column name and row index
      public String getCellData(String columnName, int rowIndex) {
            return getCellData(columns.get(columnName), rowIndex);
      }

      // Set cell data by column index — dùng lại cachedCellStyle để tránh vượt giới hạn POI
      public void setCellData(String text, int columnIndex, int rowIndex) {
            try {
                  row = sh.getRow(rowIndex);
                  if (row == null) row = sh.createRow(rowIndex);
                  cell = row.getCell(columnIndex);
                  if (cell == null) cell = row.createCell(columnIndex);
                  cell.setCellValue(text);
                  cell.setCellStyle(getOrCreateCellStyle());
                  writeWorkbook();
            } catch (Exception e) {
                  System.out.println(e.getMessage());
            }
      }

      // Set cell data by column name
      public void setCellData(String text, String columnName, int rowIndex) {
            setCellData(text, columns.get(columnName), rowIndex);
      }

      // Lấy dữ liệu từ file excel dùng cho DataProvider
      public Object[][] getExcelData(String filePath, String sheetName) {
            try (FileInputStream fis = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                  Sheet sheet = workbook.getSheet(sheetName);
                  Row headerRow = sheet.getRow(0);
                  int noOfRows = sheet.getPhysicalNumberOfRows();
                  int noOfCols = headerRow.getLastCellNum();
                  System.out.println("Rows: " + noOfRows + " - Cols: " + noOfCols);

                  Object[][] data = new Object[noOfRows - 1][noOfCols];
                  for (int i = 1; i < noOfRows; i++) {
                        Row dataRow = sheet.getRow(i);
                        for (int j = 0; j < noOfCols; j++) {
                              Cell c = dataRow.getCell(j);
                              switch (c.getCellType()) {
                                    case STRING:
                                          data[i - 1][j] = c.getStringCellValue();
                                          break;
                                    case NUMERIC:
                                          data[i - 1][j] = String.valueOf(c.getNumericCellValue());
                                          break;
                                    default:
                                          data[i - 1][j] = "";
                                          break;
                              }
                        }
                  }
                  return data;
            } catch (Exception e) {
                  System.out.println("Exception reading Excel: " + e.getMessage());
                  throw new RuntimeException(e);
            }
      }

      // Dùng cho trường hợp nhiều Field trong File Excel (Hashtable)
      public Object[][] getDataHashTable(String excelPath, String sheetName, int startRow, int endRow) {
            System.out.println("Excel Path: " + excelPath);
            try {
                  File f = new File(excelPath);
                  if (!f.exists()) {
                        throw new IOException("File Excel path not found: " + excelPath);
                  }
                  setExcelFile(excelPath, sheetName);
                  int rows = getLastRowNum();
                  int cols = getColumns();
                  System.out.println("Row: " + rows + " - Column: " + cols);
                  System.out.println("StartRow: " + startRow + " - EndRow: " + endRow);

                  Object[][] data = new Object[(endRow - startRow) + 1][1];
                  for (int rowNums = startRow; rowNums <= endRow; rowNums++) {
                        Hashtable<String, String> table = new Hashtable<>();
                        for (int colNum = 0; colNum < cols; colNum++) {
                              table.put(getCellData(colNum, 0), getCellData(colNum, rowNums));
                        }
                        data[rowNums - startRow][0] = table;
                  }
                  return data;
            } catch (IOException e) {
                  e.printStackTrace();
                  return new Object[0][0];
            }
      }

      public int getColumns() {
            try {
                  return sh.getRow(0).getLastCellNum();
            } catch (Exception e) {
                  System.out.println(e.getMessage());
                  throw e;
            }
      }

      public int getLastRowNum() {
            return sh.getLastRowNum();
      }

      public int getPhysicalNumberOfRows() {
            return sh.getPhysicalNumberOfRows();
      }

      // Tạo CellStyle một lần rồi cache lại — tránh lỗi "too many cell styles" của POI
      private CellStyle getOrCreateCellStyle() {
            if (cachedCellStyle == null) {
                  cachedCellStyle = wb.createCellStyle();
                  cachedCellStyle.setFillPattern(FillPatternType.NO_FILL);
                  cachedCellStyle.setAlignment(HorizontalAlignment.CENTER);
                  cachedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            }
            return cachedCellStyle;
      }

      private void writeWorkbook() throws IOException {
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                  wb.write(fileOut);
            }
      }

}
