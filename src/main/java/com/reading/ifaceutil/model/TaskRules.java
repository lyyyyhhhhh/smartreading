package com.reading.ifaceutil.model;


import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_rules", uniqueConstraints = {
        @UniqueConstraint(name = "idx_type", columnNames = {"task_type"})
})
@Data
public class TaskRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "condition_json", length = 1000)
    private String conditionJson;

    @Column(name = "reward_points")
    private Integer rewardPoints;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "is_active")
    private Boolean isActive;

    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "update_time")
    private LocalDateTime updateTime;

}
