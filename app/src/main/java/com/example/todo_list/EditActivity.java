package com.example.todo_list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        Button backButton = findViewById(R.id.title_back);
        editText = findViewById(R.id.edit_text);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("extra_position", 0);
        final String positionStr = id + "";
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedData = editText.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString(positionStr, savedData);
                editor.apply();

                finish();
            }
        });
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String text = pref.getString(positionStr, "");
        editText.setText(text);
    }
}
