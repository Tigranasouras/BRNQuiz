package com.exam.brnquiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment {

    // --- Views ---
    private TextView questionText;
    private ListView answersList;
    private Button submitBtn;
    private Button voiceBtn;

    private ArrayAdapter<String> adapter;
    private int selectedIndex = -1;

    private BRNViewModel vm;

    private final ActivityResultLauncher<String> audioPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    startVoiceInput();
                } else {
                    Toast.makeText(requireContext(), "Mic permission denied.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> results = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        handleSpokenAnswer(results.get(0));
                    } else {
                        Toast.makeText(requireContext(), "Didn't catch a valid choice.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public QuestionFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(BRNViewModel.class); // shared VM
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        bindViews(root);
        setupListView();
        setupListeners();
        observeViewModel();
    }
    private void bindViews(@NonNull View root) {
        questionText = root.findViewById(R.id.questionText);
        answersList  = root.findViewById(R.id.answersList);
        submitBtn    = root.findViewById(R.id.submitBtn);
        voiceBtn     = root.findViewById(R.id.voiceBtn);
    }

    private void setupListView() {
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>()
        );
        answersList.setAdapter(adapter);
        answersList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        submitBtn.setEnabled(false); //disables submit
    }

    private void setupListeners() {
        answersList.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            submitBtn.setEnabled(true);
        });

        submitBtn.setOnClickListener(v -> onSubmit());


        if (voiceBtn != null) { //if request needed
            voiceBtn.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    audioPermLauncher.launch(Manifest.permission.RECORD_AUDIO);
                } else {
                    startVoiceInput();
                }
            });
        }
    }

    private void observeViewModel() {
        vm.getCurrentQuestion().observe(getViewLifecycleOwner(), q -> {
            if (q == null) return;

            //resets
            selectedIndex = -1;
            submitBtn.setEnabled(false);
            answersList.clearChoices();

            questionText.setText(q.getQuestionText()); //updates
            adapter.clear();
            adapter.addAll(q.getChoices());
            adapter.notifyDataSetChanged();
        });
    }

    private void onSubmit() {
        if (selectedIndex == -1) {
            submitBtn.setEnabled(false);
            Toast.makeText(requireContext(), "Select an answer.", Toast.LENGTH_SHORT).show();
            return;
        }

        Question q = vm.getCurrentQuestion().getValue();
        if (q == null) return;

        if (selectedIndex == q.getCorrectIndex()) {
            Toast.makeText(requireContext(), "Yay! +1 Point", Toast.LENGTH_SHORT).show();
            vm.incrementCorrect();
        } else {
            Toast.makeText(requireContext(), "wrong! Use your BRN(AI)", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your answer");
        speechLauncher.launch(intent);
    }

    private void handleSpokenAnswer(String spoken) {
        if (spoken == null) return;

        String clean = spoken.trim().toLowerCase();
        //Match the spoken text to one of the visible choices
        for (int i = 0; i < adapter.getCount(); i++) {
            String choice = adapter.getItem(i);
            if (choice == null) continue;

            String c = choice.toLowerCase();
            if (clean.equals(c) || clean.contains(c)) {
                selectedIndex = i;
                answersList.setItemChecked(i, true);
                submitBtn.setEnabled(true);
                Toast.makeText(requireContext(), "Heard: " + choice, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(requireContext(), "Didn't catch a valid choice.", Toast.LENGTH_SHORT).show();
    }
}
