package com.work.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.work.cafe.data.UserData;
import com.work.cafe.model.LoginModel;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, LoginModel.SignUpCallback, View.OnFocusChangeListener {

    private final String TAG = SignUpActivity.class.getSimpleName();

    private EditText id_view;
    private EditText pwd_view;
    private EditText pwd_check_view;
    private EditText nickname_view;

    private View email_check_light;
    private View pwd_check_light;

    private TextView email_warning;
    private TextView pwd_warning;

    private Button signup_btn;

    private boolean email_check = false;
    private boolean pwd_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        id_view = findViewById(R.id.edit_id);
        pwd_view = findViewById(R.id.edit_pwd);
        pwd_check_view = findViewById(R.id.edit_pwd_check);
        nickname_view = findViewById(R.id.edit_nickname);

        email_check_light = findViewById(R.id.edit_id_light);
        pwd_check_light = findViewById(R.id.edit_pwd_light);

        email_warning = findViewById(R.id.edit_id_warning);
        pwd_warning = findViewById(R.id.edit_pwd_check_warning);

        findViewById(R.id.sign_up).setOnClickListener(this);


        id_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                String id = id_view.getText().toString();

                if (!hasFocus && !id.isEmpty()) {
                    Log.d(TAG, "onFocusChange: email");

                    new LoginModel().checkUserID(id, SignUpActivity.this);
                }
            }
        });

        pwd_view.setOnFocusChangeListener(this);
        pwd_check_view.setOnFocusChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up) {
            if (email_check && pwd_check) {

                String id = id_view.getText().toString();
                String pwd = pwd_view.getText().toString();
                String nickname = nickname_view.getText().toString();

                UserData userData = new UserData(id, pwd, nickname);

                new LoginModel().createUser(userData, this);
            } else {
                Toast.makeText(this, R.string.checking_sign_up_text, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCheckEmail(boolean exits) {

        Log.d(TAG, "onCheckEmail: exits = " + exits);
        email_check = !exits;
        if (exits) {
            email_check_light.setBackgroundColor(getResources().getColor(R.color.colorFailure));
            email_warning.setText(R.string.checking_email_warning);
        } else {
            email_check_light.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
            email_warning.setText("");
        }
    }

    @Override
    public void onCreateUser(boolean created) {

        if (created) {
            finish();
            Toast.makeText(this, R.string.sign_up_success, Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, R.string.sign_up_failure, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        String pwd = pwd_view.getText().toString();
        String pwd_check = pwd_check_view.getText().toString();

        if (!hasFocus && !pwd.isEmpty() && !pwd_check.isEmpty()) {
            this.pwd_check = pwd.equals(pwd_check);

            if (this.pwd_check) {
                pwd_check_light.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
                pwd_warning.setText("");
            } else {
                pwd_check_light.setBackgroundColor(getResources().getColor(R.color.colorFailure));
                pwd_warning.setText(R.string.checking_pwd_warning);
            }
        }
    }
}