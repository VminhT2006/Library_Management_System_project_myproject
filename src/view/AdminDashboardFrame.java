package view;

import controller.DocumentController;
import controller.TransactionController;
import controller.WalletController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import manager.DocumentManager;
import manager.TransactionManager;
import manager.UserManager;
import model.Admin;
import model.Book;
import model.Document;
import model.EBook;
import model.Magazine;
import model.Member;
import model.Thesis;
import model.Transaction;
import model.User;
import util.DateUtil;
import util.FileUtil;
import util.FormatUtil;
import util.Validator;

/**
 * Cửa sổ Quản trị viên (SRS Mục 6.2 - FR-03, FR-06, FR-09, FR-13, UC-02, UC-05, UC-08).
 * Gồm 3 tab: Quản lý Kho Tài liệu, Quản lý Giao dịch & Trả sách, Báo cáo & Thống kê.
 */
public class AdminDashboardFrame extends JFrame {
    private static final String[] DOC_TYPES = {"Sách", "Tạp chí", "Luận văn", "E-Book"};

    private final UserManager userManager;
    private final DocumentManager documentManager;
    private final TransactionManager transactionManager;
    private final DocumentController documentController;
    private final TransactionController transactionController;
    private final WalletController walletController;

    // --- Tab 1: Quản lý tài liệu ---
    private JComboBox<String> cboType;
    private JTextField txtId;
    private JTextField txtTitle;
    private JTextField txtYear;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField txtAuthor;
    private JTextField txtPublisher;
    private JTextField txtBookQty;
    private JTextField txtIssue;
    private JTextField txtMonth;
    private JTextField txtMagQty;
    private JTextField txtUniversity;
    private JTextField txtAdvisor;
    private JTextField txtMajor;
    private JTextField txtUrl;
    private JTextField txtSize;
    private JTextField txtFormat;
    private JTextField txtSearch;
    private JComboBox<String> cboCriteria;
    private DefaultTableModel docModel;
    private JTable docTable;
    private JLabel lblDocStatus;

    // --- Tab 2: Giao dịch ---
    private JComboBox<String> cboStatusFilter;
    private DefaultTableModel transModel;
    private JTable transTable;

    // --- Tab 3: Thống kê ---
    private JLabel lblKpiTotalDocs;
    private JLabel lblKpiBorrowed;
    private JLabel lblKpiOverdue;
    private JLabel lblKpiFine;
    private DefaultTableModel topModel;
    private DefaultTableModel debtorModel;

    public AdminDashboardFrame(Admin admin, UserManager userManager,
                               DocumentManager documentManager, TransactionManager transactionManager) {
        this.userManager = userManager;
        this.documentManager = documentManager;
        this.transactionManager = transactionManager;
        this.documentController = new DocumentController(documentManager);
        this.transactionController = new TransactionController(documentManager, userManager, transactionManager);
        this.walletController = new WalletController(userManager);

        setTitle("Quản trị Thư viện - Xin chào " + admin.getFullName());
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UiUtil.BG);

        // ── Navbar ─────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UiUtil.NAV_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        topBar.setPreferredSize(new java.awt.Dimension(0, 52));

        JLabel lblBrand = new JLabel("📚  Library Management System");
        lblBrand.setFont(UiUtil.NAV_FONT);
        lblBrand.setForeground(java.awt.Color.WHITE);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightNav.setOpaque(false);
        JLabel lblWelcome = new JLabel("👤  " + admin.getFullName() + "  |  " + admin.getUserID());
        lblWelcome.setFont(UiUtil.SMALL_FONT);
        lblWelcome.setForeground(UiUtil.NAV_TEXT);
        JButton btnLogout = UiUtil.makeBtn("Đăng xuất", UiUtil.COLOR_DANGER);
        btnLogout.addActionListener(e -> handleLogout());
        rightNav.add(lblWelcome);
        rightNav.add(btnLogout);

