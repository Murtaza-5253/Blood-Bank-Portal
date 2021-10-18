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

public class Status_Activity extends AppCompatActivity {
    List<String> LSTName;
    List<String> LSTMobNo;
    List<String> LSTRName;
    List<String> LSTRMobNo;
    List<String> LSTREmail;
    List<String> LSTBloodGroup;
    List<String> LSTDate;
    List<String> LSTStatus;
    RecyclerView lstStatusList;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String email;
    LinearLayout lytMain,lytDetail;
    TextView txtName,txtEmail,txtMobNo,txtBloodGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_layout);

        lstStatusList=findViewById(R.id.lstStatus);
        lytMain=findViewById(R.id.lytMain);
        lytDetail=findViewById(R.id.lytDetail);

        txtName=findViewById(R.id.txtName);
        txtEmail=findViewById(R.id.txtEmail);
        txtMobNo=findViewById(R.id.txtMobNo);
        txtBloodGroup=findViewById(R.id.txtBloodGroup);

        LSTName=new ArrayList<>();
        LSTMobNo=new ArrayList<>();
        LSTRName=new ArrayList<>();
        LSTRMobNo=new ArrayList<>();
        LSTREmail=new ArrayList<>();
        LSTBloodGroup=new ArrayList<>();
        LSTDate=new ArrayList<>();
        LSTStatus=new ArrayList<>();

        rq= Volley.newRequestQueue(Status_Activity.this);
        pd=new ProgressDialog(Status_Activity.this);
        lstStatusList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
        email=sp.getString("EMAIL","Guest");

        getStatusList();
    }
    public void getStatusList()
    {
        pd.setTitle("Please Wait..!");
        pd.setMessage("Getting Status List..");
        pd.show();
        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/access_status_list.php";
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
                        LSTREmail.add(obj.getString("REmail"));
                        LSTDate.add(obj.getString("Date"));
                        LSTStatus.add(obj.getString("Status"));
                        LSTBloodGroup.add(obj.getString("BloodGroup"));
                    }
                    MyClass MA=new MyClass();
                    lstStatusList.setAdapter(MA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(LSTName.size()==0)
                {
                    pd.dismiss();
                    Toast.makeText(Status_Activity.this, "No Status List Found..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Status_Activity.this,DashBoard_Activity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Status_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void closeLayout(View view) {
        lytDetail.setVisibility(View.GONE);
        lytMain.setVisibility(View.VISIBLE);
    }

    class MyClass extends RecyclerView.Adapter<MyClass.NewHolder>
    {
        @NonNull
        @Override
        public MyClass.NewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());
            View v=li.inflate(R.layout.custom_status_list,parent,false);
            return new MyClass.NewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyClass.NewHolder holder, final int position) {
            holder.txtLName.setText(LSTRName.get(position));
            holder.txtLDate.setText(LSTDate.get(position));
            holder.txtLBloodGroup.setText(LSTBloodGroup.get(position));
            holder.txtStatus.setText(LSTStatus.get(position));
            if(holder.txtStatus.getText().toString().equalsIgnoreCase("Pending"))
            {
                holder.txtViewDetail.setVisibility(View.GONE);
            }

            if(holder.txtStatus.getText().toString().equalsIgnoreCase("Declined"))
            {
                holder.txtStatus.setTextColor(Color.RED);
                holder.btnCancelReqeust.setVisibility(View.GONE);
                holder.txtViewDetail.setVisibility(View.GONE);
            }
            if(holder.txtStatus.getText().toString().equalsIgnoreCase("Approved"))
            {
                holder.txtStatus.setTextColor(Color.GREEN);
                holder.txtViewDetail.setVisibility(View.VISIBLE);
                holder.btnCancelReqeust.setVisibility(View.GONE);
                holder.txtViewDetail.setVisibility(View.VISIBLE);
                holder.txtViewDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lytDetail.setVisibility(View.VISIBLE);
                        lytMain.setVisibility(View.GONE);
                        txtName.setText(LSTRName.get(position));
                        txtMobNo.setText(LSTRMobNo.get(position));
                        txtEmail.setText(LSTREmail.get(position));
                        txtBloodGroup.setText(LSTBloodGroup.get(position));
                    }
                });
            }
            holder.btnCancelReqeust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.setTitle("Please Wait..!");
                    pd.setMessage("Cancelling Your Request");
                    pd.show();
                    String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/delete_status_list.php";
                    strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Toast.makeText(Status_Activity.this,"Request Cancelled Successfull!",Toast.LENGTH_SHORT).show();
                            getStatusList();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(Status_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> mp=new HashMap<>();
                            mp.put("EMAIL",email);
                            mp.put("REMAIL",LSTREmail.get(position));
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
            TextView txtLName,txtLDate,txtLBloodGroup,txtViewDetail,txtStatus;
            Button btnCancelReqeust;
            public NewHolder(@NonNull View itemView) {
                super(itemView);
                txtLName=itemView.findViewById(R.id.txtLName);
                txtLDate=itemView.findViewById(R.id.txtLDate);
                txtLBloodGroup=itemView.findViewById(R.id.txtLBloodGroup);
                txtViewDetail=itemView.findViewById(R.id.txtViewDetail);
                txtStatus=itemView.findViewById(R.id.txtStatus);
                btnCancelReqeust=itemView.findViewById(R.id.btnCancelRequest);
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Status_Activity.this,DashBoard_Activity.class));
        finish();
    }
}