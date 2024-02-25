package com.example.drivinglessons.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class InputFragment extends Fragment
{
    private static final String FRAGMENT_TITLE_REGISTER = "register", FRAGMENT_TITLE_EDIT = "edit", IS_REGISTER = "is register", USER = "user", IS_STUDENT = "is student", STUDENT_FRAGMENT = "student fragment", TEACHER_FRAGMENT = "teacher fragment";

    private FirebaseManager fm;
    private InputStudentFragment studentFragment;
    private InputTeacherFragment teacherFragment;
    private boolean isRegister;
    private boolean isStudent;
    private Bitmap image;
    private User user;

    private TextView title;
    private TextInputLayout fullNameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout, birthdateInputLayout;
    private EditText fullNameInput, emailInput, passwordInput, confirmPasswordInput, birthdateInput;
    private RadioGroup roleInput;
    private Button buttonInput;

    public static InputFragment newInstance(InputStudentFragment studentFragment, InputTeacherFragment teacherFragment)
    {
        return newInstance(null, true, studentFragment, teacherFragment);
    }

    public static InputFragment newInstance(User user, boolean isStudent, InputStudentFragment studentFragment, InputTeacherFragment teacherFragment)
    {
        InputFragment fragment = new InputFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putBoolean(IS_REGISTER, user == null);
        args.putParcelable(USER, user == null ? User.emptyUser() : user);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(STUDENT_FRAGMENT, studentFragment);
        args.putParcelable(TEACHER_FRAGMENT, teacherFragment);
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
            isRegister = args.getBoolean(IS_REGISTER);
            user = args.getParcelable(USER);
            isStudent = args.getBoolean(IS_STUDENT);
            studentFragment = args.getParcelable(STUDENT_FRAGMENT);
            teacherFragment = args.getParcelable(TEACHER_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fm = FirebaseManager.getInstance(getContext());

        title = view.findViewById(R.id.textViewFragmentInputTitle);
        fullNameInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputFullName);
        fullNameInput = view.findViewById(R.id.editTextFragmentInputFullName);
        emailInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputEmail);
        emailInput = view.findViewById(R.id.editTextFragmentInputEmail);
        passwordInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputPassword);
        passwordInput = view.findViewById(R.id.editTextFragmentInputPassword);
        confirmPasswordInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputConfirmPassword);
        confirmPasswordInput = view.findViewById(R.id.editTextFragmentInputConfirmPassword);
        birthdateInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputBirthdate);
        birthdateInput = view.findViewById(R.id.editTextFragmentInputBirthdate);
        roleInput = view.findViewById(R.id.radioGroupFragmentInputRole);
        buttonInput = view.findViewById(R.id.buttonFragmentInput);

        if (!isRegister)
        {
            title.setText(R.string.edit);
            buttonInput.setText(R.string.edit);

            fullNameInput.setText(user.name);
            emailInput.setText(user.email);
            passwordInput.setText(user.password);
            confirmPasswordInput.setText(user.password);
            birthdateInput.setText(Constants.DATE_FORMAT.format(user.birthdate));
            roleInput.check(isStudent ? R.id.radioButtonFragmentInputStudent : R.id.radioButtonFragmentInputTeacher);

            emailInputLayout.setVisibility(View.GONE);
            passwordInputLayout.setVisibility(View.GONE);
            confirmPasswordInputLayout.setVisibility(View.GONE);
            roleInput.setFocusable(false);
            fullNameInput.setOnEditorActionListener((v, actionId, event) ->
                    actionId == EditorInfo.IME_ACTION_DONE && user.birthdate == null && createBirthdateDialog());
        }
        else
        {
            title.setText(R.string.register);
            buttonInput.setText(R.string.register);
            confirmPasswordInput.setOnEditorActionListener((v, actionId, event) ->
                    actionId == EditorInfo.IME_ACTION_DONE && user.birthdate == null && createBirthdateDialog());
        }

        replaceFragment();

        fullNameInput.addTextChangedListener((TextListener) s ->
        {
            String str = fullNameInput.getText().toString().trim().toLowerCase();
            if (str.isEmpty())
                fullNameInputLayout.setError(null);
            else if (str.length() < 2)
                fullNameInputLayout.setError("name must contain at least 2 letters");
            else if (str.length() > 32)
                fullNameInputLayout.setError("name must contain at most 32 letters");
            else if (!Pattern.matches(" *[a-z]+([ -][a-z]+)+ *", str))
                fullNameInputLayout.setError("name must be separated by a dash or a space");
            else fullNameInputLayout.setError(null);

            user.name = fixName(str);
        });

        emailInput.addTextChangedListener((TextListener) s ->
        {
            String str = emailInput.getText().toString().trim();

            if (str.isEmpty())
                emailInputLayout.setError(null);
            else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches())
                emailInputLayout.setError("invalid email address");
            else emailInputLayout.setError(null);

            user.email = str;
        });

        passwordInput.addTextChangedListener((TextListener) s ->
        {
            String str = passwordInput.getText().toString();

            if (str.isEmpty())
                passwordInputLayout.setError(null);
            else if (str.length() < 6)
                passwordInputLayout.setError("password must contain at least 6 characters");
            else if (str.length() > 127)
                passwordInputLayout.setError("password must contain at most 127 characters");
            else passwordInputLayout.setError(null);

            confirmPasswordInput.setText(confirmPasswordInput.getText());
            user.password = str;
        });

        confirmPasswordInput.addTextChangedListener((TextListener) s ->
        {
            String str = confirmPasswordInput.getText().toString();
            if (str.isEmpty())
                confirmPasswordInputLayout.setError(null);
            else if (str.length() < 6)
                confirmPasswordInputLayout.setError("password must contain at least 6 characters");
            else if (str.length() > 127)
                confirmPasswordInputLayout.setError("password must contain at most 127 characters");
            else if (!str.equals(user.password))
                confirmPasswordInputLayout.setError("confirm password must equal to password");
            else confirmPasswordInputLayout.setError(null);
        });
        birthdateInput.addTextChangedListener((TextListener) s -> birthdateInputLayout.setError(null));
        birthdateInput.setOnClickListener(v -> createBirthdateDialog());

        roleInput.setOnCheckedChangeListener((radioGroup, i) ->
        {
            isStudent = i == R.id.radioButtonFragmentInputStudent;
            replaceFragment();
        });

        buttonInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (user.name.isEmpty())
                    fullNameInputLayout.setError("full name mustn't be empty");
                if (user.email.isEmpty())
                    emailInputLayout.setError("email mustn't be empty");
                if (user.password.isEmpty())
                    passwordInputLayout.setError("password mustn't be empty");
                if (confirmPasswordInput.getText().toString().isEmpty())
                    confirmPasswordInputLayout.setError("confirm password mustn't be empty");
                if (user.birthdate == null)
                    birthdateInputLayout.setError("birthdate mustn't be empty");

                InputTeacherFragment.Data data = teacherFragment.getData();

                if (fullNameInputLayout.getError() != null || emailInputLayout.getError() != null || passwordInputLayout.getError() != null || confirmPasswordInputLayout.getError() != null || birthdateInputLayout.getError() != null || (!isStudent && data.cost == null)) return;

                buttonInput.setOnClickListener(null);
                View.OnClickListener listener = this;

                Date now = Calendar.getInstance().getTime();

                Balance balance = isRegister ? new Balance(0.0, now) : new Balance();

                if (isStudent)
                {
                    Student student = new Student(user, studentFragment.isTheory());
                    fm.saveStudent(getContext(), student, balance, bitmapToBytes(image), new FirebaseRunnable() {
                        @Override
                        public void run()
                        {

                        }
                    }, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            buttonInput.setOnClickListener(listener);
                        }
                    });
                }
            }
        });
    }

    private boolean createBirthdateDialog()
    {
        clearFocus();

        Calendar c = Calendar.getInstance();

        if (user.birthdate != null) c.setTime(user.birthdate);

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) ->
        {
            c.set(year, month, dayOfMonth);
            user.birthdate = c.getTime();
            birthdateInput.setText(Constants.DATE_FORMAT.format(user.birthdate));
        }, y, m, d);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, -16);
        calendar.add(Calendar.MONTH, -6);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();

        return true;
    }

    private String fixName(String name)
    {
        if (name.isEmpty()) return "";

        StringBuilder stringBuilder = new StringBuilder(name);

        stringBuilder.setCharAt(0, (char) (stringBuilder.charAt(0) - 'a' + 'A'));

        for (int i = 0; i < stringBuilder.length() - 1; i++)
            if (stringBuilder.charAt(i) == ' ' || stringBuilder.charAt(i) == '-')
                stringBuilder.setCharAt(i + 1, (char) (stringBuilder.charAt(i + 1) - 'a' + 'A'));

        return stringBuilder.toString();
    }

    private byte[] bitmapToBytes(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void clearFocus()
    {
        View view = requireActivity().getCurrentFocus();
        if (view != null)
        {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void replaceFragment()
    {
        if (isStudent) replaceFragment(studentFragment);
        else replaceFragment(teacherFragment);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentInput, fragment).commit();
    }

    @NonNull
    @Override
    public String toString()
    {
        return isRegister ? FRAGMENT_TITLE_REGISTER : FRAGMENT_TITLE_EDIT;
    }
}
