package com.project.teman_belajar.module.quiz.service.questions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.teman_belajar.common.global_exception.exception.ResourceNotFoundException;
import com.project.teman_belajar.module.quiz.dto.request.CheckAnswerRequest;
import com.project.teman_belajar.module.quiz.dto.response.CheckAnswerResponse;
import static com.project.teman_belajar.module.quiz.utils.QuestionDtoHelper.buildPreviewQuestionDto;
import com.project.teman_belajar.module.quiz.entities.Questions;
import com.project.teman_belajar.module.quiz.repository.QuestionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryQuestionsService {

    private final QuestionsRepository questionsRepository;

    public List<Questions> getAllQuestionsByQuizId(UUID quizId) {
        List<Questions> questions = questionsRepository.findByQuizId(quizId);

        if(questions.isEmpty()){
            throw new ResourceNotFoundException("Kuis ini tidak memiliki pertanyaan!");
        }

        return questions;
    }

    public CheckAnswerResponse checkAnswer(CheckAnswerRequest request) {
        Optional<Questions> existingQuestion = questionsRepository.findById(request.questionId());

        if(existingQuestion.isEmpty()) {
            throw new ResourceNotFoundException("Pertanyaan tidak ditemukan");
        }

        Questions question = existingQuestion.get();

        String correctAnswerField = findCorrectAnswerField(question);

        Boolean isCorrect = isAnswerCorrect(question, request.answer());

        return CheckAnswerResponse.builder()
                .correctAnswerField(correctAnswerField)
                .isCorrect(isCorrect)
                .correctAnswer(question.getCorrectAnswer())
                .explanation(question.getExplanation())
                .build();
    }

    public Boolean isAnswerBelongsToQuestion(Questions question, String answer) {
        List<String> questionsAnswer = List.of(
                question.getAnswerA(),
                question.getAnswerB(),
                question.getAnswerC(),
                question.getAnswerD()
        );
        return questionsAnswer.stream()
                .anyMatch(answer::equals);
    }

    private String findCorrectAnswerField(Questions question) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> fieldMap = objectMapper.convertValue(
                buildPreviewQuestionDto(question),
                new TypeReference<>() {}
        );

        return fieldMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null &&
                        entry.getValue().toString().contains(question.getCorrectAnswer())
                )
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(
                        () -> new RuntimeException("Field dengan Jawaban Benar tidak ditemukan!")
                );
    }

    private Boolean isAnswerCorrect(Questions question, String answer) {
        return question.getCorrectAnswer().equals(answer);
    }

}
