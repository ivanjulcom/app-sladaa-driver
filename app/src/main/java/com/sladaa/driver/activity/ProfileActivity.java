package com.sladaa.driver.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sladaa.driver.R;
import com.sladaa.driver.model.AreaD;
import com.sladaa.driver.model.LoginUser;
import com.sladaa.driver.model.User;
import com.sladaa.driver.retrofit.APIClient;
import com.sladaa.driver.retrofit.GetResult;
import com.sladaa.driver.utils.SessionManager;
import com.sladaa.driver.utils.Utiles;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity implements GetResult.MyListener {


    String areaSelect;
    List<AreaD> areaDS = new ArrayList<>();

    SessionManager sessionManager;
    User user;
    @BindView(R.id.ed_username)
    TextInputEditText edUsername;
    @BindView(R.id.ed_address)
    TextInputEditText edAddress;
    @BindView(R.id.ed_email)
    TextInputEditText edEmail;
    @BindView(R.id.ed_alternatmob)
    TextInputEditText edAlternatmob;
    @BindView(R.id.ed_password)
    TextInputEditText edPassword;
    @BindView(R.id.txt_save)
    TextView txtSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(ProfileActivity.this);
        user = sessionManager.getUserDetails("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        setcountaint(user);
    }

    private void setcountaint(User user) {
        edUsername.setText("" + user.getName());
        edAddress.setText("" + user.getaddress());
        edEmail.setText("" + user.getEmail());
        edAlternatmob.setText("" + user.getMobile());
        edPassword.setText("" + user.getPassword());
    }



    @OnClick(R.id.txt_save)
    public void onViewClicked() {
        if (validation()) {
            updateUser();
        }
    }

    private void updateUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("name", edUsername.getText().toString());
            jsonObject.put("email", edEmail.getText().toString());
            jsonObject.put("mobile", edAlternatmob.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());
            jsonObject.put("aid", areaSelect);
            jsonObject.put("address", edAddress.getText().toString());
            jsonObject.put("imei", Utiles.getIMEI(ProfileActivity.this));
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().updateProfile((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        Log.e("response", "-->" + result);
        try {
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                LoginUser response = gson.fromJson(result.toString(), LoginUser.class);
                Toast.makeText(ProfileActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equals("true")) {
                    sessionManager.setUserDetails("", response.getUser());
                    sessionManager.setIntData("dcharge", response.getdCharge());
                    listener.onrefaress();
                    finish();
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }


    public static com.sladaa.driver.fregment.ProfileFragment listener;


    public boolean validation() {
        if (edUsername.getText().toString().isEmpty()) {
            edUsername.setError("Masukan Nama");
            return false;
        }
        if (edAddress.getText().toString().isEmpty()) {
            edAddress.setError("Masukan Alamat");
            return false;
        }

        if (edEmail.getText().toString().isEmpty() || !isEmailValid(edEmail.getText().toString())) {
            edEmail.setError("Msukan Email yang Valid");
            return false;
        }
        if (edAlternatmob.getText().toString().isEmpty()) {
            edAlternatmob.setError("Masukan No. Telepon Valid");
            return false;
        }
        return true;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; go home
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
