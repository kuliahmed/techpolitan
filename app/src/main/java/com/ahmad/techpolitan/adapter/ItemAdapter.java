package com.ahmad.techpolitan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmad.techpolitan.HistoryObject;
import com.ahmad.techpolitan.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    List<HistoryObject> itemList1;
    private Context context;

    public ItemAdapter(List<HistoryObject> itemList, Context context) {

        this.itemList1 = itemList;
        this.context = context;

    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, final int position) {

        holder.tvStatus.setText(itemList1.get(position).getType());
        holder.tvDate.setText(itemList1.get(position).getDate());
        holder.tvLokasi.setText(itemList1.get(position).getLocationIn());

        holder.cardView.setOnClickListener(view -> showBottomSheetDialog(itemList1.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatus;
        TextView tvDate;
        TextView tvLokasi;
        ConstraintLayout constraintLayout;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.lblDateValue);
            tvStatus = itemView.findViewById(R.id.lblStatus);
            tvLokasi = itemView.findViewById(R.id.lblLocationValue);
            constraintLayout = itemView.findViewById(R.id.clHistory);
            cardView = itemView.findViewById(R.id.cvHistory);

        }
    }

    private void showBottomSheetDialog(HistoryObject historyObject) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.TransparentDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_history_layout);

        TextView tvName = bottomSheetDialog.findViewById(R.id.lblNameValue);
        TextView tvNIP = bottomSheetDialog.findViewById(R.id.lblNipValue);
        TextView tvDate = bottomSheetDialog.findViewById(R.id.lblDateValue);
        TextView tvType = bottomSheetDialog.findViewById(R.id.lblTypeValue);
        TextView tvTimeIn = bottomSheetDialog.findViewById(R.id.lblTimeInValue);
        TextView tvTimeOut = bottomSheetDialog.findViewById(R.id.lblTimeOutValue);
        TextView tvLocationIn = bottomSheetDialog.findViewById(R.id.lblLocationInValue);
        TextView tvLocationOut = bottomSheetDialog.findViewById(R.id.lblLocationOutValue);
        CardView cvClose = bottomSheetDialog.findViewById(R.id.cvClose);

        tvName.setText(historyObject.getName());
        tvNIP.setText(historyObject.getNip());
        tvDate.setText(historyObject.getDate());
        tvType.setText(historyObject.getType());
        tvTimeIn.setText(historyObject.getSignInTime());
        tvTimeOut.setText(historyObject.getSignOutTime());
        tvLocationIn.setText(historyObject.getLocationIn());
        tvLocationOut.setText(historyObject.getLocationOut());
        cvClose.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
}