package demjanov.av.ru.rx21;

import android.graphics.Bitmap;

import io.reactivex.annotations.Nullable;

public class Model {

    //-----Variables begin-------------------------------
    private Bitmap bitmap;
    private String loadPath;
    private String safePath;
    //-----Variables end---------------------------------


    /////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////
    //-----Constructors begin----------------------------

    public Model(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Model(@Nullable String loadPath, @Nullable String safePath) {
        this.loadPath = loadPath;
        this.safePath = safePath;
    }
    //-----Constructors end------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ///////////////////////////////////////////////////
    //-----Begin----------------------------------------

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLoadPath() {
        return loadPath;
    }

    public void setLoadPath(String loadPath) {
        this.loadPath = loadPath;
    }

    public String getSafePath() {
        return safePath;
    }

    public void setSafePath(String safePath) {
        this.safePath = safePath;
    }

    //-----End------------------------------------------


    ////////////////////////////////////////////////////////////////////////////////
    //method recycleBitmap
    ////////////////////////////////////////////////////////////////////////////////
    public void recycleBitmap(){
        if(this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
    }
}
