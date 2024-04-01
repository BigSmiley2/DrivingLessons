package com.example.drivinglessons.fragments.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.adapters.RatingAdapter;
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.util.WrapContentLinearLayoutManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class RatingViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "ratings", ID = "id";

    private String id;

    private FirebaseManager fm;
    private RatingAdapter adapter;

    private RecyclerView recyclerView;

    public RatingViewFragment() {}

    @NonNull
    public static RatingViewFragment newInstance(String id)
    {
        RatingViewFragment fragment = new RatingViewFragment();

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
        return inflater.inflate(R.layout.fragment_rating_view, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentRatingView);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(requireContext()));
        adapter = new RatingAdapter(new FirebaseRecyclerOptions.Builder<Rating>().setQuery(fm.getRatingQuery(id), this::getRating).build());
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener((v, event) ->
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                v.getParent().requestDisallowInterceptTouchEvent(true);
            else if (event.getAction() == MotionEvent.ACTION_UP)
                v.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

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

    private Rating getRating(@NonNull DataSnapshot snapshot)
    {
        return snapshot.getValue(Rating.class);
    }

    protected RatingViewFragment(@NonNull Parcel in)
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

    public static final Creator<RatingViewFragment> CREATOR = new Creator<RatingViewFragment>()
    {
        @Override
        public RatingViewFragment createFromParcel(Parcel in)
        {
            return new RatingViewFragment(in);
        }

        @Override
        public RatingViewFragment[] newArray(int size)
        {
            return new RatingViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
