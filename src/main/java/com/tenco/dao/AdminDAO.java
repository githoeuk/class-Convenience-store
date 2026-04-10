package com.tenco.dao;

import com.tenco.dto.Admin;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // 사고 흐름 - 관리자 로그인 처리
    // 1. 쿼리 결정 -> id,pw, 쿼리에 던져서 일치하는 행을 조회 해야 한다. -> SELECT
    // 2. 커넥션 객체 가져오기 (외부 자원을 열어두면 메모리 누스 close() , try re...
    // 3. 쿼리 생성 및 요청 객체 만들기
    //      - Pstmt 결정, ? , ? 바인딩 처리,
    //      - excuteQuery()
    // 4. 결과집합을 DTO에 담기
    //     - rs.next() 가 true -> 일치하는 행이 존재 함.
    // 5. 리턴 결과 결정 -> 성공 : rs 에서 컬럼값을 꺼내서 Admin 객체에 담기
    //                    실패 : null 반환


    public Admin login(String adminId, String password) {

        String sql = """
                SELECT * FROM admins WHERE admin_id = ? and password = ?
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, adminId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 1. 다중 행인지 단일 행인지 쿼리 출력값 확인
                // 단일행 -> 1 row가 나오거나 아예 안나오거나
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setAdminId(rs.getString("admin_id"));
                    admin.setName(rs.getString("name"));
                    return admin;
                } else {
                    return null;
                }
            } // end of rs

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } // end of pstmt

    } // end of login

    // 테스트 코드 작성 확인 삭제 예쩡
    public static void main(String[] args) {
        // 예외 클래스는 2가지로 분류
        // checked exception
        // unchecked exception

        // RuntimeException 는 unchecked exception 이다.
        // 즉 사용하는입장에서 try 구문을 필요하면 사용해도 되고 없으면 사용 안해도 된다.
        // SQLException 는 checked exception 이다
        // 강제적으로 처리해야하는 강제성이 생긴다.
        try{
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.login("admin", "admin123");
            System.out.println(admin.toString());
        } catch (Exception e) {
            System.out.println("오류 발생");
            // throw new RuntimeException(e);
        }
        System.out.println("오류 발생 없음");
    } // end of main


} // end of class
