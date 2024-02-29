package com.example.drivinglessons.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.fragments.StudentFiltersFragment;
import com.example.drivinglessons.fragments.TeacherFiltersFragment;
import com.example.drivinglessons.util.dialogs.DialogCancel;

public class UserFiltersDialogFragment extends DialogFragment implements Parcelable
{
    private static final String TITLE = "user filters", IS_STUDENT = "is student", TEACHER_FILTERS = "teacher filters", STUDENT_FILTERS = "student filters";

    private boolean isStudent;
    private TeacherFiltersFragment teacherFilters;
    private StudentFiltersFragment studentFilters;
    private DialogCancel cancel;

    public UserFiltersDialogFragment() {}

    @NonNull
    public static UserFiltersDialogFragment newInstance(boolean isStudent)
    {
        return newInstance(false, isStudent);
    }

    @NonNull
    public static UserFiltersDialogFragment newInstance(boolean isOwner, boolean isStudent)
    {
        return newInstance(isStudent, TeacherFiltersFragment.newInstance(isOwner), StudentFiltersFragment.newInstance());
    }

    @NonNull
    public static UserFiltersDialogFragment newInstance(TeacherFiltersFragment teacherFilters, StudentFiltersFragment studentFilters)
    {
        return newInstance(false, teacherFilters, studentFilters);
    }

    @NonNull
    public static UserFiltersDialogFragment newInstance(boolean isStudent, TeacherFiltersFragment teacherFilters, StudentFiltersFragment studentFilters)
    {
        UserFiltersDialogFragment fragment = new UserFiltersDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(TEACHER_FILTERS, teacherFilters);
        args.putParcelable(STUDENT_FILTERS, studentFilters);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            isStudent = args.getBoolean(IS_STUDENT);
            teacherFilters = args.getParcelable(TEACHER_FILTERS);
            studentFilters = args.getParcelable(STUDENT_FILTERS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_user_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        replaceFragment();
    }

    private void replaceFragment()
    {
        if (isStudent) replaceFragment(studentFilters);
        else replaceFragment(teacherFilters);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentUserFilters, fragment).commit();
    }

    public StudentFiltersFragment.Data getStudentData()
    {
        return studentFilters.getData();
    }

    public TeacherFiltersFragment.Data getTeacherData()
    {
        return teacherFilters.getData();
    }

    public void setCancel(DialogCancel cancel)
    {
        this.cancel = cancel;
    }

    public boolean isStudent()
    {
        return isStudent;
    }

    public void setStudent(boolean isStudent)
    {
        this.isStudent = isStudent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null)
        {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            //window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog)
    {
        super.onCancel(dialog);
        cancel.cancel();
    }

    protected UserFiltersDialogFragment(@NonNull Parcel in)
    {
        isStudent = in.readByte() == 1;
        teacherFilters = in.readParcelable(TeacherFiltersFragment.class.getClassLoader());
        studentFilters = in.readParcelable(StudentFiltersFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(teacherFilters, flags);
        dest.writeParcelable(studentFilters, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<UserFiltersDialogFragment> CREATOR = new Creator<UserFiltersDialogFragment>()
    {
        @Override
        public UserFiltersDialogFragment createFromParcel(Parcel in)
        {
            return new UserFiltersDialogFragment(in);
        }

        @Override
        public UserFiltersDialogFragment[] newArray(int size)
        {
            return new UserFiltersDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
