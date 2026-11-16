package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import manager.DocumentManager;
import model.Document;

/**
 * Màn hình tra cứu công khai dành cho Khách vãng lai (SRS Mục 2 - Guest, UC-03).
 * Xem danh mục và tìm kiếm tức thì mà không cần đăng nhập.
 */
public class GuestCatalogFrame extends JFrame {
    private final DocumentManager documentManager;

    private JTextField txtSearch;
    private JComboBox<String> cboCriteria;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public GuestCatalogFrame(DocumentManager documentManager) {
        this.documentManager = documentManager;
        setTitle("Danh mục Tài liệu Công khai (Khách vãng lai)");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboCriteria = new JComboBox<>(new String[]{"Tất cả", "Tiêu đề", "Tác giả", "Mã ID"});
        txtSearch = new JTextField(24);
        topPanel.add(new JLabel("Tiêu chí:"));
        topPanel.add(cboCriteria);
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(txtSearch);

        tableModel = UiUtil.createReadOnlyModel(UiUtil.documentColumns());
        JTable table = new JTable(tableModel);

        lblStatus = new JLabel(" ");

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        UiUtil.attachLiveSearch(txtSearch, this::refreshTable);
        cboCriteria.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Document doc : documentManager.searchDocuments(
                txtSearch.getText(), String.valueOf(cboCriteria.getSelectedItem()))) {
            tableModel.addRow(UiUtil.documentRow(doc));
        }
        lblStatus.setText("Tìm thấy " + tableModel.getRowCount() + " kết quả phù hợp");
    }
}
