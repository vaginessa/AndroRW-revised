package alepacheco.com.rw.apl;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import alepacheco.com.rw.R;

/**
 * Created by joao on 05/07/2017.
 */

public class Helper {

    public static void makeToast(final Context ctx, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx,
                        text,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void changeWallPaper(Context ctx, int drawableResource){
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(ctx);
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), drawableResource);
            myWallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
