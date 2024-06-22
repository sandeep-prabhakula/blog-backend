package com.sandeepprabhakula.blogging.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private Object userData;
    private String jwtToken;
}
