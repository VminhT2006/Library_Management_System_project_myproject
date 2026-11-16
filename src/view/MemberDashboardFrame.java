package view;

import controller.TransactionController;
import controller.WalletController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.table.DefaultTableModel;
import manager.DocumentManager;
import manager.TransactionManager;
import manager.UserManager;
import model.Book;
import model.Document;
import model.Member;
import model.Transaction;
import strategy.SortByQuantityDescending;
import strategy.SortByTitleAscending;
import strategy.SortByYearDescending;
import util.DateUtil;
import util.FormatUtil;
import util.Validator;

/**
 * Cửa sổ Độc giả (SRS Mục 6.3 - FR-05, FR-10, FR-11, FR-12, FR-15,
 * UC-04, UC-05, UC-06, UC-07, UC-09).
 */
public class MemberDashboardFrame extends JFrame {
    private static final String SORT_NONE = "-- Không sắp xếp --";
    private static final String SORT_TITLE = "Tên sách A-Z";
    private static final String SORT_YEAR = "Năm xuất bản mới nhất";
    private static final String SORT_QUANTITY = "Số lượng tồn kho nhiều nhất";

    private final Member member;
    private final UserManager userManager;
    private final DocumentManager documentManager;
    private final TransactionManager transactionManager;
    private final TransactionController transactionController;
    private final WalletController walletController;

    // Header
    private JLabel lblBalance;
    private JLabel lblDebt;
    private JLabel lblBadge;
    private JButton btnBorrow;

    // Tab 1: Tra cứu & Mượn
    private JTextField txtSearch;
    private JComboBox<String> cboCriteria;
    private JComboBox<String> cboSort;
    private DefaultTableModel docModel;
    private JTable docTable;
    private JLabel lblDocStatus;

    // Tab 2: Sách đang mượn & Lịch sử
    private JComboBox<String> cboStatusFilter;
    private DefaultTableModel transModel;
    private JTable transTable;

    public MemberDashboardFrame(Member member, UserManager userManager,
                                DocumentManager documentManager, TransactionManager transactionManager) {
        this.member = member;
        this.userManager = userManager;
        this.documentManager = documentManager;
        this.transactionManager = transactionManager;
        this.transactionController = new TransactionController(documentManager, userManager, transactionManager);
        this.walletController = new WalletController(userManager);

        setTitle("Thư viện Độc giả - Xin chào " + member.getFullName());
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UiUtil.BG);

        add(buildHeaderPanel(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UiUtil.BOLD_FONT);
        tabs.setBackground(UiUtil.BG);
        tabs.addTab("  🔍  Tra cứu & Mượn  ", buildCatalogTab());
        tabs.addTab("  📋  Lịch sử cá nhân  ", buildMyLoansTab());
        add(tabs, BorderLayout.CENTER);

        refreshHeader();
        refreshDocumentTable();
        refreshTransactionTable();
    }

    // =========================================================================
    // HEADER: THÔNG TIN CÁ NHÂN & VÍ TIỀN (FR-15, UC-09)
    // =========================================================================

