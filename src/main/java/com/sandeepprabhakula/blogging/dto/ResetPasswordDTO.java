package com.sandeepprabhakula.blogging.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordDTO {
    private String uid;
    private String password;
}
