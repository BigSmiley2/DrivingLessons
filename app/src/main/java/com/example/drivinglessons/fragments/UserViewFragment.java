package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.dialogs.UserFiltersDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class UserViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "users", ID = "id", SEARCH = "search", USER_FILTERS = "user filters";

    private String id, search;
    private UserFiltersDialogFragment userFilters;

    private ImageView filters;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

    public UserViewFragment() {}

    @NonNull
    public static UserViewFragment newInstance(boolean isStudent)
    {
        return newInstance(UserFiltersDialogFragment.newInstance(isStudent));
    }

    @NonNull
    public static UserViewFragment newInstance(UserFiltersDialogFragment userFilters)
    {
        return newInstance("","", userFilters);
    }
    @NonNull
    public static UserViewFragment newInstance(String id, String search, UserFiltersDialogFragment userFilters)
    {
        UserViewFragment fragment = new UserViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(SEARCH, search);
        args.putParcelable(USER_FILTERS, userFilters);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            id = args.getString(ID);
            search = args.getString(SEARCH);
            userFilters = args.getParcelable(USER_FILTERS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_user_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        filters = view.findViewById(R.id.imageViewFragmentUserViewFilters);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentUserViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentUserViewSearch);

        userFilters.setCancel(() ->
        {
            if (userFilters.isStudent())
            {
                userFilters.getStudentData();
            }
            else
            {
                userFilters.getTeacherData();
            }
        });

        filters.setOnClickListener(v -> userFilters.show(getChildFragmentManager(), null));
    }

    protected UserViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
        search = in.readString();
        userFilters = in.readParcelable(UserFiltersDialogFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(search);
        dest.writeParcelable(userFilters, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<UserViewFragment> CREATOR = new Parcelable.Creator<UserViewFragment>()
    {
        @Override
        public UserViewFragment createFromParcel(Parcel in)
        {
            return new UserViewFragment(in);
        }

        @Override
        public UserViewFragment[] newArray(int size)
        {
            return new UserViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
