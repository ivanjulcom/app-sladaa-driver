package com.sladaa.driver.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static com.sladaa.driver.utils.SessionManager.currncy;

public class OrderGenieDetailsActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, GetResult.MyListener {


    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_pmobile)
    TextView txtPmobile;
    @BindView(R.id.img_call_customer)
    ImageView imgCallCustomer;
    @BindView(R.id.txt_address_store)
    TextView txtAddressStore;
    @BindView(R.id.txt_dmobile)
    TextView txtDmobile;
    @BindView(R.id.img_call_store)
    ImageView imgCallStore;
    @BindView(R.id.txt_pmethod)
    TextView txtPmethod;
    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.lvl_dilevry)
    LinearLayout lvlDilevry;
    @BindView(R.id.txt_deliverd)
    TextView txtDeliverd;
    @BindView(R.id.lvl_pickup)
    LinearLayout lvlPickup;
    @BindView(R.id.txt_pickup)
    TextView txtPickup;
    @BindView(R.id.lvl_accept_reject)
    LinearLayout lvlAcceptReject;
    @BindView(R.id.txt_accept)
    TextView txtAccept;
    @BindView(R.id.txt_reject)
    TextView txtReject;
    @BindView(R.id.activity_main_rfab)
    RapidFloatingActionButton rfaButtons;
    @BindView(R.id.activity_main_rfal)
    RapidFloatingActionLayout rfaLayout;

    RapidFloatingActionHelper rfabHelper;

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
    ArrayList<String> list;

    CustPrograssbar custPrograssbar;
    User user;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genieorder_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setElevation(0f);
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
        order = (PendingOrderItem) getIntent().getParcelableExtra("MyClass");
        productinfoArrayList = getIntent().getParcelableArrayListExtra("MyList");
        list = getIntent().getStringArrayListExtra("Myimage");
        txtAddress.setText("" + order.getCustomerDaddress());

        txtAddressStore.setText("" + order.getCustomerPaddress());
        txtDmobile.setText("" + order.getCustomerDmobile());
        txtPmobile.setText("" + order.getCustomerPmobile());
        txtPmethod.setText("PAYMENT METHOD : " + order.getmPMethod() + " ");
        txtTotal.setText("TOTAL : " + sessionManager.getStringData(currncy) + " " + order.getmTotal());


        if (order.getmStatus().equalsIgnoreCase("Sedang diproses")) {
            lvlAcceptReject.setVisibility(View.GONE);
            rfaLayout.setVisibility(View.VISIBLE);
            lvlPickup.setVisibility(View.VISIBLE);
            lvlDilevry.setVisibility(View.GONE);

        } else if (order.getmStatus().equalsIgnoreCase("on route")) {
            lvlAcceptReject.setVisibility(View.GONE);
            rfaLayout.setVisibility(View.VISIBLE);
            lvlPickup.setVisibility(View.GONE);
            lvlDilevry.setVisibility(View.VISIBLE);
        }else if (order.getmStatus().equalsIgnoreCase("Completed")) {
            lvlAcceptReject.setVisibility(View.GONE);
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
                startActivity(new Intent(OrderGenieDetailsActivity.this, SignatureGenieActivity.class).putExtra("oid", order.getOrderid()).putExtra("rid", user.getId()));
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
                intent.setData(Uri.parse("tel:" + order.getCustomerPmobile()));
                startActivity(intent);
                break;
            case R.id.img_call_store:
                Intent intent1 = new Intent(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:" + order.getCustomerDmobile()));
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

            Call<JsonObject> call = APIClient.getInterface().getPKGOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            jsonObject.put("status", "cancle");
            jsonObject.put("comment", comment);
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getPKGOstatus((JsonObject) jsonParser.parse(jsonObject.toString()));
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
                        listener.onClickItem("on route", order);
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
        View sheetView = getLayoutInflater().inflate(R.layout.orderlist_layout1, null);
        mBottomSheetDialog.setContentView(sheetView);
        RecyclerView myRecyclertemp = sheetView.findViewById(R.id.lvl_data);

        myRecyclertemp.setLayoutManager(new GridLayoutManager(this, 2));
        ItemAdp itemAdp = new ItemAdp(OrderGenieDetailsActivity.this, list);
        myRecyclertemp.setAdapter(itemAdp);

        mBottomSheetDialog.show();


    }

    public class ItemAdp extends RecyclerView.Adapter<ItemAdp.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        Context mContext;


        public ItemAdp(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mContext = context;
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.pending_order_imagge, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {

            Glide.with(mContext).load(APIClient.baseUrl + "/" + mData.get(i)).into(holder.imgIcon);
            Log.e("Image Url","--> "+APIClient.baseUrl + "/" + mData.get(i));

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.imageView)
            ImageView imgIcon;


            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }


    }
}
