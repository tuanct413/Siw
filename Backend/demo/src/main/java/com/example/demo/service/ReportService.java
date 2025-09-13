package com.example.demo.service;

import com.example.demo.DTO.ApiResponse;

import com.example.demo.DTO.PagedResponse;
import com.example.demo.DTO.ResponseDTO;
import com.example.demo.Model.Report;
import com.example.demo.Model.User;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Implementation.ReportInterface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class ReportService implements ReportInterface {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }
    /// tạo một report mới
    @Override
    public ResponseEntity<ApiResponse<ResponseDTO>> createReport(long userId, Report report) {
        Optional<User> optionalUser  = userRepository.findById(userId);
        if(optionalUser .isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body
                    (new ApiResponse<>("User Không tồn tại ",null));

        }
        User user = optionalUser.get();
        report.setUser(user);
        report.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        Report savedReport = reportRepository.save(report);
        ResponseDTO dto = new ResponseDTO(
                savedReport.getId(),
                savedReport.getTitle(),
                savedReport.getContent(),
                savedReport.getCreatedAt()
        );
        return ResponseEntity.ok(new ApiResponse<>(" Tạo Report Thành công", dto));
    }
    ///lấy Tất cả các danh sách Report của một User
    @Override
    public ResponseEntity<ApiResponse<PagedResponse<ResponseDTO>>> getReportsByUser(Long userId, int page, int size) {
        Optional<User> optionalUser  = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("User Không tồn tại", null));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report> reports = reportRepository.findByUserId(userId, pageable);

        // Convert Report -> DTO
        List<ResponseDTO> dtos = reports.getContent()
                .stream()
                .map(report -> new ResponseDTO(
                        report.getId(),
                        report.getTitle(),
                        report.getContent(),
                        report.getCreatedAt()
                ))
                .toList();

        PagedResponse<ResponseDTO> pagedResponse = new PagedResponse<>(
                dtos,
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages(),
                reports.isLast()
        );

        return ResponseEntity.ok(new ApiResponse<>("Danh sách report của user", pagedResponse));
    }
    /// Lấy tất cả các report của tất cả User
    public  ResponseEntity<ApiResponse<PagedResponse<Report>>> getAllReports(int page, int size){
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report>reports = reportRepository.findAll(pageable);
        PagedResponse<Report> pagedResponse = new PagedResponse<>(
                reports.getContent(),
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages(),
                reports.isLast()
        );
        return ResponseEntity.ok(new ApiResponse<>("Danh sách report của tất cả user", pagedResponse));
    }
//    /// Lấy một report bất kì qua idreport
//    public  Report getReportById(Long id){
//
//    }
//    /// xóa một report bất kì qua idreport
//    public boolean deleteReport(Long id){
//
//    }
}
