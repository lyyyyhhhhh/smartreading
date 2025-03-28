package com.reading.ifaceutil.repository.usersignin;

import com.reading.ifaceutil.model.UserSignIn;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserSignInRepositoryImpl {
    @PersistenceContext
    private EntityManager entityManager;
    public List<UserSignIn> findFrequentUsers() {
        String jpql = "SELECT us.userId, COUNT(us.id) AS signInCount FROM UserSignIn us " +
                "GROUP BY us.userId ORDER BY signInCount DESC";
        return entityManager.createQuery(jpql, UserSignIn.class).setMaxResults(30).getResultList();
    }

}
