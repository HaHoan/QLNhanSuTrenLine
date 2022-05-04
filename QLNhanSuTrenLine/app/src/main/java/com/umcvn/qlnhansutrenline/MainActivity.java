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
import android.widget.ImageButton;
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
    private static final int BARCODE_ACTIVITY_REQUEST_CODE = 1;
    private static final int MAX_ITEM_IN_LIST = 100;
    private EditText searchEdt;
    private List<PersonInfo> listStaffs;
    private List<PersonInfo> listStaffsLoaded;
    private  ListAdapter adapter;
    private SOService mService;
    private   RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button searchButton;
    private  SpotsDialog dialog;
    private ImageButton btnBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEdt = findViewById(R.id.searchEdt);
        searchButton = findViewById(R.id.searchBtn);
        btnBarcode = findViewById(R.id.btnBarcode);
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
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeActivity.class);
                startActivityForResult(intent, BARCODE_ACTIVITY_REQUEST_CODE);
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
                    if(response.body().size() > MAX_ITEM_IN_LIST){
                        listStaffsLoaded = response.body().subList(0,MAX_ITEM_IN_LIST);
                    }else{
                        listStaffsLoaded = response.body();
                    }
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
                loadAllStaffs();
            }
        }else if(requestCode == BARCODE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("Barcode");
                searchEdt.setText(barcode);
                searchStaff();
            }
        }
    }
}