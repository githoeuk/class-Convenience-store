package com.tenco.dao;

import com.tenco.dto.Product;

import java.sql.SQLException;

public class testmain {





    public static void main(String[] args) throws SQLException {
        ProductDAO productDAO = new ProductDAO();
        SalesDAO salesDAO = new SalesDAO();


        System.out.println(productDAO.findLowStock());

    }

}
