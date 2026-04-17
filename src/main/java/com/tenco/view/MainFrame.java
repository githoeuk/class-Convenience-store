package com.tenco.view;

import com.tenco.dto.Product;
import com.tenco.service.StoreService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {

    private StoreService service;

    private JTable table;
    private DefaultTableModel model;

    private List<Product> currentList;

    public MainFrame(StoreService service) {
        this.service = service;

        setTitle("편의점 관리 시스템");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
        setVisible(true);

        loadProducts(); // ⭐ 시작 시 자동 로드
    }

    private void init() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel();

        JButton listBtn = new JButton("상품목록");
        JButton searchBtn = new JButton("검색");
        JButton saleBtn = new JButton("판매");
        JButton addBtn = new JButton("등록");
        JButton updateBtn = new JButton("수정");
        JButton deleteBtn = new JButton("삭제");
        JButton lowBtn = new JButton("재고부족");
        JButton salesBtn = new JButton("매출");
        JButton logoutBtn = new JButton("로그아웃");

        top.add(listBtn);
        top.add(searchBtn);
        top.add(saleBtn);
        top.add(addBtn);
        top.add(updateBtn);
        top.add(deleteBtn);
        top.add(lowBtn);
        top.add(salesBtn);
        top.add(logoutBtn);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "ID","바코드","상품명","카테고리","가격","재고","유통기한"
        },0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 이벤트 연결
        listBtn.addActionListener(e -> loadProducts());
        searchBtn.addActionListener(e -> handleSearch());
        saleBtn.addActionListener(e -> handleSale());
        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        lowBtn.addActionListener(e -> handleLowStock());
        salesBtn.addActionListener(e -> handleSales());
        logoutBtn.addActionListener(e -> logout());
    }

    // ================= 공통 =================

    private boolean checkLogin() {
        if (!service.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "로그인 필요");
            return false;
        }
        return true;
    }

    private void executeAndRefresh(Runnable action) {
        try {
            action.run();
            loadProducts(); // ⭐ 자동 갱신
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= 데이터 =================

    private void loadProducts() {
        try {
            currentList = service.getProductList();

            model.setRowCount(0);

            for (Product p : currentList) {
                model.addRow(new Object[]{
                        p.getId(), p.getBarcode(), p.getName(),
                        p.getCategory(), p.getPrice(),
                        p.getStock(), p.getExpireDate()
                });
            }

            applyRenderer();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void applyRenderer() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) return c;

                c.setBackground(Color.WHITE);

                if (currentList == null || row >= currentList.size()) return c;

                Product p = currentList.get(row);

                if (service.isLowStock(p)) {
                    c.setBackground(new Color(255, 102, 102)); // 빨강
                } else if (service.isNearExpiry(p)) {
                    c.setBackground(new Color(255, 204, 102)); // 주황
                }

                return c;
            }
        });
    }

    // ================= 기능 =================

    private void handleSearch() {
        String barcode = JOptionPane.showInputDialog("바코드");

        try {
            Product p = service.findByBarcode(barcode);

            model.setRowCount(0);

            if (p != null) {
                currentList = List.of(p);
                model.addRow(new Object[]{
                        p.getId(), p.getBarcode(), p.getName(),
                        p.getCategory(), p.getPrice(),
                        p.getStock(), p.getExpireDate()
                });
                applyRenderer();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void handleSale() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String barcode = model.getValueAt(row,1).toString();
        String qty = JOptionPane.showInputDialog("수량");

        executeAndRefresh(() ->
                {
                    try {
                        service.processSale(barcode, Integer.parseInt(qty));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private void handleAdd() {
        if (!checkLogin()) return;

        executeAndRefresh(() -> {
            Product p = Product.builder()
                    .barcode(JOptionPane.showInputDialog("바코드"))
                    .name(JOptionPane.showInputDialog("상품명"))
                    .category(JOptionPane.showInputDialog("카테고리"))
                    .price(BigDecimal.valueOf(Integer.parseInt(JOptionPane.showInputDialog("가격"))))
                    .cost(BigDecimal.valueOf(Integer.parseInt(JOptionPane.showInputDialog("원가"))))
                    .stock(Integer.parseInt(JOptionPane.showInputDialog("재고")))
                    .expireDate(java.time.LocalDate.parse(
                            JOptionPane.showInputDialog("유통기한 yyyy-MM-dd")))
                    .build();

            try {
                service.addProduct(p);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleUpdate() {
        if (!checkLogin()) return;

        int row = table.getSelectedRow();
        if (row == -1) return;

        executeAndRefresh(() -> {
            String barcode = model.getValueAt(row,1).toString();
            Product p = null;
            try {
                p = service.findByBarcode(barcode);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            p.setPrice(BigDecimal.valueOf(
                    Integer.parseInt(JOptionPane.showInputDialog("가격", p.getPrice()))));
            p.setStock(Integer.parseInt(
                    JOptionPane.showInputDialog("재고", p.getStock())));

            try {
                service.updateProduct(p);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleDelete() {
        if (!checkLogin()) return;

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row,0);

        executeAndRefresh(() -> {
            try {
                service.deleteProduct(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleLowStock() {
        if (!checkLogin()) return;

        try {
            currentList = service.getLowStockProducts();

            model.setRowCount(0);

            for (Product p : currentList) {
                model.addRow(new Object[]{
                        p.getId(), p.getBarcode(), p.getName(),
                        p.getCategory(), p.getPrice(),
                        p.getStock(), p.getExpireDate()
                });
            }

            applyRenderer();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void handleSales() {
        if (!checkLogin()) return;

        try {
            var list = service.getTodaySales();

            StringBuilder sb = new StringBuilder();
            for (var s : list) {
                sb.append(s).append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    new JScrollPane(new JTextArea(sb.toString())));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void logout() {
        service.logout();
        new LoginFrame(service);
        dispose();
    }
}