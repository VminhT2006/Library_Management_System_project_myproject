package model;

/**
 * Lớp Thesis đại diện cho Luận văn tốt nghiệp, kế thừa từ Document (SRS Mục 5.2).
 * Quy tắc nghiệp vụ: Luận văn chỉ được đọc tại chỗ trong thư viện (canBorrow() luôn trả về false).
 */
public class Thesis extends Document {
    private String university;
    private String advisor;
    private String major;

    public Thesis() {
        super();
    }

    public Thesis(String id, String title, int publicationYear, String university, String advisor, String major) {
        super(id, title, publicationYear);
        setUniversity(university);
        setAdvisor(advisor);
        setMajor(major);
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        if (university == null || university.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên trường đại học không được để trống!");
        }
        this.university = university.trim();
    }

    public String getAdvisor() {
        return advisor;
    }

    public void setAdvisor(String advisor) {
        if (advisor == null || advisor.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên giảng viên hướng dẫn không được để trống!");
        }
        this.advisor = advisor.trim();
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("Chuyên ngành không được để trống!");
        }
        this.major = major.trim();
    }

    @Override
    public boolean canBorrow() {
        return false;
    }

    @Override
    public double calculateLateFee(int days) {
        return 0.0;
    }

    @Override
    public String getTypeName() {
        return "Luận văn";
    }
}
