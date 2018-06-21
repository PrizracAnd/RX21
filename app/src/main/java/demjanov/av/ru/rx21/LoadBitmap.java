package demjanov.av.ru.rx21;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoadBitmap {
    public final static String LOADFILE = "LOADFILE";

    private Context context;
    private Model model;
    private boolean isOK;
    private boolean isComplete = false;
    private Disposable disposable;

    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public LoadBitmap(Context context, Model model) {
        this.context = context;
        this.model = model;
    }

    /////////////////////////////////////////////////////
    // method runLoad
    ////////////////////////////////////////////////////
    public void runLoad(){


        if(model.getLoadPath() == null){
            this.isOK = false;
            this.isComplete = true;
        } else {
            this.isComplete = false;

            Single<Bitmap> single = Single.create((SingleEmitter<Bitmap> emitter) -> {
                Bitmap bitmap;
                TimeUnit.SECONDS.sleep(10);
                FileInputStream fis = null;

                File file = new File(context.getExternalFilesDir(null), model.getLoadPath());
                if (isExternalStorageWritable()) {
                    if (file.exists()) {
                        fis = new FileInputStream(file);
                        bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();
                        emitter.onSuccess(bitmap);
                    } else throw new IOException("File is not exists");
                } else throw new IOException("External storage not writable");

            }).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation());


            this.disposable = single.subscribeWith(new DisposableSingleObserver<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    model.setBitmap(bitmap);
                    isOK = true;
                    isComplete = true;
                }

                @Override
                public void onError(Throwable e) {
                    isOK = false;
                    isComplete = true;
                    Log.d(LOADFILE, e.getMessage());
                }
            });
        }

    }



    /////////////////////////////////////////////////////
    //method isExternalStorageWritable
    ////////////////////////////////////////////////////
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(state.equals (Environment.MEDIA_MOUNTED) || state.equals (Environment.MEDIA_MOUNTED_READ_ONLY)){
            return true;
        }else return false;
    }

    /////////////////////////////////////////////////////
    //method dispose
    ////////////////////////////////////////////////////
    public void dispose(){
        this.disposable.dispose();
    }

    public boolean isOK() {
        return isOK;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
