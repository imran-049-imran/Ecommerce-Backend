package com.restapis.ecommerce.io;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthenticationRequest {

    private String email;
    private String password;



}
