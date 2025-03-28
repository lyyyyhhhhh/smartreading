package com.reading.ifaceutil.repository;

import com.reading.ifaceutil.model.TaskRules;
import org.hibernate.annotations.SQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRulesRepository extends JpaRepository<TaskRules, Long> {

    @Query("SELECT h FROM TaskRules h WHERE h.taskType = :actionType")
    TaskRules findByTaskType(String actionType);
}
