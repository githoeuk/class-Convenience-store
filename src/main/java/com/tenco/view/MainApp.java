package com.tenco.view;

import com.tenco.service.StoreService;
import com.tenco.view.LoginFrame;

public class MainApp {
    public static void main(String[] args) {
        StoreService service = new StoreService();
        new LoginFrame(service);
    }
}