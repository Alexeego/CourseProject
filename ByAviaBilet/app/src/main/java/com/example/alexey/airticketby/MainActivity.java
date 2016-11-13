package com.example.alexey.airticketby;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexey.airticketby.connection.Connection;
import com.example.alexey.airticketby.connection.MessageType;
import com.example.alexey.airticketby.ray.Ray;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    static MainActivity context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //////////////////////////////////
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initDialog();
        setEnterFragment();
        tryConnectOrCreateDialog();
    }

    EditText editIp;
    EditText editPort;
    AlertDialog alertDialog;

    private void initDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Не удалось подключиться к серверу по данным параметрам");
        View view = getLayoutInflater().inflate(R.layout.connection_dialog, null);
        editIp = (EditText) view.findViewById(R.id.editIP);
        editPort = (EditText) view.findViewById(R.id.editPort);
        editIp.setText(preferences.getString("ip", ""));
        editPort.setText(preferences.getString("port", ""));
        view.findViewById(R.id.buttonConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                new AsyncConnect().execute(editIp.getText().toString(), editPort.getText().toString()); // Asynchronous connection
            }
        });
        view.findViewById(R.id.buttonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setView(view);
        dialog.setCancelable(false);
        alertDialog = dialog.create();
    }
    void tryConnectOrCreateDialog() {
        if (preferences.getBoolean("autoConnect", false)) {
            new AsyncConnect().execute(preferences.getString("ip", ""), preferences.getString("port", ""));
        } else {
            alertDialog.show();
        }
    }

    class AsyncConnect extends AsyncTask<String, String, Connection> {

        private Socket socket = new Socket();
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Подключение к серверу");
            progressDialog.setMessage("Пожалуйста подождите");
            progressDialog.setCancelable(false);
            progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Прервать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Toast.makeText(context, "Ошибка в прерывании", Toast.LENGTH_SHORT).show();
                    }
                }

                ;
            });
            alertDialog.dismiss();
            progressDialog.show();
        }
        @Override
        protected Connection doInBackground(String... params) {
            if (params == null || params.length != 2) return null;
            Connection connection = null;
            try {
                socket.connect(new InetSocketAddress(InetAddress.getByName(params[0]), Integer.parseInt(params[1])), 5000);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                connection = new Connection(socket, outputStream, inputStream);
                if (MessageType.valueOf((String) inputStream.readObject()) == MessageType.CONNECT_REQUEST) {
                    outputStream.writeObject(MessageType.USER_ANDROID.toString());
                    outputStream.flush();
                    return connection;
                }
            } catch (Exception ignore) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (IOException ignored) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(Connection connection) {
            progressDialog.dismiss();
            if (connection != null) {
                MainActivity.connection = connection;
                Toast.makeText(MainActivity.context, "Соединение установленно", Toast.LENGTH_SHORT).show();

                connectAuthorization();
                new BackgroundAsyncTask().execute(); // Start listener

                ConnectionFragment.editNameAuthorization.setEnabled(true);
                ConnectionFragment.editPasswordAuthorization.setEnabled(true);
                ConnectionFragment.buttonAuthorization.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.context, "Соединение не удалось установить", Toast.LENGTH_SHORT).show();
                alertDialog.show();
            }
        }
    }

    enum ConnectionState {
        TRY_CONNECTION,
        AUTHORIZATION,
        REGISTRATION,
        CONNECT
    }

    static ConnectionState nowConnectionState = ConnectionState.TRY_CONNECTION;
    static String userName = null;
    static ArrayList<Ray> rays = null;
    static Connection connection = null;
    static Stack<Fragment> stackFragments = new Stack<>();
    static Fragment fragment = null;

    final static Object lock = new Object();

    static void connectError() {
        synchronized (lock) {
            if (nowConnectionState != ConnectionState.TRY_CONNECTION) {
                nowConnectionState = ConnectionState.TRY_CONNECTION;
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception ignore) {
                    }
                }
                connection = null;
                rays = null;
                userName = null;
                stackFragments.clear();
                context.setEnterFragment();
                context.tryConnectOrCreateDialog();
                Toast.makeText(MainActivity.context, "Соединение разорвано", Toast.LENGTH_SHORT).show();
            }
        }
    }
    static void connectRegistration() {
        nowConnectionState = ConnectionState.REGISTRATION;
    }
    static void connectAuthorization() {
        nowConnectionState = ConnectionState.AUTHORIZATION;
    }
    static void connectSuccess() {
        nowConnectionState = ConnectionState.CONNECT;
    }

    void setEnterFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (nowConnectionState) {
            case TRY_CONNECTION: {
            }
            case AUTHORIZATION: {
                fragment = new ConnectionFragment();
                break;
            }
            case REGISTRATION: {
                fragment = new RegistrationFragment();
                break;
            }
            case CONNECT: {
                fragment = new MainWindowFragment();
                break;
            }
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (nowConnectionState == ConnectionState.CONNECT && fragment instanceof MainWindowFragment) {
                connectError();
            } else if (nowConnectionState == ConnectionState.REGISTRATION && fragment instanceof RegistrationFragment) {
                connectAuthorization();
                setEnterFragment();
            } else if(nowConnectionState == ConnectionState.CONNECT && fragment  instanceof MyTicketsFragment) {
                context.setEnterFragment();
            } else if (!stackFragments.isEmpty()) {
                    fragment = stackFragments.pop();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        nowConnectionState = ConnectionState.TRY_CONNECTION;
        if (connection != null)
            try {
                connection.close();
            } catch (IOException ignore) {
            }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_rays:
                stackFragments.clear();
                setEnterFragment();
                break;
            case R.id.nav_my_tickets:
                if (nowConnectionState == ConnectionState.CONNECT) {
                    if(!(fragment  instanceof MyTicketsFragment)) {
                        stackFragments.clear();
                        fragment = new MyTicketsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                } else
                    Toast.makeText(context, "Необходимо аворизоваться", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_manage:
                startActivity(new Intent(this, PrefActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
