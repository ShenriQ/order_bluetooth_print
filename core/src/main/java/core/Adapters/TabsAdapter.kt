package core.Adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import core.UI.BaseFragment


class TabsAdapter(fm: FragmentManager, var fragments:ArrayList<BaseFragment>, var titles:ArrayList<String>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): BaseFragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles.get(position)
    }
}