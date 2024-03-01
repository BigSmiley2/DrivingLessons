package com.example.drivinglessons.util.firebase;

import android.content.Context;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirebaseManager
{
    private static FirebaseManager fm;
    private final SharedPreferencesManager spm;
    private final FirebaseAuth auth;
    private final FirebaseDatabase db;
    private final FirebaseStorage st;

    private FirebaseManager(Context context)
    {
        spm = SharedPreferencesManager.getInstance(context);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        st = FirebaseStorage.getInstance();
    }

    public static FirebaseManager getInstance(Context context)
    {
        return fm == null ? fm = new FirebaseManager(context) : fm;
    }

    public void setCurrentTeacher(Context c, String teacherId, FirebaseRunnable success)
    {
        if (spm.getIsStudent())
            setStudentTeacher(getCurrentUid(), teacherId, new FirebaseRunnable()
            {
                @Override
                public void run()
                {
                    spm.putHasTeacher(true);
                    toastS(c, R.string.assigned_teacher);
                    success.runAll();
                }
            }, new FirebaseRunnable()
            {
                @Override
                public void run(Exception e)
                {
                    super.run(e);
                    toastS(c, R.string.went_wrong_error);
                }
            });
        else toastS(c, R.string.went_wrong_error);
    }

    public void setStudentTeacher(Context c, String id, String teacherId)
    {
        setStudentTeacher(id, teacherId, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                toastS(c, R.string.assigned_teacher);
            }
        }, new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
            }
        });
    }

    private void setStudentTeacher(String id, String teacherId, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        Student student = new Student();
        student.teacherId = teacherId;

        db.getReference("student").child(id).updateChildren(student.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public StorageReference getStorageReference(String path)
    {
        return st.getReference(path);
    }

    public Query getDatabaseQuery(String path)
    {
        return db.getReference(path);
    }

    public Query getStudentsQuery()
    {
        return getDatabaseQuery("student").orderByChild("name");
    }

    public Query getTeachersQuery()
    {
        return getDatabaseQuery("teacher").orderByChild("name");
    }

    public Query getUsersQuery(boolean isStudent)
    {
        return isStudent ? getStudentsQuery() : getTeachersQuery();
    }

    public Query getTeacherLessonsQuery(String id)
    {
        return getDatabaseQuery("lesson").orderByChild("teacherId").equalTo(id);
    }

    public Query getStudentLessonsQuery(String id)
    {
        return getDatabaseQuery("lesson").orderByChild("studentId").equalTo(id);
    }

    public Query getUserLessonsQuery(boolean isStudent, String id)
    {
        return isStudent ? getStudentLessonsQuery(id) : getTeacherLessonsQuery(id);
    }

    public Query getTestLessonsQuery()
    {
        return getDatabaseQuery("lesson").orderByChild("isTest").equalTo(true);
    }

    public void getStudentChanged(String id, @NonNull FirebaseRunnable success)
    {
        getStudentChanged(id, success, new FirebaseRunnable() {});
    }

    public void getStudentChanged(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("student").child(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Student student = snapshot.getValue(Student.class);

                success.runAll(student);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                failure.runAll();
            }
        });
    }

    public void getTeacherChanged(String id, @NonNull FirebaseRunnable success)
    {
        getTeacherChanged(id, success, new FirebaseRunnable() {});
    }

    public void getTeacherChanged(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("teacher").child(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Teacher teacher = snapshot.getValue(Teacher.class);

                success.runAll(teacher);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                failure.runAll();
            }
        });
    }


    public void saveStudent(Context c, Student student, Balance balance, byte[] image, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable complete)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
                complete.runAll(e);
            }
        };
        FirebaseRunnable onward = new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                student.id = auth.getUid();
                student.imagePath = getPath(student.id);
                saveInStorage(student.imagePath, image, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        saveBalanceInDatabase(student.id, balance, new FirebaseRunnable()
                        {
                            @Override
                            public void run()
                            {
                                saveStudentInDatabase(student, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        signInSharedPreferences(student, new FirebaseRunnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                success.runAll();
                                                complete.runAll();
                                            }
                                        }, failure);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        };
        if (isSigned()) deleteInStorage(student.imagePath, onward, failure);
        else registerToAuth(student, onward, failure);
    }

    public  void saveTeacher(Context c, Teacher teacher, Balance balance, byte[] image, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable complete)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
                complete.runAll(e);
            }
        };
        FirebaseRunnable onward = new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                teacher.id = auth.getUid();
                teacher.imagePath = getPath(teacher.id);
                saveInStorage(teacher.imagePath, image, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        saveBalanceInDatabase(teacher.id, balance, new FirebaseRunnable()
                        {
                            @Override
                            public void run()
                            {
                                saveTeacherInDatabase(teacher, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        signInSharedPreferences(teacher, new FirebaseRunnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                success.runAll();
                                                complete.runAll();
                                            }
                                        }, failure);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        };
        if (isSigned()) deleteInStorage(teacher.imagePath, onward, failure);
        else registerToAuth(teacher, onward, failure);
    }

    private void registerToAuth(@NonNull User user, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener(authResult -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    public void saveBalanceInDatabase(String id, @NonNull Balance balance, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("balance").child(id).updateChildren(balance.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveStudentInDatabase(@NonNull Student student, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("student").child(student.id).updateChildren(student.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveInStorage(String path, byte[] image, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        st.getReference(path).putBytes(image)
                .addOnSuccessListener(taskSnapshot -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    public void deleteInStorage(String path, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        st.getReference(path).delete()
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveTeacherInDatabase(@NonNull Teacher teacher, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("teacher").child(teacher.id).updateChildren(teacher.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void sendPasswordReset(Context c, String email, @NonNull FirebaseRunnable complete)
    {
        sendPasswordReset(email, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                toastS(c, R.string.reset_email_success);
                complete.runAll();
            }
        }, new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.reset_email_failure);
                complete.runAll();
            }
        });
    }

    private void sendPasswordReset(String email, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void signInAuth(String email, String password, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    private void signOutAuth(@NonNull FirebaseRunnable success)
    {
        auth.signOut();
        success.runAll();
    }

    public void signIn(Context c, String email, String password, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable complete)
    {
        fm.signInAuth(email, password, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                fm.getCurrentUserFromDatabase(new FirebaseRunnable()
                {
                    @Override
                    public void run(User user)
                    {
                        signInSharedPreferences(user, new FirebaseRunnable()
                        {
                            @Override
                            public void run(User user)
                            {
                                success.runAll(user);
                                complete.runAll(user);
                            }
                        },  new FirebaseRunnable()
                        {
                            @Override
                            public void run(Exception e)
                            {
                                super.run(e);
                                toastS(c, R.string.went_wrong_error);
                                complete.runAll(e);
                            }
                        });
                    }
                }, new FirebaseRunnable()
                {
                    @Override
                    public void run(Exception e)
                    {
                        super.run(e);
                        toastS(c, R.string.went_wrong_error);
                        complete.runAll(e);
                    }
                });
            }
        }, new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.email_or_password_incorrect);
                complete.runAll(e);
            }
        });
    }

    public void signOut(@NonNull FirebaseRunnable success)
    {
        signOutAuth(new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                signOutSharedPreferences(success);
            }
        });
    }

    public void signOut()
    {
        signOut(new FirebaseRunnable() {});
    }

    private void signInSharedPreferences(@NonNull User user, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        boolean isStudent = user instanceof Student, isOwner = Constants.isOwner(user.email);

        if (isStudent)
        {
            Student student = (Student) user;

            boolean hasTeacher = student.teacherId != null;

            spm.put(true, hasTeacher, null, isOwner);

            getStudentCanTest(user, success, failure);
        }
        else
        {
            spm.put(false, null, null, isOwner);
            success.runAll(user);
        }
    }

    private void signOutSharedPreferences(@NonNull FirebaseRunnable success)
    {
        spm.clear();
        success.runAll();
    }

    public void getStudentCanTest(User user, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getStudentLessons(user, new FirebaseRunnable()
        {
            @Override
            public void run(List<Lesson> lessons)
            {
                Calendar calendar = Calendar.getInstance();
                double time = 0;

                for (Lesson lesson : lessons)
                    if (lesson.isConfirmed && lesson.end.getTime() < calendar.getTime().getTime())
                        time += lesson.getDuration();

                spm.putCanTest(Constants.TEST_TIME < time);

                success.runAll(user);
            }
        }, failure);
    }

    public void getStudentLessons(@NonNull User user, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getStudentLessons(user.id, success, failure);
    }

    public void getStudentLessons(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("lesson").orderByChild("studentId").equalTo(id).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    List<Lesson> lessons = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        lessons.add(snapshot.getValue(Lesson.class));

                    success.runAll(lessons);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getTeacherLessons(@NonNull User user, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getTeacherLessons(user.id, success, failure);
    }

    public void getTeacherLessons(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("lesson").orderByChild("teacherId").equalTo(id).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    List<Lesson> lessons = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        lessons.add(snapshot.getValue(Lesson.class));

                    success.runAll(lessons);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getUserFromDatabase(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getStudentFromDatabase(id, success, failure);

        getTeacherFromDatabase(id, success, failure);
    }

    public void getStudentFromDatabase(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("student").orderByKey().equalTo(id).limitToFirst(1).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    Student student = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        student = snapshot.getValue(Student.class);

                    if (student != null) success.runAll(student);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getTeacherFromDatabase(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("teacher").orderByKey().equalTo(id).limitToFirst(1).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    Teacher teacher = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        teacher = snapshot.getValue(Teacher.class);

                    if (teacher != null) success.runAll(teacher);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getCurrentUserFromDatabase(@NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getUserFromDatabase(auth.getUid(), success, failure);
    }

    public String getCurrentUid()
    {
        return auth.getUid();
    }

    public boolean isSigned()
    {
        return getCurrentUid() != null;
    }

    @NonNull
    private String getPath(String id)
    {
        Date now = Calendar.getInstance().getTime();
        return "image/" + id + "/" + Constants.FILE_FORMAT.format(now) + ".png";
    }

    private void toastS(Context c, int message)
    {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private void toastS(Context c, String message)
    {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private void toastL(Context c, int message)
    {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    private void toastL(Context c, String message)
    {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }
}
