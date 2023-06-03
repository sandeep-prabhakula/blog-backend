package com.sandeepprabhakula.blogging.data;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("users")
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String email;

    private String password;

    private String roles;

    public User(String email,String password,String roles){
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

}
