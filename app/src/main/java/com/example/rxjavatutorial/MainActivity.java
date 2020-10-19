package com.example.rxjavatutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private String greeting = "Hello RxJava";
    private Observable<String> greetingObservable;


    // private Disposable greetingObserverDisposable;
    // private Observer<String> greetingObserverTest;

    private DisposableObserver<String> greetingObserver;
    private DisposableObserver<String> greetingObserver2;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);

        greetingObservable = Observable.just(greeting);

        // scheduler.io()
        // for non CPU intensive tasks: database interaction/network communications etc

        // AndroidSchedulers.mainThread()
        // for UI thread

        // from this point onwards, data stream will execute on the io thread and observer will receive data from the io thread
        // greetingObservable.subscribeOn(Schedulers.io());

        // from here, the data stream will move to main thread again => can change UI related work
        // greetingObservable.observeOn(AndroidSchedulers.mainThread());

        // observer
        // in onDestroy, dispose() has to be called to prevent observer listening observable after the destruction of Activity/Fragment --> memory leak
        // To do so, need to keep the reference of Disposable instance in onSubscribe method to later call its dispose method to cancel observing.
        /*
        greetingObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: ");
                tv.setText(s);
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
         */

        // disposableObserver
        // no OnSubscribe method as we can directly call dispose method on disposableObserver == DisposableObserver can dispose itself
        greetingObserver = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: ");
                tv.setText(s);
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

        greetingObserver2 = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: ");
                Toast.makeText(getApplicationContext(), s + " " + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
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


        /*
        compositeDisposable.add(greetingObserver);
        compositeDisposable.add(greetingObserver2);
        greetingObservable.subscribe(greetingObserver);
        greetingObservable.subscribe(greetingObserver2);
         */

        // subscribeWith return observer and in this case the observer is DisposableObserver, so we can add returned observer to compositeDisposable
        compositeDisposable.add(greetingObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(greetingObserver));

        compositeDisposable.add(greetingObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(greetingObserver2));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // observer
        // disposable.dispose();

        // disposableObserver
        // cons: if we have many observers, we may forget to call its dispose ==> compositeDisposable
        // greetingObserver.dispose();

        compositeDisposable.clear();
    }
}
