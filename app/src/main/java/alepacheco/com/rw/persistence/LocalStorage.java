package alepacheco.com.rw.persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by joao on 28/06/2017.
 */

public class LocalStorage {

    //Tags
    public static String TAG_NAME_PREFERENCES = "lolipop";
    public static String NULL_VALUE = "i3sds3";

    public static String TAG_ID_USER = "Ae3223d";
    public static String TAG_SENDED_TO_SERVER = "AD33d33sd";
    public static String TAG_KEY = "9eJedl";
    public static String TAG_TEMP_KEY = "Yuotpw23";
    public static String TAG_ENCRYPTED = "3UexaU";

    //Variaveis de controle
    private Context context;
    private static SharedPreferences settings;
    private SharedPreferences.Editor editor;

    //Instancia
    private static LocalStorage instance;

    private LocalStorage(Context context){
        this.context = context;
        loadSharedPreferences();
    }

    //inicializa as preferencias internas
    private void loadSharedPreferences(){
        settings = context.getSharedPreferences(TAG_NAME_PREFERENCES, context.MODE_PRIVATE);
        editor = settings.edit();
    }


    public static LocalStorage getInstance(Context context){
        if(instance == null){
            return new LocalStorage(context);
        }
        return instance;
    }

    //busca por tag
    public String getByTag(String tag){
        return settings.getString(tag,NULL_VALUE);
    }

    //Insere os dados pela tag
    public void setByTag(String tag, String data){
        editor.putString(tag, data);
        editor.apply();
    }


    public Boolean getBooleanByTag(String tag){
        return settings.getBoolean(tag,false);
    }

    //Insere os dados pela tag
    public void setByTag(String tag, Boolean data){
        editor.putBoolean(tag, data);
        editor.apply();
    }


    public Boolean CheckSendedToServer(){
        return settings.getBoolean(TAG_SENDED_TO_SERVER,false);
    }

    public void setSendendToServer(){
        editor.putBoolean(TAG_SENDED_TO_SERVER,true);
        editor.apply();
    }

}