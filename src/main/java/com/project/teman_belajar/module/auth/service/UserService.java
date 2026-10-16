package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.common.validator.EmailValidator;
import com.project.teman_belajar.module.auth.dto.request.UpdateUserProfileReuqest;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.dto.response.UserProfileResponse;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.exception.custom_exception.UserNotFoundException;
import com.project.teman_belajar.module.auth.repository.UserRepository;
import com.project.teman_belajar.module.quiz.projection.QuizStats;
import com.project.teman_belajar.module.quiz.service.quiz.QueryQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final QueryQuizService queryQuizService;

    public UserProfileResponse getUserProfile(UUID userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Pengguna tidak ditemukan"));

        return transformUserProfileResponse(user);
    }

    public SuccessResponse updateUserProfile(UpdateUserProfileReuqest request, Users requestedUser) {
        Users user = userRepository.findById(requestedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Pengguna tidak ditemukan"));

        if(EmailValidator.isInvalidEmail(request.email())) {
            throw new IllegalArgumentException("Email tidak valid!");
        }

        user.setEmail(request.email());
        user.setName(request.name());

        userRepository.save(user);

        return new SuccessResponse(
                "Berhasil mengubah profile pengguna",
                new Date()
        );
    }



    private UserProfileResponse transformUserProfileResponse(Users user){

        QuizStats quizStats = queryQuizService.getQuizStats(user.getId());

        return UserProfileResponse.builder()
                .userName(user.getName())
                .email(user.getEmail())
                .jumlahAttemptedQuiz(quizStats.getTotalAttempts())
                .averageScore(quizStats.getAverageScore())
                .build();
    }
}
