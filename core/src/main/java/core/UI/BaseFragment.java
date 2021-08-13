package core.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.fooddeliverysystem.R;

import core.Listeners.NavigationRequestListener;
import core.Utils.AppLog;
import core.Utils.AppUtils;

import java.io.Serializable;


public abstract class BaseFragment extends Fragment implements Serializable {

    private static final String TAG = "BaseFragment";
    private NavigationRequestListener mNavigationRequestListener;
    public View view;
    public BaseActivity activity;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);

        activity = ((BaseActivity) getActivity());
        AppLog.v(TAG, (((Object) this).getClass().getSimpleName()) + " was attached");
        try {
            mNavigationRequestListener = (NavigationRequestListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " must implement " + NavigationRequestListener.class.getSimpleName());
        }
    }

    private ImageView back;
    public  ImageView msg;
    public TextView title;
    public Toolbar toolbar;

    public void setToolbar(/*String titleText,*/ View.OnClickListener backClickListener) {
        toolbar = view.findViewById(R.id.toolbar);
        back = view.findViewById(R.id.back);
        back.setImageResource(R.drawable.arrow_back);
        if (backClickListener != null)
            back.setOnClickListener(backClickListener);
        back.setVisibility(backClickListener != null ? View.VISIBLE : View.GONE);
    }

    public void chatButton(View.OnClickListener msgClickListener) {
        msg = view.findViewById(R.id.msg);
        if (msgClickListener != null)
            msg.setOnClickListener(msgClickListener);
        msg.setVisibility(msgClickListener != null ? View.VISIBLE : View.GONE);
    }

    public void setTitle(String titleText) {
        title = view.findViewById(R.id.title);
        title.setText(titleText);
        title.setVisibility(titleText != null ? View.VISIBLE : View.GONE);
    }

    public void goBack() {
        mNavigationRequestListener.onGoBack();
    }

    public void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        mNavigationRequestListener.onReplaceFragment(containerId, fragment, addToBackStack);
    }

    public void addFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        mNavigationRequestListener.onAddFragment(containerId, fragment, addToBackStack);
    }

    public void startActivity(Intent intent) {
        mNavigationRequestListener.onStartActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    boolean blockTouches = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        this.view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return blockTouches;
            }
        });

        setupComponents(view);
    }

    public void disableInteraction() {
//        ((BaseActivity) getActivity()).disableInteraction();
        blockTouches = true;
    }

    public void enableInteraction() {
//        ((BaseActivity)getActivity()).enableInteraction();
        blockTouches = false;
    }

    public void showLoader() {

        if (getActivity() instanceof BaseActivity)
            activity.showLoader();
    }

    public void hideLoader() {
        if (getActivity() instanceof BaseActivity)
            activity.hideLoader();
    }

    /*
     *       Social Media
     *
     * */

    private String message = "Please extend activity by SocialMediaActivity";

    //    public void registerFaceBook(LoginButton loginButton, ISocialMediaListener iSocialMediaListener) throws Exception {
//        if (!(getActivity() instanceof SocialMediaActivity))
//            throw new Exception(message);
//        ((SocialMediaActivity) getActivity()).registerFacebook(loginButton, iSocialMediaListener);
//
//    }
//
//    public void registerGoogle(ISocialMediaListener iSocialMediaListener) throws Exception {
//        if (!(getActivity() instanceof SocialMediaActivity))
//            throw new Exception(message);
//        ((SocialMediaActivity) getActivity()).registerGoogle(iSocialMediaListener);
//
//    }
    public void toast(String message) {
        AppUtils.Toast(message);
    }

    public void setupComponents(View rootView) {

        initializeComponents(rootView);
        setupListeners(rootView);
    }

    public abstract void initializeComponents(View rootView);

    public abstract void setupListeners(View rootView);

}