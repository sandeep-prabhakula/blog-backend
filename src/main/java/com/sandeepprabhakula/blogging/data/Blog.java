package com.sandeepprabhakula.blogging.data;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("blogs")
@Setter
@Getter
@RequiredArgsConstructor
public class Blog {

    @Id
    private String id;

    private String title;

    private String description;

    private String postedAt;

    private String image;
}

