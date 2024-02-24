package com.example.drivinglessons.util.validation;

import android.text.TextWatcher;

public interface TextListener extends TextWatcher
{
    default void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {}

    default void onTextChanged(CharSequence var1, int var2, int var3, int var4) {}
}
