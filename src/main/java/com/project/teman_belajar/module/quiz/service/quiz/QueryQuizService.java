package com.project.teman_belajar.module.quiz.service.quiz;

import com.project.teman_belajar.common.global_exception.exception.ResourceNotFoundException;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.quiz.dto.response.AttemptedQuizDetailResponse;
import static com.project.teman_belajar.module.quiz.dto.response.AttemptedQuizDetailResponse.QuestionAttemptResponse;
import com.project.teman_belajar.module.quiz.dto.response.AttemptedQuizResponse;
import com.project.teman_belajar.module.quiz.dto.response.PreviewQuestion;
import com.project.teman_belajar.module.quiz.dto.response.PreviewQuizResponse;
import com.project.teman_belajar.module.quiz.entities.QuestionAttempt;
import com.project.teman_belajar.module.quiz.entities.Questions;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import com.project.teman_belajar.module.quiz.entities.QuizAttempts;
import com.project.teman_belajar.module.quiz.projection.PreviewQuiz;
import com.project.teman_belajar.module.quiz.projection.QuizStats;
import com.project.teman_belajar.module.quiz.repository.QuizAttemptsRepository;
import com.project.teman_belajar.module.quiz.repository.QuizRepository;
import com.project.teman_belajar.module.quiz.service.questions.QueryQuestionsService;
import com.project.teman_belajar.module.quiz.utils.QuestionDtoHelper;
import com.project.teman_belajar.module.summarize.entities.Summary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryQuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptsRepository quizAttemptsRepository;
    private final QueryQuestionsService queryQuestionsService;

    public List<PreviewQuizResponse> findAllQuizByUserId(UUID userId) {
        List<PreviewQuiz> quizzes = quizRepository.findAllByUserId(userId);

        return quizzes.stream()
                .map(this::transformQuizResponse)
                .toList();
    }

    public List<PreviewQuestion> getAllQuestionsByQuizId(UUID quizId) {
        List<Questions> questions = queryQuestionsService.getAllQuestionsByQuizId(quizId);

        return buildPreviewQuestionList(questions);
    }

    @Transactional(readOnly = true)
    public List<AttemptedQuizResponse> getAttemptQuizHistory(UUID userId) {

        List<QuizAttempts> attemptedQuiz = quizAttemptsRepository.findByUserIdWithRelations(userId);

        return attemptedQuiz.stream()
                .map(aq -> AttemptedQuizResponse.builder()
                        .quizId(aq.getQuiz() != null ? aq.getQuiz().getId() : null)
                        .attemptedQuizId(aq.getId())
                        .quizTitle(aq.getQuiz() != null ? aq.getQuiz().getTitle() : "-")
                        .folderName(Optional.ofNullable(aq.getQuiz())
                                .map(Quiz::getSummary)
                                .map(Summary::getFolders)
                                .map(Folders::getName)
                                .orElse("-"))
                        .score(aq.getScore() == null ? "-" : aq.getScore().toString())
                        .countAttemptedQuestion(String.valueOf(findCorrectQuestionsCount(aq)))
                        .attemptedAt(formatDate(aq.getAttemptAt()))
                        .build()
                ).toList();

    }

    @Transactional(readOnly = true)
    public AttemptedQuizDetailResponse getAttemptedQuizDetail(UUID attemptedQuizId, UUID quizId) {

        QuizAttempts existingQuizAttempt = quizAttemptsRepository.findById(attemptedQuizId)
                .orElseThrow(() -> new ResourceNotFoundException("Pengerjaan Kuis tidak ditemukan!"));

        if (!existingQuizAttempt.getQuiz().getId().equals(quizId)) {
            throw new IllegalArgumentException("Attempt ini tidak cocok dengan ID kuis yang diberikan!");
        }

        return AttemptedQuizDetailResponse.builder()
                .quizId(quizId)
                .questionAttemptResponse(
                        transformQuestionAttemptResponse(existingQuizAttempt.getQuestionAttempts())
                )
                .build();
    }

    private long findCorrectQuestionsCount(QuizAttempts quizAttempt) {
        List<QuestionAttempt> attemptedQuestions = quizAttempt.getQuestionAttempts();
        return attemptedQuestions.stream()
                .filter(QuestionAttempt::getIsCorrect)
                .count();
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "d MMMM yyyy",
                Locale.of("id", "ID")
        );

        return formatter.format(date);
    }

    private PreviewQuizResponse transformQuizResponse(PreviewQuiz quiz) {

        LocalDateTime dateTime = quiz.getCreatedAt();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm:ss a", Locale.of("id", "ID"));

        String formattedDate = dateTime.format(formatter);

        return PreviewQuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .createdAt(formattedDate)
                .build();
    }

    private List<PreviewQuestion> buildPreviewQuestionList(List<Questions> questionsList) {
        return questionsList.stream()
                .map(QuestionDtoHelper::buildPreviewQuestionDto)
                .toList();
    }

    private List<QuestionAttemptResponse> transformQuestionAttemptResponse(List<QuestionAttempt> questionAttempts) {
        return questionAttempts.stream()
                .map(qa -> QuestionAttemptResponse.builder()
                        .questionId(qa.getQuestion().getId())
                        .isCorrect(qa.getIsCorrect())
                        .selectedAnswer(qa.getSelectedAnswer())
                        .explanation(qa.getQuestion().getExplanation())
                        .build()
                ).toList();
    }

    public QuizStats getQuizStats(UUID userId) {
        return quizAttemptsRepository.getUserQuizStats(
                userId
        );
    }
}
