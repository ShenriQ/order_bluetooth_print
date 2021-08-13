package core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import core.Utils.CoreConstants;
// bluetooth printer
import com.mazenrashed.printooth.Printooth;

public class Core extends Application {
    public static final String TAG = Core.class.getSimpleName();
    private static Context context;
    private static Core mInstance;
    private static String lang;
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
//        MultiDex.install(this);
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
        mInstance = this;
        context = this.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lang = CoreConstants.Chinese;
//        lang = CoreConstants.ENGLISH;

        setLanguage(lang);
//        FacebookSdk.sdkInitialize(context);

        // bluetooth printer
        Printooth.INSTANCE.init(this);
    }

//    public NetworkComponent getAppModule() {
//        return appComponent;
//    }

    public static synchronized Core getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return context;
    }

    public static String getLang() {
        return lang;
    }

    private void setLanguage(String language) {
        Locale locale = new Locale(language);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
