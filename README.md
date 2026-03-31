# 🚀 FinalProject — Selenium Java Test Automation Framework

> Framework kiểm thử tự động cho hệ thống **eCommerce CMS** ([https://cms.anhtester.com](https://cms.anhtester.com/login)) sử dụng **Selenium WebDriver 4**, **TestNG**, và mô hình **Page Object Model (POM)**.

---

## 📋 Mục lục

- [Tổng quan](#-tổng-quan)
- [Kiến trúc & Design Pattern](#-kiến-trúc--design-pattern)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [Yêu cầu môi trường](#-yêu-cầu-môi-trường)
- [Cài đặt & Cấu hình](#%EF%B8%8F-cài-đặt--cấu-hình)
- [Chạy test](#-chạy-test)
- [Chi tiết các Module Test](#-chi-tiết-các-module-test)
- [Framework Core — WebUI Keywords](#-framework-core--webui-keywords)
- [Data-Driven Testing](#-data-driven-testing)
- [Reporting & Artifacts](#-reporting--artifacts)
- [Parallel Execution](#-parallel-execution)
- [Tác giả](#-tác-giả)

---

## 🎯 Tổng quan

Dự án kiểm thử tự động các chức năng CRUD chính của hệ thống CMS eCommerce, bao gồm:

| Module       | Chức năng kiểm thử                                                    |
| ------------ | ---------------------------------------------------------------------- |
| **Login**    | Đăng nhập thành công, sai email/password, field rỗng, validate HTML5   |
| **Dashboard**| Điều hướng đến các trang con (Brand, Category, Product), Logout        |
| **Brand**    | Thêm mới, xóa, validate trường bắt buộc                               |
| **Category** | Thêm/sửa/xóa, tìm kiếm, phân trang (Pagination), kiểm tra chi tiết   |
| **Product**  | Thêm/sửa, xem chi tiết, validate required fields, tìm kiếm, phân trang|
| **Flow E2E** | Login → Add Brand → Add Category → Add Product                         |

---

## 🏗 Kiến trúc & Design Pattern

### Page Object Model (POM)

Mỗi trang web được đại diện bởi một **Page class** riêng biệt, tách biệt hoàn toàn giữa:
- **Locators** (định vị phần tử): khai báo `By` locator ở đầu class
- **Actions** (hành động): các method thao tác trên trang
- **Verifications** (kiểm tra): các method assert/verify kết quả

```
LoginPage  ──loginCMS()──▶  DashboardPage  ──navigateToBrandPage()──▶  BrandPage
                                           ──navigateToCategoryPage()──▶  CategoryPage
                                           ──navigateToProductPage()──▶  ProductPage
```

### ThreadLocal WebDriver

`DriverManager` sử dụng `ThreadLocal<WebDriver>` để mỗi thread có instance WebDriver riêng biệt, đảm bảo an toàn khi chạy test song song (parallel execution).

```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
```

### Keyword-Driven Framework

Lớp `WebUI` đóng vai trò là **Keyword Library** — tập trung tất cả thao tác tương tác với trình duyệt thông qua các phương thức static, tích hợp sẵn:
- Explicit Wait (WebDriverWait)
- Logging (Log4j2)
- Reporting (Extent Report + Allure)
- Allure `@Step` annotation

---

## 🛠 Công nghệ sử dụng

| Thành phần              | Công nghệ / Phiên bản                     |
| ----------------------- | ------------------------------------------ |
| Ngôn ngữ                | Java 17                                    |
| Build tool              | Maven 3.8+                                 |
| Browser automation      | Selenium WebDriver `4.35.0`                |
| Test framework          | TestNG `7.11.0`                            |
| Driver management       | WebDriverManager `6.3.2`                   |
| HTML report             | Extent Reports `5.1.1`                     |
| Interactive report      | Allure TestNG `2.27.0`                     |
| Data-driven (Excel)     | Apache POI `5.2.4`                         |
| Video recording         | Monte Screen Recorder `0.7.7.0`           |
| Logging                 | Log4j2 `2.23.1`                            |
| Boilerplate reduction   | Lombok `1.18.32`                           |
| File utilities          | Commons IO `2.15.0`                        |
| AOP (Allure)            | AspectJ Weaver `1.9.22`                    |
| Build plugin            | Maven Surefire `3.2.5`                     |

---

## 📁 Cấu trúc thư mục

```
FinalProject/
├── pom.xml                                    # Maven configuration & dependencies
├── README.md
│
├── src/main/java/                             # ═══ FRAMEWORK CORE ═══
│   ├── constants/
│   │   └── ConfigData.java                    # Hằng số cấu hình (URL, Email, Password)
│   ├── drivers/
│   │   └── DriverManager.java                 # ThreadLocal WebDriver management
│   ├── helpers/
│   │   ├── PropertiesHelper.java              # Đọc/ghi file .properties
│   │   ├── ExcelHelper.java                   # Đọc/ghi file Excel (.xlsx)
│   │   ├── CaptureHelper.java                 # Chụp ảnh & quay video màn hình
│   │   └── SystemHelper.java                  # Lấy đường dẫn thư mục hiện tại
│   ├── keywords/
│   │   └── WebUI.java                         # ⭐ Keyword Library (592 dòng)
│   ├── reports/
│   │   ├── ExtentReportManager.java           # Khởi tạo Extent Reports
│   │   ├── ExtentTestManager.java             # Quản lý test item trong Extent
│   │   └── AllureManager.java                 # Allure screenshot & text attachment
│   └── utils/
│       └── LogUtils.java                      # Wrapper Log4j2 (info/warn/error/debug)
│
├── src/test/java/                             # ═══ TEST LAYER ═══
│   ├── AT04_CMS/
│   │   ├── pages/                             # Page Object classes
│   │   │   ├── LoginPage.java                 #   ├─ Trang đăng nhập
│   │   │   ├── DashboardPage.java             #   ├─ Trang chính, điều hướng menu
│   │   │   ├── BrandPage.java                 #   ├─ Quản lý thương hiệu
│   │   │   ├── CategoryPage.java              #   ├─ Quản lý danh mục
│   │   │   └── ProductPage.java               #   └─ Quản lý sản phẩm
│   │   └── testcases/                         # Test case classes
│   │       ├── LoginTest.java                 #   ├─ 5 test cases
│   │       ├── DashboardTest.java             #   ├─ 4 test cases
│   │       ├── BrandTest.java                 #   ├─ 3 test cases
│   │       ├── CategoryTest.java              #   ├─ 5 test cases
│   │       ├── ProductTest.java               #   ├─ 7 test cases
│   │       └── FlowTest.java                  #   └─ 1 test case (E2E)
│   ├── common/
│   │   └── BaseTest.java                      # @BeforeMethod / @AfterMethod
│   ├── dataProvider/
│   │   └── DataProviderFactory.java           # TestNG DataProvider (từ Excel)
│   └── listeners/
│       └── TestListener.java                  # ITestListener (report + screenshot + video)
│
├── src/test/resources/                        # ═══ RESOURCES ═══
│   ├── configs/
│   │   └── configs.properties                 # Cấu hình chạy test
│   ├── suite/                                 # TestNG XML suites
│   │   ├── CMS.xml                            #   ├─ Suite tổng (3 test groups)
│   │   ├── LoginTest.xml                      #   ├─ Suite Login
│   │   ├── DashboardTest.xml                  #   ├─ Suite Dashboard
│   │   ├── BrandTest.xml                      #   ├─ Suite Brand
│   │   ├── CategoryTest.xml                   #   ├─ Suite Category
│   │   ├── ProductTest.xml                    #   ├─ Suite Product
│   │   ├── FlowTest.xml                       #   ├─ Suite Flow E2E
│   │   ├── FullTest.xml                       #   └─ Suite Full
│   │   └── CMS2.xml                           #
│   └── testData/                              # Dữ liệu test
│       ├── data-eCommerceCMS.xlsx             #   ├─ Data chính (Brand/Category/Product)
│       └── UserData.xlsx                      #   └─ Data Login
│
├── report/                                    # ═══ OUTPUT (git-ignored) ═══
│   ├── ExtentReport/ExtentReport.html         # Extent HTML Report
│   ├── Screenshots/                           # Ảnh chụp fail/pass
│   └── VideoRecords/                          # Video quay (nếu bật)
│
├── allure-results/                            # Allure raw results (git-ignored)
└── logs/                                      # Log files (git-ignored)
```

---

## 💻 Yêu cầu môi trường

| Yêu cầu       | Chi tiết                                       |
| -------------- | ---------------------------------------------- |
| **JDK**        | Java 17 trở lên                                |
| **Maven**      | 3.8+ (hoặc sử dụng Maven Wrapper nếu có)       |
| **Trình duyệt**| Chrome, Edge hoặc Firefox (đã cài sẵn)          |
| **OS**         | Windows / macOS / Linux                         |

> **Lưu ý:** WebDriverManager (`6.3.2`) sẽ tự động tải driver tương ứng với phiên bản trình duyệt đã cài trên máy. Không cần tải chromedriver/geckodriver thủ công.

---

## ⚙️ Cài đặt & Cấu hình

### 1. Clone repository

```bash
git clone <repository-url>
cd FinalProject
```

### 2. Cài đặt dependencies

```bash
mvn clean install -DskipTests
```

### 3. Cấu hình test

Chỉnh sửa file `src/test/resources/configs/configs.properties`:

```properties
# ── Trình duyệt ──
BROWSER = chrome                  # chrome | edge | firefox

# ── Thông tin đăng nhập ──
URL      = https://cms.anhtester.com/login
EMAIL    = admin@example.com
PASSWORD = 123456

# ── Đường dẫn dữ liệu ──
EXCEL_PATH      = src/test/resources/testData/data-eCommerceCMS.xlsx
IMAGE_PATH      = src\\test\\resources\\testdata\\lenovo.jpg

# ── Đường dẫn output ──
SCREENSHOT_PATH   = report/Screenshots/
VIDEO_RECORD_PATH = report/VideoRecords/

# ── Bật / Tắt tính năng ──
VIDEO_RECORD    = off             # on | off — Quay video màn hình
SCREENSHOT_FAIL = on              # on | off — Chụp ảnh khi test FAIL
SCREENSHOT_PASS = off             # on | off — Chụp ảnh khi test PASS
```

> **Lưu ý:** Browser cũng có thể được truyền qua tham số `<parameter name="BROWSER" value="...">` trong file TestNG XML suite. Nếu không truyền, mặc định là `chrome`.

---

## ▶ Chạy test

### Chạy toàn bộ test (Suite tổng)

```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suite/CMS.xml
```

### Chạy từng module riêng lẻ

```bash
# Login tests (5 test cases)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/LoginTest.xml

# Dashboard tests (4 test cases)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/DashboardTest.xml

# Brand tests (3 test cases)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/BrandTest.xml

# Category tests (5 test cases)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/CategoryTest.xml

# Product tests (7 test cases)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/ProductTest.xml

# Flow E2E test (1 test case)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suite/FlowTest.xml
```

---

## 🧪 Chi tiết các Module Test

### 1. LoginTest — `5 test cases`

| # | Test case                      | Mô tả                                                        |
|---|-------------------------------|---------------------------------------------------------------|
| 1 | `testLoginSucess`              | Đăng nhập thành công bằng tài khoản hợp lệ                   |
| 2 | `testLoginWithEmailInvalid`    | Đăng nhập thất bại với email không tồn tại                    |
| 3 | `testLoginWithPasswordInvalid` | Đăng nhập thất bại với mật khẩu sai                           |
| 4 | `testEmailNull`                | Validate HTML5 khi để trống email                              |
| 5 | `testIncorrectFormatEmail`     | Validate HTML5 khi email sai định dạng (thiếu `@`)             |

### 2. DashboardTest — `4 test cases`

| # | Test case                     | Mô tả                                                         |
|---|------------------------------|----------------------------------------------------------------|
| 1 | `testNavigateBrandPage`       | Login → Điều hướng đến trang Brand                             |
| 2 | `testNavigateCategoryPage`    | Login → Điều hướng đến trang Category                          |
| 3 | `testNavigateProductPage`     | Login → Điều hướng đến trang Product                           |
| 4 | `testLogout`                  | Login → Đăng xuất → Verify quay về trang Login                 |

### 3. BrandTest — `3 test cases`

| # | Test case              | Mô tả                                                              |
|---|------------------------|---------------------------------------------------------------------|
| 1 | `testAddNewBrand`       | Thêm Brand mới với đầy đủ thông tin (tên, logo, meta) → Verify success |
| 2 | `testAddBrandNullName`  | Thêm Brand với tên rỗng → Verify HTML5 required validation         |
| 3 | `testDeleteBrand`       | Tìm kiếm → Xóa Brand → Verify delete confirmation & success alert  |

### 4. CategoryTest — `5 test cases`

| # | Priority | Test case                 | Mô tả                                                      |
|---|----------|---------------------------|-------------------------------------------------------------|
| 1 | 1        | `testAddNewCategory`       | Thêm Category mới → Verify success + lấy tên category đầu tiên |
| 2 | 2        | `testEditCategory`         | Sửa Ordering Number → Verify update success + kiểm tra chi tiết |
| 3 | 3        | `testAddNewCategoryNull`   | Thêm Category với tên rỗng → Verify HTML5 validation        |
| 4 | 4        | `testDeleteCategory`       | Xóa Category → Verify dialog xác nhận + success alert       |
| 5 | 5        | `testDataTable`            | Tìm kiếm + kiểm tra phân trang (Pagination) trên data table |

### 5. ProductTest — `7 test cases`

| # | Test case                          | Mô tả                                                    |
|---|------------------------------------|-----------------------------------------------------------|
| 1 | `testAddNewProductSuccess`          | Thêm sản phẩm đầy đủ + xem chi tiết sản phẩm vừa tạo    |
| 2 | `testAddProduct_withProductNameNull`| Validate required: Product Name rỗng                      |
| 3 | `testAddProduct_withUnitNull`       | Validate required: Unit rỗng                              |
| 4 | `testAddProduct_withUnitPriceNull`  | Validate required: Unit Price rỗng                        |
| 5 | `testAddProduct_withDiscountNull`   | Validate required: Discount rỗng                          |
| 6 | `testEditProduct`                   | Sửa sản phẩm (Meta Description) → Verify update success   |
| 7 | `searchDataTable`                   | Tìm kiếm + kiểm tra phân trang trên danh sách sản phẩm   |

### 6. FlowTest — `1 test case` (End-to-End)

```
Login  ──▶  Add Brand  ──▶  Add Category  ──▶  Add Product
```

Kiểm tra luồng nghiệp vụ hoàn chỉnh xuyên suốt từ đăng nhập đến tạo sản phẩm.

---

## 🔧 Framework Core — WebUI Keywords

Lớp `WebUI.java` (~600 dòng) cung cấp đầy đủ các keyword tương tác với trình duyệt:

### Tương tác cơ bản
| Method                    | Mô tả                                   |
|--------------------------|------------------------------------------|
| `openURL(url)`            | Mở URL                                  |
| `clickElement(by)`        | Click phần tử (có wait clickable)        |
| `sendKeys(by, text)`      | Nhập text vào input                      |
| `clearAndSendKeys(by, text)` | Xóa nội dung cũ rồi nhập text mới    |
| `getTextElement(by)`      | Lấy text của phần tử                    |
| `getAttributeElement(by, attr)` | Lấy giá trị attribute             |

### Dropdown
| Method                          | Mô tả                                    |
|--------------------------------|-------------------------------------------|
| `selectStaticDropdown(by, text)` | Chọn giá trị trong `<select>` HTML       |
| `selectDynamicDropdown(by, searchBy, text)` | Chọn trong dropdown có search |

### Verification & Assertion
| Method                        | Mô tả                                     |
|------------------------------|---------------------------------------------|
| `verifyEquals(actual, expected)` | So sánh bằng (trả về boolean)           |
| `assertEquals(actual, expected, msg)` | So sánh bằng (throw exception nếu fail) |
| `verifyContains(actual, expected)` | Kiểm tra chứa (trả về boolean)        |
| `assertContains(actual, expected, msg)` | Kiểm tra chứa (throw exception)   |
| `verifyHTML5RequiredField(by)` | Kiểm tra field có thuộc tính `required`   |
| `getHTML5MessageField(by)`     | Lấy validation message HTML5              |
| `checkContainsValueOnTableByColumn(col, value)` | Kiểm tra giá trị trên data table |

### Wait strategies
| Method                        | Mô tả                                     |
|------------------------------|---------------------------------------------|
| `waitForElementVisible(by)`   | Chờ phần tử hiển thị (default 20s)         |
| `waitForElementClickable(by)` | Chờ phần tử có thể click (default 20s)     |
| `waitForElementPresent(by)`   | Chờ phần tử tồn tại trong DOM              |
| `waitForPageLoaded()`         | Chờ `document.readyState === "complete"`    |

### Advanced actions
| Method                          | Mô tả                                   |
|--------------------------------|------------------------------------------|
| `uploadFileWithLocalForm(by, path)` | Upload file qua dialog hệ thống (Robot class) |
| `getImage(browse, search, text, img, btn)` | Chọn ảnh từ media library      |
| `scrollToElement(by)`           | Cuộn đến phần tử bằng JavaScript         |
| `mouseHover(by)`                | Di chuột hover lên phần tử               |
| `dragAndDrop(from, to)`         | Kéo thả phần tử                         |
| `highLightElement(by)`          | Tô viền đỏ cho phần tử (debug)           |
| `switchToWindowOrTabByUrl(url)` | Chuyển sang tab/window theo URL           |

> Tất cả method đều tích hợp sẵn logging (`LogUtils`), Extent Report (`ExtentTestManager`) và Allure `@Step` annotation.

---

## 📊 Data-Driven Testing

### Nguồn dữ liệu Excel

Framework sử dụng **Apache POI** để đọc dữ liệu test từ file Excel (`.xlsx`):

| File Excel                  | Sheet          | Dùng cho                         |
|----------------------------|----------------|----------------------------------|
| `data-eCommerceCMS.xlsx`    | `BrandData`    | Dữ liệu thêm/xóa Brand          |
| `data-eCommerceCMS.xlsx`    | `CategoryData` | Dữ liệu thêm/sửa/xóa Category   |
| `data-eCommerceCMS.xlsx`    | `ProductData`  | Dữ liệu thêm/sửa Product        |
| `UserData.xlsx`             | `LoginData`    | Dữ liệu đăng nhập (DataProvider) |

### Cấu trúc dữ liệu Excel

```
Row 0   : Header (tên cột)
Row 1+  : Dữ liệu test input
Row 6   : Dữ liệu expected (URL, header text, messages...)
```

### DataProvider

`DataProviderFactory` cung cấp 2 phương thức đọc dữ liệu:

```java
@DataProvider(name = "DataLogin", parallel = true)     // Trả về Object[][]
@DataProvider(name = "data_provider_login_excel_hashtable")  // Trả về Hashtable
```

---

## 📈 Reporting & Artifacts

### Extent Report (HTML)

- **Output:** `report/ExtentReport/ExtentReport.html`
- Ghi log từng bước test (`Status.INFO`, `Status.PASS`, `Status.FAIL`)
- Đính kèm screenshot Base64 khi test fail
- Tự động flush report sau mỗi test context

### Allure Report

- **Raw results:** `allure-results/` (hoặc `report/AllureReport/` nếu chạy qua Maven Surefire)
- Mỗi method trong `WebUI` đều có annotation `@Step` → hiển thị chi tiết từng bước
- Screenshot PNG đính kèm khi test fail

Mở Allure Report local:
```bash
allure serve allure-results
```

### Screenshots

| Cấu hình           | Thư mục output            | Khi nào                  |
|--------------------|--------------------------|--------------------------| 
| `SCREENSHOT_FAIL=on` | `report/Screenshots/`   | Khi test case FAIL       |
| `SCREENSHOT_PASS=on` | `report/Screenshots/`   | Khi test case PASS       |

### Video Recording

| Cấu hình            | Thư mục output             | Định dạng |
|---------------------|---------------------------|-----------|
| `VIDEO_RECORD=on`    | `report/VideoRecords/`     | AVI       |

Video được quay bằng **Monte Screen Recorder**, bắt đầu record ở `onTestStart` và dừng ở `onTestSuccess`/`onTestFailure`/`onTestSkipped`.

---

## 🔀 Parallel Execution

Framework hỗ trợ chạy test song song thông qua cấu hình TestNG XML suite:

### Cấu hình trong `CMS.xml`

```xml
<!-- Chạy tuần tự -->
<test name="Flow test">
    <parameter name="BROWSER" value="chrome"/>
    <classes>
        <class name="AT04_CMS.testcases.FlowTest"/>
    </classes>
</test>

<!-- Chạy song song theo class (mỗi class 1 thread) -->
<test name="Login, Dashboard, Brand" parallel="classes">
    <parameter name="BROWSER" value="edge"/>
    <classes>
        <class name="AT04_CMS.testcases.LoginTest"/>
        <class name="AT04_CMS.testcases.DashboardTest"/>
        <class name="AT04_CMS.testcases.BrandTest"/>
    </classes>
</test>
```

### Thread Safety

- `DriverManager` sử dụng `ThreadLocal<WebDriver>` → mỗi thread có driver riêng
- `ExtentTestManager` lưu test item theo `Thread.currentThread().getId()`  
- `BaseTest` khởi tạo driver ở `@BeforeMethod` và cleanup ở `@AfterMethod`

---

## 📐 Luồng thực thi Test

```
┌─────────────┐     ┌──────────────────┐     ┌───────────────┐
│  TestNG XML  │────▶│    BaseTest      │────▶│  TestListener │
│  Suite       │     │  @BeforeMethod   │     │  onTestStart  │
└─────────────┘     │  createDriver()  │     │  startRecord  │
                    │  DriverManager   │     │  Extent init  │
                    │  .setDriver()    │     └───────────────┘
                    └──────────────────┘
                            │
                            ▼
                    ┌──────────────────┐
                    │   Test Method    │
                    │  (LoginTest,     │
                    │   BrandTest,...) │
                    └──────────────────┘
                            │
                    ┌───────┴────────┐
                    ▼                ▼
            ┌──────────────┐  ┌──────────────┐
            │  Page Object │  │   WebUI      │
            │  (LoginPage, │  │  Keywords    │
            │   BrandPage) │  │  + Wait      │
            └──────────────┘  │  + Log       │
                              │  + Report    │
                              └──────────────┘
                            │
                            ▼
                    ┌──────────────────┐
                    │    BaseTest      │
                    │  @AfterMethod    │
                    │  DriverManager   │
                    │  .quit()         │
                    └──────────────────┘
                            │
                            ▼
                    ┌───────────────────┐
                    │   TestListener    │
                    │  onTestSuccess/   │
                    │  onTestFailure    │
                    │  → Screenshot    │
                    │  → Stop Record   │
                    │  → Extent/Allure │
                    └───────────────────┘
```

---

## 👤 Tác giả

- **Author:** ninhnguyen2408
- **Framework:** Selenium Java | TestNG | POM
- **Target:** eCommerce CMS — [https://cms.anhtester.com](https://cms.anhtester.com/login)
