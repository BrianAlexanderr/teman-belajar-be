package com.project.teman_belajar.module.quiz.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Questions {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String question_text;

	private String answerA;
	private String answerB;
	private String answerC;
	private String answerD;

	private String correctAnswer;

	private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<QuestionAttempt> questionAttemptList;
}
