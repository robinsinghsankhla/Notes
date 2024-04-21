package com.example.notes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "NotePres";
    private String NOTE_COUNT;
    private EditText titleText;
    private EditText contentText;
    private LinearLayout container;
    private List<Notes> notesList;
    private AppCompatButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleText = findViewById(R.id.titleEditText);
        contentText = findViewById(R.id.contentEditText);
        container = findViewById(R.id.containerLL);
        saveBtn = findViewById(R.id.saveNoteBtn);
        notesList =new ArrayList<>();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        loadNotesFromSharedPreference();
        displayNotes();

    }

    private void loadNotesFromSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt(NOTE_COUNT,0);

        Notes note = new Notes();
        for (int i=0;i<noteCount;i++){
            String title = sharedPreferences.getString("note_title_"+i,"");
            String content = sharedPreferences.getString("note_content_"+i,"");

            note.setTitle(title);
            note.setContent(content);

            notesList.add(note);
        }
    }

    private void displayNotes() {
        for(Notes notes : notesList)
            createNoteView(notes);
    }

    private void saveNote() {
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        if(!title.isEmpty() && !content.isEmpty()){
            Notes note = new Notes();
            note.setTitle(title);
            note.setContent(content);
            notesList.add(note);

            saveNotesToSharedPreference();
            createNoteView(note);
            clearInputField();//to empty editText
        }
    }

    private void clearInputField() {
        titleText.getText().clear();
        contentText.getText().clear();
    }

    private void createNoteView(final Notes note) {
        View view = getLayoutInflater().inflate(R.layout.note_item,null);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView titleText = view.findViewById(R.id.titleNotetiew);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView contentText = view.findViewById(R.id.contentNoteView);

        titleText.setText(note.getTitle());
        contentText.setText(note.getContent());

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialogBx(note);
                return true;
            }
        });

        //set to container
        container.addView(view);

    }

    private void showDeleteDialogBx(Notes notes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(notes);
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();
    }

    private void deleteNote(Notes notes) {
        notesList.remove(notes);
        saveNotesToSharedPreference();//to delete from the SharedPreference
        updateContainer();// to update the container list
    }

    private void updateContainer() {
        container.removeAllViews();
        displayNotes();
    }


    private void saveNotesToSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(NOTE_COUNT,notesList.size());//no of notes
        Notes notes;
        for (int i=0;i<notesList.size();i++){
            notes = notesList.get(i);
            editor.putString("note_title_"+i,notes.getTitle());
            editor.putString("note_content_"+i,notes.getContent());
        }
        editor.apply();

    }
}