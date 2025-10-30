package com.exam.brnquiz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;

public class BRNViewModel extends ViewModel {

    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> correctCount = new MutableLiveData<>(0);

    public BRNViewModel() {
        // Set initial (hardcoded) question
        currentQuestion.setValue(new Question(
                "What is the capital of Colombia?",
                Arrays.asList("Medellin", "Madrid", "Paris", "Bogota"),
                3 // Bogota
        ));
    }

    public LiveData<Question> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<Integer> getCorrectCount() {
        return correctCount;
    }

    public void setQuestion(Question q) {
        currentQuestion.setValue(q);
    }

    public void incrementCorrect() {
        Integer c = correctCount.getValue();
        if (c == null) c = 0;
        correctCount.setValue(c + 1);
    }
}
