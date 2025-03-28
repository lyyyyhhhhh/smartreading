package com.reading.ifaceutil.repository.userpointslog;

import com.reading.ifaceutil.utils.constants.AppConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserPointsLogRepositoryImpl implements UserPointsLogRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <Object> List<Object> findAllByTableNameAndUserId(String tableName, Long userId, Class<Object> entityClass) {
        if (!AppConstants.ALLOWED_TABLES.contains(tableName)) {
            return null;
        }
        String sql = "SELECT * FROM " + tableName + "WHERE user_id = " + userId;
        Query query = entityManager.createQuery(sql, entityClass);
        return query.getResultList();
    }
}
