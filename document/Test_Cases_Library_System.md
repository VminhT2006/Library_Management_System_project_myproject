# TÀI LIỆU KỊCH BẢN KIỂM THỬ (TEST CASES)

- **Dự án:** Hệ thống Quản lý Thư viện (Library Management System - Desktop GUI Version)
- **Môn học:** Lập trình Hướng đối tượng (OOP)
- **Công nghệ:** Java Swing & AWT (Mô hình MVC, Strategy Pattern, Java Stream API, CSV File I/O)

---

Tài liệu này tổng hợp toàn bộ các kịch bản kiểm thử (**Test Cases**) trích xuất trực tiếp từ mã nguồn của hệ thống, bao phủ đầy đủ các tầng: `util`, `model`, `strategy`, `manager`, `controller`, `view`, và `main`. Các kịch bản bao gồm: **Happy Path (Luồng chuẩn)**, **Boundary (Biên)**, **Negative/Exception (Ngoại lệ)**, và **Edge Cases (Tình huống đặc biệt)**.

---

## 1. Tầng Tiện ích (`util.*`)

### 1.1. Kiểm tra dữ liệu đầu vào — [`Validator.java`](../src/util/Validator.java)

| Mã TC | Phương thức | Đầu vào (Input) | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-VAL-01** | `requireText` | `("  Java OOP  ", "Tiêu đề")` | Trả về `"Java OOP"` (đã `trim()`) | Positive |
| **TC-VAL-02** | `requireText` | `(null, "Tiêu đề")` hoặc `("   ", "Tiêu đề")` | Ném `IllegalArgumentException("Tiêu đề không được để trống!")` | Negative |
| **TC-VAL-03** | `requireNonNull` | `(new Object(), "Độc giả")` | Trả về đúng đối tượng đầu vào | Positive |
| **TC-VAL-04** | `requireNonNull` | `(null, "Độc giả")` | Ném `IllegalArgumentException("Độc giả không được để trống (null)!")` | Negative |
| **TC-VAL-05** | `requireEmail` / `isValidEmail` | `"docgia.01+test@library.edu.vn"` | Hợp lệ, trả về chuỗi đã `trim()` | Positive |
| **TC-VAL-06** | `requireEmail` / `isValidEmail` | `"abc"`, `"abc@gmail"`, `"@gmail.com"`, `"a@b."`, `"a b@gmail.com"` | Ném `IllegalArgumentException("Email không đúng định dạng...")` / `false` | Negative |
| **TC-VAL-07** | `requirePhone` / `isValidPhone` | `"0901234567"` (10 số) và `"01234567890"` (11 số) | Hợp lệ, trả về chuỗi số điện thoại | Boundary |
| **TC-VAL-08** | `requirePhone` / `isValidPhone` | `"090123456"` (9 số), `"090123456789"` (12 số), `"8490123456"` (không bắt đầu bằng `0`), `"090123456a"` | Ném `IllegalArgumentException("Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng số 0!")` | Boundary / Negative |
| **TC-VAL-09** | `requireUrl` / `isValidUrl` | `"https://library.edu.vn/book.pdf"`, `"HTTP://EXAMPLE.COM"` | Hợp lệ (không phân biệt hoa thường ở tiền tố), giữ nguyên chuỗi gốc sau `trim()` | Positive |
| **TC-VAL-10** | `requireUrl` / `isValidUrl` | `"ftp://files.com"`, `"www.google.com"`, `""`, `null` | Ném `IllegalArgumentException("... phải bắt đầu bằng http:// hoặc https://!")` | Negative |
| **TC-VAL-11** | `requirePositive(int)` | `1` (biên dưới hợp lệ) | Trả về `1` | Boundary |
| **TC-VAL-12** | `requirePositive(int)` | `0`, `-1` | Ném `IllegalArgumentException("... phải lớn hơn 0!")` | Boundary / Negative |
| **TC-VAL-13** | `requireNonNegative(int)` | `0`, `10` | Trả về `0`, `10` | Boundary |
| **TC-VAL-14** | `requireNonNegative(int)` | `-1` | Ném `IllegalArgumentException("... không được âm!")` | Boundary / Negative |
| **TC-VAL-15** | `requireRange` | `(1, 1, 12, "Tháng")`, `(12, 1, 12, "Tháng")` | Trả về `1`, `12` | Boundary |
| **TC-VAL-16** | `requireRange` | `(0, 1, 12, "Tháng")`, `(13, 1, 12, "Tháng")` | Ném `IllegalArgumentException("Tháng phải nằm trong khoảng từ 1 đến 12!")` | Boundary / Negative |
| **TC-VAL-17** | `requirePositive(double)` | `0.0001` | Trả về `0.0001` | Boundary |
| **TC-VAL-18** | `requirePositive(double)` | `0.0`, `-5.0`, `Double.NaN`, `Double.POSITIVE_INFINITY` | Ném `IllegalArgumentException("... phải lớn hơn 0!")` | Edge / Negative |
| **TC-VAL-19** | `requireNonNegative(double)` | `0.0`, `100.5` | Trả về `0.0`, `100.5` | Boundary |
| **TC-VAL-20** | `requireNonNegative(double)` | `-0.01`, `Double.NaN`, `Double.NEGATIVE_INFINITY` | Ném `IllegalArgumentException("... không được âm!")` | Edge / Negative |
| **TC-VAL-21** | `parseInt` / `parsePositiveInt` / `parseNonNegativeInt` | `" 42 "`, `"abc"`, `"12.5"`, `"99999999999999"` | `"42"` -> `42`; các trường hợp còn lại ném `IllegalArgumentException("... phải là một số nguyên hợp lệ!")` | Positive / Negative |
| **TC-VAL-22** | `parseDouble` / `parsePositiveDouble` | `"8.5"` và `"8,5"` (dấu phẩy thập phân) | Cả hai đều trả về `8.5` (nhờ `.replace(',', '.')`) | Edge / Positive |
| **TC-VAL-23** | `parseDouble` | `"abc"`, `""`, `null` | Ném `IllegalArgumentException` | Negative |

