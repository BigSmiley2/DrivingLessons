package com.example.drivinglessons.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;

import java.util.Date;
import java.util.Locale;

public class LessonInfoDialogFragment extends DialogFragment implements Parcelable
{
    private static final String TITLE = "lesson info", ID = "id";

    private String id;

    private FirebaseManager fm;

    private TextView title, start, end, student, teacher, cost;
    private ImageView confirmed;

    public LessonInfoDialogFragment() {}

    @NonNull
    public static LessonInfoDialogFragment newInstance(String id)
    {
        LessonInfoDialogFragment fragment = new LessonInfoDialogFragment();

        Bundle args = new Bundle();
        args.putString(ID, id);
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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_lesson_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());

        title = view.findViewById(R.id.textViewDialogFragmentLessonInfoTitle);
        start = view.findViewById(R.id.textViewDialogFragmentLessonInfoStartDate);
        end = view.findViewById(R.id.textViewDialogFragmentLessonInfoEndDate);
        student = view.findViewById(R.id.textViewDialogFragmentLessonInfoStudent);
        teacher = view.findViewById(R.id.textViewDialogFragmentLessonInfoTeacher);
        confirmed = view.findViewById(R.id.imageViewDialogFragmentLessonInfoConfirmed);
        cost = view.findViewById(R.id.textViewDialogFragmentLessonInfoCost);

        fm.getLessonChanged(id, new FirebaseRunnable()
        {
            @Override
            public void runAll(Lesson lesson)
            {
                title.setText(lesson.isTest ? R.string.driving_test : R.string.driving_lesson);
                start.setText(LessonInfoDialogFragment.toString(lesson.start));
                end.setText(LessonInfoDialogFragment.toString(lesson.end));
                student.setText(lesson.studentName);
                teacher.setText(lesson.teacherName == null ? "?" : lesson.teacherName);
                cost.setText(String.format(Locale.ROOT, "Cost: %.2f₪", lesson.cost));
                confirmed.setImageResource(lesson.isConfirmed ? R.drawable.check_colored : R.drawable.uncheck_colored);
            }
        });
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
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            //window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        return dialog;
    }

    public void setId(String id)
    {
        Bundle args = new Bundle();
        args.putString(ID, id);
        setArguments(args);
    }

    @NonNull
    private static String toString(Date date)
    {
        return String.format(Locale.ROOT,"%s\t\t%s", Constants.DATE_FORMAT.format(date), Constants.TIME_FORMAT.format(date));
    }

    protected LessonInfoDialogFragment(@NonNull Parcel in)
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

    public static final Creator<LessonInfoDialogFragment> CREATOR = new Creator<LessonInfoDialogFragment>()
    {
        @Override
        public LessonInfoDialogFragment createFromParcel(Parcel in)
        {
            return new LessonInfoDialogFragment(in);
        }

        @Override
        public LessonInfoDialogFragment[] newArray(int size)
        {
            return new LessonInfoDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return TITLE;
    }
}
