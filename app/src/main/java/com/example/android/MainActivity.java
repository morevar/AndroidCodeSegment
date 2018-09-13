package com.example.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.android.media.ActVideoDemo1;
import com.example.android.media.ActVideoDemo2;
import com.example.android.media.ActVideoDemo201;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, ActVideoDemo1.class));
        //startActivity(new Intent(this, ActVideoDemo2.class));
        startActivity(new Intent(this, ActVideoDemo201.class));
        //finish();
    }
}
