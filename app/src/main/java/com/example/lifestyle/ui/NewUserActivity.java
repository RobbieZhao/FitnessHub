package com.example.lifestyle.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.lifestyle.ProfileViewModel;
import com.example.lifestyle.R;

public class NewUserActivity extends AppCompatActivity {

    private ProfileViewModel mProfileViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        ProfileFragment fragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString("MODE", "NEW_USER");
        fragment.setArguments(bundle);

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.whole_screen, fragment, "profile_fragment");
        fTrans.commit();

        getSupportActionBar().setTitle("Welcome to LifeStyle!");
//        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

    }

    public ProfileViewModel getmProfileViewModel(){
        return mProfileViewModel;
    }

}