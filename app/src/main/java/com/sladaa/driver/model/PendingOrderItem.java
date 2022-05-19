
package com.sladaa.driver.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class PendingOrderItem implements Parcelable {

    @SerializedName("astatus")
    private String mAstatus;
    @SerializedName("Delivery_charge")
    private String mDCharge;
    @SerializedName("customer_address")
    private String mDelivery;
    @SerializedName("pickup")
    private String mPickup;
    @SerializedName("pickup_name")
    private String mPickupname;
    @SerializedName("pickup_mobile")
    private String mPickupmobile;
    @SerializedName("customer_email")
    private String mEmail;
    @SerializedName("customer_mobile")
    private String mMobile;

    @SerializedName("customer_name")
    private String mName;
    @SerializedName("order_date")
    private String mOdate;
    @SerializedName("p_method_name")
    private String mPMethod;

    @SerializedName("pickup_email")
    private String pickupEmail;

    @SerializedName("Order_Product_Data")
    private ArrayList<Productinfo> mProductinfo;
    @SerializedName("Order_Status")
    private String mStatus;
    @SerializedName("timesloat")
    private String mTimesloat;
    @SerializedName("Order_Total")
    private String mTotal;

    @SerializedName("sign")
    private String sign;

    @SerializedName("order_id")
    private String orderid;

    @SerializedName("customer_paddress")
    private String customerPaddress;

    @SerializedName("customer_pmobile")
    private String customerPmobile;

    @SerializedName("customer_daddress")
    private String customerDaddress;

    @SerializedName("customer_dmobile")
    private String customerDmobile;

    @SerializedName("photos")
    private ArrayList<String> photos;

    @SerializedName("category")
    private String category;


    protected PendingOrderItem(Parcel in) {
        mAstatus = in.readString();
        mDCharge = in.readString();
        mDelivery = in.readString();
        mPickup = in.readString();
        mPickupname = in.readString();
        mPickupmobile = in.readString();
        mEmail = in.readString();
        mMobile = in.readString();
        mName = in.readString();
        mOdate = in.readString();
        mPMethod = in.readString();
        pickupEmail = in.readString();
        mProductinfo = in.createTypedArrayList(Productinfo.CREATOR);
        mStatus = in.readString();
        mTimesloat = in.readString();
        mTotal = in.readString();
        sign = in.readString();
        orderid = in.readString();
        customerPaddress = in.readString();
        customerPmobile = in.readString();
        customerDaddress = in.readString();
        customerDmobile = in.readString();
        photos = in.createStringArrayList();
        category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAstatus);
        dest.writeString(mDCharge);
        dest.writeString(mDelivery);
        dest.writeString(mPickup);
        dest.writeString(mPickupname);
        dest.writeString(mPickupmobile);
        dest.writeString(mEmail);
        dest.writeString(mMobile);
        dest.writeString(mName);
        dest.writeString(mOdate);
        dest.writeString(mPMethod);
        dest.writeString(pickupEmail);
        dest.writeTypedList(mProductinfo);
        dest.writeString(mStatus);
        dest.writeString(mTimesloat);
        dest.writeString(mTotal);
        dest.writeString(sign);
        dest.writeString(orderid);
        dest.writeString(customerPaddress);
        dest.writeString(customerPmobile);
        dest.writeString(customerDaddress);
        dest.writeString(customerDmobile);
        dest.writeStringList(photos);
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PendingOrderItem> CREATOR = new Creator<PendingOrderItem>() {
        @Override
        public PendingOrderItem createFromParcel(Parcel in) {
            return new PendingOrderItem(in);
        }

        @Override
        public PendingOrderItem[] newArray(int size) {
            return new PendingOrderItem[size];
        }
    };

    public String getmAstatus() {
        return mAstatus;
    }

    public void setmAstatus(String mAstatus) {
        this.mAstatus = mAstatus;
    }

    public String getmDCharge() {
        return mDCharge;
    }

    public void setmDCharge(String mDCharge) {
        this.mDCharge = mDCharge;
    }

    public String getmDelivery() {
        return mDelivery;
    }

    public void setmDelivery(String mDelivery) {
        this.mDelivery = mDelivery;
    }

    public String getmPickup() {
        return mPickup;
    }

    public void setmPickup(String mPickup) {
        this.mPickup = mPickup;
    }

    public String getmPickupname() {
        return mPickupname;
    }

    public void setmPickupname(String mPickupname) {
        this.mPickupname = mPickupname;
    }

    public String getmPickupmobile() {
        return mPickupmobile;
    }

    public void setmPickupmobile(String mPickupmobile) {
        this.mPickupmobile = mPickupmobile;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmOdate() {
        return mOdate;
    }

    public void setmOdate(String mOdate) {
        this.mOdate = mOdate;
    }

    public String getmPMethod() {
        return mPMethod;
    }

    public void setmPMethod(String mPMethod) {
        this.mPMethod = mPMethod;
    }

    public ArrayList<Productinfo> getmProductinfo() {
        return mProductinfo;
    }

    public void setmProductinfo(ArrayList<Productinfo> mProductinfo) {
        this.mProductinfo = mProductinfo;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmTimesloat() {
        return mTimesloat;
    }

    public void setmTimesloat(String mTimesloat) {
        this.mTimesloat = mTimesloat;
    }

    public String getmTotal() {
        return mTotal;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPickupEmail() {
        return pickupEmail;
    }

    public void setPickupEmail(String pickupEmail) {
        this.pickupEmail = pickupEmail;
    }


    public String getCustomerPaddress() {
        return customerPaddress;
    }

    public void setCustomerPaddress(String customerPaddress) {
        this.customerPaddress = customerPaddress;
    }

    public String getCustomerPmobile() {
        return customerPmobile;
    }

    public void setCustomerPmobile(String customerPmobile) {
        this.customerPmobile = customerPmobile;
    }

    public String getCustomerDaddress() {
        return customerDaddress;
    }

    public void setCustomerDaddress(String customerDaddress) {
        this.customerDaddress = customerDaddress;
    }

    public String getCustomerDmobile() {
        return customerDmobile;
    }

    public void setCustomerDmobile(String customerDmobile) {
        this.customerDmobile = customerDmobile;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
