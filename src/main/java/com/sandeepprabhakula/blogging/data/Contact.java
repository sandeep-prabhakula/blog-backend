package com.sandeepprabhakula.blogging.data;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("contact")
@Setter
@Getter
@RequiredArgsConstructor
public class Contact {

    @Id
    private String id;

    private String name;

    private String email;

    private String message;
}
