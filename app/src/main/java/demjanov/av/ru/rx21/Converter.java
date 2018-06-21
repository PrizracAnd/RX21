package demjanov.av.ru.rx21;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Converter {
    private Model model;
    private MainActivity view;
    private Context context;

    private volatile boolean isConvert;
    private Disposable disposable;
    private LoadBitmap loadBitmap;
    private SafeBitmap safeBitmap;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public Converter(MainActivity view) {
        this.view = view;
        this.context = view.getApplicationContext();
    }

    /////////////////////////////////////////////////////
    // Method convert
    ////////////////////////////////////////////////////
    public void convert(String filename){
        this.model = new Model(filename, filename + "." + SafeBitmap.FORMAT_PNG);
        this.isConvert = true;
        load();



    }


    /////////////////////////////////////////////////////
    // Method load
    ////////////////////////////////////////////////////
    private void load(){
        this.loadBitmap = new LoadBitmap(context, model);
        Single<Boolean> single = Single.create((SingleEmitter<Boolean> emitter) -> {
            TimeUnit.SECONDS.sleep(10);
            this.loadBitmap.runLoad();
            while (!this.loadBitmap.isComplete()){
                if(!isConvert){
                    this.loadBitmap.dispose();
                    throw new Exception("Stopped!");
                }
            }
            if(this.loadBitmap.isOK()){
                emitter.onSuccess(true);
            }else throw new Exception("Not Load!");
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());


        this.disposable = single.subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean){
                    safe();
                } else endConvert(false);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LoadBitmap.LOADFILE, e.getMessage());
                endConvert(false);
            }
        });
    }


    /////////////////////////////////////////////////////
    // Method safe
    ////////////////////////////////////////////////////
    private void safe() {
        this.safeBitmap = new SafeBitmap(context, model, SafeBitmap.FORMAT_PNG);
        Single<Boolean> single = Single.create((SingleEmitter<Boolean> emitter) -> {
            this.safeBitmap.runSafe();
            while (!this.safeBitmap.isComplete()){
                if(!isConvert){
                    this.safeBitmap.dispose();
                    throw new Exception("Stopped!");
                }
            }
            if(this.safeBitmap.isOK()){
                emitter.onSuccess(true);
            }else throw new Exception("Not Safe!");
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = single.subscribeWith(new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                endConvert(aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(SafeBitmap.SAFEFILE, e.getMessage());
                endConvert(false);
            }
        });
    }


    /////////////////////////////////////////////////////
    // Method endConvert
    ////////////////////////////////////////////////////
    private void endConvert(boolean isOK) {
        this.model.recycleBitmap();
        view.setIsComplete(isOK);
    }


    /////////////////////////////////////////////////////
    // Method stopConvert
    ////////////////////////////////////////////////////
    public void stopConvert(){
        this.isConvert = false;
    }

}
