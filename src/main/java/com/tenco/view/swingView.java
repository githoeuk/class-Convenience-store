package com.tenco.view;

import com.tenco.dto.Product;
import com.tenco.service.StoreService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class swingView extends JFrame {

    private StoreService service = new StoreService();

    private JTable table;
    private DefaultTableModel model;

    public swingView() {
        setTitle("무인 편의점 재고 관리 시스템 (JTable 버전)");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 테이블 컬럼
        String[] columns = {"ID", "바코드", "상품명", "카테고리", "가격", "재고"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 버튼 패널
        JPanel panel = new JPanel(new GridLayout(2, 5));

        JButton btn1 = new JButton("목록");
        JButton btn2 = new JButton("검색");
        JButton btn3 = new JButton("판매");
        JButton btn4 = new JButton("매출");
        JButton btn5 = new JButton("로그인");
        JButton btn6 = new JButton("등록");
        JButton btn7 = new JButton("수정");
        JButton btn8 = new JButton("삭제");
        JButton btn9 = new JButton("재고부족");

        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(btn4);
        panel.add(btn5);
        panel.add(btn6);
        panel.add(btn7);
        panel.add(btn8);
        panel.add(btn9);

        add(panel, BorderLayout.SOUTH);

        // 이벤트 연결
        btn1.addActionListener(e -> loadTable());
        btn2.addActionListener(e -> search());
        btn3.addActionListener(e -> sale());
        btn4.addActionListener(e -> todaySales());
        btn5.addActionListener(e -> login());
        btn6.addActionListener(e -> addProduct());
        btn7.addActionListener(e -> updateProduct());
        btn8.addActionListener(e -> deleteProduct());
        btn9.addActionListener(e -> lowStock());

        setVisible(true);
    }

    // 테이블 로드
    private void loadTable() {
        try {
            List<Product> list = service.getProductList();
            model.setRowCount(0); // 초기화

            for (Product p : list) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getBarcode(),
                        p.getName(),
                        p.getCategory(),
                        p.getPrice(),
                        p.getStock()
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void search() {
        String barcode = JOptionPane.showInputDialog("바코드 입력");
        try {
            Product p = service.findByBarcode(barcode);
            model.setRowCount(0);

            if (p != null) {
                model.addRow(new Object[]{
                        p.getId(), p.getBarcode(), p.getName(),
                        p.getCategory(), p.getPrice(), p.getStock()
                });
            } else {
                JOptionPane.showMessageDialog(this, "상품 없음");
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void sale() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "상품 선택하세요");
                return;
            }

            String barcode = model.getValueAt(row, 1).toString();
            int qty = Integer.parseInt(JOptionPane.showInputDialog("수량"));

            service.processSale(barcode, qty);
            loadTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void todaySales() {
        try {
            service.getTodaySales();
            JOptionPane.showMessageDialog(this, "콘솔 확인");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void login() {
        String id = JOptionPane.showInputDialog("아이디");
        String pw = JOptionPane.showInputDialog("비밀번호");
        try {
            boolean result = service.login(id, pw);
            JOptionPane.showMessageDialog(this, result ? "성공" : "실패");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void addProduct() {
        try {
            String barcode = JOptionPane.showInputDialog("바코드");
            String name = JOptionPane.showInputDialog("상품명");
            String category = JOptionPane.showInputDialog("카테고리");
            int price = Integer.parseInt(JOptionPane.showInputDialog("가격"));
            int cost = Integer.parseInt(JOptionPane.showInputDialog("원가"));

            Product p = Product.builder()
                    .barcode(barcode)
                    .name(name)
                    .category(category)
                    .price(BigDecimal.valueOf(price))
                    .cost(BigDecimal.valueOf(cost))
                    .build();

            service.addProduct(p);
            loadTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateProduct() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "상품 선택하세요");
                return;
            }

            String barcode = model.getValueAt(row, 1).toString();
            Product p = service.findByBarcode(barcode);

            int price = Integer.parseInt(JOptionPane.showInputDialog("가격"));
            int stock = Integer.parseInt(JOptionPane.showInputDialog("재고"));

            p.setPrice(BigDecimal.valueOf(price));
            p.setStock(stock);

            service.updateProduct(p); // ⭐ 중요
            loadTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteProduct() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "상품 선택하세요");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            service.deleteProduct(id);
            loadTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void lowStock() {
        try {
            List<Product> list = service.getLowStockProducts();
            model.setRowCount(0);

            for (Product p : list) {
                model.addRow(new Object[]{
                        p.getId(), p.getBarcode(), p.getName(),
                        p.getCategory(), p.getPrice(), p.getStock()
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }

    public static void main(String[] args) {
        new swingView();
    }
}
