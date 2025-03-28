package com.reading.ifaceutil.repository;

import com.reading.ifaceutil.model.UserTaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserTaskProgressRepository extends JpaRepository<UserTaskProgress, Long> {
    List<UserTaskProgress> findByUserIdAndCreateDateAndTaskTypeOrderById(Long userId, LocalDate createDate, String taskType);
    List<UserTaskProgress> findByUserIdAndCreateDateAndTaskType(Long userId, LocalDate createDate, String taskType);
    List<UserTaskProgress> findByUserIdAndCreateDate(Long userId, LocalDate createDate);

    // 根据状态查询，按 create_time 字段降序排序
    List<UserTaskProgress> findByUserIdAndCreateDateAndTaskTypeAndStatusOrderByCreateTimeDesc(
            Long userId, LocalDate createDate, String taskType, String status);
}
