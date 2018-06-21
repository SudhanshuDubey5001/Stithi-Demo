package stithi.my.com.stithi.ProfilePicSetter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SliderManager extends FragmentStatePagerAdapter{

    public SliderManager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentCreation.createInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

//  will be called when notifydatasetchanged is called------>
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
