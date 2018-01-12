package com.example.chongchenlearn901.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText etAccount;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(signUpListener);
        btnLogin.setOnClickListener(loginListener);
        etPassword.setOnKeyListener(this.passwordKeyListener);
        backgroundLayout.setOnClickListener(hideKeyboardListener);

        if(ParseUser.getCurrentUser() != null){
            goToUserList();
        }
    }

    private View.OnClickListener signUpListener = (v) -> {
        Account account = getAccount();

        String msg = getInvalidInputMsg(account);
        if(!TextUtils.isEmpty(msg)){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(account.account);
        user.setPassword(account.password);

        user.signUpInBackground(e -> {
            String result = e == null? "Sign Up successfully!": "Sign Up had an error " + e.getMessage();
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            if(e == null){
                startActivity(new Intent(this, UserListActivity.class));
            }
        });
    };

    private View.OnClickListener loginListener = (v) -> {
        Account account = this.getAccount();

        String msg = getInvalidInputMsg(account);
        if(!TextUtils.isEmpty(msg)){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "test: ");
        ParseUser.logInInBackground(account.account, account.password, (user, e) -> {
            String result = e == null? "Login successfully!": "Login had an error: " + e.getMessage();
            Log.d(TAG, "loginInBackground: err == null? " + (e == null));
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            if(e == null){
                goToUserList();
            }
        });
    };

    private String getInvalidInputMsg(Account account){
        ArrayList<String> msg = new ArrayList<>();
        if(TextUtils.isEmpty(account.account)){
            msg.add("Account");
        }
        if(TextUtils.isEmpty(account.password)){
            msg.add("Password");
        }


        return msg.size() == 0? null: TextUtils.join(", ", msg) + " should not be empty!";
    }

    private View.OnKeyListener passwordKeyListener = (v, keyCode, event) -> {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            this.btnLogin.callOnClick();
        }
        return false;
    };

    private View.OnClickListener hideKeyboardListener = (v) -> {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    private void goToUserList(){
        startActivity(new Intent(this, UserListActivity.class));
    }

    private class Account{
        String account;
        String password;

        public Account(String account, String password) {
            this.account = account;
            this.password = password;
        }
    }

    private Account getAccount(){
        return new Account(this.etAccount.getText().toString(), this.etPassword.getText().toString());
    }
}
