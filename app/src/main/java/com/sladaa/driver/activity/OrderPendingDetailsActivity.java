package com.sladaa.driver.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sladaa.driver.R;
import com.sladaa.driver.model.PendingOrderItem;
import com.sladaa.driver.model.Productinfo;
import com.sladaa.driver.model.RestResponse;
import com.sladaa.driver.model.User;
import com.sladaa.driver.retrofit.APIClient;
import com.sladaa.driver.retrofit.GetResult;
import com.sladaa.driver.utils.CustPrograssbar;
import com.sladaa.driver.utils.SessionManager;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.sladaa.driver.retrofit.APIClient.baseUrl;
import static com.sladaa.driver.utils.SessionManager.currncy;

public class OrderPendingDetailsActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, GetResult.MyListener {


    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_address_store)
    TextView txtAddressStore;
    @BindView(R.id.txt_email_store)
    TextView txtEmailStore;
    @BindView(R.id.txt_name_store)
    TextView txtNameStore;

    @BindView(R.id.txt_pmethod)
    TextView txtPmethod;
    @BindView(R.id.txt_qty)
    TextView txtQty;
    @BindView(R.id.txt_total)
    TextView txtTotal;


    @BindView(R.id.activity_main_rfab)
    RapidFloatingActionButton rfaButtons;
    @BindView(R.id.activity_main_rfal)
    RapidFloatingActionLayout rfaLayout;

    @BindView(R.id.txt_deliverd)
    TextView txtDeliverd;
    @BindView(R.id.lvl_dilevry)
    LinearLayout lvlDilevry;
    @BindView(R.id.txt_accept)
    TextView txtAccept;
    @BindView(R.id.txt_reject)
    TextView txtReject;
    @BindView(R.id.lvl_accept_reject)
    LinearLayout lvlAcceptReject;

    @BindView(R.id.lvl_pickup)
    LinearLayout lvlPickup;

    private RapidFloatingActionHelper rfabHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.navigation_product:
                bottonOrderMakeDecision();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ArrayList<Productinfo> productinfoArrayList;
    PendingOrderItem order;
    SessionManager sessionManager;

    CustPrograssbar custPrograssbar;
    User user;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Pesanan");
        getSupportActionBar().setElevation(0f);
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
        order = (PendingOrderItem) getIntent().getParcelableExtra("MyClass");
        productinfoArrayList = getIntent().getParcelableArrayListExtra("MyList");


        txtAddress.setText("" + order.getmDelivery());
        txtEmail.setText("" + order.getmEmail());
        txtName.setText("" + order.getmName());
        txtAddressStore.setText("" + order.getmPickup());
        txtEmailStore.setText("" + order.getPickupEmail());
        txtNameStore.setText("" + order.getmPickupname());
        txtPmethod.setText("METODE PEMBAYARAN : " + order.getmPMethod() + " ");
        txtTotal.setText("TOTAL : " + sessionManager.getStringData(currncy) + " " + order.getmTotal());
        txtQty.setText("QTY : " + productinfoArrayList.size());


        if (order.getmStatus().equalsIgnoreCase("Sedang diproses")) {
            lvlAcceptReject.setVisibility(View.GONE);
            rfaLayout.setVisibility(View.VISIBLE);
            lvlPickup.setVisibility(View.VISIBLE);
            lvlDilevry.setVisibility(View.GONE);

        } else if (order.getmStatus().equalsIgnoreCase("Dalam perjalanan")) {
            lvlAcceptReject.setVisibility(View.GONE);
            rfaLayout.setVisibility(View.VISIBLE);
            lvlPickup.setVisibility(View.GONE);
            lvlDilevry.setVisibility(View.VISIBLE);
        }

        setFloting();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFloting() {

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Issue with an ongoing order")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Payment issue with my order")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Address wrong ")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Other")
                .setResId(R.drawable.ic_clear_white)
                .setIconNormalColor(Color.parseColor("#C81507"))
                .setIconPressedColor(R.color.colorred)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundDrawable(getDrawable(R.drawable.button_round))
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(this, 2))
                .setIconShadowColor(R.color.colorred)
                .setIconShadowDy(RFABTextUtil.dip2px(this, 1))
        ;

        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButtons,
                rfaContent
        ).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }


    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {


        orderCencel(item.getLabel());


        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        orderCencel(item.getLabel());
        rfabHelper.toggleContent();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.txt_deliverd, R.id.txt_accept, R.id.txt_reject, R.id.img_call_customer, R.id.img_call_store, R.id.txt_pickup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_deliverd:
                startActivity(new Intent(OrderPendingDetailsActivity.this, SignatureActivity.class).putExtra("oid", order.getOrderid()).putExtra("rid", user.getId()));
                break;
            case R.id.txt_accept:
                orderStatus("accept");
                break;
            case R.id.txt_reject:
                orderStatus("reject");
                break;

            case R.id.txt_pickup:
                orderStatus("pickup");
                break;
            case R.id.img_call_customer:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + order.getmMobile()));
                startActivity(intent);
                break;
            case R.id.img_call_store:
                Intent intent1 = new Intent(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:" + order.getmPickupmobile()));
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private void orderStatus(String status) {
        custPrograssbar.prograsscreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("oid", order.getOrderid());
            jsonObject.put("status", status);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void orderCencel(String comment) {
        custPrograssbar.prograsscreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("oid", order.getOrderid());
            jsonObject.put("status", "batal");
            jsonObject.put("comment", comment);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            order.setmProductinfo(productinfoArrayList);
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {

                    if (response.getNextStep().equalsIgnoreCase("pickup")) {
                        lvlAcceptReject.setVisibility(View.GONE);
                        rfaLayout.setVisibility(View.VISIBLE);
                        lvlPickup.setVisibility(View.VISIBLE);
                        lvlDilevry.setVisibility(View.GONE);
                        listener.onClickItem("Sedang diproses", order);

                    } else if (response.getNextStep().equalsIgnoreCase("Deliverey")) {
                        lvlPickup.setVisibility(View.GONE);
                        lvlDilevry.setVisibility(View.VISIBLE);
                        listener.onClickItem("Dalam perjalanan", order);
                    }

                } else {
                    listener.onClickItem("reject", order);
                    finish();
                }

            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result, RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    listener.onClickItem("reject", order);
                    finish();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PenddingFragment listener;

    public interface PenddingFragment {
        public void onClickItem(String s, PendingOrderItem orderItem);

    }

    public void bottonOrderMakeDecision() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.orderlist_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        LinearLayout linearLayout = sheetView.findViewById(R.id.lvl_data);

        for (int i = 0; i < productinfoArrayList.size(); i++) {
            Productinfo listdatum = productinfoArrayList.get(i);
            LayoutInflater inflater = LayoutInflater.from(OrderPendingDetailsActivity.this);

            View view = inflater.inflate(R.layout.pending_order_item, null);
            ImageView imgView = view.findViewById(R.id.imageView);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtItems = view.findViewById(R.id.txt_items);
            TextView txtWight = view.findViewById(R.id.txt_wight);
            TextView txtPrice = view.findViewById(R.id.txt_price);
            TextView txtPricedis = view.findViewById(R.id.txt_pricedis);
            Glide.with(OrderPendingDetailsActivity.this).load(baseUrl + listdatum.getProductImage()).placeholder(R.drawable.slider).into(imgView);
            Log.e("url", baseUrl + listdatum.getProductImage());
            txtTitle.setText("" + listdatum.getProductName());
            txtItems.setText(" X " + listdatum.getProductQty() + " Items");
            txtWight.setText(" " + listdatum.getProductWeight() + "");
            if (Double.parseDouble(listdatum.getDiscount()) == 0) {
                txtPricedis.setVisibility(View.GONE);
                txtPrice.setText(sessionManager.getStringData(currncy) + listdatum.getProductPrice() + "");

            } else {
                double ress = (Double.parseDouble(listdatum.getProductPrice()) * Double.parseDouble(listdatum.getDiscount())) / 100;
                ress = Double.parseDouble(listdatum.getProductPrice()) - ress;
                txtPrice.setText(sessionManager.getStringData(currncy) + ress + "");

                txtPricedis.setPaintFlags(txtPricedis.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtPricedis.setText(sessionManager.getStringData(currncy) + "" + listdatum.getProductPrice());
            }
            linearLayout.addView(view);
        }

        mBottomSheetDialog.show();


    }
}
