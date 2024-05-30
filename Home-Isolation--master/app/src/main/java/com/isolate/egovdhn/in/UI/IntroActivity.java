package com.isolate.egovdhn.in.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.R;

import org.jetbrains.annotations.Nullable;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Prefs.getFirstUse(this)){
            startActivity(new Intent(IntroActivity.this, SplashActivity.class));
            finish();
        }

        addSlide(AppIntroFragment.newInstance(
                "Home Isolation Monitoring Movement And Tracking", "District Administration Dhanbad", R.drawable.cir_logo, Color.rgb(0, 120, 0), Color.WHITE, Color.WHITE));
        addSlide(AppIntroFragment.newInstance(
                "Get Started", "Stay home and avail all the medical facilities", R.drawable.stay_home, Color.BLUE));
        addSlide(AppIntroFragment.newInstance(
                "Online Consultation", "Get online appointments through google meet", R.drawable.consulting, Color.rgb(150, 0, 0), Color.rgb(255, 255, 255), Color.rgb(255, 255, 255)));
        addSlide(AppIntroFragment.newInstance(
                "Update your Status", "Update your health status on a regular basis", R.drawable.insurance, Color.rgb(150, 0, 120)));
        addSlide(AppIntroFragment.newInstance(
                "Medical Documents", "Avail all the medical documents like prescriptions in online mode by downloading pdfs", R.drawable.medical_record, Color.rgb(120, 120, 0)));
    }
    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent signInIntent;
        Prefs.setFirstUse(this, false);
        signInIntent = new Intent(getApplication(), SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent signInIntent;
        Prefs.setFirstUse(this, false);
        signInIntent = new Intent(getApplication(), SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }
}