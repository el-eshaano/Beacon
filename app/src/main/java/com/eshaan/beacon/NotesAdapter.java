package com.eshaan.beacon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<String> {

    ArrayList<String> currentNotes;
    Context context;

    public NotesAdapter(Context context, ArrayList<String> currentNotes) {
        super(context, R.layout.list_row, currentNotes);

        this.context = context;
        this.currentNotes = currentNotes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_row, null);

            TextView number = convertView.findViewById(R.id.number);
            number.setText(String.valueOf(position + 1));

            TextView note = convertView.findViewById(R.id.note);
            note.setText(currentNotes.get(position));

            ImageButton deleteButton = convertView.findViewById(R.id.delete);
            deleteButton.setOnClickListener(v -> {
                String noteText = currentNotes.get(position);
                NotesDatabase notesDatabase = new NotesDatabase(context);
                notesDatabase.deleteNoteByNoteText(noteText);
                currentNotes.remove(noteText);
                this.notifyDataSetChanged();
            });
        }
        return convertView;
    }
}
