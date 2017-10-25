package com.android.ressin;

import android.view.View;

/**
 * Created by prashanth kurella on 10/7/2017.
 */

public interface CustomItemClickListener {
    void onItemClick(View v, int position);

    void onTextFieldClick(View view);

    void editClicked(int postion);

    void deleteClicked(int position);

    void shiftClicked(int position);
}
