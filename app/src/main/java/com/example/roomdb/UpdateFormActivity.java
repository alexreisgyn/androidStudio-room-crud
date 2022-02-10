package com.example.roomdb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roomdb.db.LoliDB;
import com.example.roomdb.models.Loli;

public class UpdateFormActivity extends AppCompatActivity {

    // Activity's UI components
    private EditText editLoliName;
    private EditText editLoliAge;
    private EditText editImgURL;
    private Button btnEdit;

    // Data passing from MainActivity
    private Loli loli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_form);

        // Map UI to this class's properties
        initUI();

        // Get data passing from MainActivity
        loli = (Loli) getIntent().getExtras().get("loli");

        // If loli => load data to UI
        if (loli != null) {
            editLoliName.setText(loli.getLoliName());
            editLoliAge.setText(loli.getLoliAge());
            editImgURL.setText(loli.getImgUrl());
        }

        // Click update btn
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLoli();
            }
        });
    }

    // Map UI to this class's properties
    private void initUI() {
        editLoliName = findViewById(R.id.edt_loliName);
        editLoliAge = findViewById(R.id.edt_loliAge);
        editImgURL = findViewById(R.id.edt_imgURL);
        btnEdit = findViewById(R.id.btn_updateLoli);
    }

    // Handle Update
    private void updateLoli() {

        // Get data from form
        String strLoliName = editLoliName.getText().toString().trim();
        String strLoliAge = editLoliAge.getText().toString().trim();
        String strImgURL = editImgURL.getText().toString().trim();

        // Fields empty
        if (TextUtils.isEmpty(strLoliName) ||
                TextUtils.isEmpty(strLoliAge) ||
                TextUtils.isEmpty(strImgURL)) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update loli
        loli.setLoliName(strLoliName);
        loli.setLoliAge(strLoliAge);
        loli.setImgUrl(strImgURL);

        // Update Loli
        LoliDB.getInstance(this).loliDAO().updateLoli(loli);
        Toast.makeText(this, "Loli updated!", Toast.LENGTH_SHORT).show();

        // Fire back result to previous Activity (Main)
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}