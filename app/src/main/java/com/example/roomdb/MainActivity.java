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

    private EditText editLoliName;
    private EditText editLoliAge;
    private EditText editImgURL;
    private Button addBtn;
    private RecyclerView rcvLoliList;
    private EditText editSearch;

    private LoliAdapter loliAdapter;
    private List<Loli> nListLoli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

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

        nListLoli = new ArrayList<>();
        loliAdapter.setData(nListLoli);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoliList.setLayoutManager(linearLayoutManager);
        rcvLoliList.setAdapter(loliAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLoli();
            }
        });

        loadData();

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

    private void initUI() {
        editLoliName = findViewById(R.id.edt_loliName);
        editLoliAge = findViewById(R.id.edt_loliAge);
        editImgURL = findViewById(R.id.edt_imgURL);
        addBtn = findViewById(R.id.btn_addLoli);
        rcvLoliList = findViewById(R.id.rcv_loliList);
        editSearch = findViewById(R.id.edt_search);
    }

    private void addLoli() {
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

        Loli loli = new Loli(strLoliName, strLoliAge, strImgURL);

        if (isLoliExist(loli)) {
            Toast.makeText(this, "Loli exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoliDB.getInstance(this).loliDAO().insertLoli(loli);

        Toast.makeText(this, "Loli added!", Toast.LENGTH_SHORT).show();

        editLoliName.setText("");
        editLoliAge.setText("");
        editImgURL.setText("");

        hideSoftKeyboard();

        loadData();
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        nListLoli = LoliDB.getInstance(this).loliDAO().listLolis();
        loliAdapter.setData(nListLoli);
    }

    private boolean isLoliExist(Loli loli) {
        List<Loli> ls = LoliDB.getInstance(this).loliDAO().getLoliByName(loli.getLoliName());

        return ls != null && !ls.isEmpty();
    }

    private void clickUpdateLoli(Loli loli) {
        Intent intent = new Intent(MainActivity.this, UpdateFormActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("loli", loli);

        intent.putExtras(bundle);

        activityResultLauncher.launch(intent);
    }

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

    private void clickDeleteLoli(Loli loli) {
        new AlertDialog.Builder(this)
                .setTitle("Comfirm delete loli")
                .setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoliDB.getInstance(MainActivity.this).loliDAO().deleteLoli(loli);
                        Toast.makeText(MainActivity.this, "Loli deleted!", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("No", null);
    }

    private void handleSearchLoli() {
        String keyword = editSearch.getText().toString().trim();

        nListLoli = new ArrayList<>();
        nListLoli = LoliDB.getInstance(this).loliDAO().searchLoliByName(keyword);

        loliAdapter.setData(nListLoli);
        hideSoftKeyboard();
    }
}