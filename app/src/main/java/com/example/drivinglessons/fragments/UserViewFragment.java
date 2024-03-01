package com.example.drivinglessons.fragments;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.adapters.UserAdapter;
import com.example.drivinglessons.dialogs.UserFiltersDialogFragment;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.validation.TextListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;

import java.util.regex.Pattern;

public class UserViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "users", SEARCH = "search", IS_OWNER = "is owner", IS_STUDENT = "is student", USER_FILTERS = "user filters";

    private boolean isOwner, isStudent;
    private String search;
    private UserFiltersDialogFragment userFilters;

    private FirebaseManager fm;
    private UserAdapter adapter;

    private RecyclerView recyclerView;
    private ImageView filters;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

    public UserViewFragment() {}

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner)
    {
        return newInstance(isOwner, true);
    }

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isStudent)
    {
        return newInstance(isOwner, isStudent, UserFiltersDialogFragment.newInstance(isStudent));
    }

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isStudent, UserFiltersDialogFragment userFilters)
    {
        return newInstance("", isOwner, isStudent, userFilters);
    }
    @NonNull
    public static UserViewFragment newInstance(String search, boolean isOwner, boolean isStudent, UserFiltersDialogFragment userFilters)
    {
        UserViewFragment fragment = new UserViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(SEARCH, search);
        args.putBoolean(IS_OWNER, isOwner);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(USER_FILTERS, userFilters);
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
            search = args.getString(SEARCH);
            isOwner = args.getBoolean(IS_OWNER);
            isStudent = args.getBoolean(IS_STUDENT);
            userFilters = args.getParcelable(USER_FILTERS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_user_view, container, false);
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentUserView);
        filters = view.findViewById(R.id.imageViewFragmentUserViewFilters);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentUserViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentUserViewSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new UserAdapter(new FirebaseRecyclerOptions.Builder<User>().setQuery(fm.getUsersQuery(isStudent), this::getUser).build());
        recyclerView.setAdapter(adapter);

        userFilters.setCancel(this::setFilters);

        filters.setOnClickListener(v -> userFilters.show(getChildFragmentManager(), null));

        searchInput.addTextChangedListener((TextListener) s ->
        {
            String str = searchInput.getText().toString().trim().toLowerCase();

            if (str.isEmpty())
                searchInputLayout.setError(null);
            else if (str.length() > 32)
                searchInputLayout.setError("name must contain at most 32 letters");
            else if (!Pattern.matches(" *[a-z]+([ -][a-z]+)* *", str))
                searchInputLayout.setError("name must be separated by a dash or a space");
            else searchInputLayout.setError(null);

            if (searchInputLayout.getError() != null) return;

            search = Constants.fixName(str);

            adapter.setName(search);
            adapter.notifyDataSetChanged();
        });

        adapter.setName(search);
        setFilters();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void setFilters()
    {
        if (isStudent) setStudentFilters(userFilters.getStudentData());
        else setTeacherFilters(userFilters.getTeacherData());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setStudentFilters(StudentFiltersFragment.Data data)
    {
        if (data != null)
        {
            adapter.setTheory(data.isTheory);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setTeacherFilters(TeacherFiltersFragment.Data data)
    {
        if (data != null)
        {
            adapter.setManual(data.isManual);
            adapter.setTester(data.isTester);
            adapter.notifyDataSetChanged();
        }
    }

    private User getUser(DataSnapshot snapshot)
    {
        return isStudent ? snapshot.getValue(Student.class) : snapshot.getValue(Teacher.class);
    }

    protected UserViewFragment(@NonNull Parcel in)
    {
        search = in.readString();
        isOwner = in.readByte() == 1;
        isStudent = in.readByte() == 1;
        userFilters = in.readParcelable(UserFiltersDialogFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(search);
        dest.writeByte((byte) (isOwner ? 1 : 0));
        dest.writeByte((byte) (isStudent ? 1 : 0));
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
