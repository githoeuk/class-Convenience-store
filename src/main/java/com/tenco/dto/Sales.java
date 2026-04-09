package com.tenco.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// loombok추가
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Sales {

    private int id;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private LocalDate soldAt; // 데이터타입_localDate로 수정
}
