package com.reading.ifaceutil.repository;

import com.reading.ifaceutil.model.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {

    // 根据用户ID查询当前总积分
    Optional<UserPoints> findByUserId(Long userId);
}
