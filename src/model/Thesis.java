package model;

import util.Validator;

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
        this.university = Validator.requireText(university, "Tên trường đại học");
    }

    public String getAdvisor() {
        return advisor;
    }

    public void setAdvisor(String advisor) {
        this.advisor = Validator.requireText(advisor, "Tên giảng viên hướng dẫn");
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = Validator.requireText(major, "Chuyên ngành");
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
