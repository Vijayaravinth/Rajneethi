package com.software.cb.rajneethi.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.BoothAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.BoothDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

/**
 * Created by w7 on 10/8/2017.
 */

public class BoothSelectionActivity extends Util implements checkInternet.ConnectivityReceiverListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.booth_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.btn_retry)
    Button btn_retry;

    ArrayList<BoothDetails> booth_list = new ArrayList<>();
    private SharedPreferenceManager sharedPreferenceManager;
    private String TAG = "Booth Selection";

    BoothAdapter adapter;

    ArrayList<String> booth_selected_list = new ArrayList<>();

    String userid;

    @BindString(R.string.selectBooth)
    String toolbarTitle;

    IOSDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_selection);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,toolbarTitle);

        if (getIntent().getExtras() != null) {

            userid = getIntent().getExtras().getString("userid");
            Log.w(TAG, "user id :" + userid);
        }

        dialog = show_dialog(BoothSelectionActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        set_Linearlayout_manager(recyclerView, this);
        set_adapter();

        call_api();

        //getboothinfo();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    @BindString(R.string.noInternet)
    String noInternet;

    //alert dialog for delete
    public void allocate_alert_dialog(final int pos, final String booth_name, final String title, final boolean is_alocated, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkInternet.isConnected()) {

                            String url;
                            Log.w(TAG, "Booth name : " + booth_name);

                            if (is_alocated) {
                                url = API.ALLOCATE_BOOTH + userid + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&booth=" + booth_name;
                            } else {
                                url = API.DEALLOCATE_BOOTH + userid + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&booth=" + booth_name;
                            }
                            Log.w(TAG, "url :" + url);
                            // allocate_deallocate_booths(url, pos, is_alocated);
                            allocate(url, pos, is_alocated);
                        } else {
                            Toastmsg(BoothSelectionActivity.this, noInternet);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }

    private void allocate(final String url, final int pos, final boolean is_allocated) {

        dialog.show();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
               //
                dialog.dismiss();
                toast();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();
                dialog.dismiss();
                Log.w(TAG, "Response : " + myResponse);
                if (myResponse.contains("Success")) {
                    if (is_allocated) {
                        BoothDetails obj = booth_list.get(pos);
                        obj.setIs_selected(true);
                        notifyDataSetChanged();
                    } else {
                        BoothDetails obj = booth_list.get(pos);
                        obj.setIs_selected(false);
                        notifyDataSetChanged();
                    }
                }
            }
        });

    }


    private void toast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toastmsg(BoothSelectionActivity.this, Error);
            }
        });
    }
    private void notifyDataSetChanged() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /*alocate and deallocatre booths*/
    private void allocate_deallocate_booths(final String url, final int pos, final boolean is_allocated) {
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response : " + response);
                dialog.dismiss();
                if (response.contains("Success")) {
                    if (is_allocated) {
                        BoothDetails obj = booth_list.get(pos);
                        obj.setIs_selected(true);
                        adapter.notifyDataSetChanged();
                    } else {
                        BoothDetails obj = booth_list.get(pos);
                        obj.setIs_selected(false);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.w(TAG, "Error : " + error.toString());

                dialog.dismiss();
                Toastmsg(BoothSelectionActivity.this, Error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();

    }

    @BindString(R.string.Error)
    String Error;


    //call api
    private void call_api() {
        if (checkInternet.isConnected()) {
            dialog.show();
            get_booth_details();
        } else {
            show_retry();
            Toastmsg(BoothSelectionActivity.this, noInternet);
        }
    }

    @OnClick(R.id.btn_retry)
    public void retry() {
        call_api();
    }

    //set adapter
    private void set_adapter() {
        adapter = new BoothAdapter(this, booth_list, BoothSelectionActivity.this);
        recyclerView.setAdapter(adapter);
    }

    //select booth name
    public void select_booth(int pos) {
        BoothDetails obj = booth_list.get(pos);
        obj.setIs_selected(true);
        booth_selected_list.add(obj.getBooth_name());
        adapter.notifyDataSetChanged();

    }

    //remove booth
    public void remove_booth(int pos) {
        BoothDetails obj = booth_list.get(pos);
        obj.setIs_selected(false);
        adapter.notifyDataSetChanged();
        booth_selected_list.remove(obj.getBooth_name());
    }

    //show retry_button
    public void show_retry() {
        if (!btn_retry.isShown()) {
            btn_retry.setVisibility(View.VISIBLE);
        }
    }

    //hide retry button
    private void hide_retry() {
        if (btn_retry.isShown()) {
            btn_retry.setVisibility(View.GONE);
        }
    }

    /*  @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.select_booth_menu, menu);
          return super.onCreateOptionsMenu(menu);
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
              case R.id.add:
                  if (booth_selected_list.size() > 0) {
                      if (checkInternet.isConnected()) {
                          show_dialog(BoothSelectionActivity.this);
                          added_selected_booth();
                      }
                  } else {
                      Toastmsg(BoothSelectionActivity.this, "Please select booth");
                  }
                  return true;
          }
          return super.onOptionsItemSelected(item);
      }
  */


    /*get booth details*/
    private void get_booth_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.BOOTH_INFO + "userId=" + userid + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&projectId=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hide_retry();

                dialog.dismiss();
                Log.w(TAG, "Response " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            int status = object.getInt("astatus");
                            if (status == 0) {
                                booth_list.add(new BoothDetails(object.getString("booth"), false));
                            } else {
                                booth_list.add(new BoothDetails(object.getString("booth"), true));
                            }

                        }

                    }

                    if (booth_list.size() > 0) {
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());

                dialog.dismiss();
                show_retry();
                Toastmsg(BoothSelectionActivity.this, Error);

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

}
