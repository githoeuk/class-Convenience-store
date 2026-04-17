package com.tenco.view;

import com.tenco.dto.Product;
import com.tenco.service.StoreService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StoreView {

    private final StoreService service = new StoreService();
    private final Scanner sc = new Scanner(System.in);


    // 메인
    public static void main(String[] args) {
        StoreView storeView = new StoreView();

       storeView.start();
    }

    // 9번 재고 부족 알림
    private void lowInfo () throws SQLException {
        List<Product> productList = service.getLowStockProducts();
        System.out.println(productList.toString());
    }

    // 8번. 상품 소프트 삭제
    private void softDelete() throws SQLException {
        int productId = readInt("삭제할 상품의 제품ID를 입력하세요 : ");
        if (service.deleteProduct(productId)){
            System.out.println("삭제되었습니다.");
        }else {
            System.out.println("제품ID에 해당하는 제품이 없습니다. 제품ID : " + productId);
        }
    }

    // 7번. 상품 업데이트
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
            System.out.println(product.getName() + "상품이 수정되었습니다.");
        }
    }

    //  6번. 상품 등록
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

        System.out.println("유효기간 : ");
        LocalDate expire = LocalDate.parse(sc.nextLine().trim());
        if (expire == null){
            System.out.println("다시 입력해주세요");
            return;
        }
        Product product = Product.builder()
                .barcode(barcode)
                .name(name)
                .category(category)
                .price(BigDecimal.valueOf(price))
                .cost(BigDecimal.valueOf(cost))
                .expireDate(expire)
                .build();

        boolean check = service.addProduct(product);
        if (check){
            service.getProductList();
        }else {
            return;
        }
    } // end of addProduct

    // 5번 로그인 관련
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

    // 4번. 오늘 매출 조회
    private void totalToday() throws SQLException {
        service.getTodaySales();
    }

    // 3번. 판매 처리
    private void processSale() throws SQLException {
        System.out.print("바코드 번호를 입력하세요 : ");
        String barcode = sc.nextLine().trim();
        if (barcode == null){
            System.out.println("입력이 필요합니다.");
            return;
        }
        int quantity = readInt("구매 개수를 입력해주세요 : ");
        if (quantity <= 0){
            System.out.println("입력이 필요합니다");
            return;
        }
        service.processSale(barcode,quantity);
    }

    // 2번. 바코드 상품 검색
    private void searchToBarcode() throws SQLException {
        System.out.println("해당 제품의 바코드 번호를 입력하세요 : ");
        String barcode = sc.nextLine().trim();
        if (barcode == null) {
            System.out.print("바코드 번호를 입력하세요 ");
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

    // 1번. 상품 목록 조회
    private void productList() throws SQLException {
        List<Product> productList = service.getProductList();
        System.out.println(productList.toString());
        // service.isNearExpiry(productList.);
    }

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

    // 로그인 확인
    private void isLoggedIn() {
        boolean check = service.isLoggedIn();
        if (check) {
            System.out.println("현재 로그인 상태입니다.");
        } else {
            System.out.println("현재 로그아웃 상태입니다.");
        }
    } // end of isLoggedIn

    public void start() {
        StoreView storeView = new StoreView();
        System.out.println("시스템 부팅....");

        while (true){
            printMenu();
            int ch = readInt("선택 : ");

            try{
                switch (ch){
                    case 1:
                        // 상품 목록 조회
                        storeView.productList();
                        break;
                    case 2:
                        // 바코드로 상품 검색
                        storeView.searchToBarcode();
                        break;
                    case 3:
                        // 판매 처리
                        storeView.processSale();
                        break;
                    case 4:
                        // 오늘 매출 조회
                        storeView.totalToday();
                        break;
                    case 5:
                        // 관리자 로그인
                        storeView.login();
                        break;
                    case 6:
                        if (!service.isLoggedIn()){
                            System.out.println("로그인 후 이용가능합니다");
                            break;
                        }
                        // 상품 등록
                        storeView.addProduct();
                        break;
                    case 7:
                        if (!service.isLoggedIn()){
                            System.out.println("로그인 후 이용가능합니다");
                           break;
                        }
                        //상품 수정
                        storeView.updateProduct();
                        break;
                    case 8:
                        if (!service.isLoggedIn()){
                            System.out.println("로그인 후 이용가능합니다");
                            break;
                        }
                        //상품 소프트 삭제
                        storeView.softDelete();
                        break;
                    case 9:
                        if (!service.isLoggedIn()){
                            System.out.println("로그인 후 이용가능합니다");
                            break;
                        }
                        //재고 부족 알림
                        storeView.lowInfo();
                        break;
                    case 0:
                        //종료
                        sc.close();
                        return;
                    default:
                        System.out.println("목록에 있는 번호를 입력하세요");
                }
            } catch (Exception e) {
                System.out.println("오류 : " + e.getMessage());
            }
        } // end of switch
    } // end of start

    private void printMenu() {
        System.out.println("\n============ 무인 편의점 재고 관리 시스템 ============");
        System.out.println("--------------------------------------------------------");
        System.out.println("1. 상품 번호 조회");
        System.out.println("2. 바코드로 상품 검색");
        System.out.println("3. 판매 처리");
        System.out.println("4. 오늘 매출 조회");
        System.out.println("5. 관리자 로그인");
        System.out.println("6. 상품 등록");
        System.out.println("7. 상품 수정");
        System.out.println("8. 상품 삭제");
        System.out.println("9. 재고 부족 알림");
        System.out.println("0. 종료");
    } // end of printMenu

} // end of class
