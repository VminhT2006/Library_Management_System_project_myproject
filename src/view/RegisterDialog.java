package view;

import controller.LoginController;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.Member;

/**
 * Hộp thoại Đăng ký tài khoản độc giả mới (SRS FR-08, UC-01 luồng Guest).
 * Hỗ trợ phím Esc để đóng hộp thoại (SRS Mục 7.1).
 */
public class RegisterDialog extends JDialog {
    private final LoginController loginController;

    private JTextField txtFullName;
    private JTextField txtUserName;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;

    public RegisterDialog(JFrame parent, LoginController loginController) {
        super(parent, "Đăng ký tài khoản độc giả", true);
        this.loginController = loginController;
        setSize(400, 320);
        setLocationRelativeTo(parent);
        initComponents();
        UiUtil.bindEscapeToClose(getRootPane(), this::dispose);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField(16);
        txtUserName = new JTextField(16);
        txtPassword = new JPasswordField(16);
        txtConfirm = new JPasswordField(16);
        JButton btnOk = new JButton("Đăng ký");
        JButton btnCancel = new JButton("Hủy bỏ");

        String[] labels = {"Họ và tên:", "Tên đăng nhập:", "Mật khẩu:", "Xác nhận mật khẩu:"};
        JTextField[] fields = {txtFullName, txtUserName, txtPassword, txtConfirm};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        panel.add(btnOk, gbc);
        gbc.gridx = 1;
        panel.add(btnCancel, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnOk);

        btnOk.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> dispose());
    }

    private void handleRegister() {
        try {
            Member member = loginController.register(
                    txtFullName.getText(),
                    txtUserName.getText(),
                    new String(txtPassword.getPassword()),
                    new String(txtConfirm.getPassword()));
            JOptionPane.showMessageDialog(this,
                    "Đăng ký tài khoản thành công!\nMã độc giả của bạn: " + member.getUserID()
                            + "\nHãy đăng nhập để bắt đầu sử dụng hệ thống.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }
}
