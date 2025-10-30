package com.exam.brnquiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

public class QuizActivity extends AppCompatActivity {

    private BRNViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom);
            return insets;
        });

        // Activity-scoped ViewModel (shared by fragments)
        vm = new ViewModelProvider(this).get(BRNViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.scoreFrag, new ScoreFragment(), "ScoreFragment")
                    .replace(R.id.quizFrag, new QuestionFragment(), "QuestionFragment")
                    .commit();
        }
    }
}


//I made a project messing around with this feature so thank god for that
//Task Q feature = “Answer by Voice” with microphone permission.
//Lets the user speak an answer, auto-selects the matching choice, and enables Submit.
//Manifest: RECORD_AUDIO permission in manifest
//A mic button in fragment_question.xml.
//2 ActivityResultLaunchers for:
//requesting runtime audio permission (audioPermLauncher)
//launching speech recognition (speechLauncher)
//Helper methods: startVoiceInput() and handleSpokenAnswer(String spoken)
//Click listener for voiceBtn that requests permission or starts voice input.
