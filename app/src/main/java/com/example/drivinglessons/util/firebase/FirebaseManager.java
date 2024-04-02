package com.example.drivinglessons.util.firebase;

import android.content.Context;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.Rating;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.Transaction;
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
import java.util.Locale;

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

    public void updateLesson(Context c, @NonNull Lesson lesson, FirebaseRunnable success)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
            }
        };

        Transaction transaction = new Transaction();
        transaction.id = lesson.id;
        transaction.toId = lesson.teacherId;
        transaction.toName = lesson.teacherName;

        getTeacherLessons(lesson.teacherId, new FirebaseRunnable()
        {
            @Override
            public void run(List<?> lessons)
            {
                boolean isIntercepting = false;
                int i;

                for (i = 0; i < lessons.size() && !(isIntercepting = lesson.isIntercepting((Lesson) lessons.get(i))); i++);

                if (isIntercepting)
                {
                    Lesson intercepted = (Lesson) lessons.get(i);
                    toastS(c, String.format(Locale.ROOT, "Lesson is intercepting another who is between %s - %s",
                            Constants.TIME_FORMAT.format(intercepted.start), Constants.TIME_FORMAT.format(intercepted.end)));
                    failure.runAll();
                }
                else saveTransactionInDatabase(transaction, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        saveLessonInDatabase(lesson, success, failure);
                    }
                }, failure);
            }
        }, failure);


    }

    public void addMoney(String id, double amount, FirebaseRunnable success, FirebaseRunnable failure)
    {
        getUserFromDatabase(id, new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                String id1 = db.getReference("transaction").push().getKey();

                saveTransactionInDatabase(new Transaction(id1, null, id, null, user.name, Calendar.getInstance().getTime(), amount, true), new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        getBalanceFromDatabase(id, new FirebaseRunnable()
                        {
                            @Override
                            public void run(Balance balance)
                            {
                                balance.amount += amount;
                                saveBalanceInDatabase(id, balance, success, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        }, failure);
    }

    public void saveRating(@NonNull Rating rating, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        rating.id = db.getReference("rating").child(rating.toId).push().getKey();
        db.getReference("rating").child(rating.toId).child(rating.id).updateChildren(rating.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void getUserRatings(String id, FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getRatingQuery(id).get()
                .addOnSuccessListener(snapshot ->
                {
                    List<Rating> ratings = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        ratings.add(dataSnapshot.getValue(Rating.class));

                    success.runAll(ratings);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void confirmLesson(Context c, @NonNull Lesson lesson)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
            }
        };

        confirmLesson(lesson.id, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                confirmTransaction(lesson.id, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        getBalanceFromDatabase(lesson.teacherId, new FirebaseRunnable()
                        {
                            @Override
                            public void run(Balance balance)
                            {
                                balance.amount += lesson.cost;
                                saveBalanceInDatabase(lesson.teacherId, balance, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        toastS(c, R.string.lesson_confirmed);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        }, failure);
    }

    public void cancelLesson(Context c, @NonNull Lesson lesson)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
            }
        };
        cancelLesson(lesson.id, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                cancelTransaction(lesson.id, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        getBalanceFromDatabase(lesson.studentId, new FirebaseRunnable()
                        {
                            @Override
                            public void run(Balance balance)
                            {
                                balance.amount += lesson.cost;
                                saveBalanceInDatabase(lesson.studentId, balance, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        toastS(c, R.string.lesson_canceled);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        }, failure);
    }

    public void cancelTransaction(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("transaction").child(id).removeValue()
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public  void confirmTransaction(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        Transaction t = new Transaction();
        t.isConfirmed = true;
        db.getReference("transaction").child(id).updateChildren(t.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void cancelLesson(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("lesson").child(id).removeValue()
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void confirmLesson(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        Lesson l = new Lesson();
        l.isConfirmed = true;
        db.getReference("lesson").child(id).updateChildren(l.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void getRatingChanged(String id, FirebaseRunnable success)
    {
        getRatingChanged(id, success, new FirebaseRunnable() {});
    }

    public void getRatingChanged(String id, FirebaseRunnable success, FirebaseRunnable failure)
    {
        getRatingQuery(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                List<Rating> ratings = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    ratings.add(dataSnapshot.getValue(Rating.class));

                success.runAll(ratings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                failure.runAll();
            }
        });
    }

    public Query getRatingQuery(String id)
    {
        return db.getReference("rating").child(id).orderByChild("date");
    }

    public Query getUserStudentsQuery()
    {
        if (!spm.getIsStudent())
            return getDatabaseQuery("student").orderByChild("teacherId").equalTo(getCurrentUid());
        return null;
    }

    public void setUserTeacher(Context c, String teacherId, FirebaseRunnable success)
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

    public Query getTransactionsQuery()
    {
        return getDatabaseQuery("transaction").orderByChild("date");
    }

    public Query getTestLessonsQuery()
    {
        return getDatabaseQuery("lesson").orderByChild("isTest").equalTo(true);
    }

    public void getBalanceChanged(String id, @NonNull FirebaseRunnable success)
    {
        getBalanceChanged(id, success, new FirebaseRunnable() {});
    }

    public void getBalanceChanged(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("balance").child(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Balance balance = snapshot.getValue(Balance.class);

                success.runAll(balance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                failure.runAll();
            }
        });
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

    public void getLessonChanged(String id, @NonNull FirebaseRunnable success)
    {
        getLessonChanged(id, success, new FirebaseRunnable() {});
    }

    public void getLessonChanged(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("lesson").child(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Lesson lesson = snapshot.getValue(Lesson.class);

                success.runAll(lesson);
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
                                                //complete.runAll();
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

    public void saveTeacher(Context c, Teacher teacher, Balance balance, byte[] image, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable complete)
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
                                                //complete.runAll();
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

            spm.put(true, student.teacherId == null ? null : true, null, isOwner);

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
            public void run(List<?> lessons)
            {
                Calendar calendar = Calendar.getInstance();
                double time = 0;

                for (Object object : lessons)
                {
                    Lesson lesson = (Lesson) object;
                    if (lesson.isConfirmed && lesson.end.getTime() < calendar.getTime().getTime())
                        time += lesson.getDuration();
                }

                spm.putCanTest(Constants.TEST_TIME < time);

                success.runAll(user);
            }
        }, failure);
    }

    public void getStudentCanTest(String id, @NonNull FirebaseRunnable success)
    {
        getStudentCanTest(id, success, new FirebaseRunnable() {});
    }

    public void getStudentCanTest(String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        getStudentLessons(id, new FirebaseRunnable()
        {
            @Override
            public void run(List<?> lessons)
            {
                Calendar calendar = Calendar.getInstance();
                double time = 0;

                for (Object object : lessons)
                {
                    Lesson lesson = (Lesson) object;
                    if (lesson.isConfirmed && lesson.end.getTime() < calendar.getTime().getTime())
                        time += lesson.getDuration();
                }

                spm.putCanTest(Constants.TEST_TIME < time);

                success.runAll();
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

    public void saveLessonInDatabase(@NonNull Lesson lesson, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("lesson").child(lesson.id).updateChildren(lesson.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void saveTransactionInDatabase(@NonNull Transaction transaction, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("transaction").child(transaction.id).updateChildren(transaction.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void getBalanceFromDatabase(@NonNull String id, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        db.getReference("balance").child(id).get()
                .addOnSuccessListener(snapshot ->
                {
                    Balance balance = snapshot.getValue(Balance.class);
                    success.runAll(balance);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void saveLesson(Context c, @NonNull Lesson lesson, @NonNull FirebaseRunnable success, @NonNull FirebaseRunnable failure)
    {
        FirebaseRunnable failure1 = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                super.run(e);
                toastS(c, R.string.went_wrong_error);
                failure.runAll();
            }
        };

        FirebaseRunnable onward = new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                getBalanceFromDatabase(lesson.studentId, new FirebaseRunnable()
                {
                    @Override
                    public void run(Balance balance)
                    {

                        if (balance.amount < lesson.cost)
                        {
                            toastS(c, R.string.enough_money_error);
                            failure.runAll();
                        }
                        else
                        {
                            balance.amount -= lesson.cost;

                            saveBalanceInDatabase(lesson.studentId, balance, new FirebaseRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    Date now = Calendar.getInstance().getTime();

                                    String id = db.getReference("transaction").push().getKey();

                                    lesson.id = id;
                                    saveTransactionInDatabase(new Transaction(id, lesson.studentId, lesson.teacherId, lesson.studentName, lesson.teacherName, now, lesson.cost, false), new FirebaseRunnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            saveLessonInDatabase(lesson, success, failure1);
                                        }
                                    }, failure1);
                                }
                            }, failure1);
                        }
                    }
                }, failure1);
            }
        };

        getStudentFromDatabase(lesson.studentId, new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                Student student = (Student) user;

                lesson.studentName = user.name;

                if (lesson.isTest)
                {
                    lesson.cost = lesson.getDuration() * Constants.TEST_COST;
                    getStudentLessons(lesson.studentId, new FirebaseRunnable()
                    {
                        @Override
                        public void run(List<?> lessons)
                        {
                            boolean isIntercepting = false;
                            int i;

                            for (i = 0; i < lessons.size() && !(isIntercepting = lesson.isIntercepting((Lesson) lessons.get(i))); i++);

                            if (isIntercepting)
                            {
                                Lesson intercepted = (Lesson) lessons.get(i);
                                toastS(c, String.format(Locale.ROOT, "Lesson is intercepting another who is between %s - %s",
                                        Constants.TIME_FORMAT.format(intercepted.start), Constants.TIME_FORMAT.format(intercepted.end)));
                                failure.runAll();
                            }

                            else onward.runAll();
                        }
                    }, failure1);
                }

                else
                {
                    lesson.teacherId = student.teacherId;
                    getTeacherFromDatabase(lesson.teacherId, new FirebaseRunnable()
                    {
                        @Override
                        public void run(User user)
                        {
                            Teacher teacher = (Teacher) user;

                            if (teacher.isTester)
                            {
                                toastS(c, "Assign a new teacher your teacher switched to a different role");
                                failure.runAll();
                            }

                            else
                            {
                                lesson.teacherName = user.name;
                                lesson.cost = lesson.getDuration() * teacher.costPerHour;

                                getTeacherLessons(lesson.teacherId, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run(List<?> lessons)
                                    {
                                        boolean isIntercepting = false;
                                        int i;

                                        for (i = 0; i < lessons.size() && !(isIntercepting = lesson.isIntercepting((Lesson) lessons.get(i))); i++);

                                        if (isIntercepting)
                                        {
                                            Lesson intercepted = (Lesson) lessons.get(i);
                                            toastS(c, String.format(Locale.ROOT, "Lesson is intercepting another who is between %s - %s",
                                                    Constants.TIME_FORMAT.format(intercepted.start), Constants.TIME_FORMAT.format(intercepted.end)));
                                            failure.runAll();
                                        }

                                        else onward.runAll();
                                    }
                                }, failure1);
                            }
                        }
                    }, failure1);
                }
            }
        }, failure1);

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
        return "image/" + id + "/profile.png";
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
