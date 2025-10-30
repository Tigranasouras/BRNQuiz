package com.exam.brnquiz;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> choices;
    private int correctIndex;

    public Question(String questionText, List<String> choices, int correctIndex) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }
}
