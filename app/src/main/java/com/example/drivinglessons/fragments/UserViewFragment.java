package com.example.drivinglessons.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.InfoActivity;
import com.example.drivinglessons.MainActivity;
import com.example.drivinglessons.R;
import com.example.drivinglessons.adapters.UserAdapter;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.dialogs.StudentFiltersDialogFragment;
import com.example.drivinglessons.dialogs.TeacherFiltersDialogFragment;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.TextListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.regex.Pattern;

public class UserViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "users", ID = "id", SEARCH = "search", IS_OWNER = "is owner", IS_SELECTOR = "is selector", IS_STUDENT = "is student", STUDENT_FILTERS = "student filters", TEACHER_FILTERS = "teacher filters";

    private boolean isOwner, isSelector, isStudent;
    private String search, id;
    private StudentFiltersDialogFragment studentFilters;
    private TeacherFiltersDialogFragment teacherFilters;

    private FirebaseManager fm;
    private UserAdapter adapter;

    private TextView title;
    private RecyclerView recyclerView;
    private RadioGroup roleInput;
    private ImageView filters;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

    public UserViewFragment() {}

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isSelector, String id)
    {
        return newInstance(isOwner, isSelector, true, id);
    }

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isSelector, boolean isStudent, String id)
    {
        return newInstance(isOwner, isSelector, isStudent, id, StudentFiltersDialogFragment.newInstance(true), TeacherFiltersDialogFragment.newInstance(false));
    }

    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isSelector, boolean isStudent, String id, StudentFiltersDialogFragment studentFilters, TeacherFiltersDialogFragment teacherFilters)
    {
        return newInstance(isOwner, isSelector, isStudent, id, "", studentFilters, teacherFilters);
    }
    @NonNull
    public static UserViewFragment newInstance(boolean isOwner, boolean isSelector, boolean isStudent, String id, String search, StudentFiltersDialogFragment studentFilters, TeacherFiltersDialogFragment teacherFilters)
    {
        UserViewFragment fragment = new UserViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(SEARCH, search);
        args.putBoolean(IS_OWNER, isOwner);
        args.putBoolean(IS_SELECTOR, isSelector);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(STUDENT_FILTERS, studentFilters);
        args.putParcelable(TEACHER_FILTERS, teacherFilters);
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
            search = args.getString(SEARCH);
            isOwner = args.getBoolean(IS_OWNER);
            isSelector = args.getBoolean(IS_SELECTOR);
            isStudent = args.getBoolean(IS_STUDENT);
            studentFilters = args.getParcelable(STUDENT_FILTERS);
            teacherFilters = args.getParcelable(TEACHER_FILTERS);
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

        title = view.findViewById(R.id.textViewFragmentUserViewTitle);
        recyclerView = view.findViewById(R.id.recyclerViewFragmentUserView);
        filters = view.findViewById(R.id.imageViewFragmentUserViewFilters);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentUserViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentUserViewSearch);
        roleInput = view.findViewById(R.id.radioGroupFragmentUserViewRole);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(null);

        Query query = isOwner || !isStudent ? fm.getUsersQuery(isStudent) : fm.getUserStudentsQuery();

        adapter = new UserAdapter(new FirebaseRecyclerOptions.Builder<User>().setQuery(query, this::getUser).build(), this::createOptions);
        recyclerView.setAdapter(adapter);

        if (isOwner) roleInput.setVisibility(View.VISIBLE);
        else title.setText(isStudent ? R.string.students : R.string.teachers);

        roleInput.setOnCheckedChangeListener((radioGroup, i) ->
        {
            isStudent = i == R.id.radioButtonFragmentUserViewStudent;
            adapter = new UserAdapter(new FirebaseRecyclerOptions.Builder<User>().setQuery(fm.getUsersQuery(isStudent), this::getUser).build(), adapter);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        });

        filters.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                filters.setOnClickListener(null);

                if (isStudent)
                {
                    studentFilters.show(getChildFragmentManager(), null);
                    studentFilters.setCancel(() ->
                    {
                        filters.setOnClickListener(this);
                        setFilters();
                    });
                }
                else
                {
                    teacherFilters.show(getChildFragmentManager(), null);
                    teacherFilters.setCancel(() ->
                    {
                        filters.setOnClickListener(this);
                        setFilters();
                    });
                }
            }
        });

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
    public void onResume()
    {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        adapter.stopListening();
    }

    private void setFilters()
    {
        if (isStudent) setStudentFilters(studentFilters.getData());
        else setTeacherFilters(teacherFilters.getData());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setStudentFilters(StudentFiltersDialogFragment.Data data)
    {
        if (data != null)
        {
            adapter.setTheory(data.isTheory);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setTeacherFilters(TeacherFiltersDialogFragment.Data data)
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

    private void createOptions(@NonNull UserAdapter.ViewHolder viewHolder, final int position, User user)
    {
        PopupMenu menu = new PopupMenu(requireContext(), viewHolder.options);

        MenuItem info, assign, delete;

        Menu m = menu.getMenu();

        menu.getMenuInflater().inflate(R.menu.menu_user_options, m);

        info = m.findItem(R.id.menuItemUserOptionsMenuInfo);
        assign = m.findItem(R.id.menuItemUserOptionsMenuAssign);
        delete = m.findItem(R.id.menuItemUserOptionsMenuDelete);

        assign.setVisible(isSelector || (!isStudent && !isOwner));
        delete.setVisible(isOwner);

        menu.setOnMenuItemClickListener(menuItem ->
        {
            final int id = menuItem.getItemId();

            if (id == info.getItemId()) startInfoActivity(user);

            else if (id == assign.getItemId()) assign(user);

            else if (id == delete.getItemId());

            else return false;

            return true;
        });

        menu.show();
    }

    private void startInfoActivity(User user)
    {
        Intent intent = new Intent(requireContext(), InfoActivity.class);
        intent.putExtra(InfoActivity.USER, user);
        startActivity(intent);
    }

    private void assign(User user)
    {
        if (isSelector)
        {
            Intent intent = requireActivity().getIntent();
            intent.putExtra("user", user);
            requireActivity().finish();
        }
        else if (!isStudent) fm.setUserTeacher(requireContext(), user.id, new FirebaseRunnable() {
            @Override
            public void run()
            {
                ((MainActivity<?>) requireActivity()).createAndLinkFragments();
            }
        });
    }

    protected UserViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
        search = in.readString();
        isOwner = in.readByte() == 1;
        isSelector = in.readByte() == 1;
        isStudent = in.readByte() == 1;
        studentFilters = in.readParcelable(StudentFiltersDialogFragment.class.getClassLoader());
        teacherFilters = in.readParcelable(TeacherFiltersDialogFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(search);
        dest.writeByte((byte) (isOwner ? 1 : 0));
        dest.writeByte((byte) (isSelector ? 1 : 0));
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(studentFilters, flags);
        dest.writeParcelable(teacherFilters, flags);
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
