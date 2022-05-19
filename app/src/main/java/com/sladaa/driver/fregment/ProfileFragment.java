package com.sladaa.driver.fregment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.sladaa.driver.R;
import com.sladaa.driver.activity.InfoActivity;
import com.sladaa.driver.activity.ProfileActivity;
import com.sladaa.driver.model.Ostatus;
import com.sladaa.driver.model.RestResponse;
import com.sladaa.driver.model.User;
import com.sladaa.driver.retrofit.APIClient;
import com.sladaa.driver.retrofit.GetResult;
import com.sladaa.driver.utils.CustPrograssbar;
import com.sladaa.driver.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class ProfileFragment extends Fragment implements GetResult.MyListener {

    @BindView(R.id.img_logout)
    ImageView imgLogout;
    @BindView(R.id.ed_username)
    TextView edUsername;
    @BindView(R.id.ed_email)
    TextView edEmail;
    @BindView(R.id.ed_phone)
    TextView edPhone;
    @BindView(R.id.switch1)
    Switch switch1;
    @BindView(R.id.txt_complet)
    TextView txtComplet;
    @BindView(R.id.txt_sale)
    TextView txtSale;
    @BindView(R.id.txt_cencel)
    TextView txtCencel;
    @BindView(R.id.txt_recived)
    TextView txtRecived;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.img_edit)
    ImageView imgEdit;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    SessionManager sessionManager;
    User user;
    CustPrograssbar custPrograssbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        ProfileActivity.listener = this;
        sessionManager = new SessionManager(getActivity());
        custPrograssbar = new CustPrograssbar();
        user = sessionManager.getUserDetails("");
        edUsername.setText("" + user.getName());
        edEmail.setText("" + user.getEmail());
        edPhone.setText("" + user.getMobile());

        if (sessionManager.getBooleanData("status")) {
            txtStatus.setText("Tersedia");
            switch1.setChecked(true);
        } else {
            switch1.setChecked(false);
            txtStatus.setText("Tidak Tersedia");
        }
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status("1");
                    txtStatus.setText("Tersedia");
                } else {
                    status("0");
                    txtStatus.setText("Tidak Tersedia");

                }
            }
        });
        orderStatus();
        return view;
    }

    private void status(String key) {
        custPrograssbar.prograsscreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("status", key);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getStatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void orderStatus() {
        custPrograssbar.prograsscreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());

            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().orderStatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closeprograssbar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equalsIgnoreCase("true")) {
                    sessionManager.setBooleanData("status", switch1.isChecked());
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Ostatus ostatus = gson.fromJson(result.toString(), Ostatus.class);
                txtComplet.setText("" + ostatus.getOrderData().getTotalCompleteOrder());
                txtSale.setText(sessionManager.getStringData(SessionManager.currncy) + " " + ostatus.getOrderData().getTotalSale());
                txtCencel.setText("" + ostatus.getOrderData().getTotalRejectOrder());
                txtRecived.setText("" + ostatus.getOrderData().getTotalReceiveorder());

                if (ostatus.getOrderData().getRiderStatus().equalsIgnoreCase("1")) {
                    switch1.setChecked(true);
                    sessionManager.setBooleanData("status", true);
                } else {
                    switch1.setChecked(false);
                    sessionManager.setBooleanData("status", false);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.img_logout)
    public void onClick() {

    }

    public void onrefaress() {
        if (user != null && sessionManager != null) {
            user = sessionManager.getUserDetails("");
            edUsername.setText("" + user.getName());
            edEmail.setText("" + user.getEmail());
            edPhone.setText("" + user.getMobile());
        }

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @OnClick({R.id.img_edit, R.id.img_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_edit:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.img_logout:


                AlertDialog myDelete = new AlertDialog.Builder(getActivity())
                        .setTitle("Keluar")
                        .setMessage("Anda yakin ingi Keluar!")
                        .setIcon(R.drawable.ic_logout)

                        .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                dialog.dismiss();
                                sessionManager.logoutUser();
                                status("0");
                                txtStatus.setText("Tidak Tersedia");
                                startActivity(new Intent(getActivity(), InfoActivity.class));
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .create();
                myDelete.show();


                break;
            default:
                break;
        }
    }
}