---

### 1.2. Xử lý Ngày tháng, Tiền tệ, Chuỗi & File CSV — [`DateUtil.java`](../src/util/DateUtil.java), [`FormatUtil.java`](../src/util/FormatUtil.java), [`TextUtil.java`](../src/util/TextUtil.java), [`FileUtil.java`](../src/util/FileUtil.java)

| Mã TC | Phương thức | Đầu vào (Input) | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-DATE-01** | `DateUtil.format` | `LocalDate.of(2026, 11, 16)` | `"16/11/2026"` | Positive |
| **TC-DATE-02** | `DateUtil.format` | `null` | `""` (chuỗi rỗng) | Edge |
| **TC-DATE-03** | `DateUtil.parse` | `"16/11/2026"` (dd/MM/yyyy) và `"2026-11-16"` (ISO) | Cả hai đều trả về `LocalDate.of(2026, 11, 16)` | Positive |
| **TC-DATE-04** | `DateUtil.parse` | `null`, `""`, `"   "` | Trả về `null` | Edge |
| **TC-DATE-05** | `DateUtil.parse` | `"2026/11/16"`, `"32/01/2026"`, `"abc"` | Ném `IllegalArgumentException("Ngày tháng không đúng định dạng (dd/MM/yyyy): ...")` | Negative |
| **TC-FMT-01** | `FormatUtil.formatVND` | `0`, `5000`, `50000`, `1250000` | `"0 VNĐ"`, `"5.000 VNĐ"`, `"50.000 VNĐ"`, `"1.250.000 VNĐ"` | Positive |
| **TC-TXT-01** | `TextUtil.normalize` | `"  Lập Trình Hướng Đối Tượng ĐH  "` | `"lap trinh huong doi tuong dh"` (bỏ dấu tiếng Việt, chuyển `đ/Đ` -> `d`, chữ thường) | Positive |
| **TC-TXT-02** | `TextUtil.normalize` | `null`, `""`, `"   "` | `""` | Edge |
| **TC-TXT-03** | `TextUtil.containsKeyword` | `source = "Nguyễn Văn Độc Giả"`, `keyword = "doc gia"` / `""` | `"doc gia"` -> `true`; `""` hoặc `null` -> `true`; `source = null` & `keyword = "a"` -> `false` | Positive / Edge |
| **TC-FILE-01** | `FileUtil.toCsvLine` & `parseCsvLine` | Các trường bình thường: `("B001", "Book", "Clean Code")` | `"B001,Book,Clean Code"` -> parse ngược lại đúng 3 phần tử | Positive |
| **TC-FILE-02** | `FileUtil.toCsvLine` & `parseCsvLine` | Trường chứa dấu phẩy và nháy kép: `("B001", "Sách \"Hay\", Tập 1")` | Chuỗi CSV bọc trong `"..."` và nhân đôi `""`; `parseCsvLine` khôi phục nguyên vẹn `"Sách \"Hay\", Tập 1"` | Edge |
| **TC-FILE-03** | `FileUtil.readLines` | Đường dẫn `null`, `""`, hoặc file không tồn tại | Trả về `List` rỗng (không ném ngoại lệ), tự động bỏ qua dòng trắng khi đọc | Edge |
| **TC-FILE-04** | `FileUtil.writeLines` | Đường dẫn hợp lệ trong thư mục chưa tồn tại vs `filePath = null` / `lines = null` | Tự tạo thư mục cha (`mkdirs()`) và ghi UTF-8 trả về `true`; nếu `null` trả về `false` | Positive / Negative |

---

## 2. Tầng Mô hình Dữ liệu & Đa hình (`model.*`)

### 2.1. Tài liệu & Đa hình (`Document`, `Book`, `Magazine`, `Thesis`, `EBook`) — [`Document.java`](../src/model/Document.java), [`Book.java`](../src/model/Book.java), [`Magazine.java`](../src/model/Magazine.java), [`Thesis.java`](../src/model/Thesis.java), [`EBook.java`](../src/model/EBook.java)

