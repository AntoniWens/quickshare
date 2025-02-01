package com.example.quickhire.ui.add_vacancy

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AppliedFragment()
            1 -> BookmarkFragment()
            else -> AppliedFragment()
        }
    }
}