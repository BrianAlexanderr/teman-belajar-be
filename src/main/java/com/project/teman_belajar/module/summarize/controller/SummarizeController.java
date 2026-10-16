package com.project.teman_belajar.module.summarize.controller;

import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.summarize.dto.request.SummaryRequest;
import com.project.teman_belajar.module.summarize.dto.response.SummaryDetailResponse;
import com.project.teman_belajar.module.summarize.dto.response.SummaryPreviewResponse;
import com.project.teman_belajar.module.summarize.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/summary")
@RequiredArgsConstructor
public class SummarizeController {

    public final SummaryService summaryService;

    @PostMapping("/summarize")
    public ResponseEntity<SummaryDetailResponse> createSummary(
            @RequestBody SummaryRequest summaryRequest
    ) {
        return ResponseEntity.ok(
                summaryService.createCombinedSummary(summaryRequest)
        );
    }

    @GetMapping("/list")
    public ResponseEntity<List<SummaryPreviewResponse>> getListSummary(
            @RequestParam UUID folderId
    ) {
        return ResponseEntity.ok(
                summaryService.getListSummaryByFolder(folderId)
        );
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<SummaryPreviewResponse>> getListAllSummary(
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                summaryService.getAllListSummary(user.getId())
        );
    }

    @GetMapping("/detail")
    public ResponseEntity<SummaryDetailResponse> getDetailSummary(
            @RequestParam UUID summaryId
    ) {
        return ResponseEntity.ok(
                summaryService.getDetailSummary(summaryId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteSummary(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                summaryService.deleteSummary(id)
        );
    }

}
