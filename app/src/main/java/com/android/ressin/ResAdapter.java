package com.android.ressin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prashanth kurella on 11/15/2017.
 */

public class ResAdapter extends RecyclerView.Adapter<ResAdapter.ResViewHolder> {

    private List<ResultObj> mDataset;
    private ItemClickListener listener;

    public ResAdapter(List<ResultObj> mDataset, ItemClickListener listener) {
        this.mDataset = mDataset;
        this.listener = listener;
    }

    @Override
    public ResViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View resView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.res_adapter, parent, false);
        final ResViewHolder mViewHolder = new ResViewHolder(resView);

        resView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ResViewHolder holder, int position) {
        holder.tText.setText(mDataset.get(position).getTitle());
        if (!mDataset.get(position).getDistance().equals("waiting for GPS ..."))
            holder.dText.setText(mDataset.get(position).getDistance() + " Kms away ...");
        else
            holder.dText.setText(mDataset.get(position).getDistance());
        holder.lText.setText(mDataset.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ResViewHolder extends RecyclerView.ViewHolder {
        protected TextView tText;
        protected TextView dText;
        protected TextView lText;

        public ResViewHolder(View v) {
            super(v);
            tText = v.findViewById(R.id.title_text);
            dText = v.findViewById(R.id.dist_text);
            lText = v.findViewById(R.id.link_text);
        }

    }
}