| Mã TC | Lớp / Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-DOC-01** | `Document.setId` | Nhập `" b001 "` | Tự động `trim()` và viết hoa thành `"B001"` | Positive |
| **TC-DOC-02** | `Document.setPublicationYear` | Năm `1`, năm hiện tại (`LocalDate.now().getYear()`) | Hợp lệ | Boundary |
| **TC-DOC-03** | `Document.setPublicationYear` | Năm `0`, `-2020`, hoặc `currentYear + 1` | Ném `IllegalArgumentException("Năm xuất bản phải nằm trong khoảng từ 1 đến ...")` | Boundary / Negative |
| **TC-BOOK-01** | `Book.canBorrow` | `quantity = 1` vs `quantity = 0` | `quantity = 1` -> `true`; `quantity = 0` -> `false` | Boundary |
| **TC-BOOK-02** | `Book.calculateLateFee` | `days = 3`, `days = 0`, `days = -2` | `days = 3` -> `15000.0`; `days <= 0` -> `0.0` | Boundary |
| **TC-BOOK-03** | `Book.setWaitingList` | Truyền `null` | Ném `IllegalArgumentException("Hàng đợi đặt trước không được để trống (null)!")` | Negative |
| **TC-MAG-01** | `Magazine` constructor / setters | `issueNumber = 1`, `publishMonth = 1..12`, `quantity = 0` | Khởi tạo thành công; `getTypeName()` trả về `"Tạp chí"` | Boundary |
| **TC-MAG-02** | `Magazine` setters | `issueNumber = 0`, `publishMonth = 0` hoặc `13`, `quantity = -1` | Ném `IllegalArgumentException` tương ứng | Negative |
| **TC-MAG-03** | `Magazine.canBorrow` & `calculateLateFee` | `quantity > 0` vs `0`; `days = 4` | `canBorrow()` theo `quantity > 0`; phí trễ hạn `4 * 5000 = 20000.0` | Positive |
| **TC-THE-01** | `Thesis.canBorrow` & `calculateLateFee` | Bất kỳ đối tượng `Thesis` hợp lệ, `days = 10` | `canBorrow()` luôn trả về `false` (chỉ đọc tại chỗ); `calculateLateFee(10)` luôn trả về `0.0` | Polymorphism |
| **TC-THE-02** | `Thesis` setters | `university`, `advisor`, hoặc `major` rỗng/`null` | Ném `IllegalArgumentException` | Negative |
| **TC-EBK-01** | `EBook` setters | `url = "https://..."`, `fileSizeMb = 2.5`, `format = " pdf "` | Hợp lệ; `getFormat()` tự động viết hoa thành `"PDF"` | Positive |
| **TC-EBK-02** | `EBook.canBorrow` & `calculateLateFee` | Bất kỳ `EBook`, `days = 100` | `canBorrow()` luôn trả về `true` (không giới hạn bản sao); `calculateLateFee(100)` trả về `0.0` | Polymorphism |
| **TC-EBK-03** | `EBook` setters | `url = "invalid"`, `fileSizeMb = 0`, `format = ""` | Ném `IllegalArgumentException` | Negative |

---

### 2.2. Người dùng & Ví tiền Độc giả (`Person`, `User`, `Admin`, `Member`) — [`User.java`](../src/model/User.java), [`Member.java`](../src/model/Member.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-USR-01** | `User.setUserID` & `setRole` | `userID = " mem001 "`, `role = " member "` | Chuẩn hóa thành `"MEM001"` và `"MEMBER"` | Positive |
| **TC-USR-02** | `User.checkPassword` | `password = "123456"`; kiểm tra với `" 123456 "`, `"12345"`, `""`, `null` | `" 123456 "` -> `true`; các trường hợp còn lại -> `false` | Positive / Edge |
| **TC-MEM-01** | `Member` khởi tạo mặc định | `new Member("M1", "u", "p", "Name")` | `role = "MEMBER"`, `tier = "Tiêu chuẩn"`, `banned = false`, `balance = 0.0`, `unpaidFine = 0.0`, `maxBorrowBook = 3` | Positive |
| **TC-MEM-02** | `Member.setUnpaidFine` | Đặt `unpaidFine = 50000.0` (đúng bằng ngưỡng `BAN_FINE_THRESHOLD`) | `banned` vẫn là `false` (vì điều kiện là `unpaidFine > 50000.0`) | Boundary |
| **TC-MEM-03** | `Member.setUnpaidFine` | Đặt `unpaidFine = 50000.01` hoặc `55000.0` (vượt ngưỡng) | Tự động đặt `banned = true` | Boundary |
| **TC-MEM-04** | `Member.deductFine` | `balance = 30000`, `unpaidFine = 0`, gọi `deductFine(0)` | Trả về `true`, `balance` giữ nguyên `30000` | Boundary |
| **TC-MEM-05** | `Member.deductFine` | `balance = 30000`, gọi `deductFine(20000)` (đủ ví) | Trả về `true`, `balance = 10000`, `unpaidFine = 0`, `banned = false` | Positive |
| **TC-MEM-06** | `Member.deductFine` | `balance = 10000`, `unpaidFine = 0`, gọi `deductFine(30000)` (thiếu 20.000 <= 50.000) | Trả về `false`, `balance = 0`, `unpaidFine = 20000`, `banned = false` | Positive |
| **TC-MEM-07** | `Member.deductFine` | `balance = 10000`, `unpaidFine = 20000`, gọi `deductFine(45000)` (thiếu 35.000 -> tổng nợ 55.000 > 50.000) | Trả về `false`, `balance = 0`, `unpaidFine = 55000`, tự động khóa `banned = true` | Boundary / Business Rule |
| **TC-MEM-08** | `Member.deposit` | Đang nợ `unpaidFine = 60000`, `banned = true`, `balance = 0` -> nạp `deposit(40000)` (chưa đủ trả hết nợ) | `balance = 40000`, `unpaidFine` vẫn là `60000`, `banned` vẫn là `true` (chưa trừ nợ từng phần) | Edge / Business Rule |
| **TC-MEM-09** | `Member.deposit` | Tiếp tục từ TC-MEM-08 (`balance = 40000`, `unpaidFine = 60000`, `banned = true`) -> nạp thêm `deposit(25000)` | Tổng số dư `65000 >= 60000` -> tự động trừ hết nợ: `balance = 5000`, `unpaidFine = 0`, `banned = false` | Business Rule |
| **TC-MEM-10** | `Member.deposit` | Nạp `0` hoặc `-10000` | Ném `IllegalArgumentException("Số tiền nạp phải lớn hơn 0!")` | Negative |

