package com.android.ressin;

import android.view.View;
import android.widget.TextView;

/**
 * Created by prashanth kurella on 10/7/2017.
 */

public interface CustomItemClickListener {
    void onItemClick(View v, int position);

    void onTextFieldClick(TextView tv);

    void editClicked(int postion, TextView tv);

    void deleteClicked(int position);

    void shiftClicked(int position);

    void focusChange(TextView tv, int position);
}
