package com.clarityunited.quadtrivia.controller;
import com.clarityunited.quadtrivia.dto.assesment.CheckAnswersResponse;
import com.clarityunited.quadtrivia.dto.quiz.QuizResponse;
import com.clarityunited.quadtrivia.dto.quizanswer.SubmittedQuizAnswerRequest;
import com.clarityunited.quadtrivia.service.QuizService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class QuizController {

    private final QuizService quizService;

   // public QuizController(QuizService quizService) {
   //     this.quizService = quizService;
   // }

    @GetMapping("/questions")
    public Mono<QuizResponse> getQuestions(Authentication authentication) {
        return quizService.createQuizForUser(authentication.getName());
    }

    @PostMapping("/checkanswers")
    public CheckAnswersResponse checkAnswers(@Valid @RequestBody SubmittedQuizAnswerRequest request,
                                             Authentication authentication) {
        return quizService.checkAnswers(authentication.getName(), request);
    }
}