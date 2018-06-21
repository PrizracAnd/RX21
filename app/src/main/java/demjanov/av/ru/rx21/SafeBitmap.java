package demjanov.av.ru.rx21;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SafeBitmap {
    public final static String SAFEFILE = "SAFEFILE";
    public final static String FORMAT_PNG = "PNG";
    public final static String FORMAT_JPEG = "JPEG";


    private Context context;
    private Model model;
    private String format;
    private final static int BITMAP_QUALITY = 100;
    private boolean isOK;
    private boolean isComplete = false;
    private Disposable disposable;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////

    public SafeBitmap(Context context, Model model, String format) {
        this.context = context;
        this.model = model;
        this.format = format;
    }

    /////////////////////////////////////////////////////
    // method runSafe
    ////////////////////////////////////////////////////
    public void runSafe(){
        Bitmap bitmap = model.getBitmap();

        if(model.getSafePath() == null || bitmap == null || bitmap.isRecycled()){
            this.isOK = false;
            this.isComplete = true;
        } else {
            this.isComplete = false;

            Single<Boolean> single = Single.create((SingleEmitter<Boolean> emitter) -> {
                FileOutputStream fos = null;

                File file = new File(this.context.getExternalFilesDir(null), model.getSafePath());
                if (isExternalStorageWritable()) {
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    fos = new FileOutputStream(file);
                    switch (this.format) {
                        case FORMAT_PNG:
                            bitmap.compress(Bitmap.CompressFormat.PNG, this.BITMAP_QUALITY, fos);
                            break;
                        case FORMAT_JPEG:
                            bitmap.compress(Bitmap.CompressFormat.JPEG, this.BITMAP_QUALITY, fos);
                            break;
                        default:
                            fos.flush();
                            fos.close();
                            throw new IOException("Incorrect format!");
                    }

                    fos.flush();
                    fos.close();
                    emitter.onSuccess(true);
                } else throw new IOException("External storage not writable");

            }).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation());

            this.disposable = single.subscribeWith(new DisposableSingleObserver<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    isOK = aBoolean;
                    isComplete = true;
                }

                @Override
                public void onError(Throwable e) {
                    isOK = false;
                    isComplete = true;
                    Log.d(SAFEFILE, e.getMessage());
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
        return this.isOK;
    }

    public boolean isComplete() {
        return this.isComplete;
    }
}
