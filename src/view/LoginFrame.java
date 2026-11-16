package view;

import controller.LoginController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import manager.DocumentManager;
import manager.TransactionManager;
import manager.UserManager;
import model.Admin;
import model.Member;
import model.User;

/**
 * Màn hình Đăng nhập – layout hai cột: trái gradient màu, phải form trắng.
 */
public class LoginFrame extends JFrame {
    private final UserManager userManager;
    private final DocumentManager documentManager;
    private final TransactionManager transactionManager;
    private final LoginController loginController;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame(UserManager userManager, DocumentManager documentManager,
                      TransactionManager transactionManager) {
        this.userManager = userManager;
        this.documentManager = documentManager;
        this.transactionManager = transactionManager;
        this.loginController = new LoginController(userManager);

        setTitle("Hệ thống Quản lý Thư viện");
        setSize(820, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Bảng trái: gradient xanh với logo ────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, UiUtil.PRIMARY_DARK,
                        getWidth(), getHeight(), UiUtil.SECONDARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(300, 500));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 24, 6, 24);

        // Icon sách (vẽ đơn giản bằng text emoji)
        JLabel icon = new JLabel("📚", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 24, 10, 24);
        panel.add(icon, gbc);

        JLabel title = new JLabel("Library", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 24, 2, 24);
        panel.add(title, gbc);

        JLabel title2 = new JLabel("Management", SwingConstants.CENTER);
        title2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title2.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 24, 2, 24);
        panel.add(title2, gbc);

        JLabel title3 = new JLabel("System", SwingConstants.CENTER);
        title3.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title3.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 24, 20, 24);
        panel.add(title3, gbc);

        JLabel sub = new JLabel("Hệ thống Quản lý Thư viện", SwingConstants.CENTER);
        sub.setFont(UiUtil.SMALL_FONT);
        sub.setForeground(new Color(255, 255, 255, 180));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 16, 0, 16);
        panel.add(sub, gbc);

        return panel;
    }

    // ── Bảng phải: form đăng nhập ─────────────────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UiUtil.SURFACE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 50, 6, 50);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chào mừng trở lại!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UiUtil.TEXT);
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 50, 4, 50);
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Đăng nhập vào tài khoản của bạn", SwingConstants.CENTER);
        lblSub.setFont(UiUtil.SMALL_FONT);
        lblSub.setForeground(UiUtil.TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 50, 20, 50);
        panel.add(lblSub, gbc);

        // Label + field: Tên đăng nhập
        gbc.insets = new Insets(4, 50, 2, 50);
        gbc.gridy = 2;
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(UiUtil.BOLD_FONT);
        lblUser.setForeground(UiUtil.TEXT);
        panel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(UiUtil.LABEL_FONT);
        txtUsername.setPreferredSize(new Dimension(260, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 50, 10, 50);
        panel.add(txtUsername, gbc);

        // Label + field: Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(UiUtil.BOLD_FONT);
        lblPass.setForeground(UiUtil.TEXT);
        gbc.gridy = 4;
        gbc.insets = new Insets(4, 50, 2, 50);
        panel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UiUtil.LABEL_FONT);
        txtPassword.setPreferredSize(new Dimension(260, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 50, 16, 50);
        panel.add(txtPassword, gbc);

        // Nút Đăng nhập
        JButton btnLogin = UiUtil.makeBtn("Đăng nhập", UiUtil.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(260, 42));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 50, 10, 50);
        panel.add(btnLogin, gbc);

        // Dòng đăng ký
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setBackground(UiUtil.SURFACE);
        JLabel lblOr = new JLabel("Chưa có tài khoản?");
        lblOr.setFont(UiUtil.SMALL_FONT);
        lblOr.setForeground(UiUtil.TEXT_MUTED);
        JButton btnRegister = new JButton("Đăng ký ngay");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegister.setForeground(UiUtil.PRIMARY);
        btnRegister.setBackground(UiUtil.SURFACE);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regRow.add(lblOr);
        regRow.add(btnRegister);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 50, 6, 50);
        panel.add(regRow, gbc);

        // Nút Duyệt khách
        JButton btnGuest = UiUtil.makeOutlineBtn("Duyệt danh mục với quyền Khách", UiUtil.TEXT_MUTED);
        btnGuest.setPreferredSize(new Dimension(260, 36));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 50, 20, 50);
        panel.add(btnGuest, gbc);

        // Events
        getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(e -> handleLogin());
        btnRegister.addActionListener(e -> new RegisterDialog(this, loginController).setVisible(true));
        btnGuest.addActionListener(e -> new GuestCatalogFrame(documentManager).setVisible(true));

        return panel;
    }

    private void handleLogin() {
        try {
            User user = loginController.login(txtUsername.getText(), new String(txtPassword.getPassword()));
            dispose();
            if (user instanceof Admin) {
                new AdminDashboardFrame((Admin) user, userManager, documentManager, transactionManager)
                        .setVisible(true);
            } else {
                new MemberDashboardFrame((Member) user, userManager, documentManager, transactionManager)
                        .setVisible(true);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocusInWindow();
        }
    }
}
