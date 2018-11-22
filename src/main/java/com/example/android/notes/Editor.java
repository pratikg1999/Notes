package com.example.android.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class Editor extends AppCompatActivity {
    EditText editText;
    Intent intent;
    String content;
    String title;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editText = (EditText) findViewById(R.id.note);

        intent  = getIntent();
        content = intent.getStringExtra("content");
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("placeholder", 0);
        editText.setText(content);

    }
    void saveNote(){

        if(content!=null){
            MainActivity.notes.get(id).content = editText.getText().toString();
            MainActivity.notes.get(id).extractTitle();
        }
        else{
            String tempContent = editText.getText().toString();
            if(tempContent!=null && tempContent!=""){
                Note note = new Note(tempContent);
                note.extractTitle();
                MainActivity.notes.add(note);
            }
        }
        MainActivity.myAdapter.notifyDataSetChanged();
        savePermanently();

    }

    void savePermanently(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.android.notes", Context.MODE_PRIVATE);
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        for(int i=0;i<MainActivity.notes.size();i++){
            titles.add(MainActivity.notes.get(i).title);
            contents.add(MainActivity.notes.get(i).content);
        }
        try {
            String titlesString = ObjectSerializer.serialize(titles);
            String contentsString = ObjectSerializer.serialize(contents);
            sharedPreferences.edit().putString("titles", titlesString).apply();
            sharedPreferences.edit().putString("contents", contentsString).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause executed", Toast.LENGTH_SHORT).show();
        saveNote();
    }
}
