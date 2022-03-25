package com.umcvn.qlnhansutrenline;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private EditText searchEdt;
    private List<PersonInfo> listStaffs;
    private List<PersonInfo> listStaffsLoaded;
    private  ListAdapter adapter;
    private SOService mService;
    private   RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button searchButton;
    private  SpotsDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEdt = findViewById(R.id.searchEdt);
        searchButton = findViewById(R.id.searchBtn);
       Toolbar toolbar = findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
        mService = ApiUtils.getSOService();
        dialog = new SpotsDialog(this,"Loading...");
        listStaffs = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.person_list);
        adapter = new ListAdapter(listStaffs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              //  filter(s.toString());
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStaff();
            }
        });
        loadAllStaffs();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadAllStaffs();
            }
        });
    }
    private void searchStaff(){
        if(searchEdt.getText().toString().trim().length() == 0){
            replaceOldListWithNewList(listStaffsLoaded);
            return;
        }
        dialog.show();
        mService.getStaffBy(searchEdt.getText().toString()).enqueue(new Callback<PersonInfo>() {
            @Override
            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
                if(response.isSuccessful()){
                    if(response.body() == null){
                        replaceOldListWithNewList(null);
                    }else{
                       List<PersonInfo> list = new ArrayList<>();
                       list.add(response.body());
                       replaceOldListWithNewList(list);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Connect failed!",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<PersonInfo> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Connect failed!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private void insertSingleItem(PersonInfo item) {
        int insertIndex = 0;
        listStaffs.add(insertIndex, item);
        adapter.notifyItemInserted(insertIndex);
        recyclerView.scrollToPosition(0);
    }
    private void replaceOldListWithNewList(List<PersonInfo> newList) {
        listStaffs.clear();
        if(newList != null){
            listStaffs.addAll(newList);
        }
        adapter.notifyDataSetChanged();
    }

    public void loadAllStaffs() {

        dialog.show();
        mService.getAllStaffs().enqueue(new Callback<List<PersonInfo>>() {
            @Override
            public void onResponse(Call<List<PersonInfo>> call, Response<List<PersonInfo>> response) {
                if(response.isSuccessful()){
                    listStaffsLoaded = response.body();
                   replaceOldListWithNewList(listStaffsLoaded);

                }else{
                    Toast.makeText(MainActivity.this,"Connect failed!",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<PersonInfo>> call, Throwable t) {
                Toast.makeText(MainActivity.this,"No data!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });


    }
    private void filter(String text) {
        adapter.getFilter().filter(text);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_new_person_btn) {
            Intent intent = new Intent(this, DetailActivity.class);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

//                // Get String data from Intent
//                String IDOld = data.getStringExtra("IDOld");
//                if(IDOld.equals("")){
//                    String code = data.getStringExtra("Code");
//                    String line = data.getStringExtra("Line");
//                    String process = data.getStringExtra("Process");
//                    String Id = data.getStringExtra("ID");
//                    insertSingleItem(new PersonInfo(code, line, process,Id));
//                }else{
//                    loadAllStaffs();
//                }
                loadAllStaffs();

            }
        }
    }
}