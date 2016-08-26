package br.com.gardenall.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.com.gardenall.R;
import br.com.gardenall.utils.Prefs;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se o usuário já tiver logado entra direto, senão vai para tela de login
        boolean login = Prefs.getBoolean(this, "login");
        if(login == true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);

            // Address the email and password field
            emailEditText = (EditText) findViewById(R.id.username);
            passEditText = (EditText) findViewById(R.id.password);
        }
    }

    public void checkLogin(View arg0) {
        TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        final String email = emailEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            tilEmail.setError("E-mail inválido");
        }
        TextInputLayout tilPass = (TextInputLayout) findViewById(R.id.text_input_layout_password);
        final String pass = passEditText.getText().toString();
        if (!isValidPassword(pass)) {
            //Set error message for password field
            tilPass.setError("Sua senha não pode ser vazia");
        }

        // Validation Completed
        if(isValidEmail(email) && isValidPassword(pass))
        {
            // Salva o login
            Prefs.setBoolean(getBaseContext(), "login", true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        /*String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();*/

        if(email != null && email.length() >= 4)
            return true;
        else
            return false;
    }

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }
}