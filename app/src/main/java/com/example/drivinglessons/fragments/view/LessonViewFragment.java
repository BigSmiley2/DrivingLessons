package com.example.drivinglessons.fragments.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivinglessons.R;
import com.example.drivinglessons.adapters.LessonAdapter;
import com.example.drivinglessons.dialogs.LessonDialog;
import com.example.drivinglessons.dialogs.LessonFiltersDialogFragment;
import com.example.drivinglessons.dialogs.LessonInfoDialogFragment;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.validation.TextListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;

import java.util.regex.Pattern;

public class LessonViewFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "lessons", ID = "id", SEARCH = "search", IS_OWNER = "is owner", IS_STUDENT = "is student", LESSON_FILTERS = "lesson filters", LESSON_INFO = "lesson info";

    private boolean isOwner, isStudent;
    private String id, search;
    private LessonFiltersDialogFragment lessonFilters;
    private LessonInfoDialogFragment lessonInfo;

    private FirebaseManager fm;
    private LessonAdapter adapter;

    private RecyclerView recyclerView;
    private ImageView filters, add;
    private TextInputLayout searchInputLayout;
    private EditText searchInput;

    public LessonViewFragment() {}

    @NonNull
    public static LessonViewFragment newInstance()
    {
        return newInstance(null, "", true, false, LessonFiltersDialogFragment.newInstance(true), LessonInfoDialogFragment.newInstance(null));
    }

    @NonNull
    public static LessonViewFragment newInstance(String id, boolean isStudent)
    {
        return newInstance(id, "", false, isStudent, LessonFiltersDialogFragment.newInstance(false), LessonInfoDialogFragment.newInstance(null));
    }
    @NonNull
    public static LessonViewFragment newInstance(String id, String search, boolean isOwner, boolean isStudent, LessonFiltersDialogFragment lessonFilters, LessonInfoDialogFragment lessonInfo)
    {
        LessonViewFragment fragment = new LessonViewFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(SEARCH, search);
        args.putBoolean(IS_OWNER, isOwner);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(LESSON_FILTERS, lessonFilters);
        args.putParcelable(LESSON_INFO, lessonInfo);
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
            isStudent = args.getBoolean(IS_STUDENT);
            lessonFilters = args.getParcelable(LESSON_FILTERS);
            lessonInfo = args.getParcelable(LESSON_INFO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_lesson_view, container, false);
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentLessonView);
        filters = view.findViewById(R.id.imageViewFragmentLessonViewFilters);
        add = view.findViewById(R.id.imageViewFragmentLessonViewAdd);
        searchInputLayout = view.findViewById(R.id.textInputLayoutFragmentLessonViewSearch);
        searchInput = view.findViewById(R.id.editTextFragmentLessonViewSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(null);

        if (!isOwner && isStudent) add.setVisibility(View.VISIBLE);

        adapter = new LessonAdapter(new FirebaseRecyclerOptions.Builder<Lesson>().setQuery(isOwner ? fm.getTestLessonsQuery() : fm.getUserLessonsQuery(isStudent, id), this::getLesson).build(), this::createOptions);
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                add.setOnClickListener(null);

                LessonDialog lessonDialog = new LessonDialog(requireContext(), id, false);

                lessonDialog.setOnDismissListener(dialogInterface -> add.setOnClickListener(this));

                lessonDialog.show();
            }
        });

        filters.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                filters.setOnClickListener(null);

                lessonFilters.show(getChildFragmentManager(), null);
                lessonFilters.setCancel(() ->
                {
                    filters.setOnClickListener(this);
                    setFilters();
                });
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

    private Lesson getLesson(@NonNull DataSnapshot snapshot)
    {
        return snapshot.getValue(Lesson.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setFilters()
    {
        LessonFiltersDialogFragment.Data data = lessonFilters.getData();

        if (data != null)
        {
            adapter.setConfirmed(data.isConfirmed);
            adapter.setPast(data.isPast);
            adapter.setAssigned(data.isAssigned);
            adapter.notifyDataSetChanged();
        }
    }

    private void createOptions(@NonNull LessonAdapter.ViewHolder viewHolder, final int position, Lesson lesson)
    {
        PopupMenu menu = new PopupMenu(requireContext(), viewHolder.options);

        MenuItem info, confirm, assign;

        Menu m = menu.getMenu();

        menu.getMenuInflater().inflate(R.menu.menu_lesson_options, m);

        info = m.findItem(R.id.menuItemLessonOptionsMenuInfo);
        confirm = m.findItem(R.id.menuItemLessonOptionsMenuConfirm);
        assign = m.findItem(R.id.menuItemLessonOptionsMenuAssign);

        confirm.setVisible(!isStudent && !isOwner);
        assign.setVisible(isOwner);

        menu.setOnMenuItemClickListener(menuItem ->
        {
            final int id = menuItem.getItemId();

            if (id == info.getItemId()) lessonInfo(lesson);

            else if (id == confirm.getItemId() && lesson.isConfirmed) Toast.makeText(requireContext(), "The lesson is already confirmed", Toast.LENGTH_SHORT).show();

            else if (id == confirm.getItemId()) confirmLesson(lesson);

            else if (id == assign.getItemId());

            else return false;

            return true;
        });

        menu.show();
    }

    private void confirmLesson(Lesson lesson)
    {
        Constants.createAlertDialog(requireContext(), "Will you attend this lesson?", "yes", "no", (dialog, which) ->
        {
            if (which == DialogInterface.BUTTON_POSITIVE) fm.confirmLesson(requireContext(), lesson);
            else if (which == DialogInterface.BUTTON_NEGATIVE) fm.cancelLesson(requireContext(), lesson);
        });
    }

    private void lessonInfo(@NonNull Lesson lesson)
    {
        lessonInfo.setId(lesson.id);
        lessonInfo.show(getChildFragmentManager(), null);
    }

    protected LessonViewFragment(@NonNull Parcel in)
    {
        id = in.readString();
        search = in.readString();
        isOwner = in.readByte() == 1;
        isStudent = in.readByte() == 1;
        lessonFilters = in.readParcelable(LessonFiltersDialogFragment.class.getClassLoader());
        lessonInfo = in.readParcelable(LessonInfoDialogFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(search);
        dest.writeByte((byte) (isOwner ? 1 : 0));
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(lessonFilters, flags);
        dest.writeParcelable(lessonInfo, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<LessonViewFragment> CREATOR = new Creator<LessonViewFragment>()
    {
        @Override
        public LessonViewFragment createFromParcel(Parcel in)
        {
            return new LessonViewFragment(in);
        }

        @Override
        public LessonViewFragment[] newArray(int size)
        {
            return new LessonViewFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
