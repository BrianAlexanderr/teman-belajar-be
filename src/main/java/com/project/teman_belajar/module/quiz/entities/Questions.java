package com.project.teman_belajar.module.quiz.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Questions {

	@Id
	@GeneratedValue
	private Integer id;

	private String question_text;

	private String answer_a;
	private String answer_b;
	private String answer_c;
	private String answer_d;

	private String correct_answer;

	private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
