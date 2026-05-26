package com.analyticsService.Analytics.Service.repositories;

import com.analyticsService.Analytics.Service.entities.TaskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskMetricRepository extends JpaRepository<TaskMetric, Long> {

    Optional<TaskMetric> findByUserId(Long userId);
    List<TaskMetric> findAll();
}