        topBar.add(lblBrand, BorderLayout.WEST);
        topBar.add(rightNav, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UiUtil.BOLD_FONT);
        tabs.setBackground(UiUtil.BG);
        tabs.addTab("  📂  Kho Tài liệu  ", buildDocumentTab());
        tabs.addTab("  🔄  Giao dịch & Trả sách  ", buildTransactionTab());
        tabs.addTab("  📊  Báo cáo & Thống kê  ", buildStatisticsTab());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) {
                refreshStatistics();
            }
        });
        add(tabs, BorderLayout.CENTER);

        refreshDocumentTable();
        refreshTransactionTable();
        refreshStatistics();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?", "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(userManager, documentManager, transactionManager).setVisible(true);
        }
    }

    // =========================================================================
    // TAB 1: QUẢN LÝ KHO TÀI LIỆU (FR-03, FR-14, UC-02)
    // =========================================================================

    private JPanel buildDocumentTab() {
        JPanel tab = new JPanel(new BorderLayout(5, 5));

        // ----- Form nhập liệu (nửa trên) -----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboType = new JComboBox<>(DOC_TYPES);
        txtId = new JTextField(12);
        txtTitle = new JTextField(12);
        txtYear = new JTextField(12);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Loại tài liệu:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboType, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Mã (ID):"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTitle, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Năm xuất bản:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtYear, gbc);

        // Form động theo loại tài liệu (FR-14): CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        txtAuthor = new JTextField(10);
        txtPublisher = new JTextField(10);
        txtBookQty = new JTextField(10);
        cardPanel.add(buildCard("Tác giả:", txtAuthor, "Nhà xuất bản:", txtPublisher, "Số lượng:", txtBookQty),
                DOC_TYPES[0]);
        txtIssue = new JTextField(10);
        txtMonth = new JTextField(10);
        txtMagQty = new JTextField(10);
        cardPanel.add(buildCard("Số phát hành:", txtIssue, "Tháng XB (1-12):", txtMonth, "Số lượng:", txtMagQty),
                DOC_TYPES[1]);
        txtUniversity = new JTextField(10);
        txtAdvisor = new JTextField(10);
        txtMajor = new JTextField(10);
        cardPanel.add(buildCard("Trường ĐH:", txtUniversity, "GV hướng dẫn:", txtAdvisor, "Chuyên ngành:", txtMajor),
                DOC_TYPES[2]);
        txtUrl = new JTextField(10);
        txtSize = new JTextField(10);
        txtFormat = new JTextField(10);
        cardPanel.add(buildCard("Link đọc (URL):", txtUrl, "Dung lượng (MB):", txtSize, "Định dạng:", txtFormat),
                DOC_TYPES[3]);
        cboType.addActionListener(e -> cardLayout.show(cardPanel, String.valueOf(cboType.getSelectedItem())));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        formPanel.add(cardPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        buttonPanel.setBackground(UiUtil.BG);
        JButton btnAdd    = UiUtil.makeBtn("+ Thêm mới",     UiUtil.COLOR_OK);
        JButton btnUpdate = UiUtil.makeBtn("✏  Cập nhật",    UiUtil.PRIMARY);
        JButton btnDelete = UiUtil.makeBtn("🗑  Xóa",         UiUtil.COLOR_DANGER);
        JButton btnClear  = UiUtil.makeBtn("↺  Làm mới Form", UiUtil.TEXT_MUTED);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        gbc.gridy = 3;
        formPanel.add(buttonPanel, gbc);

        // ----- Live Search + Bảng (nửa dưới) -----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboCriteria = new JComboBox<>(new String[]{"Tất cả", "Tiêu đề", "Tác giả", "Mã ID"});
        txtSearch = new JTextField(24);
        searchPanel.add(new JLabel("Tiêu chí:"));
        searchPanel.add(cboCriteria);
        searchPanel.add(new JLabel("Tìm kiếm tức thì:"));
        searchPanel.add(txtSearch);

        docModel = UiUtil.createReadOnlyModel(UiUtil.documentColumns());
        docTable = new JTable(docModel);
        UiUtil.styleTable(docTable);
        lblDocStatus = new JLabel(" ");
        lblDocStatus.setFont(UiUtil.SMALL_FONT);
        lblDocStatus.setForeground(UiUtil.TEXT_MUTED);
        lblDocStatus.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UiUtil.BG);
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(UiUtil.makeScrollPane(docTable), BorderLayout.CENTER);
        bottomPanel.add(lblDocStatus, BorderLayout.SOUTH);

        tab.add(formPanel, BorderLayout.NORTH);
        tab.add(bottomPanel, BorderLayout.CENTER);

        // Sự kiện
        UiUtil.attachLiveSearch(txtSearch, this::refreshDocumentTable);
        cboCriteria.addActionListener(e -> refreshDocumentTable());
        docTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
        btnAdd.addActionListener(e -> handleAddDocument());
        btnUpdate.addActionListener(e -> handleUpdateDocument());
        btnDelete.addActionListener(e -> handleDeleteDocument());
        btnClear.addActionListener(e -> clearForm());

        return tab;
    }

    private JPanel buildCard(String l1, JTextField f1, String l2, JTextField f2, String l3, JTextField f3) {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT));
        card.add(new JLabel(l1));
        card.add(f1);
        card.add(new JLabel(l2));
        card.add(f2);
        card.add(new JLabel(l3));
        card.add(f3);
        return card;
    }

    private void refreshDocumentTable() {
        docModel.setRowCount(0);
        for (Document doc : documentController.search(
                txtSearch.getText(), String.valueOf(cboCriteria.getSelectedItem()))) {
            docModel.addRow(UiUtil.documentRow(doc));
        }
        lblDocStatus.setText("Tìm thấy " + docModel.getRowCount() + " kết quả phù hợp");
    }

    /** Nhấp chuột vào một dòng -> dữ liệu tự động điền lên Form (UC-02). */
    private void fillFormFromSelection() {
        int row = docTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Document doc = documentController.findById(String.valueOf(docModel.getValueAt(row, 0)));
        if (doc == null) {
            return;
        }
        txtId.setText(doc.getId());
        txtTitle.setText(doc.getTitle());
        txtYear.setText(String.valueOf(doc.getPublicationYear()));
        if (doc instanceof Book) {
            Book b = (Book) doc;
            cboType.setSelectedItem(DOC_TYPES[0]);
            txtAuthor.setText(b.getAuthor());
            txtPublisher.setText(b.getPublisher());
            txtBookQty.setText(String.valueOf(b.getQuantity()));
        } else if (doc instanceof Magazine) {
            Magazine m = (Magazine) doc;
            cboType.setSelectedItem(DOC_TYPES[1]);
            txtIssue.setText(String.valueOf(m.getIssueNumber()));
            txtMonth.setText(String.valueOf(m.getPublishMonth()));
            txtMagQty.setText(String.valueOf(m.getQuantity()));
        } else if (doc instanceof Thesis) {
            Thesis t = (Thesis) doc;
            cboType.setSelectedItem(DOC_TYPES[2]);
            txtUniversity.setText(t.getUniversity());
            txtAdvisor.setText(t.getAdvisor());
            txtMajor.setText(t.getMajor());
        } else if (doc instanceof EBook) {
            EBook e = (EBook) doc;
            cboType.setSelectedItem(DOC_TYPES[3]);
            txtUrl.setText(e.getDownloadUrl());
            txtSize.setText(String.valueOf(e.getFileSizeMb()));
            txtFormat.setText(e.getFormat());
        }
    }

    /** Dựng đối tượng Document từ Form theo loại được chọn (FR-14, Validation FR-03). */
    private Document buildDocumentFromForm() {
        String id = Validator.requireText(txtId.getText(), "Mã tài liệu (ID)");
        String title = Validator.requireText(txtTitle.getText(), "Tiêu đề");
        int year = Validator.parseInt(txtYear.getText(), "Năm xuất bản");
        String type = String.valueOf(cboType.getSelectedItem());

        switch (type) {
            case "Sách":
                return new Book(id, title, year,
                        txtAuthor.getText(), txtPublisher.getText(),
                        Validator.parseNonNegativeInt(txtBookQty.getText(), "Số lượng sách"));
            case "Tạp chí":
                return new Magazine(id, title, year,
                        Validator.parsePositiveInt(txtIssue.getText(), "Số phát hành"),
                        Validator.parseInt(txtMonth.getText(), "Tháng xuất bản"),
                        Validator.parseNonNegativeInt(txtMagQty.getText(), "Số lượng tạp chí"));
            case "Luận văn":
                return new Thesis(id, title, year,
                        txtUniversity.getText(), txtAdvisor.getText(), txtMajor.getText());
            default:
                return new EBook(id, title, year,
                        txtUrl.getText(),
                        Validator.parsePositiveDouble(txtSize.getText(), "Dung lượng file (MB)"),
                        txtFormat.getText());
        }
    }

    private void handleAddDocument() {
        try {
            Document doc = buildDocumentFromForm();
            documentController.addDocument(doc);
            refreshDocumentTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm tài liệu mới thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateDocument() {
        try {
            Document newDoc = buildDocumentFromForm();
            Document oldDoc = documentController.findById(newDoc.getId());
            if (oldDoc == null) {
                throw new IllegalArgumentException("Không tìm thấy tài liệu cần cập nhật: " + newDoc.getId());
            }
            // Bảo toàn hàng đợi đặt trước khi cập nhật Sách
            if (oldDoc instanceof Book && newDoc instanceof Book) {
                ((Book) newDoc).setWaitingList(((Book) oldDoc).getWaitingList());
            }
            documentController.updateDocument(newDoc);
            refreshDocumentTable();
            JOptionPane.showMessageDialog(this, "Cập nhật tài liệu thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteDocument() {
        int row = docTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài liệu cần xóa trên bảng!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = String.valueOf(docModel.getValueAt(row, 0));
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài liệu [" + id + "] khỏi hệ thống?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            documentController.deleteDocument(id);
            refreshDocumentTable();
            clearForm();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        JTextField[] all = {txtId, txtTitle, txtYear, txtAuthor, txtPublisher, txtBookQty,
                txtIssue, txtMonth, txtMagQty, txtUniversity, txtAdvisor, txtMajor,
                txtUrl, txtSize, txtFormat};
        for (JTextField field : all) {
            field.setText("");
        }
        docTable.clearSelection();
    }

    // =========================================================================
    // TAB 2: QUẢN LÝ GIAO DỊCH & TRẢ SÁCH (FR-06, FR-09, UC-05)
    // =========================================================================

    private JPanel buildTransactionTab() {
        JPanel tab = new JPanel(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboStatusFilter = new JComboBox<>(new String[]{
                TransactionManager.FILTER_ALL, TransactionManager.FILTER_BORROWED,
                TransactionManager.FILTER_RETURNED, TransactionManager.FILTER_OVERDUE});
        JButton btnReturn  = UiUtil.makeBtn("✔  Xác nhận trả sách", UiUtil.COLOR_OK);
        JButton btnCollect = UiUtil.makeBtn("💰  Thu phí phạt",     UiUtil.COLOR_WARNING);
        topPanel.add(new JLabel("Lọc trạng thái:"));
        topPanel.add(cboStatusFilter);
        topPanel.add(btnReturn);
        topPanel.add(btnCollect);

        transModel = UiUtil.createReadOnlyModel(new String[]{
                "Mã GD", "Mã độc giả", "Mã tài liệu", "Ngày mượn", "Hạn trả",
                "Ngày trả", "Trạng thái", "Tiền phạt", "Đã gia hạn"});
        transTable = new JTable(transModel);
        UiUtil.styleTableWithOverdue(transTable, 6);

        tab.add(topPanel, BorderLayout.NORTH);
        tab.add(UiUtil.makeScrollPane(transTable), BorderLayout.CENTER);

        cboStatusFilter.addActionListener(e -> refreshTransactionTable());
        btnReturn.addActionListener(e -> handleReturn(transTable, transModel));
        btnCollect.addActionListener(e -> handleCollectFine());

        return tab;
    }

    private void refreshTransactionTable() {
        transModel.setRowCount(0);
        for (Transaction tx : transactionManager.filterByStatus(
                String.valueOf(cboStatusFilter.getSelectedItem()), null)) {
            transModel.addRow(transactionRow(tx));
        }
    }

    static Object[] transactionRow(Transaction tx) {
        return new Object[]{
                tx.getTransactionID(), tx.getMemberID(), tx.getDocumentID(),
                DateUtil.format(tx.getBorrowDate()), DateUtil.format(tx.getDueDate()),
                DateUtil.format(tx.getReturnDate()), TransactionManager.getDisplayStatus(tx),
                FormatUtil.formatVND(tx.getFineAmount()), tx.isRenewed() ? "Có" : "Không"};
    }

    /** Luồng trả sách dùng chung với MemberDashboardFrame (UC-05). */
    private void handleReturn(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần trả trên bảng!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String txId = String.valueOf(model.getValueAt(row, 0));
        try {
            double fine = transactionController.previewFine(txId);
            boolean payFromWallet = false;
            if (fine > 0) {
                long days = transactionController.previewOverdueDays(txId);
                int choice = JOptionPane.showConfirmDialog(this,
                        "Bạn trả trễ " + days + " ngày. Tiền phạt: " + FormatUtil.formatVND(fine)
                                + ".\nTrừ tiền trực tiếp từ ví độc giả?",
                        "Xác nhận thanh toán tiền phạt", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    return;
                }
                payFromWallet = (choice == JOptionPane.YES_OPTION);
            }
            TransactionController.ReturnResult result = transactionController.returnDocument(txId, payFromWallet);
            StringBuilder msg = new StringBuilder();
            if (result.fine <= 0) {
                msg.append("Trả sách đúng hạn thành công!");
            } else {
                msg.append("Trả sách thành công! Tiền phạt: ").append(FormatUtil.formatVND(result.fine));
                msg.append(result.paidFullyFromWallet
                        ? "\nĐã trừ đủ tiền phạt từ ví độc giả."
                        : "\nVí không đủ số dư, phần thiếu đã được ghi nhận vào nợ tích lũy!");
            }
            if (result.heldForMemberId != null) {
                msg.append("\nSách được giữ riêng cho độc giả [").append(result.heldForMemberId)
                        .append("] trong 48h (người đầu hàng đợi đặt trước).");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Kết quả trả sách",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshTransactionTable();
            refreshDocumentTable();
            refreshStatistics();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi trả sách", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCollectFine() {
        int row = transTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch của độc giả cần thu phạt!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String memberId = String.valueOf(transModel.getValueAt(row, 1));
        User user = userManager.findById(memberId);
        if (!(user instanceof Member)) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy độc giả: " + memberId,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double collected = walletController.collectUnpaidFine((Member) user);
            JOptionPane.showMessageDialog(this,
                    "Đã thu " + FormatUtil.formatVND(collected) + " nợ phạt từ ví độc giả ["
                            + memberId + "].\nTài khoản đã được mở khóa (nếu trước đó bị khóa).",
                    "Thu phí phạt thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshStatistics();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể thu phạt", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // TAB 3: BÁO CÁO & THỐNG KÊ - Java Stream API (FR-13, UC-08)
    // =========================================================================

    private JPanel buildStatisticsTab() {
        JPanel tab = new JPanel(new BorderLayout(8, 8));

        // Hàng trên: 4 thẻ KPI màu sắc
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        kpiPanel.setBackground(UiUtil.BG);
        kpiPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        lblKpiTotalDocs = new JLabel("0", SwingConstants.CENTER);
        lblKpiBorrowed  = new JLabel("0", SwingConstants.CENTER);
        lblKpiOverdue   = new JLabel("0", SwingConstants.CENTER);
        lblKpiFine      = new JLabel("0", SwingConstants.CENTER);
        kpiPanel.add(UiUtil.makeKpiCard("Tổng đầu tài liệu",   lblKpiTotalDocs, UiUtil.PRIMARY));
        kpiPanel.add(UiUtil.makeKpiCard("Đang mượn",           lblKpiBorrowed,  UiUtil.COLOR_OK));
        kpiPanel.add(UiUtil.makeKpiCard("Lượt mượn quá hạn",   lblKpiOverdue,   UiUtil.COLOR_DANGER));
        kpiPanel.add(UiUtil.makeKpiCard("Doanh thu phạt (đ)",  lblKpiFine,      UiUtil.COLOR_WARNING));

        // Hàng dưới: 2 bảng thống kê
        topModel    = UiUtil.createReadOnlyModel(new String[]{"Mã tài liệu", "Tiêu đề", "Số lượt mượn"});
        debtorModel = UiUtil.createReadOnlyModel(new String[]{"Mã độc giả", "Họ tên", "Số GD quá hạn", "Nợ phạt"});
        JTable topTable    = new JTable(topModel);
        JTable debtorTable = new JTable(debtorModel);
        UiUtil.styleTable(topTable);
        UiUtil.styleTableWithOverdue(debtorTable, 2);
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        tablesPanel.setBackground(UiUtil.BG);
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        JScrollPane topScroll    = UiUtil.makeScrollPane(topTable);
        JScrollPane debtorScroll = UiUtil.makeScrollPane(debtorTable);
        JPanel topWrap = wrapWithTitle("Top 3 sách mượn nhiều nhất", topScroll);
        JPanel debtorWrap = wrapWithTitle("Độc giả nợ sách quá hạn", debtorScroll);
        tablesPanel.add(topWrap);
        tablesPanel.add(debtorWrap);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        bottomPanel.setBackground(UiUtil.BG);
        JButton btnRefresh = UiUtil.makeBtn("↺  Làm mới", UiUtil.PRIMARY);
        JButton btnExport  = UiUtil.makeBtn("📄  Xuất báo cáo", UiUtil.COLOR_OK);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnExport);

        tab.add(kpiPanel, BorderLayout.NORTH);
        tab.add(tablesPanel, BorderLayout.CENTER);
        tab.add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshStatistics());
        btnExport.addActionListener(e -> handleExportReport());

        return tab;
    }

    /** Bọc một component vào panel có tiêu đề đẹp. */
    private static JPanel wrapWithTitle(String title, java.awt.Component inner) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(UiUtil.SURFACE);
        p.setBorder(BorderFactory.createLineBorder(UiUtil.BORDER, 1));
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(UiUtil.BOLD_FONT);
        lbl.setForeground(UiUtil.TEXT);
        lbl.setBackground(UiUtil.BG);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        p.add(lbl, BorderLayout.NORTH);
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    private void refreshStatistics() {
        lblKpiTotalDocs.setText(String.valueOf(documentManager.getDocuments().size()));
        lblKpiBorrowed.setText(String.valueOf(transactionManager.countBorrowed()));
        lblKpiOverdue.setText(String.valueOf(transactionManager.countOverdue()));
        lblKpiFine.setText(FormatUtil.formatVND(transactionManager.totalCollectedFine()));

        topModel.setRowCount(0);
        for (Map.Entry<String, Long> entry : transactionManager.topBorrowedDocuments(3).entrySet()) {
            Document doc = documentManager.findById(entry.getKey());
            topModel.addRow(new Object[]{entry.getKey(),
                    doc != null ? doc.getTitle() : "(đã xóa)", entry.getValue()});
        }

        debtorModel.setRowCount(0);
        for (String memberId : transactionManager.overdueMemberIds()) {
            User user = userManager.findById(memberId);
            String fullName = user != null ? user.getFullName() : "(không rõ)";
            String debt = (user instanceof Member)
                    ? FormatUtil.formatVND(((Member) user).getUnpaidFine()) : "-";
            debtorModel.addRow(new Object[]{memberId, fullName,
                    transactionManager.countOverdueOfMember(memberId), debt});
        }
    }

    /** Xuất báo cáo thống kê ra file văn bản (UC-08 bước 3). */
    private void handleExportReport() {
        List<String> lines = new ArrayList<>();
        lines.add("=== BÁO CÁO THỐNG KÊ THƯ VIỆN - " + DateUtil.format(java.time.LocalDate.now()) + " ===");
        lines.add("Tổng đầu sách        : " + documentManager.getDocuments().size());
        lines.add("Sách đang mượn       : " + transactionManager.countBorrowed());
        lines.add("Lượt mượn quá hạn    : " + transactionManager.countOverdue());
        lines.add("Doanh thu phạt       : " + FormatUtil.formatVND(transactionManager.totalCollectedFine()));
        lines.add("");
        lines.add("--- Top 3 sách mượn nhiều nhất ---");
        for (Map.Entry<String, Long> entry : transactionManager.topBorrowedDocuments(3).entrySet()) {
            Document doc = documentManager.findById(entry.getKey());
            lines.add("  [" + entry.getKey() + "] "
                    + (doc != null ? doc.getTitle() : "(đã xóa)") + " - " + entry.getValue() + " lượt");
        }
        lines.add("");
        lines.add("--- Độc giả nợ sách quá hạn ---");
        for (String memberId : transactionManager.overdueMemberIds()) {
            User user = userManager.findById(memberId);
            lines.add("  [" + memberId + "] " + (user != null ? user.getFullName() : "(không rõ)")
                    + " - " + transactionManager.countOverdueOfMember(memberId) + " giao dịch quá hạn");
        }
        String path = "data/bao_cao_thong_ke.txt";
        if (FileUtil.writeLines(path, lines)) {
            JOptionPane.showMessageDialog(this, "Đã xuất báo cáo ra file: " + path,
                    "Xuất báo cáo thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể ghi file báo cáo!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
