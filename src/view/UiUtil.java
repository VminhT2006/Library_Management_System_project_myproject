package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import model.Book;
import model.Document;
import model.EBook;
import model.Magazine;
import model.Thesis;

/**
 * Lớp tiện ích giao diện – design system chung cho toàn bộ tầng View.
 */
public final class UiUtil {

    // ── Bảng màu ─────────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(37,  99,  235);  // Blue-600
    public static final Color PRIMARY_DARK   = new Color(29,  78,  216);  // Blue-700
    public static final Color PRIMARY_LIGHT  = new Color(219, 234, 254);  // Blue-100
    public static final Color SECONDARY      = new Color(124,  58, 237);  // Violet-600
    public static final Color COLOR_OK       = new Color(  5, 150, 105);  // Emerald-600
    public static final Color COLOR_DANGER   = new Color(220,  38,  38);  // Red-600
    public static final Color COLOR_WARNING  = new Color(217, 119,   6);  // Amber-600
    public static final Color COLOR_INFO     = new Color(  6, 182, 212);  // Cyan-500
    public static final Color BG             = new Color(248, 250, 252);  // Slate-50
    public static final Color SURFACE        = Color.WHITE;
    public static final Color BORDER         = new Color(226, 232, 240);  // Slate-200
    public static final Color BORDER_FOCUS   = new Color(147, 197, 253);  // Blue-300
    public static final Color TEXT           = new Color( 30,  41,  59);  // Slate-800
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);  // Slate-500
    public static final Color COLOR_OVERDUE_BG = new Color(254, 226, 226); // Red-100
    public static final Color NAV_BG         = new Color( 15,  23,  42);  // Slate-900
    public static final Color NAV_TEXT       = new Color(226, 232, 240);  // Slate-200

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font TITLE_FONT    = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD_FONT     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font SMALL_FONT    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font NAV_FONT      = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font KPI_FONT      = new Font("Segoe UI", Font.BOLD,  26);

    private UiUtil() {}

    // ── Button factory ────────────────────────────────────────────────────────

    /**
     * Tạo nút bo tròn với màu nền tùy chỉnh, chữ trắng.
     * Hover làm sáng, pressed làm tối.
     */
    public static JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isArmed() ? bg.darker()
                           : getModel().isRollover() ? bg.brighter()
                           : bg;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(BOLD_FONT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Nút outline (nền trắng, viền màu, chữ màu). */
    public static JButton makeOutlineBtn(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? accent : SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(accent);
        btn.setFont(BOLD_FONT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(accent);
            }
        });
        return btn;
    }

    // ── Table styling ─────────────────────────────────────────────────────────

    /** Áp dụng style hiện đại cho JTable: header màu, dòng xen kẽ, font đẹp. */
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(LABEL_FONT);
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT);

        // Header
        table.getTableHeader().setFont(BOLD_FONT);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? SURFACE : BG);
                    c.setForeground(TEXT);
                }
                return c;
            }
        });
    }

    /**
     * Áp dụng style table với highlight đỏ cho dòng "Quá hạn".
     */
    public static void styleTableWithOverdue(JTable table, int statusColumnIndex) {
        styleTable(table);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String status = String.valueOf(tbl.getValueAt(row, statusColumnIndex));
                    if ("Quá hạn".equals(status)) {
                        c.setBackground(COLOR_OVERDUE_BG);
                        c.setForeground(COLOR_DANGER);
                    } else {
                        c.setBackground(row % 2 == 0 ? SURFACE : BG);
                        c.setForeground(TEXT);
                    }
                }
                return c;
            }
        });
    }

    // ── KPI Card ──────────────────────────────────────────────────────────────

    /**
     * Tạo thẻ KPI: thanh màu trên đỉnh, tiêu đề mờ nhỏ, số liệu to.
     */
    public static JPanel makeKpiCard(String title, JLabel valueLabel, Color accent) {
        // Thanh màu trên đỉnh
        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(1, 5));
        stripe.setOpaque(true);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(TEXT_MUTED);

        valueLabel.setFont(KPI_FONT);
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setBackground(SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        card.add(stripe, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // ── Scroll pane ───────────────────────────────────────────────────────────

    /** JScrollPane với border nhẹ. */
    public static JScrollPane makeScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getViewport().setBackground(SURFACE);
        return sp;
    }

    // ── Section title ─────────────────────────────────────────────────────────

    /** Label tiêu đề section có đường kẻ dưới màu. */
    public static JLabel makeSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(BOLD_FONT);
        lbl.setForeground(PRIMARY);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        return lbl;
    }

    // ── Existing helpers (kept for compatibility) ─────────────────────────────

    /** Tạo TableModel chỉ đọc. */
    public static DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Các cột chuẩn của bảng danh mục tài liệu. */
    public static String[] documentColumns() {
        return new String[]{"Mã", "Loại", "Tiêu đề", "Năm XB", "Tác giả / Chi tiết", "Số lượng", "Số người chờ"};
    }

    /** Chuyển một Document thành một dòng dữ liệu (đa hình). */
    public static Object[] documentRow(Document doc) {
        String detail;
        String quantity;
        String waiting = "-";
        if (doc instanceof Book) {
            Book b = (Book) doc;
            detail = b.getAuthor() + " / " + b.getPublisher();
            quantity = String.valueOf(b.getQuantity());
            waiting = String.valueOf(b.getWaitingList().size());
        } else if (doc instanceof Magazine) {
            Magazine m = (Magazine) doc;
            detail = "Số " + m.getIssueNumber() + " (Tháng " + m.getPublishMonth() + ")";
            quantity = String.valueOf(m.getQuantity());
        } else if (doc instanceof Thesis) {
            Thesis t = (Thesis) doc;
            detail = t.getUniversity() + " - GVHD: " + t.getAdvisor();
            quantity = "Đọc tại chỗ";
        } else if (doc instanceof EBook) {
            EBook e = (EBook) doc;
            detail = e.getFormat() + " - " + e.getFileSizeMb() + " MB";
            quantity = "Không giới hạn";
        } else {
            detail = "";
            quantity = "";
        }
        return new Object[]{doc.getId(), doc.getTypeName(), doc.getTitle(),
                doc.getPublicationYear(), detail, quantity, waiting};
    }

    /** Gắn Live Search. */
    public static void attachLiveSearch(JTextComponent field, Runnable onChange) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onChange.run(); }
            @Override public void removeUpdate(DocumentEvent e)  { onChange.run(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
        });
    }

    /**
     * @deprecated Dùng {@link #styleTableWithOverdue(JTable, int)} thay thế.
     */
    @Deprecated
    public static void applyOverdueHighlight(JTable table, int statusColumnIndex) {
        styleTableWithOverdue(table, statusColumnIndex);
    }

    /** ESC để đóng dialog. */
    public static void bindEscapeToClose(JComponent rootPane, Runnable closeAction) {
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");
        rootPane.getActionMap().put("closeDialog", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                closeAction.run();
            }
        });
    }
}
