package core.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.fooddeliverysystem.R;

public class FragmentUtils {

    private static final String TAG = "FragmentUtils";

    public static void commitFragment(FragmentManager fragmentManager, int containerId,
                                      Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        final String tag = fragment.getClass().getSimpleName();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        showStackLog(fragmentManager);
    }

    public static void addFragment(FragmentManager fragmentManager, int containerId,
                                   Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        final String tag = fragment.getClass().getSimpleName();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.add(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        showStackLog(fragmentManager);
    }

    public static void removeFragment(FragmentManager fragmentManager,
                                      Fragment fragment) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.remove(fragment);
        transaction.commit();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragment = null;
        showStackLog(fragmentManager);
    }

    public static Fragment getFragmentByTag(FragmentManager fragmentManager, Class<?> fragment) {
        return fragmentManager.findFragmentByTag(fragment.getSimpleName());
    }

    public static void showStackLog(FragmentManager fragmentManager){
        String logMsg = "BackStack was changed. Count " + fragmentManager.getBackStackEntryCount() + "\n";
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++){
            logMsg += fragmentManager.getBackStackEntryAt(i).getName() + "\n";
        }
        logMsg +="---------------------------------------------------------- \n\n";
        AppLog.v(TAG,logMsg);

    }
}
