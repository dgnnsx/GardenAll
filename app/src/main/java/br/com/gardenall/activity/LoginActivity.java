package br.com.gardenall.activity;

import android.graphics.PorterDuff;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.gardenall.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        // Address the email and password field
        emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);
        emailEditText.getBackground().setColorFilter(getResources().getColor(R.color.black, null), PorterDuff.Mode.SRC_ATOP);
        passEditText.getBackground().setColorFilter(getResources().getColor(R.color.black, null), PorterDuff.Mode.SRC_ATOP);

    }

    public void checkLogin(View arg0) {
        TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        final String email = emailEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            tilEmail.setError("E-mail inválido");
        }

        final String pass = passEditText.getText().toString();
        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("Sua senha não pode ser vazia");
        }

        if(isValidEmail(email) && isValidPassword(pass))
        {
            // Validation Completed
        }

    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }
}