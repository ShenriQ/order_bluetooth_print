package core.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.fooddeliverysystem.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Locale;

import core.Core;
import core.Utils.AppLog;
import core.Utils.AppUtils;
import core.Utils.ContextWrapper;
import core.Utils.CoreConstants;
import core.Utils.FileChooser;
import core.Utils.FragmentUtils;

import core.Listeners.NavigationRequestListener;


public abstract class BaseActivity extends AppCompatActivity implements NavigationRequestListener {

    private static final String TAG = "BaseActivity";
    public FileChooser fileChooser;
    Fragment currentFragment;
    public String tag;
    protected BaseActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //Drawer
        mContext = BaseActivity.this;

    }

    KProgressHUD loader;
    ProgressBar progressBar;
    boolean isLoaderVisible;

    public void showLoader() {
        if (loader == null) {
            loader = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(getString(R.string.please_wait))
                    /*.setDetailsLabel("Downloading data")*/
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
        }
        loader.show();
        isLoaderVisible = true;
        disableInteraction();
    }

    public void hideLoader() {
        loader.dismiss();
        isLoaderVisible = false;
        enableInteraction();
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting() == true;
    }

    public ImageView back, msg;
    public TextView title;
    public Toolbar toolbar;

    public void setToolbar(/*String titleText,*/ View.OnClickListener backClickListener) {
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        back.setImageResource(R.drawable.arrow_back);
        if (backClickListener != null)
            back.setOnClickListener(backClickListener);
        back.setVisibility(backClickListener != null ? View.VISIBLE : View.GONE);

//        title = findViewById(R.id.title);
//        title.setText(titleText);
//        title.setVisibility(titleText != null ? View.VISIBLE : View.GONE);
    }

    public void chatButton(View.OnClickListener msgClickListener) {
        msg = findViewById(R.id.msg);
        if (msgClickListener != null)
            msg.setOnClickListener(msgClickListener);

        msg.setVisibility(msgClickListener != null ? View.VISIBLE : View.GONE);
    }

    public void setTitle(String titleText) {
        title = findViewById(R.id.title);
        title.setText(titleText);
        title.setVisibility(titleText != null ? View.VISIBLE : View.GONE);
    }

//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//    }

    public void disableInteraction() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableInteraction() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    //    This method will hide keyboard when you click outside the EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null && getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onReplaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        currentFragment = fragment;
        FragmentUtils.commitFragment(getSupportFragmentManager(), containerId, fragment, addToBackStack);
    }

    @Override
    public void onAddFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentUtils.addFragment(getSupportFragmentManager(), containerId, fragment, addToBackStack);
    }

    @Override
    public void onRemoveFragment(Fragment fragment) {
        FragmentUtils.removeFragment(getSupportFragmentManager(), fragment);
    }

    @Override
    public void onStartActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onStartActivityWithClearStack(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void onStartActivityWithRemoveRange(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fileChooser != null)
            fileChooser.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onGoBack() {
        onBackPressed();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || !isTaskRoot()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            clearDataObjects();
            return;
        } else {

            this.doubleBackToExitPressedOnce = true;
            String exitText = getString(R.string.press_to_exit);
            toast(exitText);
//            flashbar(exitText);
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void finish() {
        super.finish();
        //override transition to skip the standard window transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void toast(String message) {
        AppUtils.Toast(message);
    }

    @Override
    public void onResume() {
        super.onResume();
//        isVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
//        isVisible = false;
    }

    @Override
    public void onDestroy() {
        if (fileChooser != null)
            fileChooser.release();
        super.onDestroy();
    }

    public void print(String... strings) {
        for (String string : strings) {
            AppLog.e("New EventActivity: ", string);
        }
    }

//    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
//
//    public void requestStoragePermissions(final String[] permissions) {
//        rootLayout = ((ContentFrameLayout) findViewById(android.R.id.content));
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            // Provide an additional rationale to the user if the permission was not granted
//            // and the user would benefit from additional context for the use of the permission.
//            // For example if the user has previously denied the permission.
////            Snackbar.make(rootLayout, getString(R.string.storage_permission),
////                    Snackbar.LENGTH_LONG)
////                    .setAction("Okay", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//////                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
////                                requestPermissions(permissions,
////                                        PERMISSION_WRITE_EXTERNAL_STORAGE);
////                            }
////                        }
////                    })
////                    .show();
//            BaseActivityExtensionKt.showDialog(this, "Permission!", getString(R.string.storage_permission), "OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
//                        requestPermissions(permissions,
//                                PERMISSION_WRITE_EXTERNAL_STORAGE);
//                    }
//                }
//            }, null, null);
//        } else {
//            // Permission has not been granted yet. Request it directly.
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(permissions,
//                        PERMISSION_WRITE_EXTERNAL_STORAGE);
//            }
//        }
//    }
//
//    public boolean hasPermission(final String[] permissions) {
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (fileChooser != null)
            fileChooser.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        else {
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED)
//                    requestStoragePermissions(permissions);
//            }
//        }
    }

    public void showDialog(String title, String message, String yesText, DialogInterface.OnClickListener yesListener,
                           String noText, DialogInterface.OnClickListener noListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.myAlertDialog).setTitle(title).setMessage(message);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setPadding(30, 40, 40, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);
        alertDialog.setCustomTitle(textView);

        alertDialog.setPositiveButton(yesText, yesListener);
        if (noText != null)
            alertDialog.setNegativeButton(noText, noListener);
        alertDialog.show();
    }

    public void showDialogNotCancelable(String title, String message, String yesText, DialogInterface.OnClickListener yesListener,
                           String noText, DialogInterface.OnClickListener noListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.myAlertDialog).setTitle(title).setMessage(message);
        alertDialog.setCancelable(false);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setPadding(30, 40, 40, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);
        alertDialog.setCustomTitle(textView);

        alertDialog.setPositiveButton(yesText, yesListener);
        if (noText != null)
            alertDialog.setNegativeButton(noText, noListener);
        alertDialog.show();
    }


    /**
     * To change Language Use ContextWrapper.wrap function
     * get Selected language from Prefs.
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = Core.getLang();
        if (language == null)
            language = CoreConstants.Chinese;
        Locale newLocale = new Locale(language);
        Context context = ContextWrapper.wrap(newBase, newLocale);

        Configuration config = context.getResources().getConfiguration();
        config.locale = newLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        super.attachBaseContext(context);
    }

    public void setupComponents(BaseActivity activity) {
        tag = activity.getClass().getSimpleName();
        initializeComponents();
        setupListeners();
    }

    public abstract void initializeComponents();

    public abstract void setupListeners();
}