---

### 2.3. Giao dịch Mượn / Trả (`Transaction`) — [`Transaction.java`](../src/model/Transaction.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-TX-01** | Constructor 3 tham số | `new Transaction("tx01", "mem01", "b01")` | `transactionID="TX01"`, `memberID="MEM01"`, `documentID="B01"`, `borrowDate=today`, `dueDate=today+14`, `status="BORROWED"`, `fineAmount=0`, `isRenewed=false` | Positive |
| **TC-TX-02** | `setDueDate` | `dueDate` trước `borrowDate` (`borrowDate.minusDays(1)`) | Ném `IllegalArgumentException("Ngày hẹn trả không được trước ngày mượn!")` | Negative |
| **TC-TX-03** | `setReturnDate` | `returnDate` trước `borrowDate` vs `returnDate = null` | Trước `borrowDate` -> ném `IllegalArgumentException`; `null` -> hợp lệ | Boundary / Negative |
| **TC-TX-04** | `isOverdue` | `status = "BORROWED"`, `dueDate = today` vs `dueDate = today.minusDays(1)` | `dueDate = today` -> `false`; `dueDate = today.minusDays(1)` -> `true` | Boundary |
| **TC-TX-05** | `isOverdue` | `status = "RETURNED"`, `dueDate = today.minusDays(10)` | Luôn trả về `false` khi đã trả | Business Rule |
| **TC-TX-06** | `renewLoan` | Phiếu đang `"BORROWED"`, chưa quá hạn, `isRenewed = false` | Trả về `true`, `dueDate` cộng thêm 7 ngày, `isRenewed = true` | Positive |
| **TC-TX-07** | `renewLoan` | Gọi `renewLoan()` lần 2 (`isRenewed = true`), hoặc khi `isOverdue() = true`, hoặc khi `status = "RETURNED"` | Trả về `false`, `dueDate` không đổi | Negative |
| **TC-TX-08** | `calculateFine` | Trả đúng hạn (`returnDate <= dueDate`) hoặc trả trễ 4 ngày (`returnDate = dueDate.plusDays(4)`) | Đúng hạn -> `0.0`; trễ 4 ngày -> `4 * 5000 = 20000.0` (cập nhật cả `this.fineAmount`) | Positive |

---

## 3. Tầng Chiến lược Sắp xếp — Strategy Pattern (`strategy.*`)

Áp dụng trên [`SortByTitleAscending.java`](../src/strategy/SortByTitleAscending.java), [`SortByYearDescending.java`](../src/strategy/SortByYearDescending.java), [`SortByQuantityDescending.java`](../src/strategy/SortByQuantityDescending.java):

| Mã TC | Chiến lược | Đầu vào (Input) | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-SRT-01** | `SortByTitleAscending` | Danh sách sách: `"Java"`, `"clean code"`, `"Algorithms"` | Sắp xếp A-Z không phân biệt hoa thường: `"Algorithms"`, `"clean code"`, `"Java"` | Positive |
| **TC-SRT-02** | `SortByYearDescending` | Danh sách sách có năm XB: `2020`, `2025`, `2022` | Sắp xếp giảm dần: `2025`, `2022`, `2020` | Positive |
| **TC-SRT-03** | `SortByQuantityDescending` | Danh sách sách có tồn kho: `5`, `0`, `12` | Sắp xếp giảm dần: `12`, `5`, `0` | Positive |
| **TC-SRT-04** | Cả 3 lớp Strategy | Danh sách rỗng `[]` hoặc 1 phần tử | Không lỗi, giữ nguyên danh sách | Boundary |
| **TC-SRT-05** | Cả 3 lớp Strategy | Truyền `books = null` | Ném `IllegalArgumentException("Danh sách sách không được để trống (null)!")` | Negative |

---

## 4. Tầng Quản lý & Đồng bộ File (`manager.*`)

### 4.1. Quản lý Tài liệu — [`DocumentManager.java`](../src/manager/DocumentManager.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-DM-01** | `addDocument` & `findById` | Thêm `Book("B001", ...)` rồi tìm `findById("b001")` | Thêm thành công (`true`); tìm thấy không phân biệt hoa/thường | Positive |
| **TC-DM-02** | `addDocument` | Thêm tài liệu có mã `"b001"` khi `"B001"` đã tồn tại | Ném `IllegalArgumentException("Mã tài liệu đã tồn tại trong hệ thống: B001")` | Negative |
| **TC-DM-03** | `updateDocument` | Cập nhật tài liệu có ID tồn tại vs ID không tồn tại | Tồn tại -> thay thế tại chỗ, trả về `true`; không tồn tại -> trả về `false` | Positive / Negative |
| **TC-DM-04** | `removeDocument` | Xóa theo ID tồn tại, ID không tồn tại, hoặc ID rỗng | Tồn tại -> `true`; không tồn tại -> `false`; rỗng/`null` -> ném `IllegalArgumentException` | Positive / Negative |
| **TC-DM-05** | `searchDocuments` | `keyword = ""` hoặc `null` | Trả về bản sao chứa toàn bộ tài liệu trong kho | Boundary |
| **TC-DM-06** | `searchDocuments` | `criteria = "Mã ID"`, `"Tiêu đề"`, `"Tác giả"`, `"Tất cả"` với từ khóa không dấu `"nguyen van"` | `"Tác giả"` khớp cả `Book.author` và `Thesis.advisor` (nhưng loại trừ `Magazine`, `EBook`); `"Tất cả"` khớp ID, Tiêu đề, Tác giả, GVHD | Positive |
| **TC-DM-07** | `saveData` & `loadData` | Kho có đủ 4 loại (`Book` kèm hàng đợi `"MEM001|MEM002"`, `Magazine`, `Thesis`, `EBook`) | Ghi ra CSV và đọc lại khôi phục đúng kiểu lớp con (polymorphic deserialization) và thứ tự hàng đợi `Queue` | Persistence |
| **TC-DM-08** | `loadData` | File CSV có dòng thiếu cột (`< 8` cột), sai định dạng số, hoặc loại tài liệu lạ (`"AUDIO"`) | Bỏ qua dòng lỗi (in `System.err`), tiếp tục nạp các dòng hợp lệ còn lại mà không crash | Resilience |

