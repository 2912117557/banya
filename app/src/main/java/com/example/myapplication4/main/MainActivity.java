package com.example.myapplication4.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.manager.Lifecycle;
import com.example.myapplication4.R;
import com.example.myapplication4.myLib.MySingleton;
import com.example.myapplication4.shared.LoginInterceptor;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final String logTag = "MainActivity";

    private NavController navController;
    private DrawerLayout drawerLayout;
    private ActivityResultLauncher<Intent> loginRegisterLanucher;
    private MySingleton.UserInfo userInfo;
    private TextView nameTextView;
    private boolean oldIsLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.actMain_Toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.actMain_FragmentContainerView);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        drawerLayout = findViewById(R.id.actMain_DrawerLayout);
        NavigationView navView = findViewById(R.id.actMain_NavigationView);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(drawerLayout)
                        .build();
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        View headerView = navView.getHeaderView(0);
        nameTextView = headerView.findViewById(R.id.actMain_drawerlayout_header_TextView1);
//        ImageView portraitImageView = headerView.findViewById(R.id.actMain_drawerlayout_header_ImageView2);

        MySingleton mySingleton = MySingleton.getInstance(getApplicationContext());
        userInfo = mySingleton.getUserInfo_firstFromSharedPreferences();//???onResume????????????????????????????????????????????????????????????
        //???oldIsLogin?????????onResume??????????????????????????????????????????????????????resume???????????????
        //activity?????????????????????????????????????????????resume????????????
        oldIsLogin = !userInfo.isLogin;

        loginRegisterLanucher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {}
                });

        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginRegisterAcitvity.class);
                loginRegisterLanucher.launch(intent);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.userInfoFragment) {
                    UserInfo_LoginInterceptSuccess userInfo_loginInterceptSuccess = new UserInfo_LoginInterceptSuccess();
                    LoginInterceptor.intercept(MainActivity.this, userInfo_loginInterceptSuccess);
                } else if (id == R.id.settingFragment) {
                    navController.navigate(R.id.settingFragment);
                } else if (id == R.id.aboutFragment) {
                    navController.navigate(R.id.aboutFragment);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //???drawerLayout?????????????????????????????????
        boolean isLogin = userInfo.isLogin;
        if (isLogin != oldIsLogin) {
            if (isLogin) {
                String username = userInfo.username;
                nameTextView.setText(username);
                nameTextView.setClickable(false);
            } else {
                nameTextView.setText("  ??????/??????");
                nameTextView.setClickable(true);
            }
            oldIsLogin = isLogin;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            boolean res = navController.popBackStack();
            if (!res) {
                finish();
            }
        }
    }


    private class UserInfo_LoginInterceptSuccess implements LoginInterceptor.LoginInterceptSuccess {
        @Override
        public void originalOperation() {
            navController.navigate(R.id.userInfoFragment);
        }
    }

}