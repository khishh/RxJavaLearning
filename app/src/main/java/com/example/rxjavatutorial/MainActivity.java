package com.example.rxjavatutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private String greeting = "Hello RxJava";
    private String[] greetings = {"hello1", "hello2", "hello3"};
    private Integer[] numbers = {1,2,3,4,5};

    // private Observable<Integer> greetingObservable;
    private Observable<Student> studentObservable;

    // private DisposableObserver<Integer> greetingObserver;
    private DisposableObserver<Student> studentDisposableObserver;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ======== Just Operator =======

        //greetingObservable = Observable.just(greeting);

        // just operator only emit item once even if it's array
        //greetingObservable = Observable.just(greetings);

        // by inputting values separately like below, observer will emit item one by one.
        //greetingObservable = Observable.just("hello1", "hello2", "hello3");

        // ======== FromArray Operator =======

        // emit items in array one by one.
        // greetingObservable = Observable.fromArray(numbers);

        // ======== Range Operator =======

        /*

        greetingObservable = Observable.range(1, 20);

        compositeDisposable.add(
                greetingObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getObserver())
        );

         */

        // ======== Create Operator =======

        /*
        // we can customize what kind of data to be emitted into observer
        studentObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {

                List<Student> studentList = initStudents();

                // emit all Student and later will be streamed into studentDisposableObserver after emitter.onComplete();
                for(Student student : studentList){
                    Log.d(TAG, "subscribe: " + student.getEmail());
                    emitter.onNext(student);
                }

                emitter.onComplete();
            }
        });

        compositeDisposable.add(
                studentObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getStudentObserver())
        );

         */

        // ======== Map Operator =======

        // applying a function to transform each of Student instance

        /*
        studentObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                List<Student> studentList = initStudents();

                // emit all Student and later will be streamed into studentDisposableObserver after emitter.onComplete();
                for(Student student : studentList){
                    Log.d(TAG, "subscribe: " + student.getName());
                    emitter.onNext(student);
                }

                emitter.onComplete();
            }
        });

        // in map operator, modify/transform the data emitted from Observable and return it to Observer
        compositeDisposable.add(
                studentObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Student, Object>() {

                            @Override
                            public Student apply(Student student) throws Exception {
                                student.setName(student.getName().toUpperCase());
                                return student;
                            }
                        })
                        .subscribeWith(getStudentObserver())
        );

         */

        // ======== FlatMap Operator =======

        // transform the items emitted by an Observable into Observables, then flatten the emissions from those into a single Observable
        // item -> Map operator -> item
        // item -> FlatMap operator -> Observable
        // cons: FlatMap does not preserve the order of the elements

        /*

        studentObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                List<Student> studentList = initStudents();

                // emit all Student and later will be streamed into studentDisposableObserver after emitter.onComplete();
                for(Student student : studentList){
                    Log.d(TAG, "subscribe: " + student.getName());
                    emitter.onNext(student);
                }

                emitter.onComplete();
            }
        });

        // in map operator, modify/transform the data emitted from Observable and return it to Observer
        compositeDisposable.add(
                studentObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<Student, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Student student) throws Exception {
                                // either return single observables
                                //student.setName(student.getName().toUpperCase());
                                //return Observable.just(student);

                                // or return multiple observables
                                Student flatMapStudent1 = new Student(student);
                                Student flatMapStudent2 = new Student(student);

                                student.setName(student.getName().toUpperCase());
                                return Observable.just(student, flatMapStudent1, flatMapStudent2);
                            }
                        })
                        .subscribeWith(getStudentObserver())
        );

         */

        // ======== ConcatMap Operator =======
        // emit the emissions from two or more Observables without interleaving them --> cares the order of the element unlike FlatMap
        // con: concatMap waits for each observable to finish all the work until next one starts proceeding --> efficiency go down
        // determine to use FlatMap or ConcatMap by if the order is important or not

        /*
        studentObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                List<Student> studentList = initStudents();

                // emit all Student and later will be streamed into studentDisposableObserver after emitter.onComplete();
                for(Student student : studentList){
                    Log.d(TAG, "subscribe: " + student.getName());
                    emitter.onNext(student);
                }

                emitter.onComplete();
            }
        });

        compositeDisposable.add(
                studentObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .concatMap(new Function<Student, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Student student) throws Exception {
                                // either return single observables
                                //student.setName(student.getName().toUpperCase());
                                //return Observable.just(student);

                                // or return multiple observables
                                Student flatMapStudent1 = new Student(student);
                                Student flatMapStudent2 = new Student(student);

                                student.setName(student.getName().toUpperCase());
                                return Observable.just(student, flatMapStudent1, flatMapStudent2);
                            }
                        })
                        .subscribeWith(getStudentObserver())
        );
         */

        // ======== Buffer Operator =======
        // periodically gather items emitted by an Observable into bundles and mit these bundles rather than emitting the items one at a time

        /*
        Observable<Integer> bufferNumbers = Observable.range(1, 22);

        bufferNumbers.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // buffer(# of items bundled together)
                .buffer(4)
                // List<Observer> below because it is bundled together
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "onNext started: ");

                        for(Integer num: integers){
                            Log.d(TAG, "onNext: " + num);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

         */

        // ======== Filter Operator =======
        // emit only those items from an Observable that pass a predicate test

        /*
        Observable<Integer> bufferNumbers = Observable.range(1, 22);
        bufferNumbers.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer%3 == 0;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

         */

        // ======== Distinct Operator =======
        // suppress duplicate items emitted by an Observable

        /*
        Observable<Integer> duplicatedNumbers = Observable.just(1,2,3,7,3,5,5,3,4,4);
        duplicatedNumbers.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

         */

        // ======== Skip Operator =======
        // suppress the first n items emitted by an Observable

        /*
        Observable<Integer> duplicatedNumbers = Observable.just(1,2,3,7,3,5,5,3,4,4);
        duplicatedNumbers.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .skip(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

         */

        // ======== SkipLast Operator =======
        // suppress the final n items emitted by an Observable
        Observable<Integer> duplicatedNumbers = Observable.just(1,2,3,7,3,5,5,3,4,4);
        duplicatedNumbers.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .skipLast(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

    }


    /*
    private DisposableObserver getObserver(){

        greetingObserver = new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        return greetingObserver;
    }
     */

    private DisposableObserver getStudentObserver(){

        studentDisposableObserver = new DisposableObserver<Student>() {
            @Override
            public void onNext(Student student) {
                Log.d(TAG, "onNext: " + student.getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        return studentDisposableObserver;
    }

    private List<Student> initStudents(){

        List<Student> studentList = new ArrayList<>();

        Student student1 = new Student("Student1", "student1@gmail.com", 20);
        Student student2 = new Student("Student2", "student2@gmail.com", 21);
        Student student3 = new Student("Student3", "student3@gmail.com", 22);
        Student student4 = new Student("Student4", "student4@gmail.com", 23);
        Student student5 = new Student("Student5", "student5@gmail.com", 24);

        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);
        studentList.add(student4);
        studentList.add(student5);

        return studentList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
