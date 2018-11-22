package com.example.android.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    static MyAdapter myAdapter;

    static ArrayList<Note> notes;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.android.notes", Context.MODE_PRIVATE);
        notes = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> titles = null;
        ArrayList<String> contents= null;
        try {
            titles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("titles", ObjectSerializer.serialize(new ArrayList<String>())));
            contents = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("contents", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for(int i=0;i<titles.size();i++){
            Note note =  new Note(titles.get(i), contents.get(i));
            notes.add(note);
        }


        myAdapter = new MyAdapter(notes, this);

        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.addNote:
                Intent intent = new Intent(this, Editor.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }


}
class Note{
    String content;
    String title;

    Note(String title, String content){
        this.title = title;
        this.content = content;
    }
    Note(String content){
        this.content = content;
    }

    String extractTitle(){
        title = "";
        int i=0;
        while(i<content.length() && i<5){
            title+=content.charAt(i);
            i++;
        }
        return  title;
    }
}
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    ArrayList<Note> mDataSet;
    Context ctx;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.mTestView.setText(mDataSet.get(i).title);
        myViewHolder.mTestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, mDataSet.get(i).title, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ctx, Editor.class);
                intent.putExtra("placeholder", i);
                intent.putExtra("content", mDataSet.get(i).content);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
        myViewHolder.mTestView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(ctx)
                        .setTitle("Delete")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Do you really want to delete")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                mDataSet.remove(i);
                                MyAdapter.this.notifyDataSetChanged();
                                Toast.makeText(ctx, "deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return  true;
            }
        });
    }



    @Override
    public int getItemCount() {
        Toast.makeText(ctx, ""+mDataSet.size(), Toast.LENGTH_SHORT).show();
        return mDataSet.size();
    }

    MyAdapter(ArrayList<Note> list, Context ctx) {
        mDataSet = list;
        this.ctx = ctx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTestView;

        //Context context;
        public MyViewHolder(@NonNull View v) {
            super(v);
            mTestView = (TextView) v.findViewById(R.id.tvHolder);
            //context = v.getContext();


        }
        /*
        @Override
        public void onClick(View view) {
            Intent intent = new Intent()
        }
        */
    }
}
