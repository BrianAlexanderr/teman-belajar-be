package com.project.teman_belajar.module.quiz.utils;

import com.project.teman_belajar.module.quiz.dto.response.PreviewQuestion;
import com.project.teman_belajar.module.quiz.entities.Questions;

public class QuestionDtoHelper {

    public static PreviewQuestion buildPreviewQuestionDto(Questions question) {
        return PreviewQuestion.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answerA(question.getAnswerA())
                .answerB(question.getAnswerB())
                .answerC(question.getAnswerC())
                .answerD(question.getAnswerD())
                .build();
    }
}
