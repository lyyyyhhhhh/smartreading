package com.reading.ifaceutil.repository.userpointslog;

import java.util.List;

public interface UserPointsLogRepositoryCustom {

    // 用户ID查询所有积分变动记录
    <T> List<T> findAllByTableNameAndUserId(String tableName, Long userId, Class<T> entityClass);
}
