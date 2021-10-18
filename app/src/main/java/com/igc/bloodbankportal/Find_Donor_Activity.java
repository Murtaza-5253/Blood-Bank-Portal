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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Find_Donor_Activity extends AppCompatActivity {
    Spinner spBloodGroup;
    List<String> LSTBGroup;
    List<String> LSTName;
    List<String> LSTBloodGroup;
    List<String> LSTMobNo;
    List<String> LSTREmail;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String validate="no",email,pincode,mobno,name,date;
    RecyclerView lstDonorList;
    EditText txtPincode;
    int j,age;
    Calendar ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_donor_layout);
        spBloodGroup=findViewById(R.id.spBloodGroup);
        lstDonorList=findViewById(R.id.lstDonorList);
        txtPincode=findViewById(R.id.txtPincode);

        LSTBGroup=new ArrayList<>();
        LSTName=new ArrayList<>();
        LSTBloodGroup=new ArrayList<>();
        LSTMobNo=new ArrayList<>();
        LSTREmail=new ArrayList<>();

        rq= Volley.newRequestQueue(Find_Donor_Activity.this);
        pd=new ProgressDialog(Find_Donor_Activity.this);
        lstDonorList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
        email=sp.getString("EMAIL","Guest");

        getSpinner();
        getDonorList();
        getCalendar();
    }
    public void getCalendar()
    {
        ca=Calendar.getInstance();
        String day=String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
        String month=String.valueOf(ca.get(Calendar.MONTH)+1);
        String year=String.valueOf(ca.get(Calendar.YEAR));
        date=day+"/"+month+"/"+year;
    }
    public void search(View view) {
        if(txtPincode.getText().toString().isEmpty())
        {
            txtPincode.setError("Please Enter City Or Pincode To Search");
            txtPincode.requestFocus();
        }
        else
        {
            pd.setTitle("Please Wait..!");
            pd.setMessage("Searching Blood Banks..");
            pd.show();
            final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/donor_list_search.php";
            strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LSTName.clear();
                    LSTBloodGroup.clear();
                    LSTREmail.clear();
                    LSTMobNo.clear();
                    try {
                        JSONObject jsobj=new JSONObject(response);
                        JSONArray jsarray=jsobj.getJSONArray("Result");
                        for(j=0;j<jsarray.length();j++)
                        {
                            pd.dismiss();
                            JSONObject obj=jsarray.getJSONObject(j);
                            if(!email.equalsIgnoreCase(obj.getString("Email")))
                            {
                                age=Integer.parseInt(obj.getString("Age"));
                                if(!email.equalsIgnoreCase(obj.getString("Email")))
                                {
                                    if(age>=18)
                                    {
                                        LSTName.add(obj.getString("Name"));
                                        LSTBloodGroup.add(obj.getString("BloodGroup"));
                                        LSTREmail.add(obj.getString("Email"));
                                        LSTMobNo.add(obj.getString("MobNo"));
                                    }
                                }
                            }
                        }
                        MyClass MA=new MyClass();
                        lstDonorList.setAdapter(MA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(LSTName.size()==0)
                    {
                        Toast.makeText(Find_Donor_Activity.this, "No Donor Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Find_Donor_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> mp=new HashMap<>();
                    mp.put("PINCODE",txtPincode.getText().toString());
                    return mp;
                }
            };
            rq.add(strq);
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Find_Donor_Activity.this,DashBoard_Activity.class));
        finish();
    }
    public void getSpinner()
    {
        LSTBGroup.add("Select Blood Group");
        LSTBGroup.add("A+");
        LSTBGroup.add("A-");
        LSTBGroup.add("B+");
        LSTBGroup.add("B-");
        LSTBGroup.add("O+");
        LSTBGroup.add("O-");
        LSTBGroup.add("AB+");
        LSTBGroup.add("AB-");

        ArrayAdapter<String> AD=new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,LSTBGroup);
        spBloodGroup.setAdapter(AD);
        spBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!LSTBGroup.get(position).equalsIgnoreCase("Select Blood Group"))
                {
                    if(txtPincode.getText().toString().isEmpty())
                    {
                        pd.setTitle("Please Wait...!");
                        pd.setMessage("Search For Donor List");
                        pd.show();
                        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/donor_list_sort.php";
                        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LSTName.clear();
                                LSTBloodGroup.clear();
                                LSTREmail.clear();
                                LSTMobNo.clear();
                                try {
                                    JSONObject jsobj=new JSONObject(response);
                                    JSONArray jsarray=jsobj.getJSONArray("Result");
                                    for(j=0;j<jsarray.length();j++)
                                    {
                                        pd.dismiss();
                                        JSONObject obj=jsarray.getJSONObject(j);
                                        age=Integer.parseInt(obj.getString("Age"));
                                        if(!email.equalsIgnoreCase(obj.getString("Email")))
                                        {
                                            if(age>=18)
                                            {
                                                LSTName.add(obj.getString("Name"));
                                                LSTBloodGroup.add(obj.getString("BloodGroup"));
                                                LSTMobNo.add(obj.getString("MobNo"));
                                                LSTREmail.add(obj.getString("Email"));
                                            }
                                        }
                                    }
                                    MyClass MA=new MyClass();
                                    lstDonorList.setAdapter(MA);
                                } catch (JSONException e) {
                                    pd.dismiss();
                                    e.printStackTrace();
                                }
                                if(LSTName.size()==0)
                                {
                                    pd.dismiss();
                                    Toast.makeText(Find_Donor_Activity.this, "No Donor Found With Group "+LSTBGroup.get(position), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(Find_Donor_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> mp=new HashMap<>();
                                mp.put("PINCODE",pincode);
                                mp.put("BLOODGROUP",LSTBGroup.get(position));
                                return mp;
                            }
                        };
                        rq.add(strq);
                    }
                    else
                    {
                        pd.setTitle("Please Wait...!");
                        pd.setMessage("Search For Donor List");
                        pd.show();
                        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/donor_list_sort.php";
                        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LSTName.clear();
                                LSTBloodGroup.clear();
                                LSTREmail.clear();
                                LSTMobNo.clear();
                                try {
                                    JSONObject jsobj=new JSONObject(response);
                                    JSONArray jsarray=jsobj.getJSONArray("Result");
                                    for(j=0;j<jsarray.length();j++)
                                    {
                                        pd.dismiss();
                                        JSONObject obj=jsarray.getJSONObject(j);
                                        age=Integer.parseInt(obj.getString("Age"));
                                        if(!email.equalsIgnoreCase(obj.getString("Email")))
                                        {
                                            if(age>=18)
                                            {
                                                LSTName.add(obj.getString("Name"));
                                                LSTBloodGroup.add(obj.getString("BloodGroup"));
                                                LSTMobNo.add(obj.getString("MobNo"));
                                                LSTREmail.add(obj.getString("Email"));
                                            }
                                        }
                                    }
                                    MyClass MA=new MyClass();
                                    lstDonorList.setAdapter(MA);
                                } catch (JSONException e) {
                                    pd.dismiss();
                                    e.printStackTrace();
                                }
                                if(LSTName.size()==0)
                                {
                                    pd.dismiss();
                                    Toast.makeText(Find_Donor_Activity.this, "No Donor Found With Group "+LSTBGroup.get(position), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(Find_Donor_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> mp=new HashMap<>();
                                mp.put("PINCODE",txtPincode.getText().toString().trim());
                                mp.put("BLOODGROUP",LSTBGroup.get(position));
                                return mp;
                            }
                        };
                        rq.add(strq);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                pd.dismiss();
            }
        });
    }
    public void getDonorList()
    {
        pd.setTitle("Please Wait..!");
        pd.setMessage("Getting Donor List In Your Nearby Area..");
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
                        JSONObject obj=jsarray.getJSONObject(i);
                        pincode=obj.getString("Pincode");
                        name=obj.getString("Name");
                        mobno=obj.getString("MobNo");
                        validate=pincode;
                        final String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/access_donor_list.php";
                        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LSTName.clear();
                                LSTBloodGroup.clear();
                                LSTREmail.clear();
                                LSTMobNo.clear();
                                try {
                                    JSONObject jsobj=new JSONObject(response);
                                    JSONArray jsarray=jsobj.getJSONArray("Result");
                                    for(j=0;j<jsarray.length();j++)
                                    {
                                        pd.dismiss();
                                        JSONObject obj=jsarray.getJSONObject(j);
                                        age=Integer.parseInt(obj.getString("Age"));
                                        if(!email.equalsIgnoreCase(obj.getString("Email")))
                                        {
                                            if(age>=18)
                                            {
                                                LSTName.add(obj.getString("Name"));
                                                LSTBloodGroup.add(obj.getString("BloodGroup"));
                                                LSTMobNo.add(obj.getString("MobNo"));
                                                LSTREmail.add(obj.getString("Email"));
                                            }
                                        }
                                    }
                                    MyClass MA=new MyClass();
                                    lstDonorList.setAdapter(MA);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(LSTName.size()==0)
                                {
                                    pd.dismiss();
                                    Toast.makeText(Find_Donor_Activity.this, "No Donor Found In Your Nearby Area", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(Find_Donor_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Find_Donor_Activity.this, "Sorry!No Donor Found In Your Area..!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Find_Donor_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

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
            View v=li.inflate(R.layout.custom_donor_list,parent,false);
            return new MyClass.NewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyClass.NewHolder holder, final int position) {
            holder.txtName.setText(LSTName.get(position));
            holder.txtBloodGroup.setText(LSTBloodGroup.get(position));
            holder.btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.setTitle("Please Wait...!");
                    pd.setMessage("Sending Request To "+LSTName.get(position));
                    pd.show();
                    String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/register_request.php";
                    strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Toast.makeText(Find_Donor_Activity.this,"Request Sent Successfull!",Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(Find_Donor_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> mp=new HashMap<>();
                            mp.put("NAME",name);
                            mp.put("MOBNO",mobno);
                            mp.put("EMAIL",email);
                            mp.put("BLOODGROUP",LSTBloodGroup.get(position));
                            mp.put("RNAME",LSTName.get(position));
                            mp.put("RMOBNO",LSTMobNo.get(position));
                            mp.put("REMAIL",LSTREmail.get(position));
                            mp.put("DATE",date);
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
            TextView txtBloodGroup,txtName;
            Button btnRequest;
            public NewHolder(@NonNull View itemView) {
                super(itemView);
                txtName=itemView.findViewById(R.id.txtName);
                txtBloodGroup=itemView.findViewById(R.id.txtBloodGroup);
                btnRequest=itemView.findViewById(R.id.btnRequest);
            }
        }
    }
}