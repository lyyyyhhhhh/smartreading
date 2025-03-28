package com.reading.ifaceutil.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_points_log")
public class UserPointsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 主键ID

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 用户ID

    @Column(nullable = false)
    private Integer points;  // 积分变动（正数增加，负数减少）

    @Column(name = "reference_table")
    private String referenceTable; // 关联的相关表名

    @Column(name = "reference_id")
    private Long referenceId;  // 关联的任务ID/兑换记录ID

    @Column(name = "description")
    private String description;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;  // 变动时间
}
