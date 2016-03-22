package com.example.masha.tetris;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    Button bttnMenu;
    Intent intent;
    EditText eTw , eTh;
    SharedPreferences sharedPreferences;
    final String width = "" , height = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        eTw = (EditText) findViewById(R.id.width);
        eTh = (EditText) findViewById(R.id.height);

        bttnMenu = (Button) findViewById(R.id.bttnMenu);
        bttnMenu.setOnClickListener(this);

        loadText();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttnMenu:
                intent = new Intent (this, Main.class);
                saveText();
                startActivity(intent);
                break;

        }
    }

    void saveText()
    {
        sharedPreferences = getPreferences( MODE_PRIVATE);

        Editor ed = sharedPreferences.edit();

        ed.putString(width , eTw.getText().toString());
        ed.putString(height, eTh.getText().toString());

        ed.commit();
    }

    void loadText() {
        sharedPreferences = getPreferences( MODE_PRIVATE);

        String w = sharedPreferences.getString(width, "");
        eTw.setText(w);

        String h = sharedPreferences.getString(height, "");
        eTh.setText(h);
    }

//    @Override
//    protected void onDestroy(){
//        saveText();
//
//    }
}