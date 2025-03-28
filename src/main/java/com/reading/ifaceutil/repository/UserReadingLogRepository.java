package com.reading.ifaceutil.repository;

import com.reading.ifaceutil.model.UserReadingLog;
import com.reading.ifaceutil.model.UserSignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserReadingLogRepository extends JpaRepository<UserReadingLog, Long> {
    Optional<UserReadingLog> findByUserIdAndDate(Long userId, LocalDate date);
}
