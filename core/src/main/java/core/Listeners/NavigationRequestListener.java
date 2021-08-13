package core.Listeners;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public interface NavigationRequestListener {

    void onReplaceFragment(int containerId, Fragment fragment, boolean addToBackStack);

    void onAddFragment(int containerId, Fragment fragment, boolean addToBackStack);

    void onRemoveFragment(Fragment fragment);

    void onStartActivity(Intent intent);

    void onGoBack();

    //void showDialogFragment(BaseDialogFragment dialogFragment);
}
