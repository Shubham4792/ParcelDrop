package com.example.shubhampandey.parceldrop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shubhampandey.parceldrop.R;
import com.example.shubhampandey.parceldrop.util.DeliveryMethod;
import com.example.shubhampandey.parceldrop.util.FetchPickupRates;
import com.example.shubhampandey.parceldrop.util.Parcel;
import com.example.shubhampandey.parceldrop.util.ParcelType;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends BaseActivity implements View.OnClickListener {
    protected int total = 0;
    private Parcel parcel;
    protected MyTask myTask;
    private int deliveryCharge = 0;
    private int parcelTypeCharge = 0;
    private DeliveryMethod deliveryMethod;
    private double distance;
    private EditText weightEdit;
    private TextView costDisplay;
    private ParcelType parcelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weightEdit = (EditText) findViewById(R.id.weight_edit);
        TextView sameDay = (TextView) findViewById(R.id.same_day_cta);
        TextView standard = (TextView) findViewById(R.id.standard_cta);
        TextView flexible = (TextView) findViewById(R.id.flexible_cta);
        TextView food = (TextView) findViewById(R.id.food_cta);
        TextView docs = (TextView) findViewById(R.id.doc_cta);
        TextView other = (TextView) findViewById(R.id.other_cta);
        costDisplay = (TextView) findViewById(R.id.cost_display);
        TextView confirmCTA = (TextView) findViewById(R.id.confirm_cta);
        TextView cancelCTA = (TextView) findViewById(R.id.cancel_cta);
        TextView pickupDisplay = (TextView) findViewById(R.id.pickup_display);
        TextView dropDisplay = (TextView) findViewById(R.id.drop_display);
        TextView valueDist = (TextView) findViewById(R.id.distance_value);
        Intent i = getIntent();
        String pickup = i.getStringExtra(getString(R.string.pickup_extra));
        String drop = i.getStringExtra(getString(R.string.drop_extra));
        distance = i.getDoubleExtra(getString(R.string.distance), 0.0);
        distance = (double) Math.round(distance * 100d) / 100d;
        String distanceText = distance + getString(R.string.km);
        valueDist.setText(distanceText);
        parcel = new Parcel();
        deliveryMethod = new DeliveryMethod();
        parcelType = new ParcelType();
        String pickupText = getString(R.string.pickup) + pickup;
        pickupDisplay.setText(pickupText);
        String dropText = getString(R.string.drop) + drop;
        dropDisplay.setText(dropText);
        food.setOnClickListener(this);
        other.setOnClickListener(this);
        docs.setOnClickListener(this);
        sameDay.setOnClickListener(this);
        standard.setOnClickListener(this);
        flexible.setOnClickListener(this);
        confirmCTA.setOnClickListener(this);
        cancelCTA.setOnClickListener(this);
        weightEdit.setText(R.string.default_weight);
        myTask = new MyTask();
        if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
            myTask.execute(weightEdit.getText().toString());
        }

    }

    @Override
    protected int getContentPageLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.food_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                parcelType.setParcelType(ParcelType.FOOD);
                parcelTypeCharge = parcelType.getAcceptedCharge(ParcelType.FOOD);
                showErrorNotification(getString(R.string.parcel_type_selected_as) + ParcelType.FOOD + getString(R.string.fee_rs) + parcelTypeCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }

        if (v.getId() == R.id.other_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                parcelType.setParcelType(ParcelType.OTHERS);
                parcelTypeCharge = parcelType.getAcceptedCharge(ParcelType.OTHERS);
                showErrorNotification(getString(R.string.parcel_type_selected_as) + ParcelType.OTHERS + getString(R.string.fee_rs) + parcelTypeCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }

        if (v.getId() == R.id.doc_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                parcelType.setParcelType(ParcelType.DOC);
                parcelTypeCharge = parcelType.getAcceptedCharge(ParcelType.DOC);
                showErrorNotification(getString(R.string.parcel_type_selected_as) + ParcelType.DOC + getString(R.string.fee_rs) + parcelTypeCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }
        if (v.getId() == R.id.same_day_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                deliveryMethod.setDelMode(DeliveryMethod.SAME_DAY);
                deliveryCharge = deliveryMethod.getAcceptedCharge(DeliveryMethod.SAME_DAY);
                showErrorNotification(getString(R.string.delivery_mode_selected_as) + DeliveryMethod.SAME_DAY + getString(R.string.fee_rs) + deliveryCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }

        if (v.getId() == R.id.standard_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                deliveryMethod.setDelMode(DeliveryMethod.STANDARD);
                deliveryCharge = deliveryMethod.getAcceptedCharge(DeliveryMethod.STANDARD);
                showErrorNotification(getString(R.string.delivery_mode_selected_as) + DeliveryMethod.STANDARD + getString(R.string.fee_rs) + deliveryCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }
        if (v.getId() == R.id.flexible_cta) {
            if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
                deliveryMethod.setDelMode(DeliveryMethod.FLEXI);
                deliveryCharge = deliveryMethod.getAcceptedCharge(DeliveryMethod.FLEXI);
                showErrorNotification(getString(R.string.delivery_mode_selected_as) + DeliveryMethod.FLEXI + getString(R.string.fee_rs) + deliveryCharge, false);
            } else {
                showErrorNotification(getString(R.string.enter_parcel_weight), true);
            }
        }
        if (v.getId() == R.id.confirm_cta) {
            if (deliveryMethod.getDelMode() != null && !deliveryMethod.getDelMode().equals("") && parcelType.getParcelType() != null && !parcelType.getParcelType().equals("")) {
                total += deliveryCharge + parcelTypeCharge;
                parcel.setTotalPrice(total);
                String cost = getString(R.string.cost_rs) + total;
                costDisplay.setText(cost);
                createAndShowConfirmationDialog();
            } else {
                showErrorNotification(getString(R.string.ensure_correct_types), true);
            }
        }

        if (v.getId() == R.id.cancel_cta) {
            Intent i = new Intent(DetailActivity.this, AddressActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void createAndShowConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.order_confirm);
        builder.setCancelable(true);
        builder.setMessage(R.string.confirm_order);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(DetailActivity.this, ThanksActivity.class);
                startActivity(i);
                finish();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                total = total - deliveryCharge - parcelTypeCharge;
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void getUpdates() {
        if (!TextUtils.isEmpty(weightEdit.getText().toString())) {
            if (deliveryMethod.getDelMode() == null || deliveryMethod.getDelMode().equals("")) {
                showErrorNotification(getString(R.string.choose_delivery_method), true);
            }
            if (parcelType.getParcelType() == null || parcelType.getParcelType().equals("")) {
                showErrorNotification(getString(R.string.choose_parcel_type), true);
            }
        } else {
            showErrorNotification(getString(R.string.enter_parcel_weight), true);
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {
        boolean noResponse = false;

        @Override
        protected String doInBackground(String... params) {
            int x = Integer.parseInt(params[0]);
            total = 0;
            final JSONObject json = FetchPickupRates.getJSON(x);
            if (json == null) {
                noResponse = true;
            } else {
                try {
                    parcel.setDistance(distance);
                    parcel.setPerKmCharge(json.getInt("per_km_charge"));
                    deliveryMethod.setSameDayCharge(json.getJSONObject("delivery_method").getInt("same_day"));
                    deliveryMethod.setFlexiCharge(json.getJSONObject("delivery_method").getInt("flexible"));
                    deliveryMethod.setStandardCharge(json.getJSONObject("delivery_method").getInt("standard"));
                    parcelType.setDocCharge(json.getJSONObject("type").getInt("documents"));
                    parcelType.setFoodCharge(json.getJSONObject("type").getInt("food_item"));
                    parcelType.setOtherCharge(json.getJSONObject("type").getInt("other"));
                    parcel.setWeightCharge(Integer.parseInt(json.getJSONObject("weight").getString(parcel.getWeight() + " grams")));
                    parcel.setDeliveryMethod(deliveryMethod);
                    parcel.setType(parcelType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (noResponse) {
                showErrorNotification(getString(R.string.server_down), true);
            } else {
                total = (int) (total + (distance * parcel.getPerKmCharge()) + parcel.getWeightCharge());
                String costTemp = getString(R.string.cost_rs) + total + getString(R.string.without_delivery_and_parcel_charges);
                costDisplay.setText(costTemp);
                getUpdates();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
