package com.tenco.view;

import com.tenco.dto.Product;
import com.tenco.service.StoreService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class StoreView {

    private final StoreService service = new StoreService();
    private final Scanner sc = new Scanner(System.in);

    // 메인
    public static void main(String[] args) {
        StoreView storeView = new StoreView();

        try {
            storeView.searchToBarcode();
        } catch (SQLException e) {
            System.out.println("오류 발생 : " + e.getMessage());
        }


    }

    // 8. 상품 업데이트
    private void updateProduct() throws SQLException{
        System.out.println("수정할 상품의 정보를 입력해주세요");
        System.out.print("바코드 번호 : "); // barcode
        String barcode = sc.nextLine().trim();
        if (barcode == null){
            System.out.println("다시 입력해주세요");
            return;
        }
        Product product = service.findByBarcode(barcode);
        if (product == null){
            throw new SQLException("바코드에 해당하는 제품이 없습니다.");
        }else {
            System.out.print("판매가 : "); // price
            int price = readInt("판매가");
            if (price <= 0){
                System.out.println("다시 입력해주세요");
                return;
            }
            product.setPrice(BigDecimal.valueOf(price));
            System.out.print("재고 : ");
            int stock = readInt("판매가");
            if (stock <= 0){
                System.out.println("다시 입력해주세요");
                return;
            }
            product.setStock(stock);
        }
    }

    //  7. 상품 등록
    private void addProduct() throws SQLException {

        System.out.println("등록할 상품의 정보를 입력해주세요");
        System.out.print("바코드 번호 : "); // barcode
        String barcode = sc.nextLine().trim();
        if (barcode == null){
            System.out.println("다시 입력해주세요");
            return;
        }
        System.out.print("상품명 : "); // name
        String name = sc.nextLine().trim();
        if (name == null){
            System.out.println("다시 입력해주세요");
            return;
        }
        System.out.print("카테고리 : "); // category
        String category = sc.nextLine().trim();
        if (category == null){
            System.out.println("다시 입력해주세요");
            return;
        }
        System.out.print("판매가 : "); // price
        int price = readInt("판매가");
        if (price <= 0){
            System.out.println("다시 입력해주세요");
            return;
        }
        System.out.print("원가 : "); // cost
        int cost =  readInt("원가 : ");
        if (cost <= 0){
            System.out.println("다시 입력해주세요");
            return;
        }
        Product product = Product.builder()
                .barcode(barcode)
                .name(name)
                .category(category)
                .price(BigDecimal.valueOf(price))
                .cost(BigDecimal.valueOf(cost))
                .build();


        boolean check = service.addProduct(product);
        if (check){
            service.getProductList();
        }else {
            return;
        }
    } // end of addProduct

    // 6. 바코드 상품 검색
    private void searchToBarcode() throws SQLException {
        System.out.println("해당 제품의 바코드 번호를 입력하세요 : ");
        String barcode = sc.nextLine().trim();
        if (barcode == null) {
            System.out.println("바코드 번호를 입력하세요 ");
            return;
        }
        Product product = new Product();
        product = service.findByBarcode(barcode);
        if (product == null) {
            System.out.println("[ERROR] 해당 바코드의 상품이 없습니다.");
        } else {
            System.out.println(product.toString());
        }
    }

    // 5. 상품 목록 조회
    private void productList() throws SQLException {
        service.getProductList();
    }

    // 4. show name
    private void name() throws SQLException {
        service.getCurrentAdminName();
    }

    // 3.isLoggedIn
    private void isLoggedIn() {
        boolean check = service.isLoggedIn();
        if (check) {
            System.out.println("현재 로그인 상태입니다.");
        } else {
            System.out.println("현재 로그아웃 상태입니다.");
        }
    } // end of isLoggedIn

    // 2. end of logout
    private void logout() {
        service.logout();
    } // end of logout

    // 1. login
    private void login() throws SQLException {
        System.out.println("== 로그인 ==");
        System.out.print("아이디 : ");
        String id = sc.nextLine().trim();
        if (id == null) {
            System.out.println("아이디를 입력해주세요");
            return;
        }
        System.out.print("비밀번호 : ");
        String password = sc.nextLine().trim();
        if (password == null) {
            System.out.println("비밀번호를 입력해주세요");
            return;
        }
        if (service.login(id, password) == false) {
            System.out.println("아이디나 비밀번호가 잘못되었습니다.");
        } else {
            System.out.println("로그인 되었습니다.");
        }
    } // end of login


    public void start() {

    } // end of start

    private void printMenu() {

    } // end of printMenu

    // 숫자 입력을 안전하게 처리 ( 잘못된 입력 시 재요청)
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요 ");
            }
        }
    } // end of readInt


} // end of class
