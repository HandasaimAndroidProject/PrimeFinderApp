package com.example.eidancohen.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Integer> primeList;
    ArrayAdapter<Integer> primeAdapter;
    PrimeFinderTask task;
    boolean runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> ActionButtonHandler(view));

        primeList = new ArrayList<>();
        primeAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_list_item_1, primeList);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(primeAdapter);

        task = new PrimeFinderTask(10000000);
        runningTask = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(runningTask)
            pauseTask();
    }

    private class PrimeFinderTask extends AsyncTask<Void, Integer, Void> {

        private int currentNumber;

        public PrimeFinderTask(int startNumber) {
            super();
            this.currentNumber = startNumber;
        }

        public PrimeFinderTask freshClone() {
            return new PrimeFinderTask(currentNumber);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(!isCancelled()) {
                if (isPrime(currentNumber))
                    publishProgress(currentNumber);
                currentNumber++;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            for (int n : values) {
                primeAdapter.add(n);
            }
        }

        private boolean isPrime(int n) {
            if (n < 2)
                return false;
            for(int i = 2; i <= Math.sqrt(n);i++)
                if (n % i == 0)
                    return false;
            return true;
        }
    }

    public void ActionButtonHandler(View view) {
        if(!runningTask) {
            task.execute();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            runningTask = true;
        }
        else {
            pauseTask();
        }
    }

    private void pauseTask() {
        task.cancel(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        runningTask = false;
        task = task.freshClone();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
