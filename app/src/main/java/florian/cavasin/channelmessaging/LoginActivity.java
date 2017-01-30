package florian.cavasin.channelmessaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonConnect;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(this);

        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
    }


    @Override
    public void onClick(View v) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("username", editTextUserName.getText().toString());
        params.put("password", editTextPassword.getText().toString());

        Downloader connexion = new Downloader(getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=connect");

        connexion.setOnDownloadCompleteListener(new OnDownloadCompleteListener() {
            @Override
            public void onDownloadCompleted(String result) {
                //déserialisation
                Gson gson = new Gson();
                Reponse obj = gson.fromJson(result, Reponse.class);

                if (obj.getResponse().equals("Ok")) {
                    Toast.makeText(getApplicationContext(), "Connecté", Toast.LENGTH_SHORT).show();

                    //Shared preferences
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accesstoken", obj.getAccesstoken());
                    editor.putString("username", editTextUserName.getText().toString());
                    // Commit the edits!
                    editor.commit();

                    Intent myIntent = new Intent(getApplicationContext(), ChannelListActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }
        });
        connexion.execute();
        Toast.makeText(getApplicationContext(), "Connexion en cours...", Toast.LENGTH_SHORT).show();

    }
}
