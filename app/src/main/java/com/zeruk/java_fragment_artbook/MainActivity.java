package com.zeruk.java_fragment_artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.menu_row)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("addOrSee", 0);
            //NavDirections navDirections = RecyclerFragmentDirections.actionRecyclerFragmentToContentFragment();
            Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.action_recyclerFragment_to_contentFragment, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_rows, menu);
        return super.onCreateOptionsMenu(menu);
    }
}