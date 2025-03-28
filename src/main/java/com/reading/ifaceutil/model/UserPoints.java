package com.reading.ifaceutil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_points")
public class UserPoints {

    @Id
    @Column(name = "user_id")
    private Long userId;  // 用户ID

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;  // 用户当前总积分

    @Column(name = "coins", nullable = false)
    private Integer coins = 0;  // 用户当前总积分


    @Column(name = "last_update_time", nullable = false)
    private LocalDateTime lastUpdateTime;  // 最后更新时间

}
