package com.igc.bloodbankportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class Request_Activity extends AppCompatActivity {
    List<String> LSTName;
    List<String> LSTMobNo;
    List<String> LSTRName;
    List<String> LSTRMobNo;
    List<String> LSTREmail;
    List<String> LSTBloodGroup;
    List<String> LSTDate;
    List<String> LSTStatus;
    RecyclerView lstRequestList;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_layout);

        lstRequestList=findViewById(R.id.lstRequestList);

        LSTName=new ArrayList<>();
        LSTMobNo=new ArrayList<>();
        LSTRName=new ArrayList<>();
        LSTRMobNo=new ArrayList<>();
        LSTREmail=new ArrayList<>();
        LSTBloodGroup=new ArrayList<>();
        LSTDate=new ArrayList<>();
        LSTStatus=new ArrayList<>();

        rq= Volley.newRequestQueue(Request_Activity.this);
        pd=new ProgressDialog(Request_Activity.this);
        lstRequestList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
        email=sp.getString("EMAIL","Guest");

        getStatusList();
    }
    public void getStatusList()
    {
        pd.setTitle("Please Wait..!");
        pd.setMessage("Getting Status List..");
        pd.show();
        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/access_request_list.php";
        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LSTName.clear();
                LSTRMobNo.clear();
                LSTRName.clear();
                LSTRMobNo.clear();
                LSTREmail.clear();
                LSTBloodGroup.clear();
                LSTDate.clear();
                LSTStatus.clear();
                try {
                    pd.dismiss();
                    JSONObject jsobj=new JSONObject(response);
                    JSONArray jsarray=jsobj.getJSONArray("Result");
                    for(int j=0;j<jsarray.length();j++)
                    {
                        JSONObject obj=jsarray.getJSONObject(j);

                        LSTName.add(obj.getString("Name"));
                        LSTMobNo.add(obj.getString("MobNo"));
                        LSTRName.add(obj.getString("RName"));
                        LSTRMobNo.add(obj.getString("RMobNo"));
                        LSTREmail.add(obj.getString("Email"));
                        LSTDate.add(obj.getString("Date"));
                        LSTStatus.add(obj.getString("Status"));
                        LSTBloodGroup.add(obj.getString("BloodGroup"));
                    }
                    MyClass MA=new MyClass();
                    lstRequestList.setAdapter(MA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(LSTName.size()==0)
                {
                    pd.dismiss();
                    Toast.makeText(Request_Activity.this, "No Request Found..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Request_Activity.this,DashBoard_Activity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Request_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
            View v=li.inflate(R.layout.custom_request_list,parent,false);
            return new MyClass.NewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyClass.NewHolder holder, final int position) {
            holder.txtLName.setText(LSTName.get(position));
            holder.txtLDate.setText(LSTDate.get(position));
            holder.txtLBloodGroup.setText(LSTBloodGroup.get(position));

            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.setTitle("Please Wait..!");
                    pd.setMessage("Declining Request");
                    pd.show();
                    String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/decline_request.php";
                    strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Toast.makeText(Request_Activity.this,"Request Declined Successfull!",Toast.LENGTH_SHORT).show();
                            getStatusList();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(Request_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> mp=new HashMap<>();
                            mp.put("REMAIL",email);
                            mp.put("EMAIL",LSTREmail.get(position));
                            mp.put("DATE",LSTDate.get(position));
                            mp.put("BLOODGROUP",LSTBloodGroup.get(position));
                            return mp;
                        }
                    };
                    rq.add(strq);
                }
            });
            if(LSTStatus.get(position).equalsIgnoreCase("Approved"))
            {
                holder.btnAccept.setEnabled(false);
                holder.btnDecline.setVisibility(View.GONE);
                holder.btnAccept.setText("Request Approved");
            }
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.setTitle("Please Wait..!");
                    pd.setMessage("Approving Request");
                    pd.show();
                    String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/approve_request.php";
                    strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Toast.makeText(Request_Activity.this,"Request Approved Successfull!",Toast.LENGTH_SHORT).show();
                            getStatusList();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(Request_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> mp=new HashMap<>();
                            mp.put("REMAIL",email);
                            mp.put("EMAIL",LSTREmail.get(position));
                            mp.put("DATE",LSTDate.get(position));
                            mp.put("BLOODGROUP",LSTBloodGroup.get(position));
                            return mp;
                        }
                    };
                    rq.add(strq);
                }
            });
        }
        @Override
        public int getItemCount() {
            return LSTName.size();
        }
        class NewHolder extends RecyclerView.ViewHolder
        {
            TextView txtLName,txtLDate,txtLBloodGroup;
            Button btnAccept,btnDecline;
            public NewHolder(@NonNull View itemView) {
                super(itemView);
                txtLName=itemView.findViewById(R.id.txtLName);
                txtLDate=itemView.findViewById(R.id.txtLDate);
                txtLBloodGroup=itemView.findViewById(R.id.txtLBloodGroup);
                btnAccept=itemView.findViewById(R.id.btnAccept);
                btnDecline=itemView.findViewById(R.id.btnDecline);
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Request_Activity.this,DashBoard_Activity.class));
        finish();
    }
}