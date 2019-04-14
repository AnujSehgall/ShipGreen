package com.example.greenapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("CurrentUserId", MODE_PRIVATE);
        currentUserId = prefs.getString("userId", null);//"No name defined" is the default value.
        Toast.makeText(getApplicationContext(),currentUserId,Toast.LENGTH_SHORT).show();
        if (currentUserId != null){
            Intent intent = new Intent(MainActivity.this,ListCategory.class);
            startActivity(intent);
            finish();
        }

        else {
            setContentView(R.layout.activity_main);
            editTextMobile = findViewById(R.id.editTextMobile);
            findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobile = editTextMobile.getText().toString().trim();

                    if(mobile.isEmpty() || mobile.length() < 10){
                        editTextMobile.setError("Enter a valid mobile");
                        editTextMobile.requestFocus();
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                }
            });
        }

    }
}
