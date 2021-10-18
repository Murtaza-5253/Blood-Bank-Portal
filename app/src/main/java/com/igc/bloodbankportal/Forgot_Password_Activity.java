package com.igc.bloodbankportal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Forgot_Password_Activity extends AppCompatActivity {
    EditText txtEmail,txtMobNo,txtPassword;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String email,mobno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);
        txtEmail=findViewById(R.id.txtEmail);
        txtMobNo=findViewById(R.id.txtMobNo);
        txtPassword=findViewById(R.id.txtPassword);

        rq= Volley.newRequestQueue(Forgot_Password_Activity.this);
        pd=new ProgressDialog(Forgot_Password_Activity.this);
    }

    public void resetPassword(View view) {
        if(txtEmail.getText().toString().isEmpty())
        {
            txtEmail.setError("Please Enter The Email");
            txtEmail.requestFocus();
        }
        else if(txtMobNo.getText().toString().isEmpty())
        {
            txtMobNo.setError("Please Enter The Mobile Number");
            txtMobNo.requestFocus();
        }
        else if(txtPassword.getText().toString().isEmpty())
        {
            txtPassword.setError("Please Enter The New Password");
            txtPassword.requestFocus();
        }
        else
        {
            pd.setTitle("Please Wait..!");
            pd.setMessage("Updating Password..");
            final String url="https://kunalpatildemo.000webhostapp.com/BloodBank/validate_user.php";
            strq=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    email="no";
                    try {
                        JSONObject jsobj=new JSONObject(response);
                        JSONArray jsarray=jsobj.getJSONArray("Result");
                        for(int i=0;i<jsarray.length();i++)
                        {
                            pd.dismiss();
                            JSONObject obj=jsarray.getJSONObject(i);
                            email=obj.getString("Email");
                            mobno=obj.getString("MobNo");
                            if(email.equals(txtEmail.getText().toString().trim()) && mobno.equals(txtMobNo.getText().toString().trim()))
                            {
                                updatePassword();
                            }
                            else
                            {
                                Toast.makeText(Forgot_Password_Activity.this, "Incorrect Email Or Mobile Number..!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(email.equalsIgnoreCase("no"))
                    {
                        pd.dismiss();
                        Toast.makeText(Forgot_Password_Activity.this, "Sorry! This Email Is Not Registered With Us..", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Forgot_Password_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> mp=new HashMap<>();
                    mp.put("EMAIL",txtEmail.getText().toString().trim());
                    return mp;
                }
            };
            rq.add(strq);
        }
    }
    public void login(View view) {
        startActivity(new Intent(Forgot_Password_Activity.this,Login_Activity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Forgot_Password_Activity.this,Login_Activity.class));
        finish();
    }
    public void updatePassword()
    {
        String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/update_password.php";
        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Toast.makeText(Forgot_Password_Activity.this,"Password Updated Successfull!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Forgot_Password_Activity.this,Login_Activity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Forgot_Password_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mp=new HashMap<>();
                mp.put("EMAIL",txtEmail.getText().toString().trim());
                mp.put("PASSWORD",txtPassword.getText().toString().trim());
                return mp;
            }
        };
        rq.add(strq);
    }
}