package com.project.teman_belajar.module.quiz.controller;

import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.quiz.dto.request.CheckAnswerRequest;
import com.project.teman_belajar.module.quiz.dto.request.GenerateQuizRequest;
import com.project.teman_belajar.module.quiz.dto.request.QuizAttemptRequest;
import com.project.teman_belajar.module.quiz.dto.response.*;
import com.project.teman_belajar.module.quiz.service.questions.QueryQuestionsService;
import com.project.teman_belajar.module.quiz.service.quiz.CommandQuizService;
import com.project.teman_belajar.module.quiz.service.quiz.QueryQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final CommandQuizService commandQuizService;
    private final QueryQuizService queryQuizService;
    private final QueryQuestionsService queryQuestionsService;

    @GetMapping("/list")
    public ResponseEntity<List<PreviewQuizResponse>> findAllQuizByUserId(
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                queryQuizService.findAllQuizByUserId(user.getId())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PreviewQuestion>> findQuizById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                queryQuizService.getAllQuestionsByQuizId(
                        id
                )
        );
    }

    @GetMapping("/attempted")
    public ResponseEntity<List<AttemptedQuizResponse>> getAttemptQuizHistory(
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                queryQuizService.getAttemptQuizHistory(user.getId())
        );
    }

    @GetMapping("/attempted/detail/{quizId}/{attemptedQuizId}")
    public ResponseEntity<AttemptedQuizDetailResponse>  getAttemptQuizDetail(
            @PathVariable UUID attemptedQuizId,
            @PathVariable UUID quizId
    ) {
        return ResponseEntity.ok(
                queryQuizService.getAttemptedQuizDetail(attemptedQuizId, quizId)
        );
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAnswerResponse> checkAnswer(
            @RequestBody CheckAnswerRequest checkAnswerRequest
    ) {
        return ResponseEntity.ok(
                queryQuestionsService.checkAnswer(checkAnswerRequest)
        );
    }

    @PostMapping("/save")
    public ResponseEntity<QuizSuccessResponse> saveQuizAttempt(
            @RequestBody QuizAttemptRequest request,
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                commandQuizService.saveQuizAttempt(
                        request,
                        user.getId()
                )
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizSuccessResponse> submitQuizAttempt(
            @RequestBody QuizAttemptRequest request,
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                commandQuizService.submitQuizAttempt(
                        request,
                        user.getId()
                )
        );
    }

    @PostMapping("/generate")
    public ResponseEntity<SuccessResponse> generateQuiz(
            @RequestBody GenerateQuizRequest generateQuizRequest,
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                commandQuizService.generateQuiz(
                        generateQuizRequest,
                        user.getId()
                )
        );
    }

}
