package com.project.teman_belajar.module.quiz.service.quiz;

import com.project.teman_belajar.common.global_exception.exception.ResourceNotFoundException;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.repository.UserRepository;
import com.project.teman_belajar.module.llm.dto.response.GeneratedQuizResponse;
import com.project.teman_belajar.module.llm.dto.response.GeneratedQuizResponse.GeneratedQuestion;
import com.project.teman_belajar.module.llm.service.GeminiQuizService;
import com.project.teman_belajar.module.quiz.dto.request.GenerateQuizRequest;
import com.project.teman_belajar.module.quiz.dto.request.QuizAttemptRequest;
import com.project.teman_belajar.module.quiz.dto.request.QuizAttemptRequest.QuestionAttemptRequest;
import com.project.teman_belajar.module.quiz.dto.response.QuizSuccessResponse;
import com.project.teman_belajar.module.quiz.entities.QuestionAttempt;
import com.project.teman_belajar.module.quiz.entities.Questions;
import com.project.teman_belajar.module.quiz.entities.Quiz;
import com.project.teman_belajar.module.quiz.entities.QuizAttempts;
import com.project.teman_belajar.module.quiz.exception.custom_exception.AnswerNotFoundException;
import com.project.teman_belajar.module.quiz.repository.QuizAttemptsRepository;
import com.project.teman_belajar.module.quiz.repository.QuizRepository;
import com.project.teman_belajar.module.quiz.service.questions.QueryQuestionsService;
import com.project.teman_belajar.module.summarize.entities.Summary;
import com.project.teman_belajar.module.summarize.repository.SummaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandQuizService {

    private final UserRepository userRepository;
    private final SummaryRepository summaryRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptsRepository  quizAttemptsRepository;
    private final GeminiQuizService geminiQuizService;

    private final QueryQuestionsService queryQuestionsService;

    public static final Integer QUESTION_COUNT = 10;

    @Transactional
    public SuccessResponse generateQuiz(GenerateQuizRequest generateQuizRequest, UUID userId) {

        Users user = userRepository.getReferenceById(userId);
        Summary summary = summaryRepository.getReferenceById(generateQuizRequest.summary().id());


        GeneratedQuizResponse aiResponse;
        try {
            aiResponse = geminiQuizService.generateQuiz(
                    generateQuizRequest.summary().title(),
                    generateQuizRequest.summary().keyPoint(),
                    generateQuizRequest.summary().content(),
                    QUESTION_COUNT
            );
            log.info("Kuis berhasil dibuat!");
        } catch (Exception e) {
            log.error("Error generating quiz from Gemini AI: {}", e.getMessage(), e);

            throw new RuntimeException("Gagal membuat kuis. Layanan AI sedang sibuk atau terjadi kesalahan.");
        }

        Quiz quiz = mappingQuiz(aiResponse, user, summary, generateQuizRequest.summary().title());

        quizRepository.save(quiz);

        return new SuccessResponse(
                "Berhasil Membuat Kuis!",
                new Date()
        );
    }

    private Quiz mappingQuiz(GeneratedQuizResponse response, Users user, Summary summary, String requestSummaryTitle) {
        Quiz quiz = new Quiz();

        String quizTitle = response.title() != null ? response.title() :requestSummaryTitle + " Quiz";
        quiz.setTitle(quizTitle);
        quiz.setSummary(summary);
        quiz.setUser(user);

        List<Questions> questions = mappingQuestions(response.questions(), quiz);

        quiz.setQuestionsList(questions);

        return quiz;
    }

    private List<Questions> mappingQuestions(List<GeneratedQuestion> questions, Quiz quiz) {
        return questions.stream()
                .map(generatedQuestion -> {
                    Questions question = convertToQuestion(generatedQuestion);
                    question.setQuiz(quiz);
                    return question;
                })
                .toList();
    }

    private Questions convertToQuestion(GeneratedQuestion generatedQuestion) {
        Questions question = new Questions();
        question.setQuestion(generatedQuestion.question());
        question.setAnswerA(generatedQuestion.answerA());
        question.setAnswerB(generatedQuestion.answerB());
        question.setAnswerC(generatedQuestion.answerC());
        question.setAnswerD(generatedQuestion.answerD());
        question.setCorrectAnswer(generatedQuestion.correctAnswer());
        question.setExplanation(generatedQuestion.explanation());
        return question;
    }

    @Transactional
    public QuizSuccessResponse saveQuizAttempt(QuizAttemptRequest request, UUID userId) {
        return processQuizAttempt(request, userId, false);
    }

    @Transactional
    public QuizSuccessResponse submitQuizAttempt(QuizAttemptRequest request, UUID userId) {
        return processQuizAttempt(request, userId, true);
    }

    private QuizSuccessResponse processQuizAttempt(QuizAttemptRequest request, UUID userId, boolean isSubmit) {
        Quiz existingQuiz = quizRepository.getReferenceById(request.quizId());
        Users user = userRepository.getReferenceById(userId);

        List<Questions> attemptedQuestions = queryQuestionsService.getAllQuestionsByQuizId(
                request.quizId()
        );

        if (isSubmit) {
            validateAllQuestionsAnswered(attemptedQuestions, request.questionAttempts());
        }

        Map<UUID, Questions> questionMap = attemptedQuestions.stream()
                .collect(Collectors.toMap(Questions::getId, Function.identity()));

        Optional<QuizAttempts> draftAttemptOpt = quizAttemptsRepository.findFirstDraft(request.quizId(), userId);

        QuizAttempts quizAttempts;

        if (draftAttemptOpt.isPresent()) {
            quizAttempts = draftAttemptOpt.get();
            updateExistingQuizAttempts(quizAttempts, request, questionMap);
        } else {
            quizAttempts = mappingQuizAttempts(request, existingQuiz, user, questionMap);
        }

        Integer score = null;

        if (isSubmit) {
            score = calculateQuizScore(quizAttempts.getQuestionAttempts());
            quizAttempts.setScore(score);
        }

        quizAttemptsRepository.save(quizAttempts);

        String successMessage = isSubmit ? "Berhasil Mengumpulkan Kuis!" : "Berhasil Menyimpan Kuis!";
        return new QuizSuccessResponse(successMessage, score, new Date());
    }

    private void updateExistingQuizAttempts(QuizAttempts existingAttempts, QuizAttemptRequest request, Map<UUID, Questions> questionMap) {

        Map<UUID, QuestionAttempt> existingAnswersMap = existingAttempts.getQuestionAttempts().stream()
                .collect(Collectors.toMap(qa -> qa.getQuestion().getId(), Function.identity()));

        for (QuestionAttemptRequest answerRequest : request.questionAttempts()) {
            Questions question = questionMap.get(answerRequest.questionId());

            if(!queryQuestionsService.isAnswerBelongsToQuestion(question, answerRequest.selectedAnswer())) {
                throw new AnswerNotFoundException("Jawaban tidak ditemukan!");
            }

            if (existingAnswersMap.containsKey(question.getId())) {
                QuestionAttempt existingAnswer = existingAnswersMap.get(question.getId());

                existingAnswer.setSelectedAnswer(answerRequest.selectedAnswer());
                existingAnswer.setIsCorrect(answerRequest.isCorrect());
            } else {
                QuestionAttempt newAnswer = new QuestionAttempt();
                newAnswer.setQuizAttempt(existingAttempts);
                newAnswer.setQuestion(question);
                newAnswer.setSelectedAnswer(answerRequest.selectedAnswer());
                newAnswer.setIsCorrect(answerRequest.isCorrect());

                existingAttempts.getQuestionAttempts().add(newAnswer);
            }
        }
    }

    private void validateAllQuestionsAnswered(List<Questions> attemptedQuestions, List<QuestionAttemptRequest> attemptRequests) {
        Set<UUID> requiredQuestionIds = attemptedQuestions.stream()
                .map(Questions::getId)
                .collect(Collectors.toSet());

        Set<UUID> answeredQuestionIds = attemptRequests.stream()
                .map(QuestionAttemptRequest::questionId)
                .collect(Collectors.toSet());

        Set<UUID> missingQuestionIds = new HashSet<>(requiredQuestionIds);
        missingQuestionIds.removeAll(answeredQuestionIds);

        if (!missingQuestionIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Tidak dapat submit kuis. Ada " + missingQuestionIds.size() + " pertanyaan yang belum dijawab!"
            );
        }
    }

    private Integer calculateQuizScore(List<QuestionAttempt> questionAttempts) {
        if (questionAttempts == null || questionAttempts.isEmpty()) {
            return 0;
        }

        long correctAnswersCount = questionAttempts.stream()
                .filter(QuestionAttempt::getIsCorrect)
                .count();

        int totalQuestions = questionAttempts.size();

        double score = ((double) correctAnswersCount / totalQuestions) * 100;

        return (int) Math.round(score);
    }

    private QuizAttempts mappingQuizAttempts(QuizAttemptRequest request, Quiz existingQuiz, Users user, Map<UUID, Questions> questionsMap) {
        QuizAttempts quizAttempts = new QuizAttempts();

        quizAttempts.setQuiz(existingQuiz);
        quizAttempts.setUser(user);
        quizAttempts.setAttemptAt(new Date());

        List<QuestionAttempt> questionAttempts = request.questionAttempts().stream()
                .map(attemptRequest -> convertToQuestionAttempt(attemptRequest, questionsMap, quizAttempts))
                .toList();

        quizAttempts.setQuestionAttempts(questionAttempts);

        return quizAttempts;
    }

    private QuestionAttempt convertToQuestionAttempt(QuestionAttemptRequest request, Map<UUID, Questions> questionsMap, QuizAttempts quizAttempts) {
        QuestionAttempt questionAttempt = new QuestionAttempt();

        Questions question = questionsMap.get(request.questionId());

        if (question == null) {
            throw new ResourceNotFoundException("Pertanyaan tidak ditemukan!");
        }

        questionAttempt.setQuestion(question);
        questionAttempt.setIsCorrect(request.isCorrect());

        if(!queryQuestionsService.isAnswerBelongsToQuestion(question, request.selectedAnswer())) {
            throw new AnswerNotFoundException("Jawaban tidak ditemukan!");
        }

        questionAttempt.setSelectedAnswer(request.selectedAnswer());
        questionAttempt.setQuizAttempt(quizAttempts);
        return questionAttempt;
    }
}
