package com.clarityunited.quadtrivia.exception;

public class QuizSessionNotFoundException extends RuntimeException {
    public QuizSessionNotFoundException(String message) {
        super(message);
    }
}
