package com.sladaa.driver.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sladaa.driver.R;
import com.sladaa.driver.activity.OrderPendingDetailsActivity;
import com.sladaa.driver.model.PendingOrder;
import com.sladaa.driver.model.PendingOrderItem;
import com.sladaa.driver.model.User;
import com.sladaa.driver.retrofit.APIClient;
import com.sladaa.driver.retrofit.GetResult;
import com.sladaa.driver.utils.CustPrograssbar;
import com.sladaa.driver.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.sladaa.driver.utils.SessionManager.currncy;


public class PendingFragment extends Fragment implements GetResult.MyListener, OrderPendingDetailsActivity.PenddingFragment {
    @BindView(R.id.txt_itmecount)
    TextView txtItmecount;
    @BindView(R.id.recycle_pending)
    RecyclerView recyclePending;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    @BindView(R.id.txtNodata)
    TextView txtNodata;
    List<PendingOrderItem> pendinglistMain = new ArrayList<>();
    PendingAdepter myOrderAdepter;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pendding, container, false);
        ButterKnife.bind(this, view);
        OrderPendingDetailsActivity.listener = this;
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        recyclePending.setLayoutManager(recyclerLayoutManager);
        getPendingOrder();
        return view;
    }

    private void getPendingOrder() {
        custPrograssbar.prograsscreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getOlist((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

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
                PendingOrder pendingOrder = gson.fromJson(result.toString(), PendingOrder.class);
                if (pendingOrder.getResult().equalsIgnoreCase("true")) {
                    txtItmecount.setText(pendingOrder.getOrderData().size() + " Pesanan");
                    if (pendingOrder.getOrderData().isEmpty()) {
                        txtNodata.setVisibility(View.VISIBLE);
                        recyclePending.setVisibility(View.GONE);
                    } else {
                        pendinglistMain = pendingOrder.getOrderData();
                        myOrderAdepter = new PendingAdepter(pendinglistMain);
                        recyclePending.setAdapter(myOrderAdepter);
                    }
                } else {
                    txtNodata.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onClickItem(String s, PendingOrderItem orderItem) {

        for (int i = 0; i < pendinglistMain.size(); i++) {
            if (pendinglistMain.get(i).getOrderid().equalsIgnoreCase(orderItem.getOrderid())) {
                if (s.equalsIgnoreCase("reject")) {
                    pendinglistMain.remove(i);
                    myOrderAdepter.notifyDataSetChanged();
                } else {
                    orderItem.setmStatus(s);
                    pendinglistMain.set(i, orderItem);
                    myOrderAdepter.notifyDataSetChanged();
                }
                break;  // uncomment to get the first instance
            }
        }
    }

    public class PendingAdepter extends RecyclerView.Adapter<PendingAdepter.ViewHolder> {
        private List<PendingOrderItem> pendinglist;

        public PendingAdepter(List<PendingOrderItem> pendinglist) {
            this.pendinglist = pendinglist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pending_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            PendingOrderItem order = pendinglist.get(position);
            holder.txtOderid.setText("ID Pesanan #" + order.getOrderid());
            holder.txtDateandstatus.setText(order.getmStatus() + " pada " + order.getmOdate());
            holder.txtType.setText("" + order.getmPMethod());
            holder.txtPricetotla.setText(sessionManager.getStringData(currncy) + "" + order.getmTotal());

            holder.txtStuts.setText(" " + order.getmStatus() + " ");

            holder.lvlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), OrderPendingDetailsActivity.class).putExtra("MyClass", order).putParcelableArrayListExtra("MyList", order.getmProductinfo()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return pendinglist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_oderid)
            TextView txtOderid;
            @BindView(R.id.txt_pricetotla)
            TextView txtPricetotla;
            @BindView(R.id.txt_dateandstatus)
            TextView txtDateandstatus;
            @BindView(R.id.txt_type)
            TextView txtType;
            @BindView(R.id.txt_stuts)
            TextView txtStuts;

            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;
            @BindView(R.id.img_right)
            ImageView imgRight;
            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