    private JPanel buildHeaderPanel() {
        // Navbar tối
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(UiUtil.NAV_BG);
        navbar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        navbar.setPreferredSize(new java.awt.Dimension(0, 52));

        JLabel lblBrand = new JLabel("📚  Library Management System");
        lblBrand.setFont(UiUtil.NAV_FONT);
        lblBrand.setForeground(java.awt.Color.WHITE);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightNav.setOpaque(false);
        JLabel lblUser = new JLabel("👤  " + member.getFullName() + "  |  " + member.getUserID());
        lblUser.setFont(UiUtil.SMALL_FONT);
        lblUser.setForeground(UiUtil.NAV_TEXT);
        JButton btnLogout = UiUtil.makeBtn("Đăng xuất", UiUtil.COLOR_DANGER);
        btnLogout.addActionListener(e -> handleLogout());
        rightNav.add(lblUser);
        rightNav.add(btnLogout);
        navbar.add(lblBrand, BorderLayout.WEST);
        navbar.add(rightNav, BorderLayout.EAST);

        // Strip thông tin tài khoản
        JPanel infoBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        infoBar.setBackground(UiUtil.SURFACE);
        infoBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiUtil.BORDER));

        lblBalance = new JLabel();
        lblBalance.setFont(UiUtil.BOLD_FONT);
        lblBalance.setForeground(UiUtil.COLOR_OK);
        lblDebt = new JLabel();
        lblDebt.setFont(UiUtil.BOLD_FONT);
        lblDebt.setForeground(UiUtil.COLOR_DANGER);
        lblBadge = new JLabel();
        lblBadge.setFont(UiUtil.SMALL_FONT);
        lblBadge.setForeground(java.awt.Color.WHITE);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JLabel lblTier = new JLabel("Hạng: " + member.getTier());
        lblTier.setFont(UiUtil.LABEL_FONT);
        lblTier.setForeground(UiUtil.TEXT_MUTED);

        JButton btnDeposit = UiUtil.makeBtn("+ Nạp tiền", UiUtil.PRIMARY);
        btnDeposit.addActionListener(e -> handleDeposit());

        infoBar.add(lblBalance);
        infoBar.add(new JLabel("  |  "));
        infoBar.add(lblDebt);
        infoBar.add(new JLabel("  |  "));
        infoBar.add(lblTier);
        infoBar.add(new JLabel("  |  "));
        infoBar.add(lblBadge);
        infoBar.add(btnDeposit);

        JPanel header = new JPanel(new BorderLayout());
        header.add(navbar, BorderLayout.NORTH);
        header.add(infoBar, BorderLayout.SOUTH);
        return header;
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

    private void refreshHeader() {
        lblBalance.setText("Số dư ví: " + FormatUtil.formatVND(member.getBalance()));
        lblDebt.setText("Nợ phạt: " + FormatUtil.formatVND(member.getUnpaidFine()));
        if (member.isBanned()) {
            lblBadge.setText("BỊ KHÓA");
            lblBadge.setBackground(UiUtil.COLOR_DANGER);
        } else {
            lblBadge.setText("HOẠT ĐỘNG");
            lblBadge.setBackground(UiUtil.COLOR_OK);
        }
        lblBadge.setForeground(java.awt.Color.WHITE);
        if (btnBorrow != null) {
            btnBorrow.setEnabled(!member.isBanned()); // FR-15: khóa nút Mượn khi bị cấm
        }
    }

    /** UC-09: Nạp tiền qua JOptionPane.showInputDialog. */
    private void handleDeposit() {
        String input = JOptionPane.showInputDialog(this,
                "Nhập số tiền muốn nạp vào ví (VNĐ):", "Nạp tiền vào ví", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }
        try {
            double amount = Validator.parsePositiveDouble(input, "Số tiền nạp");
            boolean unlocked = walletController.deposit(member, amount);
            String msg = "Nạp tiền thành công!\nSố dư ví hiện tại: " + FormatUtil.formatVND(member.getBalance());
            if (unlocked) {
                msg += "\nNợ phạt đã được thanh toán hết - Tài khoản của bạn đã được MỞ KHÓA!";
            }
            JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshHeader();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Số tiền nạp không hợp lệ! Vui lòng nhập số nguyên dương.",
                    "Lỗi nạp tiền", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // TAB 1: TRA CỨU & MƯỢN TÀI LIỆU (FR-04, FR-05, FR-11, FR-12)
    // =========================================================================

    private JPanel buildCatalogTab() {
        JPanel tab = new JPanel(new BorderLayout(5, 5));
        tab.setBackground(UiUtil.BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(UiUtil.SURFACE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiUtil.BORDER));
        cboCriteria = new JComboBox<>(new String[]{"Tất cả", "Tiêu đề", "Tác giả", "Mã ID"});
        cboCriteria.setFont(UiUtil.LABEL_FONT);
        txtSearch = new JTextField(18);
        txtSearch.setFont(UiUtil.LABEL_FONT);
        cboSort = new JComboBox<>(new String[]{SORT_NONE, SORT_TITLE, SORT_YEAR, SORT_QUANTITY});
        cboSort.setFont(UiUtil.LABEL_FONT);
        btnBorrow = UiUtil.makeBtn("📖  Mượn tài liệu", UiUtil.COLOR_OK);
        JLabel lTieuChi = new JLabel("Tiêu chí:");
        lTieuChi.setFont(UiUtil.LABEL_FONT);
        JLabel lTimKiem = new JLabel("Tìm kiếm:");
        lTimKiem.setFont(UiUtil.LABEL_FONT);
        JLabel lSapXep = new JLabel("Sắp xếp:");
        lSapXep.setFont(UiUtil.LABEL_FONT);
        toolbar.add(lTieuChi);
        toolbar.add(cboCriteria);
        toolbar.add(lTimKiem);
        toolbar.add(txtSearch);
        toolbar.add(lSapXep);
        toolbar.add(cboSort);
        toolbar.add(btnBorrow);

        docModel = UiUtil.createReadOnlyModel(UiUtil.documentColumns());
        docTable = new JTable(docModel);
        UiUtil.styleTable(docTable);
        lblDocStatus = new JLabel(" ");
        lblDocStatus.setFont(UiUtil.SMALL_FONT);
        lblDocStatus.setForeground(UiUtil.TEXT_MUTED);
        lblDocStatus.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        tab.add(toolbar, BorderLayout.NORTH);
        tab.add(UiUtil.makeScrollPane(docTable), BorderLayout.CENTER);
        tab.add(lblDocStatus, BorderLayout.SOUTH);

        UiUtil.attachLiveSearch(txtSearch, this::refreshDocumentTable);
        cboCriteria.addActionListener(e -> refreshDocumentTable());
        cboSort.addActionListener(e -> refreshDocumentTable());
        btnBorrow.addActionListener(e -> handleBorrow());

        return tab;
    }

    /**
     * Làm mới bảng tài liệu: lọc Live Search rồi áp dụng Strategy Pattern sắp xếp Sách (FR-11).
     * Sách được sắp theo chiến lược chọn trên ComboBox, các loại tài liệu khác giữ nguyên phía sau.
     */
    private void refreshDocumentTable() {
        List<Document> filtered = documentManager.searchDocuments(
                txtSearch.getText(), String.valueOf(cboCriteria.getSelectedItem()));

        String sortChoice = String.valueOf(cboSort.getSelectedItem());
        if (!SORT_NONE.equals(sortChoice)) {
            List<Book> books = new ArrayList<>();
            List<Document> others = new ArrayList<>();
            for (Document doc : filtered) {
                if (doc instanceof Book) {
                    books.add((Book) doc);
                } else {
                    others.add(doc);
                }
            }
            switch (sortChoice) {
                case SORT_TITLE:
                    documentManager.setSorter(new SortByTitleAscending());
                    break;
                case SORT_YEAR:
                    documentManager.setSorter(new SortByYearDescending());
                    break;
                default:
                    documentManager.setSorter(new SortByQuantityDescending());
                    break;
            }
            documentManager.executeSort(books);
            filtered = new ArrayList<>(books);
            filtered.addAll(others);
        }

        docModel.setRowCount(0);
        for (Document doc : filtered) {
            docModel.addRow(UiUtil.documentRow(doc));
        }
        lblDocStatus.setText("Tìm thấy " + docModel.getRowCount() + " kết quả phù hợp");
    }

    /** UC-04: Mượn tài liệu một chạm; UC-07: chuyển sang hàng đợi đặt trước nếu hết sách. */
    private void handleBorrow() {
        int row = docTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài liệu trên bảng để mượn!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String docId = String.valueOf(docModel.getValueAt(row, 0));
        try {
            Transaction tx = transactionController.borrow(member, docId);
            JOptionPane.showMessageDialog(this,
                    "Mượn sách thành công! Hạn trả của bạn là: " + DateUtil.format(tx.getDueDate()),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshDocumentTable();
            refreshTransactionTable();
        } catch (TransactionController.OutOfStockException ex) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Tài liệu này hiện đã hết. Bạn có muốn đặt trước để giữ chỗ khi có sách trả không?",
                    "Sách tạm hết", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    int position = transactionController.joinWaitingList(member, docId);
                    JOptionPane.showMessageDialog(this,
                            "Bạn đã tham gia hàng đợi thành công! Vị trí của bạn trong danh sách chờ: #" + position,
                            "Đặt trước thành công", JOptionPane.INFORMATION_MESSAGE);
                    refreshDocumentTable();
                } catch (RuntimeException queueEx) {
                    JOptionPane.showMessageDialog(this, queueEx.getMessage(),
                            "Không thể đặt trước", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể mượn", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // TAB 2: SÁCH ĐANG MƯỢN & LỊCH SỬ CÁ NHÂN (FR-09, FR-10, UC-05, UC-06)
    // =========================================================================

    private JPanel buildMyLoansTab() {
        JPanel tab = new JPanel(new BorderLayout(5, 5));
        tab.setBackground(UiUtil.BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(UiUtil.SURFACE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiUtil.BORDER));
        cboStatusFilter = new JComboBox<>(new String[]{
                TransactionManager.FILTER_ALL, TransactionManager.FILTER_BORROWED,
                TransactionManager.FILTER_RETURNED, TransactionManager.FILTER_OVERDUE});
        cboStatusFilter.setFont(UiUtil.LABEL_FONT);
        JButton btnRenew  = UiUtil.makeBtn("🔄  Gia hạn (+7 ngày)", UiUtil.PRIMARY);
        JButton btnReturn = UiUtil.makeBtn("✔  Trả sách",           UiUtil.COLOR_OK);
        JLabel lLoc = new JLabel("Lọc trạng thái:");
        lLoc.setFont(UiUtil.LABEL_FONT);
        toolbar.add(lLoc);
        toolbar.add(cboStatusFilter);
        toolbar.add(btnRenew);
        toolbar.add(btnReturn);

        transModel = UiUtil.createReadOnlyModel(new String[]{
                "Mã GD", "Mã tài liệu", "Tên tài liệu", "Ngày mượn", "Hạn trả",
                "Ngày trả", "Trạng thái", "Tiền phạt", "Đã gia hạn"});
        transTable = new JTable(transModel);
        UiUtil.styleTableWithOverdue(transTable, 6);

        tab.add(toolbar, BorderLayout.NORTH);
        tab.add(UiUtil.makeScrollPane(transTable), BorderLayout.CENTER);

        cboStatusFilter.addActionListener(e -> refreshTransactionTable());
        btnRenew.addActionListener(e -> handleRenew());
        btnReturn.addActionListener(e -> handleReturn());

        return tab;
    }

    private void refreshTransactionTable() {
        transModel.setRowCount(0);
        for (Transaction tx : transactionManager.filterByStatus(
                String.valueOf(cboStatusFilter.getSelectedItem()), member.getUserID())) {
            Document doc = documentManager.findById(tx.getDocumentID());
            transModel.addRow(new Object[]{
                    tx.getTransactionID(), tx.getDocumentID(),
                    doc != null ? doc.getTitle() : "(đã xóa)",
                    DateUtil.format(tx.getBorrowDate()), DateUtil.format(tx.getDueDate()),
                    DateUtil.format(tx.getReturnDate()), TransactionManager.getDisplayStatus(tx),
                    FormatUtil.formatVND(tx.getFineAmount()), tx.isRenewed() ? "Có" : "Không"});
        }
    }

    /** UC-06: Gia hạn mượn tài liệu (+7 ngày, tối đa 1 lần). */
    private void handleRenew() {
        int row = transTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch đang mượn cần gia hạn!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String txId = String.valueOf(transModel.getValueAt(row, 0));
        try {
            java.time.LocalDate newDueDate = transactionController.renew(txId);
            JOptionPane.showMessageDialog(this,
                    "Gia hạn thành công! Hạn trả mới của bạn là: " + DateUtil.format(newDueDate),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshTransactionTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể gia hạn", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** UC-05: Trả tài liệu & xử lý phạt trực quan. */
    private void handleReturn() {
        int row = transTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần trả trên bảng!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String txId = String.valueOf(transModel.getValueAt(row, 0));
        try {
            double fine = transactionController.previewFine(txId);
            boolean payFromWallet = false;
            if (fine > 0) {
                long days = transactionController.previewOverdueDays(txId);
                int choice = JOptionPane.showConfirmDialog(this,
                        "Bạn trả trễ " + days + " ngày. Tiền phạt: " + FormatUtil.formatVND(fine)
                                + ".\nTrừ tiền trực tiếp từ ví của bạn?",
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
                        ? "\nĐã trừ đủ tiền phạt từ ví của bạn."
                        : "\nVí không đủ số dư, phần thiếu đã được ghi nhận vào nợ tích lũy!");
            }
            if (result.heldForMemberId != null) {
                msg.append("\nSách được giữ riêng cho độc giả [").append(result.heldForMemberId)
                        .append("] trong 48h (người đầu hàng đợi đặt trước).");
            }
            if (member.isBanned()) {
                msg.append("\nCẢNH BÁO: Tổng nợ phạt vượt quá ")
                        .append(FormatUtil.formatVND(Member.BAN_FINE_THRESHOLD))
                        .append(" - Tài khoản của bạn đã bị KHÓA!");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Kết quả trả sách",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshHeader();
            refreshDocumentTable();
            refreshTransactionTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi trả sách", JOptionPane.ERROR_MESSAGE);
        }
    }
}
