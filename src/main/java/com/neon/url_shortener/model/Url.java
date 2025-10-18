    package com.neon.url_shortener.model;

    import lombok.Data;
    import org.bson.types.ObjectId;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.DBRef;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.Field;

    import java.time.LocalDateTime;

    @Data
    @Document(collection = "url")
    public class Url {
        @Id
        private String id;
        private String originalUrl;
        private String shortUrl;
        private LocalDateTime createdAt;
        @DBRef(lazy = true)
        private User createdBy;
        private String qrCodeBase64; // Store as Base64 string

    }
