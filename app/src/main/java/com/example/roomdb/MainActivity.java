package com.example.roomdb;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomdb.db.LoliDB;
import com.example.roomdb.models.Loli;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Activity's UI components
    private EditText editLoliName;
    private EditText editLoliAge;
    private EditText editImgURL;
    private Button addBtn;
    private RecyclerView rcvLoliList;
    private EditText editSearch;

    // Load data to RecyclerView
    private LoliAdapter loliAdapter;
    // Loli list
    private List<Loli> nListLoli;

    // Create Activity (start)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Map UI to this class's properties
        initUI();

        // Callback to LoliAdapter to handle event after update & delete
        loliAdapter = new LoliAdapter(new LoliAdapter.ICLickItemLoli() {
            @Override
            public void updateLoli(Loli loli) {
                clickUpdateLoli(loli);
            }

            @Override
            public void deleteLoli(Loli loli) {
                clickDeleteLoli(loli);
            }
        });

        // Create empty data (memory address)
        nListLoli = new ArrayList<>();
        loliAdapter.setData(nListLoli);

        // Load data from db to List
        loadData();

        // Call Adapter to load data to UI
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoliList.setLayoutManager(linearLayoutManager);
        rcvLoliList.setAdapter(loliAdapter);

        // Btn add click
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLoli();
            }
        });

        // Btn search click
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleSearchLoli();
                }
                return false;
            }
        });
    }

    // Map UI to this class's properties
    private void initUI() {
        editLoliName = findViewById(R.id.edt_loliName);
        editLoliAge = findViewById(R.id.edt_loliAge);
        editImgURL = findViewById(R.id.edt_imgURL);
        addBtn = findViewById(R.id.btn_addLoli);
        rcvLoliList = findViewById(R.id.rcv_loliList);
        editSearch = findViewById(R.id.edt_search);
    }

    // Add new loli to DB
    private void addLoli() {
        // Get user's input
        String strLoliName = editLoliName.getText().toString().trim();
        String strLoliAge = editLoliAge.getText().toString().trim();
        String strImgURL = editImgURL.getText().toString().trim();

        // Fields empty => error
        if (TextUtils.isEmpty(strLoliName) ||
                TextUtils.isEmpty(strLoliAge) ||
                TextUtils.isEmpty(strImgURL)) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Loli loli = new Loli(strLoliName, strLoliAge, strImgURL);

        // Loli exist => error
        if (isLoliExist(loli)) {
            Toast.makeText(this, "Loli exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert & notify
        LoliDB.getInstance(this).loliDAO().insertLoli(loli);
        Toast.makeText(this, "Loli added!", Toast.LENGTH_SHORT).show();

        // Clear form
        editLoliName.setText("");
        editLoliAge.setText("");
        editImgURL.setText("");

        // Hide keyboard
        hideSoftKeyboard();

        // Load new data from DB & trigger re-render
        loadData();
    }

    // Hide soft keyboard
    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load new data from DB & trigger re-render
    private void loadData() {
        nListLoli = LoliDB.getInstance(this).loliDAO().listLolis();
        loliAdapter.setData(nListLoli);
    }

    // Check loli exist when add
    private boolean isLoliExist(Loli loli) {
        List<Loli> ls = LoliDB.getInstance(this).loliDAO().getLoliByName(loli.getLoliName());

        return ls != null && !ls.isEmpty();
    }

    // Update loli
    private void clickUpdateLoli(Loli loli) {
        // New Activity
        Intent intent = new Intent(MainActivity.this, UpdateFormActivity.class);

        // Passing object to new Activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("loli", loli);

        intent.putExtras(bundle);

        // Launch new Activity & listen to result
        activityResultLauncher.launch(intent);
    }

    // Launch new Activity & listen to result
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    loadData();
                }
            }
        });

    // Delete loli handle
    private void clickDeleteLoli(Loli loli) {
        // Confirm
        new AlertDialog.Builder(this)
                .setTitle("Comfirm delete loli")
                .setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete Loli from DB
                        LoliDB.getInstance(MainActivity.this).loliDAO().deleteLoli(loli);
                        Toast.makeText(MainActivity.this, "Loli deleted!", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("No", null);
    }

    // Handle Search
    private void handleSearchLoli() {
        String keyword = editSearch.getText().toString().trim();

        nListLoli = new ArrayList<>();
        nListLoli = LoliDB.getInstance(this).loliDAO().searchLoliByName(keyword);

        loliAdapter.setData(nListLoli);
        hideSoftKeyboard();
    }
}