package com.example.drivinglessons.fragments.view;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.adapters.TransactionAdapter;
import com.example.drivinglessons.firebase.entities.Transaction;
import com.example.drivinglessons.util.WrapContentLinearLayoutManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class TransactionViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "transactions", ID = "id";

    private String id;

    private FirebaseManager fm;
    private TransactionAdapter adapter;

    private RecyclerView recyclerView;
    public TransactionViewFragment() {}

    @NonNull
    public static TransactionViewFragment newInstance(String id)
    {
        TransactionViewFragment fragment = new TransactionViewFragment();

        Bundle args = new Bundle();
        args.putString(ID, id);
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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_transaction_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentTransactionView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(new FirebaseRecyclerOptions.Builder<Transaction>().setQuery(fm.getTransactionsQuery(), this::getTransaction).build(), id);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    private Transaction getTransaction(@NonNull DataSnapshot snapshot)
    {
        return snapshot.getValue(Transaction.class);
    }

    protected TransactionViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<TransactionViewFragment> CREATOR = new Creator<TransactionViewFragment>()
    {
        @Override
        public TransactionViewFragment createFromParcel(Parcel in)
        {
            return new TransactionViewFragment(in);
        }

        @Override
        public TransactionViewFragment[] newArray(int size)
        {
            return new TransactionViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
