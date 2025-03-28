package com.reading.ifaceutil.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "highlights")
@Getter
@Setter
public class Highlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // 确保这里是 Long 类型，并且字段名称与数据库一致

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private Long highlightId;

    @Column(name = "start_index", nullable = false)
    private Integer startIndex;

    @Column(name = "end_index", nullable = false)
    private Integer endIndex;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "content_text")
    private String contentText;

    @Column(name = "create_time", columnDefinition = "DATETIME")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "DATETIME")
    private LocalDateTime updateTime;

    // 在保存新对象时，设置 createTime 和 updateTime
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    // 在更新对象时，设置 updateTime
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Highlight{" +
                "id=" + id +
                ", userId=" + userId +
                ", articleId=" + articleId +
                ", highlightId=" + highlightId +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", comment='" + comment + '\'' +
                ", contentText='" + contentText + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}


