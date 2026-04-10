package com.tenco.dao;

import com.tenco.dto.Product;
import com.tenco.util.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    //1단계 - 상품 전체 목록 조회 (findAll)
    public List<Product> findAll() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = """
                SELECT * FROM product WHERE is_active = TRUE;
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {
                Product product = Product.builder()
                        .id(rs.getInt("id"))
                        .barcode(rs.getString("barcode"))
                        .name(rs.getString("name"))
                        .category(rs.getString("category"))
                        .price(rs.getBigDecimal("price"))
                        .cost(rs.getBigDecimal("cost"))
                        .stock(rs.getInt("stock"))
                        .minStock(rs.getInt("min_stock"))
                        .expireDate(rs.getDate("expire_date").toLocalDate())
                        .isActive(rs.getBoolean("is_active"))
                        .build();
                productList.add(product);
            } // end of while
        } // end of pstmt

        return productList;
    } // end of findAll


    //2단계 - 바코드로 상품 조회 (findByBarcode)
    public Product findByBarcode(String barcode) throws SQLException {

        String sql = """
                SELECT * FROM product WHERE barcode = ? AND is_active = TRUE
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);

        ) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next() == false) {
                    return null;
                } else {
                    Product product = Product.builder()
                            .id(rs.getInt("id"))
                            .barcode(rs.getString("barcode"))
                            .name(rs.getString("name"))
                            .category(rs.getString("category"))
                            .price(rs.getBigDecimal("price"))
                            .cost(rs.getBigDecimal("cost"))
                            .stock(rs.getInt("stock"))
                            .minStock(rs.getInt("min_stock"))
                            .expireDate(rs.getDate("expire_date").toLocalDate())
                            .isActive(rs.getBoolean("is_active"))
                            .build();
                    return product;
                }
            } // end of rs

        } // end of pstmt

    } // end of findByBarcode

//3단계 - 상품 등록 (insert)

    public boolean insert(Product product) throws SQLException {

        String sql = """
                INSERT INTO product (barcode,name,category,price,cost)
                values ( ?, ? , ?, ?, ?)
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, product.getBarcode());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setBigDecimal(5, product.getCost());
            pstmt.executeUpdate();
            int rows = pstmt.executeUpdate();
            return rows >= 0;
        } // end of pstmt
    } // end of insert


//4단계 - 상품 수정 (update)

    public boolean update(Product product) throws SQLException {

        String sql = """
                UPDATE product
                SET name = ?, stock = ?, expire_date = ?
                WHERE id = ?
                """;
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ptmt = conn.prepareStatement(sql)
        ) {
            ptmt.setString(1, product.getName());
            ptmt.setInt(2, product.getStock());
            ptmt.setDate(3, Date.valueOf(product.getExpireDate()));
            ptmt.setInt(4, product.getId());
            int rows = ptmt.executeUpdate();
            if (rows == 1) {
                return true;
            } else {
                return false;
            }
        } // end of ptmt

    } // end of update

    //5단계 - 소프트 삭제 (delete)
    public boolean softDelete(int id) throws SQLException {

        String sql = """
                UPDATE product
                SET is_active = FALSE
                WHERE id = ?
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() >= 0;
        }
    } // end if softDelete

    //6단계 - 재고 부족 상품 조회 (findLowStock)
    public List<Product> findLowStock() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = """
                SELECT * FROM product WHERE stock <= min_stock AND is_active = TRUE
                """;

        try (
                Connection conn = DBConnectionManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = Product.builder()
                            .id(rs.getInt("id"))
                            .barcode(rs.getString("barcode"))
                            .name(rs.getString("name"))
                            .category(rs.getString("category"))
                            .price(rs.getBigDecimal("price"))
                            .cost(rs.getBigDecimal("cost"))
                            .stock(rs.getInt("stock"))
                            .minStock(rs.getInt("min_stock"))
                            .expireDate(rs.getDate("expire_date").toLocalDate())
                            .isActive(rs.getBoolean("is_active"))
                            .build();
                    productList.add(product);
                }
            } // end of rs
        } // end of pstmt
        return productList;
    } // end of findLowStock


} // end of class
