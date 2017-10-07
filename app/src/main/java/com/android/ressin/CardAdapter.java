package com.android.ressin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by prashanth kurella on 10/7/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private CustomItemClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(String[] myDataset, Context context, CustomItemClickListener listener) {
        mDataset = myDataset;
        this.listener = listener;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        final CardViewHolder mViewHolder = new CardViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.vText.setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView vText;

        public CardViewHolder(View v) {
            super(v);
            vText = v.findViewById(R.id.info_text);
        }

    }

}
