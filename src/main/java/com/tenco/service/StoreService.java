package com.tenco.service;

import com.tenco.dao.AdminDAO;
import com.tenco.dto.Admin;
import com.tenco.dto.Product;

import java.sql.SQLException;
import java.util.List;

public class StoreService {
    /**
     * Service 계층의 역할
     *
     * 1. 서비스는 비즈니스 로직(업무 규칙)을 담당하는 중간 관리자이다.
     *    - 서비스는 뷰에서는 받은 요청을 검증하고, 필요한 DAO를 호출하는 결과를 돌려준다.
     *    - 재고보다 많이 팔 수 없다는 규칙은 서비스에서 검증한다.
     *
     */

    AdminDAO adminDAO = new AdminDAO();

    public boolean login(String adminId, String password) throws SQLException {

        if (adminId == null || adminId.trim().isEmpty() ){
            throw new SQLException("ID를 입력하세요");
        }
        if (password == null || password.trim().isEmpty() ){
            throw new SQLException("비밀번호를 입력하세요");
        }
        Admin admin = adminDAO.login(adminId,password);

        if (admin == null){
            System.out.println("아이디 혹은 비밀번호가 잘못되었습니다.");
            return false;
        }else {
            System.out.println(admin.getName()+ "님 환영합니다");
            return true;
        }
    } // end of login


    // 서비스에서는 단순 작업도 있음 (단순 DAO 위임)
    // 즉, 단순 조회는 DAO 메서를 바로 호출하여 반환 한다.
    // 별도 검증이 필요 없는 경우들
    public List<Product> getProductList() {
        // 추후 유효성 검사, 로직 변경 되었을 때 유연함을 만들어 줌.
        //return productDAO.findAll();





        return null;
    }


    // 판매 처리는 서비스에 역할을 가장 잘 보여준다.
    // 검증 -> 실행 -> 결과 반환 최소 3단계를 만들어야 하나의 서비스가 된다.
    // 1. 검증 : 상품이 실제 존재하는지 확인 (SELECT) -> ProductDAO.findByBarcode()에 위임
    //         - 상품확인, 재고 확인
    // 2. 실행 : SaleDAO.processSale() ---> 내부 트랜잭션 처리 완료
    // 3. 결과 반환 : 결과에 따른 메세지 가공해서 뷰로 전달
    public String processSale(String barcode, int quantity) {

        // 1.단계 : 상품 존재 여부 확인
        // 뽑은 Product 객체로 2번 확인 가능

        // 2.단계 : 재고 충분 여부 확인 (비즈니스)


        // 3.단계 : DAO 판매 실행 위임
        // 트랜잭션 여부에 따라 성공 실패 처리


        // 4단계 : 성공 메세지 생성해서 리턴


        return null;
    }


    // 비즈니스 판단 메서드 - 기준은 서비스가 정한다.
    // 재고 부족 판단에 상품이다.
    //
    public boolean isLowStock(Product product) {
        return product.getStock() <= product.getMinStock();
    }

}
