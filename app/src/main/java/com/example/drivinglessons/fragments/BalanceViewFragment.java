package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;

import java.util.Locale;

public class BalanceViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "balance", ID = "id", TRANSACTION_FRAGMENT = "transaction fragment";

    private TextView date, amount;
    private ImageView add;

    private FirebaseManager fm;

    private String id;
    private TransactionViewFragment transactionFragment;

    public BalanceViewFragment() {}

    @NonNull
    public static BalanceViewFragment newInstance(String id)
    {
        return newInstance(id, TransactionViewFragment.newInstance(id));
    }

    @NonNull
    public static BalanceViewFragment newInstance(String id, TransactionViewFragment transactionFragment)
    {
        BalanceViewFragment fragment = new BalanceViewFragment();

        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putParcelable(TRANSACTION_FRAGMENT, transactionFragment);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());

        Bundle args = getArguments();
        if (args != null)
        {
            id = args.getString(ID);
            transactionFragment = args.getParcelable(TRANSACTION_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_balance_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        date = view.findViewById(R.id.textViewFragmentBalanceViewDate);
        amount = view.findViewById(R.id.textViewFragmentBalanceViewAmount);
        add = view.findViewById(R.id.imageViewFragmentBalanceViewAdd);

        if (transactionFragment != null) replaceFragment(transactionFragment);

        fm.getBalanceChanged(id, new FirebaseRunnable()
        {
            @Override
            public void run(Balance balance)
            {
                date.setText(Constants.DATE_FORMAT.format(balance.date));
                amount.setText(String.format(Locale.ROOT, "%.2f₪", balance.amount));
            }
        });
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentBalanceView, fragment).commit();
    }

    protected BalanceViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
        transactionFragment = in.readParcelable(TransactionViewFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeParcelable(transactionFragment, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<BalanceViewFragment> CREATOR = new Creator<BalanceViewFragment>()
    {
        @Override
        public BalanceViewFragment createFromParcel(Parcel in)
        {
            return new BalanceViewFragment(in);
        }

        @Override
        public BalanceViewFragment[] newArray(int size)
        {
            return new BalanceViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
