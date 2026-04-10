package com.tenco.view;

import com.tenco.service.StoreService;

import java.sql.SQLException;
import java.util.Scanner;

public class StoreView {

    private final StoreService service = new StoreService();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        StoreView storeView = new StoreView();

        try {
            storeView.login();
            storeView.isLoggedIn();
            storeView.logout();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }




    // login파트
    private void login () throws SQLException {
        System.out.println("== 로그인 ==");
        System.out.print("아이디 : ");
        String id = sc.nextLine().trim();
        if (id == null){
            System.out.println("아이디를 입력해주세요");
            return;
        }
        System.out.print("비밀번호 : ");
        String password = sc.nextLine().trim();
        if (password == null){
            System.out.println("비밀번호를 입력해주세요");
            return;
        }
        if (service.login(id,password) == false){
            System.out.println("아이디나 비밀번호가 잘못되었습니다.");
        }else {
            System.out.println("로그인 되었습니다.");
        }
    } // end of login

    private void logout(){
        service.logout();
    } // end of logout

    private void isLoggedIn(){
       boolean check = service.isLoggedIn();
       if (check){
           System.out.println("현재 로그인 상태입니다.");
       }else {
           System.out.println("현재 로그아웃 상태입니다.");
       }
    } // end of isLoggedIn

    public void start(){

    } // end of start

    private void printMenu(){

    } // end of printMenu



} // end of class
