package com.tenco.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// loombok추가
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Sales {

    private int id;                  // PK
    private int productId;           // 판매된 상품 ID (FK)
    private String productName;      // 조회 JOIN 결과 받을 때 사용 (매출 조회용)
    private int quantity;            // 판매 수량
    private BigDecimal unitPrice;    // 판매 당시 단가
    private BigDecimal totalPrice;   // 총 가격
    private LocalDate soldAt;    // 판매 시각 (DATETIME → LocalDateTime)

}
