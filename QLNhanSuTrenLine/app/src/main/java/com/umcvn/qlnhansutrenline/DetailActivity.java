package com.umcvn.qlnhansutrenline;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private EditText codeEditText;
    private TextView lineEditText;
    private TextView stationEditText;
    private TextView statusTextView;
    private TextView timeTextView;
    private ImageButton btnBarcode;
    private ArrayList<String> lineArrayList;
    private ArrayList<String> stationArrayList;
    private  SpotsDialog dialogLoading;
    private  Dialog dialog;
    private SOService mService;
    private String ID;
    private static final int BARCODE_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        codeEditText = findViewById(R.id.code_edt);
        lineEditText = findViewById(R.id.lineTextView);
        stationEditText = findViewById(R.id.stationTextView);
        btnBarcode = findViewById(R.id.btnBarcode);
        statusTextView = findViewById(R.id.tvStatus);
        timeTextView = findViewById(R.id.timeTextView);
        mService = ApiUtils.getSOService();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String code = extras.getString("Code");
            String line = extras.getString("Line");
            String process = extras.getString("Process");
            ID = extras.getString("ID");
            if (code != null && line != null && process != null) {
                codeEditText.setText(code);
                lineEditText.setText(line);
                stationEditText.setText(process);
            }
        } else {
            ID = "";
            timeTextView.setText(Calendar.getInstance().getTime().toString());
        }
        dialogLoading = new SpotsDialog(this,"Waiting...");
        getProcessInfos();

        lineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelect(lineArrayList, new ActionListenerCallback() {
                    @Override
                    public void onActionSuccess(String successMessage) {
                        lineEditText.setText(successMessage);
                    }
                });
            }
        });

        stationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelect(stationArrayList, new ActionListenerCallback() {
                    @Override
                    public void onActionSuccess(String successMessage) {
                        stationEditText.setText(successMessage);
                    }
                });
            }
        });

        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBarcode();
            }
        });

    }

    private void getProcessInfos() {
        dialogLoading.show();
        mService.getProcessInfo().enqueue(new Callback<ProcessModel>() {
            @Override
            public void onResponse(Call<ProcessModel> call, Response<ProcessModel> response) {
                if (response.isSuccessful()) {
                    ProcessModel infos = response.body();
                    lineArrayList = infos.LineList;
                    stationArrayList = infos.ProcessList;
                } else {
                    Toast.makeText(DetailActivity.this, "Connect failed!", Toast.LENGTH_SHORT).show();
                }
                dialogLoading.dismiss();
            }

            @Override
            public void onFailure(Call<ProcessModel> call, Throwable t) {

            }
        });
    }

    private void checkBarcode() {
        Intent intent = new Intent(DetailActivity.this, BarcodeActivity.class);
        startActivityForResult(intent, BARCODE_ACTIVITY_REQUEST_CODE);
    }

    private void showDialogSelect(ArrayList arrayList, ActionListenerCallback callback) {
        dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onActionSuccess(adapter.getItem(position));
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.mipmap.save));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_new_person_btn) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Xác nhận");
            b.setMessage("Bạn có muốn lưu không?");
            b.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (lineEditText.getText().toString().trim().length() == 0) {
                        statusTextView.setText("Chưa chọn line");
                        return;
                    }

                    if (stationEditText.getText().toString().trim().length() == 0) {
                        statusTextView.setText("Chưa chọn công đoạn");
                        return;
                    }

                    if (codeEditText.getText().toString().trim().length() == 0) {
                        codeEditText.requestFocus();
                        statusTextView.setText("Chưa nhập staff code");
                        return;
                    }

                    saveToDb();
                }
            });
            b.setNegativeButton("Không đồng ý", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog al = b.create();
            al.show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public interface ActionListenerCallback {
        void onActionSuccess(String successMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                String barcode = data.getStringExtra("Barcode");
                codeEditText.setText(barcode);
            }
        }
    }

    private void saveToDb() {
        SpotsDialog dialog = new SpotsDialog(this, "Waiting...");
        dialog.show();
        mService.savePost(codeEditText.getText().toString(), lineEditText.getText().toString(), stationEditText.getText().toString(), ID).enqueue(new Callback<ResponseAPI>() {
            @Override
            public void onResponse(Call<ResponseAPI> call, Response<ResponseAPI> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("OK")) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        statusTextView.setText(response.body().status);
                    }

                } else {
                    statusTextView.setText("Send not successfully!");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseAPI> call, Throwable t) {
                statusTextView.setText("Failed!");
                dialog.dismiss();
            }
        });

    }
}
