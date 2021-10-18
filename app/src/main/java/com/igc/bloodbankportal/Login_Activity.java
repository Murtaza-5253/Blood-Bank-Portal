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

public class Login_Activity extends AppCompatActivity {
    EditText txtEmail,txtPassword;
    String email="no",password;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        txtEmail=findViewById(R.id.txtEmail);
        txtPassword=findViewById(R.id.txtPassword);

        rq= Volley.newRequestQueue(Login_Activity.this);
        pd=new ProgressDialog(Login_Activity.this);

    }
    public void newUser(View view) {
        startActivity(new Intent(Login_Activity.this,Registration_Activity.class));
        finish();
    }

    public void login(View view) {
        if(txtEmail.getText().toString().isEmpty())
        {
            txtEmail.setError("Please Enter The Email");
            txtEmail.requestFocus();
        }
        else if(txtPassword.getText().toString().isEmpty())
        {
            txtPassword.setError("Please Enter The Email");
            txtPassword.requestFocus();
        }
        else
        {
            pd.setTitle("Please Wait..!");
            pd.setMessage("Logging you in...");
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
                            password=obj.getString("Password");
                            if(email.equals(txtEmail.getText().toString().trim()) && password.equals(txtPassword.getText().toString().trim()))
                            {
                                SharedPreferences sp = getSharedPreferences("BloodBank", MODE_PRIVATE);
                                SharedPreferences.Editor se = sp.edit();
                                se.putString("EMAIL",txtEmail.getText().toString().trim());
                                se.putString("PASSWORD",txtPassword.getText().toString().trim());
                                se.commit();
                                Toast.makeText(Login_Activity.this, "User Login SuccessFull", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login_Activity.this,DashBoard_Activity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(Login_Activity.this, "Incorrect Email Or Password..!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(email.equalsIgnoreCase("no"))
                    {
                        pd.dismiss();
                        Toast.makeText(Login_Activity.this, "Sorry! This Email Is Not Registered With Us..", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Login_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

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
    public void forgotPass(View view) {
        startActivity(new Intent(Login_Activity.this,Forgot_Password_Activity.class));
        finish();
    }
}