package com.neon.url_shortener.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "user")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
}