---

### 4.2. Quản lý Người dùng — [`UserManager.java`](../src/manager/UserManager.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-UM-01** | `authenticate` | Nhập đúng `userName` (không phân biệt hoa/thường) và đúng `password` | Trả về đối tượng `Admin` hoặc `Member` tương ứng | Positive |
| **TC-UM-02** | `authenticate` | Sai `userName`, sai `password`, hoặc truyền `null`/`""` | Trả về `null` | Negative |
| **TC-UM-03** | `addUser` | Thêm người dùng trùng `userID` hoặc trùng `userName` (khác hoa/thường) | Ném `IllegalArgumentException("Mã người dùng đã tồn tại...")` hoặc `"Tên đăng nhập đã tồn tại..."` | Negative |
| **TC-UM-04** | `updateUser` | Đổi `userName` của `MEM001` sang `userName` đang thuộc về `MEM002` | Ném `IllegalArgumentException("Tên đăng nhập đã tồn tại...")` (nhờ `ensureUnique(updatedUser, exceptId)`) | Edge / Negative |
| **TC-UM-05** | `registerMember` | Gọi `registerMember(fullName, userName, password)` khi đã có `MEM001`, `MEM003` (khuyết `MEM002`) | `generateMemberId()` tự động cấp mã trống đầu tiên là `"MEM002"` và lưu xuống CSV | Edge / Positive |
| **TC-UM-06** | `loadData` | Dòng CSV của Member có `banned = false` nhưng `unpaidFine = 60000` (> 50.000), hoặc `banned = "abc"` | Khi `unpaidFine > 50000` tự bật `banned = true`; khi `banned = "abc"` hoặc trùng ID trong file thì bỏ qua dòng lỗi | Resilience |

---

### 4.3. Quản lý Giao dịch & Thống kê Stream — [`TransactionManager.java`](../src/manager/TransactionManager.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-TM-01** | `generateTransactionId` | Danh sách đang có 2 giao dịch (`TX0001`, `TX0002`) | Sinh mã `"TX0003"`; nếu `"TX0003"` đã tồn tại thì tăng tiếp lên `"TX0004"` | Positive |
| **TC-TM-02** | `countActiveBorrows` & `isBorrowingDocument` | Độc giả có 2 phiếu `"BORROWED"` và 1 phiếu `"RETURNED"` | `countActiveBorrows` trả về `2`; `isBorrowingDocument` trả về `true` cho sách đang mượn và `false` cho sách đã trả | Positive |
| **TC-TM-03** | `getDisplayStatus` & `filterByStatus` | Lọc theo `"Tất cả"`, `"Đang mượn"`, `"Đã trả"`, `"Quá hạn"` (với `memberId = null` cho Admin hoặc `memberId = "MEM001"` cho Member) | Giao dịch `"BORROWED"` nhưng quá hạn có `getDisplayStatus = "Quá hạn"`, nên chỉ xuất hiện khi lọc `"Quá hạn"` hoặc `"Tất cả"` (không nằm trong bộ lọc `"Đang mượn"`) | Business Rule |
| **TC-TM-04** | Quản lý giữ sách (`setHold`, `isHeldFor`, `clearHold`) | Đặt giữ sách `"b001"` cho `"mem002"` -> kiểm tra `isHeldFor("B001", "MEM002")` và `isHeldFor("B001", "MEM001")` -> gọi `clearHold("B001")` | Khớp không phân biệt hoa thường (`true` cho `MEM002`, `false` cho `MEM001`); sau `clearHold` trả về `null` | Positive |
| **TC-TM-05** | Thống kê Stream (`countBorrowed`, `countOverdue`, `totalCollectedFine`) | Có 3 phiếu `BORROWED` (trong đó 1 phiếu quá hạn) và 2 phiếu `RETURNED` (phạt `10000` và `15000`) | `countBorrowed() = 3`, `countOverdue() = 1`, `totalCollectedFine() = 25000.0` | Positive |
| **TC-TM-06** | `topBorrowedDocuments(3)` | Có 5 đầu sách với số lượt mượn khác nhau | Trả về `LinkedHashMap` tối đa 3 phần tử sắp xếp theo số lượt mượn giảm dần | Positive |
| **TC-TM-07** | `overdueMemberIds` & `countOverdueOfMember` | `MEM001` có 2 phiếu quá hạn, `MEM002` có 1 phiếu đúng hạn | `overdueMemberIds()` chỉ chứa `["MEM001"]`; `countOverdueOfMember("MEM001") = 2` | Positive |
| **TC-TM-08** | `saveData` & `loadData` | Lưu giao dịch đang quá hạn xuống `data/transactions-v2.csv` và danh sách giữ sách xuống `data/holds-v2.csv` | Khi ghi xuống CSV, trạng thái quá hạn được ghi là `"OVERDUE"`; khi nạp lại (`loadData`), `"OVERDUE"` được chuẩn hóa về `"BORROWED"` và tính động lại từ `dueDate` | Persistence |

