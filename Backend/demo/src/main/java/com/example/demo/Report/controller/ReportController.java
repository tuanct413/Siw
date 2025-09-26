package com.example.demo.Report.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.User.DTO.CustomUserDetails;
import com.example.demo.dto.PagedResponse;
import com.example.demo.Report.DTO.ResponseDTO;
import com.example.demo.Report.model.Report;
import com.example.demo.Report.service.Interface.ReportInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportInterface reportService;

    // Inject qua interface, Spring sẽ tự tìm bean ReportService implements ReportInterface
    public ReportController(ReportInterface reportService) {
        this.reportService = reportService;
    }

    // Tạo report từ user
    @PostMapping("v1/user")
    public ResponseEntity<ApiResponse<ResponseDTO>> createReport(
            @RequestBody Report report,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // lấy id từ custom details
        return reportService.createReport(userId, report);
    }
    @GetMapping("v1/findbyuser")
    public ResponseEntity<ApiResponse<PagedResponse<ResponseDTO>>> getReportByid(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // lấy id từ custom details
        return reportService.getReportsByUser(userId,page,size);
    }
    @GetMapping("v2/finall")
    public ResponseEntity<ApiResponse<PagedResponse<Report>>> getAllReport(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
            ){
        return reportService.getAllReports(page,size);
    }
    // Lấy report theo id
    @GetMapping("v1/findbyid/{id}")
    public ResponseEntity<ApiResponse<Report>> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    // Xóa Report Theo id
    @DeleteMapping("v1/remove/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteReportById(
            @PathVariable Long id ,
            Authentication authentication
    ){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        return  reportService.deleteReport(id,userId);
    }
}
