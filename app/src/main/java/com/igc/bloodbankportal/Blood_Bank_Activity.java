package com.igc.bloodbankportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blood_Bank_Activity extends AppCompatActivity {
    TextView txtBName,txtMobNo,txtEmail,txtMName,txtPincode,txtState,txtCity,txtAddress,txtLandmark;
    List<String> LSTBName;
    List<String> LSTMobNo;
    List<String> LSTEmail;
    List<String> LSTMName;
    List<String> LSTPincode;
    List<String> LSTState;
    List<String> LSTCity;
    List<String> LSTAddress;
    List<String> LSTLandmark;
    LinearLayout lytDetail,lytMain;
    EditText txtSearch;
    RecyclerView lstBankList;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String email,validate="no",pincode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_bank_layout);

        txtBName=findViewById(R.id.txtBName);
        txtMobNo=findViewById(R.id.txtMobNo);
        txtEmail=findViewById(R.id.txtEmail);
        txtMName=findViewById(R.id.txtMName);
        txtPincode=findViewById(R.id.txtPincode);
        txtCity=findViewById(R.id.txtCity);
        txtAddress=findViewById(R.id.txtAdress);
        txtState=findViewById(R.id.txtState);
        txtLandmark=findViewById(R.id.txtLandmark);
        txtSearch=findViewById(R.id.txtSearch);

        lytDetail=findViewById(R.id.lytDetail);
        lytMain=findViewById(R.id.lytMain);

        LSTBName=new ArrayList<>();
        LSTMobNo=new ArrayList<>();
        LSTEmail=new ArrayList<>();
        LSTMName=new ArrayList<>();
        LSTPincode=new ArrayList<>();
        LSTState=new ArrayList<>();
        LSTCity=new ArrayList<>();
        LSTAddress=new ArrayList<>();
        LSTLandmark=new ArrayList<>();
        lstBankList=findViewById(R.id.lstBankList);

        rq= Volley.newRequestQueue(Blood_Bank_Activity.this);
        pd=new ProgressDialog(Blood_Bank_Activity.this);
        lstBankList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
        email=sp.getString("EMAIL","Guest");

        getBankList();
    }
    public void closeLayout(View view) {
        lytMain.setVisibility(View.VISIBLE);
        lytDetail.setVisibility(View.GONE);
    }

    public void search(View view) {
        if(txtSearch.getText().toString().isEmpty())
        {
            txtSearch.setError("Please Enter Pincode Or City To Search");
            txtSearch.requestFocus();
        }
        else
        {
            pd.setTitle("Please Wait..!");
            pd.setMessage("Searching Blood Banks..");
            pd.show();
            final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/bank_list_search.php";
            strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LSTBName.clear();
                    LSTMobNo.clear();
                    LSTEmail.clear();
                    LSTMName.clear();
                    LSTPincode.clear();
                    LSTState.clear();
                    LSTCity.clear();
                    LSTAddress.clear();
                    LSTLandmark.clear();
                    try {
                        pd.dismiss();
                        JSONObject jsobj=new JSONObject(response);
                        JSONArray jsarray=jsobj.getJSONArray("Result");
                        for(int j=0;j<jsarray.length();j++)
                        {
                            JSONObject obj=jsarray.getJSONObject(j);

                            LSTBName.add(obj.getString("BName"));
                            LSTMobNo.add(obj.getString("MobNo"));
                            LSTEmail.add(obj.getString("Email"));
                            LSTPincode.add(obj.getString("Pincode"));
                            LSTState.add(obj.getString("State"));
                            LSTCity.add(obj.getString("City"));
                            LSTAddress.add(obj.getString("Address"));
                            LSTLandmark.add(obj.getString("Landmark"));
                            LSTMName.add(obj.getString("MName"));
                        }
                        MyClass MA=new MyClass();
                        lstBankList.setAdapter(MA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(LSTBName.size()==0)
                    {
                        pd.dismiss();
                        Toast.makeText(Blood_Bank_Activity.this, "No Bank List Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Blood_Bank_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> mp=new HashMap<>();
                    mp.put("PINCODE",txtSearch.getText().toString().trim());
                    return mp;
                }
            };
            rq.add(strq);
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Blood_Bank_Activity.this,DashBoard_Activity.class));
        finish();
    }
    public void getBankList()
    {
        pd.setTitle("Please Wait..!");
        pd.setMessage("Getting Blood Bank List In Your Nearby Area..");
        pd.show();
        final String url="https://kunalpatildemo.000webhostapp.com/BloodBank/access_profile.php";
        strq=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                validate="no";
                try {
                    JSONObject jsobj=new JSONObject(response);
                    JSONArray jsarray=jsobj.getJSONArray("Result");
                    for(int i=0;i<jsarray.length();i++)
                    {
                        pd.dismiss();
                        JSONObject obj=jsarray.getJSONObject(i);
                        pincode=obj.getString("Pincode");
                        validate=pincode;
                        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/access_bank_list.php";
                        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LSTBName.clear();
                                LSTMobNo.clear();
                                LSTEmail.clear();
                                LSTMName.clear();
                                LSTPincode.clear();
                                LSTState.clear();
                                LSTCity.clear();
                                LSTAddress.clear();
                                LSTLandmark.clear();
                                try {
                                    JSONObject jsobj=new JSONObject(response);
                                    JSONArray jsarray=jsobj.getJSONArray("Result");
                                    for(int j=0;j<jsarray.length();j++)
                                    {
                                        pd.dismiss();
                                        JSONObject obj=jsarray.getJSONObject(j);
                                        LSTBName.add(obj.getString("BName"));
                                        LSTMobNo.add(obj.getString("MobNo"));
                                        LSTEmail.add(obj.getString("Email"));
                                        LSTPincode.add(obj.getString("Pincode"));
                                        LSTState.add(obj.getString("State"));
                                        LSTCity.add(obj.getString("City"));
                                        LSTAddress.add(obj.getString("Address"));
                                        LSTLandmark.add(obj.getString("Landmark"));
                                        LSTMName.add(obj.getString("MName"));
                                    }
                                    MyClass MA=new MyClass();
                                    lstBankList.setAdapter(MA);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(LSTBName.size()==0)
                                {
                                    pd.dismiss();
                                    Toast.makeText(Blood_Bank_Activity.this, "No Blood Bank In Your Nearby Area", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(Blood_Bank_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> mp=new HashMap<>();
                                mp.put("PINCODE",pincode);
                                return mp;
                            }
                        };
                        rq.add(strq);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(validate.equalsIgnoreCase("no"))
                {
                    pd.dismiss();
                    Toast.makeText(Blood_Bank_Activity.this, "Sorry!No Blood Bank Found In Your Area..!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Blood_Bank_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mp=new HashMap<>();
                mp.put("EMAIL",email);
                return mp;
            }
        };
        rq.add(strq);
    }
    class MyClass extends RecyclerView.Adapter<MyClass.NewHolder>
    {
        @NonNull
        @Override
        public MyClass.NewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());
            View v=li.inflate(R.layout.custom_bank_list,parent,false);
            return new MyClass.NewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyClass.NewHolder holder, final int position) {
            holder.txtLBName.setText(LSTBName.get(position));
            holder.txtLMobNo.setText(LSTMobNo.get(position));
            holder.txtLEmail.setText(LSTEmail.get(position));
            holder.txtLMName.setText(LSTMName.get(position));
            holder.txtLPincode.setText(LSTPincode.get(position));

            holder.txtViewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lytDetail.setVisibility(View.VISIBLE);
                    lytMain.setVisibility(View.GONE);

                    txtBName.setText(LSTBName.get(position));
                    txtMobNo.setText(LSTMobNo.get(position));
                    txtEmail.setText(LSTEmail.get(position));
                    txtMName.setText(LSTMName.get(position));
                    txtPincode.setText(LSTPincode.get(position));
                    txtState.setText(LSTState.get(position));
                    txtCity.setText(LSTCity.get(position));
                    txtAddress.setText(LSTAddress.get(position));
                    txtLandmark.setText(LSTLandmark.get(position));
                }
            });
        }
        @Override
        public int getItemCount() {
            return LSTBName.size();
        }

        class NewHolder extends RecyclerView.ViewHolder
        {
            TextView txtLBName,txtLMobNo,txtLEmail,txtLMName,txtLPincode,txtViewDetail;
            public NewHolder(@NonNull View itemView) {
                super(itemView);
                txtLBName=itemView.findViewById(R.id.txtBankName);
                txtLMobNo=itemView.findViewById(R.id.txtLMobNo);
                txtLEmail=itemView.findViewById(R.id.txtLEmail);
                txtLMName=itemView.findViewById(R.id.txtLMName);
                txtLPincode=itemView.findViewById(R.id.txtLPincode);
                txtViewDetail=itemView.findViewById(R.id.txtViewDetail);
            }
        }
    }
}