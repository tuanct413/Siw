package com.example.demo.Report.service.Interface;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PagedResponse;
import com.example.demo.Report.DTO.ResponseDTO;
import com.example.demo.Report.model.Report;
import org.springframework.http.ResponseEntity;

public interface ReportInterface {
    /// tạo một report mới
    ResponseEntity<ApiResponse<ResponseDTO>> createReport(long userId, Report report);
    /// lấy Tất cả các danh sách Report của một User
     ResponseEntity<ApiResponse<PagedResponse<ResponseDTO>>> getReportsByUser(Long userId, int page, int size);
    /// Lấy tất cả các report của tất cả User
     ResponseEntity<ApiResponse<PagedResponse<Report>>> getAllReports(int page, int size);
//    /// Lấy một report bất kì qua idreport
    ResponseEntity<ApiResponse<Report>> getReportById(Long id);
//    /// xóa một report bất kì qua idreport
    ResponseEntity<ApiResponse<Boolean>> deleteReport(Long id,Long userId);

}
