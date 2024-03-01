package com.turksat46.bartender;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.button.PayButton;
import com.google.android.gms.wallet.contract.TaskResultContracts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class payment extends AppCompatActivity {

    String selectedBarID;
    int tableNumber;


    private CheckoutViewModel model;

    private Button googlePayButton;

    private final ActivityResultLauncher<Task<PaymentData>> paymentDataLauncher =
            registerForActivityResult(new TaskResultContracts.GetPaymentDataResult(), result -> {
                int statusCode = result.getStatus().getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.SUCCESS:
                        handlePaymentSuccess(result.getResult());
                        break;
                    //case CommonStatusCodes.CANCELED: The user canceled
                    case AutoResolveHelper.RESULT_ERROR:
                        handleError(statusCode, result.getStatus().getStatusMessage());
                        break;
                    case CommonStatusCodes.INTERNAL_ERROR:
                        handleError(statusCode, "Unexpected non API" +
                                " exception when trying to deliver the task result to an activity!");
                        break;
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            selectedBarID = extras.getString("barID");
            tableNumber = extras.getInt("tableID");
        }

        googlePayButton = (Button) findViewById(R.id.googlepaymentbutton);
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment(view);
            }
        });

        model = new ViewModelProvider(this).get(CheckoutViewModel.class);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Bestellung wurde abgebrochen", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(payment.this, MainActivity.class);
        startActivity(intent);
    }

    public void requestPayment(View view) {
        // The price provided to the API should include taxes and shipping.
        final Task<PaymentData> task = model.getLoadPaymentDataTask(1000L);
        task.addOnCompleteListener(paymentDataLauncher::launch);
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, "Bezahlung erfolgreich!",
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));

            startActivity(new Intent(this, MainActivity.class));

        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }

    private void handleError(int statusCode, @Nullable String message) {
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }

}