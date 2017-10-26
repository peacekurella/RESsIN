package com.android.ressin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prashanth kurella on 10/7/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>

{
    private List<ToDoObject> mDataset;
    private Context mContext;
    private CustomItemClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(List<ToDoObject> myDataset, Context context, CustomItemClickListener listener) {
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
        final TextView tv = cardView.findViewById(R.id.info_text);
        tv.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {
                                      listener.onTextFieldClick(view);
                                  }
                              }
        );
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv.showContextMenu();
                return true;
            }
        });

        final CardViewHolder mViewHolder = new CardViewHolder(cardView);
        View.OnCreateContextMenuListener ctx = new View.OnCreateContextMenuListener() {

            private MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            listener.editClicked(mViewHolder.getPosition());
                            break;

                        case 2:
                            listener.deleteClicked(mViewHolder.getPosition());
                            break;
                        case 3:
                            listener.shiftClicked(mViewHolder.getPosition());
                            break;
                    }
                    return true;
                }
            };

            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem Edit = contextMenu.add(Menu.NONE, 1, 1, "Edit");
                MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
                MenuItem ShiftUp = contextMenu.add(Menu.NONE, 3, 3, "Shift Up");
                Edit.setOnMenuItemClickListener(onEditMenu);
                Delete.setOnMenuItemClickListener(onEditMenu);
                ShiftUp.setOnMenuItemClickListener(onEditMenu);
            }
        };

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        cardView.setOnCreateContextMenuListener(ctx);

        return mViewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.vText.setText(mDataset.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
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
