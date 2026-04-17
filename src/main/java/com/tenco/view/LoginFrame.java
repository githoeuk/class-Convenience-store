package com.tenco.view;

import com.tenco.service.StoreService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private StoreService service;
    private JTextField idField;
    private JPasswordField pwField;

    public LoginFrame(StoreService service) {
        this.service = service;

        setTitle("로그인");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(3,2,10,10));

        add(new JLabel("아이디"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("비밀번호"));
        pwField = new JPasswordField();
        add(pwField);

        JButton btn = new JButton("로그인");
        add(new JLabel());
        add(btn);

        btn.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        try {
            boolean result = service.login(
                    idField.getText(),
                    new String(pwField.getPassword())
            );

            if (result) {
                new MainFrame(service);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "로그인 실패");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}