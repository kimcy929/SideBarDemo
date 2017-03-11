package com.example.sidebarinservice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sidebarinservice.R;

/**
 * Created by Kimcy929 on 21/02/2017.
 */
public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {

    public static final String TAG = NumberAdapter.class.getSimpleName();

    public NumberAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ((LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item_layout, parent, false)));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtNumber.setText("Item " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNumber;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(), "Item's position -> " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
