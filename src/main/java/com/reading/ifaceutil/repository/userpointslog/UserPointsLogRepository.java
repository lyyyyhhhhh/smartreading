package com.reading.ifaceutil.repository.userpointslog;

import com.reading.ifaceutil.model.UserPointsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPointsLogRepository extends JpaRepository<UserPointsLog, Long>, UserPointsLogRepositoryCustom {
    List<UserPointsLog> findByUserIdOrderByCreateTimeDesc(Long userId);
}
