package com.example.drivinglessons.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class AddMoneyDialog extends Dialog
{
    private FirebaseManager fm;

    private Double cost;
    private String id;

    private ImageView add;
    private TextInputLayout amountInputLayout;
    private EditText amountInput;

    public AddMoneyDialog(Context context, String id)
    {
        super(context);
        this.id = id;

        fm = FirebaseManager.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
        setContentView(R.layout.dialog_add_money);

        add = findViewById(R.id.imageViewDialogAddMoney);
        amountInput = findViewById(R.id.editTextDialogAddMoneyAmount);
        amountInputLayout = findViewById(R.id.textInputLayoutDialogAddMoneyAmount);

        amountInput.addTextChangedListener((TextListener) s ->
        {
            String str = amountInput.getText().toString();
            if (str.isEmpty())
            {
                amountInputLayout.setError(null);
                cost = null;
            }
            else if (str.length() > 10)
            {
                amountInputLayout.setError("cost must contain at most 10 characters");
                cost = null;
            }
            else if (Pattern.matches("[0-9]*(\\.[0-9]+)*", str))
            {
                cost = Double.parseDouble(str);
                amountInputLayout.setError(null);
            }
            else
            {
                amountInputLayout.setError("the lesson cost must be a real number");
                cost = null;
            }
        });
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                View.OnClickListener listener = this;
                add.setOnClickListener(null);

                if (cost == null)
                    add.setOnClickListener(this);
                else fm.addMoney(id, cost, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getContext(), R.string.money_added, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }, new FirebaseRunnable()
                {
                    @Override
                    public void runAll(Exception e)
                    {
                        super.runAll(e);
                        Toast.makeText(getContext(), R.string.went_wrong_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
