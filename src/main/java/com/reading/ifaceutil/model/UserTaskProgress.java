package com.reading.ifaceutil.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_task_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "task_type")
    private String taskType;

    @Column(name = "progress_json", length = 1000)
    private String progressJson;
    // 任务状态：PENDING,COMPLETED,CLAIMED
    private String status;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "create_time")
    @CreationTimestamp
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
