package com.reading.ifaceutil.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_purchased_articles")
public class UserPurchasedArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;  // 用户ID

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "article_name")
    private String articleName;

    @Column(name = "purchase_price")
    private int purchasePrice;

    @Column(name = "purchase_time")
    @CreationTimestamp
    private LocalDateTime purchaseTime;

}
