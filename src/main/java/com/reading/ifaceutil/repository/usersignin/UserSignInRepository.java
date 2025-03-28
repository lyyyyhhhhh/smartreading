package com.reading.ifaceutil.repository.usersignin;

import com.reading.ifaceutil.model.UserSignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserSignInRepository extends JpaRepository<UserSignIn, Long>, UserSignInRepositoryCustom {
    Optional<UserSignIn> findByUserIdAndDate(Long userId, LocalDate date);
}
