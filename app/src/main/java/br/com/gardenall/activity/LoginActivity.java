package br.com.gardenall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.gardenall.Callback.TaskFacebookEmailCompleted;
import br.com.gardenall.R;
import br.com.gardenall.domain.AppController;
import br.com.gardenall.domain.PlantaService;
import br.com.gardenall.domain.SQLiteHandler;
import br.com.gardenall.domain.Variaveis;
import br.com.gardenall.utils.NetworkUtils;
import br.com.gardenall.utils.Prefs;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity
    implements TaskFacebookEmailCompleted {
    private static final String TAG = "LoginActivity";
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private ImageButton btnLoginFacebook;
    private CallbackManager callbackManager;

    @InjectView(R.id.input_email) EditText inputEmail;
    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ButterKnife.inject(this);
        callbackManager = CallbackManager.Factory.create();

        btnLoginFacebook = (ImageButton) findViewById(R.id.btn_fb_login);
        btnLoginFacebook.setOnClickListener(loginFb());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        _loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                        checkLogin(email, password);
                    } else {
                        android.support.design.widget.Snackbar.make(findViewById(R.id.linearLayout),
                                R.string.error_conexao_indisponivel,
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.ok, onClickSnackBar())
                                .setActionTextColor(getBaseContext().getResources().getColor(R.color.colorLink))
                                .show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Digite todos os campos, por favor.", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTaskFacebookEmailComplete(String result) {
        Prefs.setString(getBaseContext(), "facebook_login", result);
    }

    private View.OnClickListener loginFb() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkAvailable(getApplicationContext())) { /* Internet disponivel */
                    // Facebook Login
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(
                            "public_profile", "email"));
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        private ProfileTracker mProfileTracker;

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            try {
                                new GetFbEmail(LoginActivity.this, loginResult).execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (Profile.getCurrentProfile() == null) {
                                mProfileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                                        setFacebookSession(newProfile);
                                        mProfileTracker.stopTracking();
                                    }
                                };
                            } else {
                                Profile profile = Profile.getCurrentProfile();
                                setFacebookSession(profile);
                            }
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(LoginActivity.this, "Login via Facebook cancelado",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(LoginActivity.this, "Erro no login via Facebook, tente novamente",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else { /* Internet indisponivel */
                    android.support.design.widget.Snackbar.make(findViewById(R.id.linearLayout),
                            R.string.error_conexao_indisponivel,
                            Snackbar.LENGTH_LONG)
                            .setAction(R.string.ok, onClickSnackBar())
                            .setActionTextColor(getBaseContext().getResources().getColor(R.color.colorLink))
                            .show();
                }
            }
        };
    }

    private void setFacebookSession(Profile profile) {
        if (profile != null) {
            Prefs.setBoolean(getBaseContext(), "login", true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Ocorreu algum erro, tente novamente!", Toast.LENGTH_LONG).show();
        }
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Entrando...");
        showDialog();

        try{
            PlantaService.getPlantasFromWeb(email);
        }catch(IOException e){
            e.printStackTrace();
        }

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Variaveis.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        Prefs.setBoolean(getBaseContext(), "login", true);

                        // Now store the user in SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String idS = user.getString("id");
                        Log.d("ID: ", idS);
                        int id = Integer.valueOf(idS);
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        int first_time_launch = user.getInt("first_time_launch");

                        // Inserting row in users table
                        db.addUser(name, email, id, created_at);

                        Log.d("FTL: ", Integer.toString(first_time_launch));

                        if (first_time_launch == 0){
                            Intent intent = new Intent(LoginActivity.this,
                                    WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(),
                                "Eita! E-mail ou senhas incorretos. Digite novamente por favor :)", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private class GetFbEmail extends AsyncTask<Void, Void, String> {
        private final LoginResult loginResult;
        private TaskFacebookEmailCompleted mCallback;
        private Context context;

        public GetFbEmail(Context context, LoginResult loginResult) {
            this.context = context;
            this.mCallback = (TaskFacebookEmailCompleted) context;
            this.loginResult = loginResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.i("LoginActivity", response.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email");
            request.setParameters(parameters);
            GraphResponse graphResponse = request.executeAndWait();
            try {
                return graphResponse.getJSONObject().getString("email");
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            mCallback.onTaskFacebookEmailComplete(response);
            hideDialog();
        }
    }

    private View.OnClickListener onClickSnackBar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(it); */
            }
        };
    }
}