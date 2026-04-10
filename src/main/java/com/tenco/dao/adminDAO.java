package com.tenco.dao;

import com.tenco.dto.Admin;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminDAO {


    public Admin login(String name , String password) throws SQLException {
        String sql =
                """
                SELECT * FROM admins WHERE admin_id = ? AND password = ? 
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1,name);
            pstmt.setString(2,password);

            try(ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    return Admin.builder()
                            .id(rs.getInt("id"))
                            .adminId(rs.getString("admin_id"))
                            .name(rs.getString("name"))
                            .build();
                }
            } // end of rs
        } // end of pstmt
        return null;
    } // end of login

} // end of class
