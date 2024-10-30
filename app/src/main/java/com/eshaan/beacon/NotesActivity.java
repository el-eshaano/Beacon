package com.eshaan.beacon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

//    Create a databse to store notes
    private NotesDatabase notesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notesDatabase = new NotesDatabase(this);

        ArrayList<String> currentNotes = notesDatabase.getAllNotes();
        Log.d("NotesActivity", "onCreate: " + currentNotes);
        ListView listView = findViewById(R.id.Notes_List);

        NotesAdapter notesAdapter = new NotesAdapter(this, currentNotes);
        listView.setAdapter(notesAdapter);

        ImageButton addNoteButton = findViewById(R.id.AddNote_Button);
        addNoteButton.setOnClickListener(v -> {
            EditText noteEditText = findViewById(R.id.Note_EditText);
            String note = noteEditText.getText().toString();
            Log.d("NotesActivity", "got note: " + note);
            notesDatabase.addNote(note);
            currentNotes.add(note);
            notesAdapter.notifyDataSetChanged();
        });
    }
}