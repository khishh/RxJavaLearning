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
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

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

    // Subject
    // a Subject can subscribe to multiple Observables and emit the data to multiple subscribers.
    // asyncSubject/behaviorSubject/PublishSubject/ReplaySubject

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ======= AsyncSubject =======
        // AsyncSubject only emit the last item of observable
        // asyncSubjectDemo1();

        // asyncSubjectDemo2();

        // ======= BehaviorSubject =======
        // When an observer subscribes to BehaviorSubject, it emits the most recently emitted item and all the subsequent items
        // behaviorSubjectDemo1();

        // behaviorSubjectDemo2();

        // ======= PublishSubject =======
        // PublicSubject emits all the subsequent items of the Observable at the time of subscription

        // publicSubjectDemo1();
        // publishSubjectDemo2();

        // ======= ReplaySubject =======
        // replay subject emits all the items of the Observable without considering when the subscriber subscribed
        // --> ReplaySubject emits all the items of the observables without considering when the subscriber subscribed.

        // replaySubjectDemo1();
        replaySubjectDemo2();

    }

    // should only emit the last item of observable which is JSON
    void asyncSubjectDemo1(){
        Observable<String> languages = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        languages.subscribe(asyncSubject);

        asyncSubject.subscribe(getFirstObserver());
        asyncSubject.subscribe(getSecondObserver());
        asyncSubject.subscribe(getThirdObserver());

    }

    // should only emit the last item of observable which is JSON. The last item till AsyncSubject.onComplete() is called
    void asyncSubjectDemo2(){

        AsyncSubject<String> asyncSubject = AsyncSubject.create();

        asyncSubject.subscribe(getFirstObserver());

        // because asyncSubject can act as an Observer. too
        asyncSubject.onNext("JAVA");
        asyncSubject.onNext("KOTLIN");
        asyncSubject.onNext("XML");

        asyncSubject.subscribe(getSecondObserver());

        asyncSubject.onNext("JSON");
        //asyncSubject.onComplete();

        asyncSubject.subscribe(getThirdObserver());
        asyncSubject.onComplete();

    }

    void behaviorSubjectDemo1(){
        Observable<String> languages = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();
        languages.subscribe(behaviorSubject);

        // subscribe to Observable at the beginning of the emission.
        behaviorSubject.subscribe(getFirstObserver());
        behaviorSubject.subscribe(getSecondObserver());
        behaviorSubject.subscribe(getThirdObserver());

    }

    void behaviorSubjectDemo2(){

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

        behaviorSubject.onNext("even before");
        behaviorSubject.onNext("before");

        behaviorSubject.subscribe(getFirstObserver());

        // because asyncSubject can act as an Observer. too
        behaviorSubject.onNext("JAVA");
        behaviorSubject.onNext("KOTLIN");
        behaviorSubject.onNext("XML");

        behaviorSubject.subscribe(getSecondObserver());

        behaviorSubject.onNext("JSON");
        behaviorSubject.onComplete();

        // thirdObserver will not receive anything because behaviorSubject already called onComplete()
        behaviorSubject.subscribe(getThirdObserver());

        // if onComplete called here, thirdObserver will also receive JSON
        // behaviorSubject.onComplete();

    }

    void publishSubjectDemo1(){
        Observable<String> languages = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        PublishSubject<String> publishSubject = PublishSubject.create();
        languages.subscribe(publishSubject);

        // each Observer will receive all items in Observable because PublishSubject will emit subsequent items at the time of subscription.
        // no item has emitted before subscription
        publishSubject.subscribe(getFirstObserver());
        publishSubject.subscribe(getSecondObserver());
        publishSubject.subscribe(getThirdObserver());

    }

    void publishSubjectDemo2(){

        PublishSubject<String> publishSubject = PublishSubject.create();

        publishSubject.onNext("even before");
        publishSubject.onNext("before");

        publishSubject.subscribe(getFirstObserver());

        // because asyncSubject can act as an Observer. too
        publishSubject.onNext("JAVA");
        publishSubject.onNext("KOTLIN");
        publishSubject.onNext("XML");

        publishSubject.subscribe(getSecondObserver());

        publishSubject.onNext("JSON");
        publishSubject.onComplete();

        // thirdObserver will not receive anything because behaviorSubject already called onComplete()
        publishSubject.subscribe(getThirdObserver());

        // if onComplete called here, thirdObserver will also receive JSON
        // behaviorSubject.onComplete();

    }

    void replaySubjectDemo1(){
        Observable<String> languages = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        ReplaySubject<String> replaySubject = ReplaySubject.create();
        languages.subscribe(replaySubject);

        replaySubject.subscribe(getFirstObserver());
        replaySubject.subscribe(getSecondObserver());
        replaySubject.subscribe(getThirdObserver());

    }

    void replaySubjectDemo2(){

        ReplaySubject<String> replaySubject = ReplaySubject.create();

        replaySubject.subscribe(getFirstObserver());

        // because asyncSubject can act as an Observer. too
        replaySubject.onNext("JAVA");
        replaySubject.onNext("KOTLIN");
        replaySubject.onNext("XML");

        replaySubject.subscribe(getSecondObserver());

        replaySubject.onNext("JSON");
        //asyncSubject.onComplete();

        replaySubject.subscribe(getThirdObserver());
        replaySubject.onComplete();

    }


    private Observer<String> getFirstObserver() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {


                Log.i(TAG, " First Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {

                Log.i(TAG, " First Observer Received " + s);

            }

            @Override
            public void onError(Throwable e) {

                Log.i(TAG, " First Observer onError ");
            }

            @Override
            public void onComplete() {

                Log.i(TAG, " First Observer onComplete ");

            }
        };

        return observer;
    }

    private Observer<String> getSecondObserver() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {


                Log.i(TAG, " Second Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {

                Log.i(TAG, " Second Observer Received " + s);

            }

            @Override
            public void onError(Throwable e) {

                Log.i(TAG, " Second Observer onError ");
            }

            @Override
            public void onComplete() {

                Log.i(TAG, " Second Observer onComplete ");

            }
        };

        return observer;
    }

    private Observer<String> getThirdObserver() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {


                Log.i(TAG, " Third Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {

                Log.i(TAG, " Third Observer Received " + s);

            }

            @Override
            public void onError(Throwable e) {

                Log.i(TAG, " Third Observer onError ");
            }

            @Override
            public void onComplete() {

                Log.i(TAG, " Third Observer onComplete ");

            }
        };

        return observer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