---

## 5. Tầng Điều khiển Nghiệp vụ (`controller.*`)

### 5.1. Đăng nhập & Đăng ký — [`LoginController.java`](../src/controller/LoginController.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-LC-01** | `login` | Đăng nhập với `admin / admin123` hoặc `member / 123456` | Trả về đối tượng `Admin` hoặc `Member` | Positive |
| **TC-LC-02** | `login` | Để trống `userName` hoặc `password` | Ném `IllegalArgumentException("Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!")` | Negative |
| **TC-LC-03** | `login` | Nhập sai tên đăng nhập hoặc mật khẩu | Ném `IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác!")` | Negative |
| **TC-LC-04** | `register` | Họ tên hợp lệ, `userName = "newuser"`, `password = "123"`, `confirm = "123"` | Tạo và lưu `Member` mới thành công (biên độ dài tối thiểu = 3 ký tự) | Boundary / Positive |
| **TC-LC-05** | `register` | `userName` chứa khoảng trắng (`"new user"`) | Ném `IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng!")` | Negative |
| **TC-LC-06** | `register` | `userName` < 3 ký tự (`"ab"`) hoặc `password` < 3 ký tự (`"12"`) | Ném `IllegalArgumentException("... phải có ít nhất 3 ký tự!")` | Boundary / Negative |
| **TC-LC-07** | `register` | `password = "123456"`, `confirmPassword = "654321"` (hoặc `null`) | Ném `IllegalArgumentException("Mật khẩu xác nhận không trùng khớp!")` | Negative |
| **TC-LC-08** | `register` | `userName` đã tồn tại (`"admin"` hoặc `"MEMBER"`) | Ném `IllegalArgumentException("Tên đăng nhập đã tồn tại: ...")` | Negative |

---

### 5.2. Mượn, Trả, Gia hạn & Hàng đợi Đặt trước — [`TransactionController.java`](../src/controller/TransactionController.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-TC-01** | `borrow` (Sách / Tạp chí còn hàng) | Member hợp lệ mượn `Book` (`quantity = 5`) hoặc `Magazine` (`quantity = 2`) | Tạo giao dịch `BORROWED` hạn 14 ngày; `quantity` giảm đi 1 (`4` / `1`); lưu file CSV | Positive |
| **TC-TC-02** | `borrow` (E-Book) | Member mượn `EBook` | Tạo giao dịch thành công, không giới hạn số lượng bản sao | Positive |
| **TC-TC-03** | `borrow` (Tài khoản bị khóa) | Member có `banned = true` bấm mượn | Ném `IllegalStateException("Tài khoản của bạn đang bị khóa do nợ phạt quá hạn!...")` | Business Rule |
| **TC-TC-04** | `borrow` (Quá hạn mức 3 cuốn) | Member đang có 3 giao dịch `BORROWED` bấm mượn cuốn thứ 4 | Ném `IllegalStateException("Bạn đã mượn tối đa 3 tài liệu! Vui lòng trả bớt...")` | Boundary / Business Rule |
| **TC-TC-05** | `borrow` (Trùng tài liệu đang mượn) | Member đang mượn `B001` (chưa trả) bấm mượn lại `B001` | Ném `IllegalStateException("Bạn đang mượn tài liệu này rồi, không thể mượn thêm bản sao!")` | Business Rule |
| **TC-TC-06** | `borrow` (Luận văn) | Member bấm mượn `Thesis` (`T001`) | Ném `IllegalStateException("Luận văn chỉ được phép đọc tại chỗ, không cho mượn về!")` | Polymorphism / Business Rule |
| **TC-TC-07** | `borrow` (Sách hết hàng -> Hàng đợi) | Member bấm mượn `Book` có `quantity = 0` (và không được giữ riêng cho Member này) | Ném `OutOfStockException` để tầng View bật hộp thoại hỏi tham gia hàng đợi đặt trước | Business Rule |
| **TC-TC-08** | `borrow` (Tạp chí hết hàng) | Member bấm mượn `Magazine` có `quantity = 0` | Ném `IllegalStateException("Tạp chí này hiện đã hết bản sao...")` (không có hàng đợi cho Tạp chí) | Business Rule |
| **TC-TC-09** | `borrow` (Nhận sách từ Hold) | Sách `B003` (`quantity = 0`) đang được giữ (`holds`) cho `MEM001`: (a) `MEM002` bấm mượn; (b) `MEM001` bấm mượn | (a) `MEM002` bị ném `OutOfStockException`; (b) `MEM001` mượn thành công, xóa `hold` của `B003`, `quantity` vẫn là `0` | Business Rule |
| **TC-TC-10** | `joinWaitingList` | Member đăng ký đặt trước `Book` hết hàng lần đầu vs đăng ký lặp lại lần 2 | Lần 1: thêm vào cuối `waitingList`, trả về vị trí `1`; Lần 2: ném `IllegalStateException("Bạn đã đăng ký đặt trước tài liệu này trước đó rồi!")` | Positive / Negative |
| **TC-TC-11** | `joinWaitingList` | Gọi đặt trước cho `Magazine`, `Thesis`, hoặc `EBook` | Ném `IllegalArgumentException("Chỉ hỗ trợ đặt trước đối với tài liệu loại Sách!")` | Negative |
| **TC-TC-12** | `returnDocument` (Đúng hạn, không có người chờ) | Trả `Book` khi chưa quá hạn, `waitingList` rỗng | `status = "RETURNED"`, `fine = 0`, `paidFully = true`, `heldFor = null`, `book.quantity` tăng thêm 1 | Positive |
| **TC-TC-13** | `returnDocument` (Đúng hạn, có hàng đợi FIFO) | Trả `Book` (`quantity = 0`) khi `waitingList` có `["MEM002", "MEM003"]` | `poll()` lấy `"MEM002"` ra khỏi hàng đợi, ghi `holds.put(bookId, "MEM002")`, **không** tăng `book.quantity` (vẫn bằng `0`), `result.heldForMemberId = "MEM002"` | Business Rule |
| **TC-TC-14** | `returnDocument` (Quá hạn, chọn trừ ví `payFromWallet = true`, đủ số dư) | Trễ 3 ngày (`fine = 15000`), `balance = 50000` | Trừ ví còn `35000`, `paidFullyFromWallet = true`, `unpaidFine = 0` | Positive |
| **TC-TC-15** | `returnDocument` (Quá hạn, chọn trừ ví `payFromWallet = true`, thiếu số dư) | Trễ 12 ngày (`fine = 60000`), `balance = 5000`, `unpaidFine = 0` | Ví về `0`, phần thiếu `55000` cộng vào `unpaidFine`, `paidFullyFromWallet = false`, tài khoản bị khóa (`banned = true`) | Business Rule |
| **TC-TC-16** | `returnDocument` (Quá hạn, chọn ghi nợ `payFromWallet = false`) | Trễ 4 ngày (`fine = 20000`), `balance = 50000`, `unpaidFine = 0` | Giữ nguyên `balance = 50000`, cộng `20000` vào `unpaidFine`, `paidFullyFromWallet = false` | Business Rule |
| **TC-TC-17** | `returnDocument` (Giao dịch đã trả hoặc không tồn tại) | Gọi trả lần 2 trên cùng `transactionId` | Ném `IllegalStateException("Giao dịch này đã được trả trước đó!")` | Negative |
| **TC-TC-18** | `renew` (Hợp lệ) | Phiếu `BORROWED`, chưa quá hạn, `isRenewed = false`, `waitingList` rỗng | Gia hạn thành công, `dueDate` tăng 7 ngày, `isRenewed = true` | Positive |
| **TC-TC-19** | `renew` (Bị chặn) | (a) Đã gia hạn 1 lần; (b) Đã quá hạn; (c) Sách đang có người trong `waitingList` | Ném `IllegalStateException` với thông báo tương ứng cho từng trường hợp | Business Rule |

