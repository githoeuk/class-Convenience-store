package com.tenco.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

// lombook추가
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Product {

    private int id;
    private String varCode;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;
    private int stock;
    private int minStock;
    private Date expireDate;
    private boolean isActive;

}
