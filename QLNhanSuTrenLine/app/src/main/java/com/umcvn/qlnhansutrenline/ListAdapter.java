package com.umcvn.qlnhansutrenline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Filterable {
    private List<PersonInfo> listdata;
    private List<PersonInfo> listdataFiltered;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    // RecyclerView recyclerView;
    public ListAdapter(List<PersonInfo> listdata) {

        this.listdata = listdata;
        this.listdataFiltered = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Nạp layout cho View biểu diễn phần tử sinh viên
        View studentView =
                inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(studentView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PersonInfo person = listdataFiltered.get(position);
        holder.textView.setText(person.getStaffCode());
        holder.textView1.setText(person.getLineId());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("Code",person.getStaffCode());
                intent.putExtra("Line",person.getLineId());
                intent.putExtra("Process",person.getProcess());
                intent.putExtra("ID",person.getId());
                ((Activity)v.getContext()).startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listdataFiltered = listdata;
                } else {
                    List<PersonInfo> filteredList = new ArrayList<>();
                    for (PersonInfo row : listdata) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getStaffCode().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listdataFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listdataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listdataFiltered = (ArrayList<PersonInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView1;
        public Button btnDetail;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.txtCode);
            this.textView1 = (TextView) itemView.findViewById(R.id.txtLine);
            this.btnDetail = (Button) itemView.findViewById(R.id.detail_button);
        }
    }

}