---

### 5.3. Quản lý Tài liệu & Ví tiền — [`DocumentController.java`](../src/controller/DocumentController.java), [`WalletController.java`](../src/controller/WalletController.java)

| Mã TC | Phương thức | Kịch bản kiểm thử | Kết quả kỳ vọng (Expected) | Phân loại |
| :--- | :--- | :--- | :--- | :--- |
| **TC-DC-01** | `DocumentController.updateDocument` & `deleteDocument` | Cập nhật / xóa mã tài liệu không tồn tại | Ném `IllegalArgumentException("Không tìm thấy tài liệu cần cập nhật/xóa: ...")` | Negative |
| **TC-WC-01** | `WalletController.deposit` | Member đang bị khóa (`banned = true`, `unpaidFine = 55000`, `balance = 0`) nạp `60000` | Trả về `true` (đã mở khóa), `balance = 5000`, `unpaidFine = 0`, lưu xuống `users-v2.csv` | Positive |
| **TC-WC-02** | `WalletController.deposit` | Member đang hoạt động bình thường nạp `20000` | Trả về `false` (vì trước đó không bị khóa), `balance` tăng `20000` | Positive |
| **TC-WC-03** | `WalletController.collectUnpaidFine` | Admin bấm thu phạt cho Member có `unpaidFine = 30000`, `balance = 50000` | Trả về `30000.0`, `balance = 20000`, `unpaidFine = 0`, `banned = false` | Positive |
| **TC-WC-04** | `WalletController.collectUnpaidFine` | (a) Member có `unpaidFine = 0`; (b) Member có `unpaidFine = 30000` nhưng `balance = 10000` | (a) Ném `IllegalStateException("Độc giả ... không có nợ phạt!")`; (b) Ném `IllegalStateException("Số dư ví của độc giả không đủ để thu phạt!...")` | Negative |

---

## 6. Tầng Giao diện Swing & Luồng Tích hợp (`view.*`, `main.Main`)

