package com.android.ressin;

import android.view.View;
import android.widget.EditText;

/**
 * Created by prashanth kurella on 10/7/2017.
 */

public interface CustomItemClickListener {
    void onItemClick(View v, int position, EditText tv);

    void onTextFieldClick(EditText tv);

    void editClicked(int postion, EditText tv);

    void deleteClicked(int position);

    void shiftClicked(int position);

    void focusChange(EditText tv, int position, boolean b);
}
