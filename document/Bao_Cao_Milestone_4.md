# BÁO CÁO ĐỒ ÁN MÔN HỌC LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (OOP)
## Đề tài: Hệ thống Quản lý Thư viện (Library Management System – Desktop GUI)

---

## MỤC LỤC
1. [MILESTONE 4 – CORE IMPLEMENTATION (Cài đặt cốt lõi)](#milestone-4--core-implementation-cài-đặt-cốt-lõi)
   - [4.1. Kiến trúc Gói mã nguồn (Package Organization)](#41-kiến-trúc-gói-mã-nguồn-package-organization)
   - [4.2. Cài đặt Java Classes & Nguyên lý OOP](#42-cài-đặt-java-classes--nguyên-lý-oop)
   - [4.3. Ứng dụng Java Collections Framework & Stream API](#43-ứng-dụng-java-collections-framework--stream-api)
   - [4.4. Cơ chế Xử lý Ngoại lệ (Exception Handling)](#44-cơ-chế-xử-lý-ngoại-lệ-exception-handling)

---

# MILESTONE 4 – CORE IMPLEMENTATION (Cài đặt cốt lõi)
*(Tương ứng giai đoạn: Java Classes + Collections + Exception Handling – Sau chương 8)*

## 4.1. Kiến trúc Gói mã nguồn (Package Organization)
Toàn bộ hệ thống được tổ chức thành **8 package mã nguồn** (cùng thư mục `data`) với tổng cộng **34 file `.java`** tách biệt rõ vai trò MVC (biên dịch sạch với `javac` không có lỗi):
* `model` (10 lớp): `Person`, `User`, `Admin`, `Member`, `Document`, `Book`, `Magazine`, `Thesis`, `EBook`, `Transaction`.
* `interfaces` (2 giao diện): `IBookSorter`, `IFileService`.
* `strategy` (3 lớp): `SortByTitleAscending`, `SortByYearDescending`, `SortByQuantityDescending`.
* `manager` (3 lớp): `DocumentManager`, `UserManager`, `TransactionManager`.
* `controller` (4 lớp): `LoginController`, `DocumentController`, `TransactionController`, `WalletController`.
* `view` (6 lớp): `LoginFrame`, `RegisterDialog`, `GuestCatalogFrame`, `AdminDashboardFrame`, `MemberDashboardFrame`, `UiUtil`.
* `util` (5 lớp): `FileUtil`, `DateUtil`, `FormatUtil`, `TextUtil`, `Validator`.
* `main` (1 lớp): `Main`.
* `data`: Lưu dữ liệu vận hành (`documents-v2.csv`, `users-v2.csv`, `transactions-v2.csv`, và sinh thêm `holds-v2.csv` khi có giữ chỗ hàng đợi).

## 4.2. Cài đặt Java Classes & Nguyên lý OOP
1. **Đóng gói (Encapsulation):** Toàn bộ các trường dữ liệu nhạy cảm (`balance`, `unpaidFine`, `quantity`, `dueDate`, `status`...) được bảo vệ bằng `private`/`protected`. Mọi thay đổi qua `setter` đều đi qua [`Validator.java`](../src/util/Validator.java) để chuẩn hóa `.trim()`, `.toUpperCase()` và kiểm tra biên giá trị trước khi gán.
2. **Đa hình (Polymorphism) & Nạp chồng (Overloading):**
   * Trong `Document`, các lớp con `Book`, `Magazine`, `Thesis`, `EBook` ghi đè `canBorrow()`, `calculateLateFee(int days)`, `getTypeName()`. Trong `Transaction.java` có sẵn 2 hàm nạp chồng: `calculateFine()` và `calculateFine(Document doc)` (gọi đa hình `doc.calculateLateFee(...)`).
   * Trong `DocumentManager`, `setSorter(IBookSorter)` cho phép chuyển đổi thuật toán sắp xếp tức thì mà không cần `switch-case`.

## 4.3. Ứng dụng Java Collections Framework & Stream API
Hệ thống sử dụng đa dạng cấu trúc dữ liệu trong `java.util` phù hợp cho từng bài toán cụ thể:

| Cấu trúc / API | Vị trí cài đặt | Mục đích & Lý do lựa chọn |
| :--- | :--- | :--- |
| **`ArrayList<T>`** | `DocumentManager` (`List<Document>`), `UserManager` (`List<User>`), `TransactionManager` (`List<Transaction>`) | Lưu trữ động toàn bộ thực thể trong bộ nhớ, hỗ trợ truy xuất nhanh, duyệt tuần tự và lọc danh sách. |
| **`Collections.unmodifiableList`** | `UserManager.getUsers()` | Trả về bản sao chỉ đọc để bảo vệ danh sách nội bộ khỏi bị sửa/xóa trái phép từ bên ngoài. |
| **`Queue<String>` (`LinkedList<>`)** | `Book.waitingList` | Cài đặt **Hàng đợi đặt trước FIFO (First-In, First-Out)**: `offer()` khi độc giả đăng ký chờ sách hết hàng, `poll()` lấy người đăng ký đầu tiên khi có người trả sách (`TransactionController`). |
| **`Map<String, String>` (`LinkedHashMap<>`)** | `TransactionManager.holds` | Ánh xạ `documentID -> memberID` để giữ riêng bản sao sách vừa được trả cho người đứng đầu hàng đợi, đồng thời lưu/đọc xuống `data/holds-v2.csv`. |
| **`Map<String, Long>` (`LinkedHashMap`)** | `TransactionManager.topBorrowedDocuments(int limit)` | Dùng kết hợp `Collectors.groupingBy(..., Collectors.counting())` để thống kê số lượt mượn theo mã tài liệu và giữ nguyên thứ tự giảm dần của Top 3. |
| **`Set<String>` (`LinkedHashSet`)** | `TransactionManager.overdueMemberIds()` | Thu thập tập mã độc giả đang có ít nhất một giao dịch quá hạn, tự động loại bỏ trùng lặp mã độc giả. |
| **`Java Stream API`** | `DocumentManager`, `TransactionManager` | Xử lý tìm kiếm Live Search (`filter`), tách danh sách `Book` (`filter` + `map`), đếm lượt mượn đang mở (`countBorrowed`), đếm quá hạn (`countOverdue`) và tính tổng tiền phạt (`mapToDouble().sum()`). |

## 4.4. Cơ chế Xử lý Ngoại lệ (Exception Handling)
Hệ thống thiết kế cơ chế bắt và xử lý ngoại lệ đa tầng theo đúng yêu cầu Mục 7.4 của SRS để đảm bảo chương trình **không bao giờ bị crash** khi nhập sai dữ liệu hoặc vi phạm nghiệp vụ:

1. **Ngoại lệ kiểm tra đầu vào tại `Validator` & `Model` (`IllegalArgumentException`):**
   * `Validator.requireText`, `requireEmail`, `requirePhone`, `requireUrl`, `requirePositive`, `requireNonNegative`, `requireRange`, `parseInt`, `parsePositiveDouble` đều bắt lỗi kiểu dữ liệu (như `NumberFormatException` khi người dùng nhập chữ vào ô năm xuất bản/số lượng/tiền nạp) và chuyển thành `IllegalArgumentException` với thông điệp tiếng Việt rõ ràng.
   * `DateUtil.parse` bắt `DateTimeParseException` để hỗ trợ cả định dạng `dd/MM/yyyy` lẫn ISO `yyyy-MM-dd` trước khi ném thông báo lỗi định dạng.

2. **Ngoại lệ kiểm soát quy tắc nghiệp vụ tại `Controller` (`IllegalStateException` & Custom Exception):**
   * `LoginController`: Ném `IllegalArgumentException` khi bỏ trống, sai mật khẩu, mật khẩu xác nhận không khớp hoặc trùng tên đăng nhập.
   * `TransactionController`:
     * Định nghĩa ngoại lệ riêng **`OutOfStockException`** (kế thừa `RuntimeException`) khi `Book` hết bản sao để tầng `View` phân biệt riêng luồng gợi ý tham gia **Hàng đợi đặt trước** với các lỗi từ chối mượn thông thường.
     * Ném `IllegalStateException` khi tài khoản bị khóa (`banned`), vượt hạn mức 3 cuốn, mượn trùng sách đang giữ, mượn `Thesis`, gia hạn quá 1 lần, gia hạn khi quá hạn hoặc gia hạn sách đang có người đặt trước.

3. **Xử lý ngoại lệ File I/O tại `util` & `manager` (`IOException`, `try-with-resources`):**
   * `FileUtil.readLines` và `FileUtil.writeLines` dùng `try-with-resources` (`BufferedReader`, `BufferedWriter`) với `StandardCharsets.UTF_8`, bắt `IOException` để log lỗi an toàn mà không làm treo ứng dụng; nếu file chưa tồn tại thì trả về danh sách rỗng để `Main` khởi tạo dữ liệu mẫu.
   * Khi phân tích từng dòng CSV trong `DocumentManager.loadData()`, `UserManager.loadData()`, `TransactionManager.loadData()`, mỗi dòng được bọc trong khối `try-catch (RuntimeException)` để bỏ qua dòng dữ liệu lỗi cục bộ mà vẫn nạp trọn vẹn phần dữ liệu hợp lệ còn lại.

4. **Bắt ngoại lệ tại tầng Giao diện Swing (`view`):**
   * Toàn bộ các sự kiện nhấn nút trên `LoginFrame`, `RegisterDialog`, `AdminDashboardFrame`, `MemberDashboardFrame` đều bọc trong `try-catch`:
     * Bắt riêng `TransactionController.OutOfStockException` để mở hộp thoại `JOptionPane.showConfirmDialog` hỏi xác nhận đặt trước.
     * Bắt `RuntimeException` chung để hiển thị thông báo lỗi cụ thể (`ex.getMessage()`) qua `JOptionPane.showMessageDialog(..., JOptionPane.ERROR_MESSAGE)`, giữ nguyên cửa sổ làm việc cho người dùng sửa lại dữ liệu.