| Mã TC | Màn hình | Thao tác trên Giao diện | Kết quả kỳ vọng (Expected) |
| :--- | :--- | :--- | :--- |
| **TC-UI-01** | [`Main.java`](../src/main/Main.java) | Khởi chạy ứng dụng khi các file CSV chưa tồn tại hoặc trống | Tự động nạp Nimbus Look & Feel, khởi tạo 2 tài khoản mẫu (`admin/admin123`, `member/123456`) và 6 tài liệu mẫu thuộc 4 loại |
| **TC-UI-02** | [`RegisterDialog.java`](../src/view/RegisterDialog.java) | Mở hộp thoại Đăng ký và nhấn phím `ESC` | Hộp thoại đóng ngay lập tức (`UiUtil.bindEscapeToClose`) |
| **TC-UI-03** | [`GuestCatalogFrame.java`](../src/view/GuestCatalogFrame.java) | Nhấn `"Duyệt danh mục với quyền Khách"`, gõ từ khóa vào ô tìm kiếm, đổi tiêu chí ComboBox | Bảng cập nhật tức thì theo từng ký tự gõ (`DocumentListener`), chỉ cho phép xem, không có nút Mượn/Sửa/Xóa |
| **TC-UI-04** | [`MemberDashboardFrame.java`](../src/view/MemberDashboardFrame.java) | Đăng nhập tài khoản Member đang có `banned = true` | Nhãn trạng thái hiện `"BỊ KHÓA"` nền đỏ, nút `"📖 Mượn tài liệu"` bị vô hiệu hóa (`setEnabled(false)`) |
| **TC-UI-05** | [`MemberDashboardFrame.java`](../src/view/MemberDashboardFrame.java) | Chọn ComboBox Sắp xếp (`"Tên sách A-Z"`, `"Năm xuất bản mới nhất"`, `"Số lượng tồn kho nhiều nhất"`) | Các dòng loại `Sách` được sắp xếp theo đúng chiến lược và hiển thị lên đầu bảng; các loại `Tạp chí`, `Luận văn`, `E-Book` giữ nguyên thứ tự ở phía dưới |
| **TC-UI-06** | [`MemberDashboardFrame.java`](../src/view/MemberDashboardFrame.java) | Bấm `"Mượn tài liệu"` hoặc `"Gia hạn"` hoặc `"Trả sách"` khi chưa chọn dòng nào trên bảng | Hiển thị `JOptionPane.WARNING_MESSAGE` nhắc chọn dòng |
| **TC-UI-07** | [`MemberDashboardFrame.java`](../src/view/MemberDashboardFrame.java) | Bấm `"Trả sách"` cho giao dịch quá hạn -> Hộp thoại `YES_NO_CANCEL_OPTION` hiện ra -> Chọn `Cancel` hoặc đóng cửa sổ (`X`) | Hủy thao tác trả sách, giao dịch vẫn giữ trạng thái `"Quá hạn"` |
| **TC-UI-08** | [`AdminDashboardFrame.java`](../src/view/AdminDashboardFrame.java) | Tab Kho Tài liệu: Đổi `Loại tài liệu` trên ComboBox (`Sách`, `Tạp chí`, `Luận văn`, `E-Book`) và click chọn từng dòng trên bảng | `CardLayout` tự động chuyển đổi 3 ô nhập đặc thù tương ứng; khi click vào dòng trên bảng, dữ liệu tự động điền lên các ô tương ứng |
| **TC-UI-09** | [`AdminDashboardFrame.java`](../src/view/AdminDashboardFrame.java) | Cập nhật thông tin một cuốn `Sách` đang có độc giả trong hàng đợi đặt trước (`waitingList`) | Hàng đợi `waitingList` của cuốn sách được bảo toàn sau khi bấm `"Cập nhật"` |
| **TC-UI-10** | [`AdminDashboardFrame.java`](../src/view/AdminDashboardFrame.java) | Tab Báo cáo & Thống kê: Bấm `"📄 Xuất báo cáo"` | Tạo file `data/bao_cao_thong_ke.txt` ghi đầy đủ 4 chỉ số KPI, Top 3 sách mượn nhiều nhất và danh sách độc giả quá hạn |

---

## 7. Các Tình huống Biên & Điểm Cần Lưu ý Phát hiện từ Code

1. **Tính phí phạt trễ hạn của `EBook` (`Transaction.calculateFine()` vs `EBook.calculateLateFee()`)**:
   - Trong `EBook.calculateLateFee(days)`, phí phạt trả về `0.0` (theo thiết kế Đa hình).
   - Tuy nhiên, `TransactionController.returnDocument()` và `previewFine()` hiện gọi `Transaction.calculateFine()`, vốn nhân cứng `overdueDays * 5000.0` mà chưa ủy quyền sang `doc.calculateLateFee(days)`. Do đó nếu một giao dịch mượn `EBook` bị quá hạn, hệ thống vẫn tính phạt `5.000 VNĐ/ngày`.
2. **Đặt trước (`joinWaitingList`) khi gọi trực tiếp ở tầng Controller**:
   - `TransactionController.joinWaitingList()` kiểm tra `book.getWaitingList().contains(member.getUserID())`, nhưng chưa kiểm tra xem `member` có đang mượn chính cuốn sách đó hay không (trên giao diện Swing thì bị chặn bởi bước `borrow()` trước khi bắt `OutOfStockException`).
3. **Thời hạn giữ sách 48h (`holds`)**:
   - Thông báo UI ghi *"Sách được giữ riêng cho độc giả [...] trong 48h"*, nhưng `holds-v2.csv` hiện chỉ lưu cặp `documentID,memberID` mà chưa lưu mốc thời gian (`timestamp`), nên quyền giữ sách tồn tại cho đến khi độc giả đó thực hiện mượn.
4. **Xóa tài liệu khi đang có giao dịch mượn (`BORROWED`)**:
   - Admin có thể xóa một tài liệu đang có người mượn. Bảng giao dịch và thống kê xử lý an toàn bằng cách hiển thị tên tài liệu là `"(đã xóa)"`, nhưng khi trả sách đã xóa thì số lượng tồn kho không còn đối tượng `Document` để cộng lại.
