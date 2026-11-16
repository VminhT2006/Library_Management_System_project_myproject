package manager;

import interfaces.IFileService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Admin;
import model.Member;
import model.User;
import util.FileUtil;
import util.Validator;

/**
 * Quản lý tài khoản Admin/Member (FR-02, FR-07, FR-08).
 * Dữ liệu được đọc/ghi tại data/users.csv bằng UTF-8.
 *
 * Lưu ý: Lớp User hiện tại kiểm tra mật khẩu dạng văn bản thường.
 * Vì vậy, file CSV của bản demo này cũng chứa mật khẩu dạng văn bản thường.
 * Không sử dụng cách lưu mật khẩu này cho ứng dụng thực tế.
 */
public class UserManager implements IFileService {
    private static final String DATA_FILE = "data/users.csv";
    private static final String HEADER =
            "role;userID;userName;password;fullName;email;phone;tier;banned;balance;unpaidFine";

    private final List<User> users;

    public UserManager() {
        users = new ArrayList<>();
        loadData();
    }

    /** Trả về bản sao danh sách (không cho thêm/xóa trực tiếp vào Collection nội bộ). */
    public List<User> getUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    public User findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (User user : users) {
            if (user.getUserID().equalsIgnoreCase(id.trim())) {
                return user;
            }
        }
        return null;
    }

    public User findByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return null;
        }
        for (User user : users) {
            if (user.getUserName().equalsIgnoreCase(userName.trim())) {
                return user;
            }
        }
        return null;
    }

    /** Trả về Admin/Member khi đăng nhập đúng, ngược lại trả về null. */
    public User authenticate(String userName, String password) {
        User user = findByUserName(userName);
        return user != null && user.checkPassword(password) ? user : null;
    }

    /** Thêm và lưu ngay; ID và tên đăng nhập phải duy nhất. */
    public boolean addUser(User user) {
        validateUser(user);
        ensureUnique(user, null);
        users.add(user);
        if (saveData()) {
            return true;
        }
        users.remove(users.size() - 1);
        return false;
    }

    /** Thay đối tượng có cùng userID bằng đối tượng mới và lưu ngay. */
    public boolean updateUser(User updatedUser) {
        validateUser(updatedUser);
        int index = indexOfId(updatedUser.getUserID());
        if (index < 0) {
            return false;
        }
        ensureUnique(updatedUser, updatedUser.getUserID());
        User oldUser = users.set(index, updatedUser);
        if (saveData()) {
            return true;
        }
        users.set(index, oldUser);
        return false;
    }

    /** Xóa tài khoản theo ID và lưu ngay. */
    public boolean removeUser(String userID) {
        String id = Validator.requireText(userID, "Mã người dùng");
        int index = indexOfId(id);
        if (index < 0) {
            return false;
        }
        User removed = users.remove(index);
        if (saveData()) {
            return true;
        }
        users.add(index, removed);
        return false;
    }

    /** Dùng cho form đăng ký chỉ yêu cầu họ tên, tên đăng nhập và mật khẩu. */
    public Member registerMember(String fullName, String userName, String password) {
        return registerMember(generateMemberId(), userName, password, fullName);
    }

    /** Đăng ký khi mã độc giả đã được xác định trước. */
    public Member registerMember(String userID, String userName, String password, String fullName) {
        Member member = new Member(userID, userName, password, fullName);
        if (!addUser(member)) {
            throw new IllegalStateException("Không thể lưu tài khoản độc giả xuống file!");
        }
        return member;
    }

    /** Đăng ký kèm thông tin liên hệ (email và số điện thoại có thể để null/rỗng). */
    public Member registerMember(String userID, String userName, String password,
                                 String fullName, String email, String phone) {
        Member member = new Member(userID, userName, password, fullName);
        if (email != null && !email.trim().isEmpty()) {
            member.setEmail(email);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            member.setPhone(phone);
        }
        if (!addUser(member)) {
            throw new IllegalStateException("Không thể lưu tài khoản độc giả xuống file!");
        }
        return member;
    }

    private String generateMemberId() {
        int counter = 1;
        while (true) {
            String id = String.format("MEM%03d", counter++);
            if (findById(id) == null) {
                return id;
            }
        }
    }

    private int indexOfId(String id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    private void ensureUnique(User user, String exceptId) {
        for (User existing : users) {
            if (exceptId != null && existing.getUserID().equalsIgnoreCase(exceptId)) {
                continue;
            }
            if (existing.getUserID().equalsIgnoreCase(user.getUserID())) {
                throw new IllegalArgumentException("Mã người dùng đã tồn tại: " + user.getUserID());
            }
            if (existing.getUserName().equalsIgnoreCase(user.getUserName())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + user.getUserName());
            }
        }
    }

    private static void validateUser(User user) {
        Validator.requireNonNull(user, "Người dùng");
        if (!(user instanceof Admin) && !(user instanceof Member)) {
            throw new IllegalArgumentException("Chỉ hỗ trợ tài khoản Admin và Member!");
        }
        Validator.requireText(user.getUserID(), "Mã người dùng");
        Validator.requireText(user.getUserName(), "Tên đăng nhập");
        Validator.requireText(user.getPassword(), "Mật khẩu");
        Validator.requireText(user.getFullName(), "Họ tên");
    }

    @Override
    public void loadData() {
        List<String> lines = FileUtil.readLines(DATA_FILE);
        List<User> loaded = new ArrayList<>();
        for (String line : lines) {
            if (line.equals(HEADER)) {
                continue;
            }
            try {
                List<String> p = parseCsvLine(line);
                if (p.size() != 11) {
                    throw new IllegalArgumentException("Số cột khác 11");
                }
                User user;
                switch (p.get(0).toUpperCase()) {
                    case "ADMIN":
                        user = new Admin(p.get(1), p.get(2), p.get(3), p.get(4));
                        break;
                    case "MEMBER":
                        Member member = new Member(p.get(1), p.get(2), p.get(3), p.get(4));
                        if (!p.get(7).isEmpty()) {
                            member.setTier(p.get(7));
                        }
                        member.setBalance(Double.parseDouble(p.get(9)));
                        member.setUnpaidFine(Double.parseDouble(p.get(10)));
                        if (!"true".equalsIgnoreCase(p.get(8)) && !"false".equalsIgnoreCase(p.get(8))) {
                            throw new IllegalArgumentException("Trạng thái banned không hợp lệ");
                        }
                        member.setBanned(Boolean.parseBoolean(p.get(8)) ||
                                member.getUnpaidFine() > Member.BAN_FINE_THRESHOLD);
                        user = member;
                        break;
                    default:
                        throw new IllegalArgumentException("Vai trò không hỗ trợ: " + p.get(0));
                }
                if (!p.get(5).isEmpty()) {
                    user.setEmail(p.get(5));
                }
                if (!p.get(6).isEmpty()) {
                    user.setPhone(p.get(6));
                }
                validateUser(user);
                boolean duplicate = false;
                for (User existing : loaded) {
                    if (existing.getUserID().equalsIgnoreCase(user.getUserID()) ||
                            existing.getUserName().equalsIgnoreCase(user.getUserName())) {
                        duplicate = true;
                        break;
                    }
                }
                if (duplicate) {
                    throw new IllegalArgumentException("Trùng ID hoặc tên đăng nhập");
                }
                loaded.add(user);
            } catch (RuntimeException e) {
                System.err.println("Bỏ qua dòng tài khoản không hợp lệ: " + e.getMessage());
            }
        }
        users.clear();
        users.addAll(loaded);
    }

    @Override
    public boolean saveData() {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (User user : users) {
            validateUser(user);
            boolean memberType = user instanceof Member;
            Member member = memberType ? (Member) user : null;
            String[] fields = {
                memberType ? "MEMBER" : "ADMIN",
                user.getUserID(), user.getUserName(), user.getPassword(), user.getFullName(),
                user.getEmail(), user.getPhone(), memberType ? member.getTier() : "",
                memberType ? String.valueOf(member.isBanned()) : "false",
                memberType ? String.valueOf(member.getBalance()) : "0",
                memberType ? String.valueOf(member.getUnpaidFine()) : "0"
            };
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < fields.length; i++) {
                if (i > 0) {
                    line.append(';');
                }
                line.append(quoteCsvField(fields[i]));
            }
            lines.add(line.toString());
        }
        return FileUtil.writeLines(DATA_FILE, lines);
    }

    /** Bao dấu nháy kép để các trường có dấu ; hoặc dấu " vẫn lưu được. */
    private static String quoteCsvField(String value) {
        String field = value == null ? "" : value;
        if (field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không được chứa ký tự xuống dòng!");
        }
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ';' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        if (inQuotes) {
            throw new IllegalArgumentException("Dấu nháy kép trong CSV chưa đóng");
        }
        result.add(field.toString());
        return result;
    }
}
