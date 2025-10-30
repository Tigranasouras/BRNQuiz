package com.exam.brnquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;

public class IntroActivity extends AppCompatActivity {

    private Button beginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        beginButton = findViewById(R.id.button);

        //Randomize the button color each launch
        // random color via HSV (random hue, decent saturation/value)
        Random r = new Random();
        float hue = r.nextInt(360);
        int color = Color.HSVToColor(new float[]{hue, 0.70f, 0.95f});
        // Apply as background tint so text remains legible with Material/Compat buttons
        ViewCompat.setBackgroundTintList(beginButton, ColorStateList.valueOf(color));

        //On Begin click launch QuizActivity
        beginButton.setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class))
        );
    }
}






