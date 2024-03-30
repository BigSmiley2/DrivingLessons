package com.example.drivinglessons.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddRatingDialogFragment extends DialogFragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "add rating", TO_ID = "to id", FROM_ID = "from id", IS_STUDENT = "is student", RATE = "rate", MESSAGE = "message";

    private String toId, fromId, message, name, imagePath;
    private boolean isStudent;
    private double rate;

    private FirebaseManager fm;
    private SharedPreferencesManager spm;

    private ConstraintLayout layout;
    private EditText messageInput;
    private TextInputLayout messageInputLayout;
    private TextView title, fullName;
    private RatingBar ratingInput;
    private ImageView send, image;


    public AddRatingDialogFragment() {}

    @NonNull
    public static AddRatingDialogFragment newInstance(String toId, String fromId, boolean isStudent)
    {
        AddRatingDialogFragment fragment = new AddRatingDialogFragment();

        Bundle args = new Bundle();
        args.putString(TO_ID, toId);
        args.putString(FROM_ID, fromId);
        args.putBoolean(IS_STUDENT, isStudent);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());
        spm = SharedPreferencesManager.getInstance(requireContext());

        Bundle args = getArguments();
        if (args != null)
        {
            toId = args.getString(TO_ID);
            fromId = args.getString(FROM_ID);
            isStudent = args.getBoolean(IS_STUDENT);
            rate = args.getDouble(RATE);
            message = args.getString(MESSAGE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.dialog_fragment_add_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        messageInput = view.findViewById(R.id.editTextDialogFragmentAddRatingMessage);
        messageInputLayout = view.findViewById(R.id.textInputLayoutDialogFragmentAddRatingMessage);
        ratingInput = view.findViewById(R.id.ratingBarDialogFragmentAddRating);
        title = view.findViewById(R.id.textViewDialogFragmentAddRatingTitle);
        fullName = view.findViewById(R.id.textViewDialogFragmentAddRatingFullName);
        send = view.findViewById(R.id.imageViewDialogFragmentAddRatingSend);
        image = view.findViewById(R.id.imageViewDialogFragmentAddRating);
        layout = view.findViewById(R.id.ConstraintLayoutDialogFragmentAddRating);

        messageInput.setText(message);
        ratingInput.setRating((float) rate);

        FirebaseRunnable success = new FirebaseRunnable()
        {
            @Override
            public void run(@NonNull User user)
            {
                fullName.setText(user.name);
                Glide.with(requireContext()).load(fm.getStorageReference(user.imagePath))
                        .thumbnail(Glide.with(requireContext()).load(R.drawable.loading).circleCrop())
                        .circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(image);
                name = user.name;
                imagePath = user.imagePath;
                setVisible();
            }
        };

        if (spm.getIsStudent()) fm.getStudentChanged(fromId, success);
        else fm.getTeacherChanged(fromId, success);

        title.setText(isStudent ? R.string.add_student_rating : R.string.add_teacher_rating);
        ratingInput.setOnRatingBarChangeListener((ratingBar, v, b) ->
        {
            if (b) rate = v;
        });
        messageInput.addTextChangedListener((TextListener) s ->
        {
            String str = messageInput.getText().toString();
            if (str.isEmpty())
                messageInputLayout.setError(null);
            else if (str.length() > 500)
                messageInputLayout.setError("Message must be at most 500 characters");
            message = str;
        });
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                send.setOnClickListener(null);
                View.OnClickListener listener = this;

                if (message.isEmpty())
                    messageInputLayout.setError("Message mustn't be empty");

                if (messageInputLayout.getError() != null)
                {
                    send.setOnClickListener(this);
                }
                else fm.saveRating(new Rating(null, fromId, toId, name, imagePath, message, Calendar.getInstance().getTime(), rate), new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(requireContext(), R.string.rating_added, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }, new FirebaseRunnable()
                {
                    @Override
                    public void run(Exception e)
                    {
                        super.run(e);
                        Toast.makeText(requireContext(), R.string.went_wrong_error, Toast.LENGTH_SHORT).show();
                        send.setOnClickListener(listener);
                    }
                });
            }
        });

    }

    private void setVisible()
    {
        layout.setVisibility(View.VISIBLE);
    }

    public void reset(double rate)
    {
        Bundle args = getArguments();
        if (args != null)
        {
            args.putDouble(RATE, rate);
            args.putString(MESSAGE, "");
            setArguments(args);
        }
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

    protected AddRatingDialogFragment(@NonNull Parcel in)
    {
        toId = in.readString();
        fromId = in.readString();
        message = in.readString();
        name = in.readString();
        imagePath = in.readString();
        isStudent = in.readByte() == 1;
        rate = in.readDouble();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(toId);
        dest.writeString(fromId);
        dest.writeString(message);
        dest.writeString(name);
        dest.writeString(imagePath);
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeDouble(rate);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<AddRatingDialogFragment> CREATOR = new Creator<AddRatingDialogFragment>()
    {
        @Override
        public AddRatingDialogFragment createFromParcel(Parcel in)
        {
            return new AddRatingDialogFragment(in);
        }

        @Override
        public AddRatingDialogFragment[] newArray(int size)
        {
            return new AddRatingDialogFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
