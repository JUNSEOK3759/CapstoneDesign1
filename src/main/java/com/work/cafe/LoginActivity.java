package com.work.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.loadingview.LoadingDialog;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.work.cafe.data.UserData;
import com.work.cafe.model.HTTPManager;
import com.work.cafe.model.LoginModel;
import com.work.cafe.model.UserSharedModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginModel.LoginCallback {

    private EditText edit_id;
    private EditText edit_pwd;

    private TextView login_warning;

    private final String TAG = LoginActivity.class.getSimpleName();

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edit_id = findViewById(R.id.edit_id);
        edit_pwd = findViewById(R.id.edit_pwd);

        login_warning = findViewById(R.id.login_warning);

        loadingDialog = LoadingDialog.Companion.get(this);


        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.not_sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.admin_sign_up).setOnClickListener(this);


        UserSharedModel.getInstance(this).remove();

        Places.initialize(getApplicationContext(), HTTPManager.PLACE_API_KEY);


        PlacesClient placesClient = Places.createClient(this);
    }

    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick: " + v.getId());
        switch (v.getId()) {
            case R.id.sign_in:
                login_warning.setText("");

                String id = edit_id.getText().toString();
                String pwd = edit_pwd.getText().toString();

                if (id.length() > 0 && pwd.length() > 0) {
                    loadingDialog.show();

                    new LoginModel().loginUser(id, pwd, this);
                } else {
                    Toast.makeText(this, "빈 자리 없이 입력 해주세요", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.not_sign_in:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.admin_sign_up:
                startActivity(new Intent(this, AdminSignUpActivity.class));
                break;
        }

    }

    @Override
    public void onLogin(boolean isLogin, UserData userData) {
        loadingDialog.hide();
        if (isLogin) {
            UserSharedModel.getInstance(this).writeData(userData);

            if (userData.type.equals("cafe")) {
                startActivity(new Intent(this, EditCafeActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

            finish();
        } else {
            login_warning.setText(R.string.checking_email_pwd_warning);
        }
    }

}