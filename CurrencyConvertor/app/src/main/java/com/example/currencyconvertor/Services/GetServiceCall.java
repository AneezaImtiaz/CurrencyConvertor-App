package com.example.currencyconvertor.Services;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.currencyconvertor.Components.Constant;
import com.example.currencyconvertor.R;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.NoSuchElementException;

public abstract class GetServiceCall extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public class APICall extends AsyncTask<String, Void, String> {

        private boolean showLoader;
        private String errorMsgAPI = "";
        private String status = "";
        private Context ctx;
        private TransparentProgressDialog mProgress;
        String inputLine, result;

        public APICall(boolean showLoader, Context ctx) {
            this.showLoader = showLoader;
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (showLoader) {
                mProgress = new TransparentProgressDialog(ctx);
                mProgress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mProgress.setCancelable(showLoader);
                mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(false);
                    }
                });
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String responseData = "";

            if (!haveNetworkConnection(ctx)) {
                errorMsgAPI = getString(R.string.please_check_internet);
                return responseData;
            }
            errorMsgAPI = "";
            try {
                responseData = requestWebService(Constant.BASE_URL + params[0]);
            } catch (Exception ex) {
                errorMsgAPI = getString(R.string.error);
                return responseData;
            }

            JSONObject parent;
            try {
                parent = new JSONObject(responseData);
                status = parent.getString("base");

                if (status.equalsIgnoreCase("fail")) {
                    String message = parent.getString("error");
                    errorMsgAPI = message;
                    if (message.equals("")) {
                        message = " ";
                    }
                    if (errorMsgAPI.equals("User not activated")) {
                        status = "true";
                    } else {
                        return message;
                    }
                }

            } catch (Exception e) {
                errorMsgAPI = getString(R.string.error);
                e.printStackTrace();
                e.getMessage();
            }
            return responseData;

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            try {
                if (showLoader) {
                    if (mProgress != null && mProgress.isShowing()) mProgress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (status.equalsIgnoreCase("EUR")) {
                    apiCallResponse(new JSONObject(result),showLoader);
                } else {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GetServiceCall.this);
                                builder.setTitle(getString(R.string.alert_title));
                                builder.setMessage(errorMsgAPI);
                                builder.setCancelable(false);
                                String ok = getString(R.string.OK);
                                builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                                builder.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /********************************
         * Check Internet Connection
         ********************************/
        private boolean haveNetworkConnection(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /********************************
         * Web Service Connection
         ********************************/
        private String requestWebService(String serviceUrl) {
            disableConnectionReuseIfNecessary();

            HttpURLConnection urlConnection = null;
            try {

                // create connection
                URL urlToRequest = new URL(serviceUrl);
                urlConnection = (HttpURLConnection) urlToRequest.openConnection();
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(31000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());

                //Create a new buffered reader and String Builder
                int statusCode = urlConnection.getResponseCode();
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                return result;

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("", "MalformedURLException URL is not valid!");
                // URL is invalid
            } catch (SocketTimeoutException e) {

                Log.d("", "SocketTimeoutException Please try again!");
                // data retrieval or connection timed out
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("", "IOException response body is not readable!");
                // could not read response body
                // (could not create input stream)
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.d("", "NullPointerException response body is no valid JSON string!");
                // response body is no valid JSON string
            } catch (NoSuchElementException e) {
                Log.d("", "NullPointerException response body is no valid JSON string!");
                e.printStackTrace();
                // response body is no valid JSON string
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }


        /*****************************************************************
         * Required in order to prevent issues in earlier Android version.
         ******************************************************************/
        private void disableConnectionReuseIfNecessary() {
            // see HttpURLConnection API doc
            if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }
        }

    }
    public abstract void apiCallResponse(JSONObject obj, boolean loader);

    /********************************
     * Progress Dialog
     ********************************/
    public class TransparentProgressDialog extends Dialog {

        Context mCtx;
        Handler h;

        private TransparentProgressDialog(Context context) {
            super(context);
            h = new Handler();
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            mCtx = context;
            setContentView(R.layout.progress_dialog);
            AVLoadingIndicatorView av = findViewById(R.id.avi_loader);
            av.setIndicatorColor(ContextCompat.getColor(context, R.color.Black));
        }

        @Override
        public void show() {
            super.show();
        }
    }

}
