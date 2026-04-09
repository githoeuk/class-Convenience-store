package com.tenco.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Admin {

    private int id;
    private String admin_id;
    private String password;
    private String name;

}
