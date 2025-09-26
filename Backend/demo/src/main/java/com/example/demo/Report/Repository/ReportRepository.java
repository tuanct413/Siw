package com.example.demo.Report.Repository;

import com.example.demo.Report.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByUserId(Long userId, Pageable pageable);

}
