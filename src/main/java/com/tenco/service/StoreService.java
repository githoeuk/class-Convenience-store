package com.tenco.service;

import com.tenco.dao.AdminDAO;
import com.tenco.dao.ProductDAO;
import com.tenco.dao.SalesDAO;
import com.tenco.dto.Admin;
import com.tenco.dto.Product;
import com.tenco.dto.Sales;

import java.sql.SQLException;
import java.util.List;

public class StoreService {
    /**
     * Service 계층의 역할
     * <p>
     * 1. 서비스는 비즈니스 로직(업무 규칙)을 담당하는 중간 관리자이다.
     * - 서비스는 뷰에서는 받은 요청을 검증하고, 필요한 DAO를 호출하는 결과를 돌려준다.
     * - 재고보다 많이 팔 수 없다는 규칙은 서비스에서 검증한다.
     *
     */

    AdminDAO adminDAO = new AdminDAO();
    ProductDAO productDAO = new ProductDAO();
    SalesDAO salesDAO = new SalesDAO();

    //2단계 - 로그인/로그아웃/로그인 상태
    // 로그인
    public boolean login(String adminId, String password) throws SQLException {

        if (adminId == null || adminId.trim().isEmpty()) {
            throw new SQLException("ID를 입력하세요");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new SQLException("비밀번호를 입력하세요");
        }
        Admin admin = adminDAO.login(adminId, password);

        if (admin == null) {
            System.out.println("아이디 혹은 비밀번호가 잘못되었습니다.");
            return false;
        } else {
            System.out.println(admin.getName() + "님 환영합니다");
            return true;
        }
    } // end of login
    // 로그아웃
    public void logout(){
        if ( adminDAO.login()){

        }
    }
    //로그인 유무
    public boolean isLoggedIn(){

    }

    // 3단계 - 상품 목록 + 알림 로직
    // 서비스에서는 단순 작업도 있음 (단순 DAO 위임)
    // 즉, 단순 조회는 DAO 메서를 바로 호출하여 반환 한다.
    // 별도 검증이 필요 없는 경우들
    public List<Product> getProductList() {
        // 추후 유효성 검사, 로직 변경 되었을 때 유연함을 만들어 줌.
        //return productDAO.findAll();


        return null;
    }


    // 4.판매 처리 로직
    // 판매 처리는 서비스에 역할을 가장 잘 보여준다.
    // 검증 -> 실행 -> 결과 반환 최소 3단계를 만들어야 하나의 서비스가 된다.
    // 1. 검증 : 상품이 실제 존재하는지 확인 (SELECT) -> ProductDAO.findByBarcode()에 위임
    //         - 상품확인, 재고 확인
    // 2. 실행 : SaleDAO.processSale() ---> 내부 트랜잭션 처리 완료
    // 3. 결과 반환 : 결과에 따른 메세지 가공해서 뷰로 전달
    public String processSale(String barcode, int quantity) throws SQLException {

        // 14. 1단계: 상품 존재 여부 확인
        Product product = productDAO.findByBarcode(barcode);
        if (product == null) {
            return "[ERROR] 해당 바코드의 상품이 없습니다.";
        }

        // 15. 2단계: 재고 충분 여부 확인 (비즈니스 검증)
        if (product.getStock() < quantity) {
            return String.format("[ERROR] 재고 부족. 현재 재고: %d개", product.getStock());
        }

        // 16. 3단계: DAO에 판매 실행 위임 (트랜잭션은 DAO 내부에서 처리)
        boolean success = salesDAO.processSale(product, quantity);
        if (!success) {
            return "[ERROR] 판매 처리 중 오류가 발생했습니다.";
        }

        // 17. 4단계: 성공 메시지 생성
        //     BigDecimal.multiply()로 단가 x 수량 = 합계를 계산한다.
        return String.format("[OK] '%s' %d개 판매 완료. 합계: %s원",
                product.getName(),
                quantity,
                product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));
    }

    //5단계 - 알림 표시 도우미 메서드
    // 비즈니스 판단 메서드 - 기준은 서비스가 정한다.

    // 재고 부족 판단에 상품이다.
    public boolean isLowStock(Product product) {
        return product.getStock() <= product.getMinStock();
    }

    // 유통기한 임박
    public boolean isNearExpiry(Product product) {
        if (product.getExpireDate() == null) return false;
        return !product.getExpireDate().isAfter(java.time.LocalDate.now().plusDays(3));
    }

    // 당일 매출
    public List<Sales> getTodaySales() throws SQLException {
        return salesDAO.findTodaySales();
    }

}
