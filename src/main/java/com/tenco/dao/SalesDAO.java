package com.tenco.dao;

import com.tenco.dto.Product;
import com.tenco.dto.Sales;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    // 판매 처리 (트랜잭션)
    public boolean processSale(Product product, int count) throws SQLException {

        Connection conn = null;

        try {
            conn.setAutoCommit(false);
            conn = DBConnectionManager.getConnection();
            // 1. 제품 존재 유무 확인(상품 존재 유무, 재고 유무 , 활성화 유무)
            String checkSql = """
                    SELECT * FROM product WHERE id = ? AND stock >= ? AND is_active = TRUE
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, product.getId());
                checkPstmt.setInt(2,count);

                try (ResultSet rs = checkPstmt.executeQuery()){
                    if (rs.next() == false) {
                        throw new SQLException("존재하지 않는 상품입니다. 상품ID : " + product);
                    }

                } // end of rs
            } // end of checkPstmt

            // 2. sales 테이블에 판매 내역 추가
            String updateSql = """
                    INSERT INTO sales ( product_id,quantity,unit_price ) values (? , ? ,?)                    
                    """;
            try(PreparedStatement updatePstmt = conn.prepareStatement(updateSql)){
                updatePstmt.setInt(1,product.getId());
                updatePstmt.setInt(2,count);
                updatePstmt.setBigDecimal(3,product.getPrice());
                updatePstmt.executeUpdate();
            } // end of updatePstmt

            // 3. product 테이블 업데이트
            String fixSql = """
                    UPDATE product
                    SET stock = stock - 1
                    WHERE id = ?
                    """;
            try(PreparedStatement fixPstmt = conn.prepareStatement(fixSql)){
                fixPstmt.setInt(1,product.getId());
                fixPstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null){
                conn.rollback();
            }
            System.out.println("오류 발생 : " + e.getMessage());
            return false;
        }finally {
            if (conn != null){

                conn.setAutoCommit(true);
                conn.close();
            }
            return true;
        } // end of 트랜잭션
    } // end of processSale

    // 오늘 매출 집계
    public List<Sales> findTodaySales() throws SQLException {
        List<Sales> salesList = new ArrayList<>();
        String sql = """
                select name ,(s.quantity * s.unit_price) - (s.quantity * p.cost) as '오늘 매출액'
                from sales s
                left join product p on s.product_id = p.id
                where date(s.sold_at) = current_date();
                """;
        // DTO 새로 생성 해야 함

    } // end of findTodaySales

}
