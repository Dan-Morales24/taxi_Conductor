package com.example.taxi_conductor.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taxi_conductor.Model.TripPlanModel;
import com.example.taxi_conductor.R;
import com.example.taxi_conductor.login.LoginConductor;
import com.example.taxi_conductor.login.SplashScreenCoductor;
import com.example.taxi_conductor.reference.Common;
import com.example.taxi_conductor.ui.gallery.GalleryFragment;
import com.example.taxi_conductor.ui.home.HomeFragment;
import com.example.taxi_conductor.ui.slideshow.SlideshowFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_conductor.databinding.ActivityNavigationConductorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;

public class NavigationConductorActivity extends AppCompatActivity {



    private AppBarConfiguration mAppBarConfiguration;
   // private ActivityNavigationConductorBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private ImageView img_avatar,img_expand;
    private Uri imageUrl;
    private AlertDialog waitingDialog;
    private StorageReference storageReference;

    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_conductor);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         navigationView = (NavigationView) findViewById(R.id.nav_view);
         img_expand = (ImageView) findViewById(R.id.menu_view);
         img_expand.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 drawerLayout.openDrawer(GravityCompat.START);
             }
         });




        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);
        setFragment(0);
      //  binding = ActivityNavigationConductorBinding.inflate(getLayoutInflater());
      //  setContentView(binding.getRoot());
       // setSupportActionBar(binding.appBarNavigationConductor.toolbar);
       //  drawer = binding.drawerLayout;
       //  navigationView = binding.navView;
        //mAppBarConfiguration = new AppBarConfiguration.Builder(
          //      R.id.nav_home)
            //    .setOpenableLayout(drawer)
           //     .build();
      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_conductor);
      //  NavigationUI.setupWithNavController(navigationView, navController);
        init();




    }


    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                menuItem.setChecked(true);
                                setFragment(0);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_gallery:
                                menuItem.setChecked(true);
                                setFragment(1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_slideshow:
                                menuItem.setChecked(true);
                                setFragment(2);
                                Toast.makeText(NavigationConductorActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;

                            case R.id.nav_sign_out:
                                menuItem.setChecked(true);
                                setFragment(3);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;

                        }
                        return true;
                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_conductor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                HomeFragment homeFragment = new HomeFragment();
               fragmentTransaction.replace(R.id.fragment, homeFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                GalleryFragment galleryFragment = new GalleryFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment, galleryFragment);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SlideshowFragment starredFragment = new SlideshowFragment();
                fragmentTransaction.addToBackStack(null);
               fragmentTransaction.replace(R.id.fragment, starredFragment);
                fragmentTransaction.commit();
                break;

            case 3:

                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationConductorActivity.this);
                builder.setTitle("Alerta")
                        .setMessage("Estas seguro que quieres cerrar sesion")
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("Cerrar Sesión", (DialogInterface, i) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(NavigationConductorActivity.this, LoginConductor.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        }).setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(NavigationConductorActivity.this,android.R.color.holo_red_dark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(NavigationConductorActivity.this,R.color.colorAccent));


                });

                dialog.show();



                break;

        }
    }



    private void init(){



        storageReference = FirebaseStorage.getInstance().getReference();
        waitingDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Espere...")
                .create();
        View headerView = navigationView.getHeaderView(0);
        TextView txt_name = (TextView) headerView.findViewById(R.id.txt_name);
        TextView txt_phone = (TextView) headerView.findViewById(R.id.txt_phone);
        img_avatar = (ImageView) headerView.findViewById(R.id.img_avatar);

        txt_name.setText(Common.builderWelcomeMessage());
        txt_phone.setText(Common.currentRide !=null ? Common.currentRide.getPhoneNumber():"");


        if(Common.currentRide != null && Common.currentRide.getAvatar()!= null &&
                !TextUtils.isEmpty(Common.currentRide.getAvatar()))
        {
            Glide.with(this)
                    .load(Common.currentRide.getAvatar())
                    .into(img_avatar);


        }
    }

/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_conductor, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_conductor);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

 */
}