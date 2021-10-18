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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration_Activity extends AppCompatActivity {
   TextInputEditText txtName,txtMobNo,txtEmail,txtPincode,txtState,txtCity,txtAddress,txtLandmark,txtBloodGroup,txtWeight,txtAge,txtGender,txtPassword;
    ProgressDialog pd;
    RequestQueue rq;
    StringRequest strq;
    String validate="no";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        txtEmail=findViewById(R.id.txtEmail);
        txtMobNo=findViewById(R.id.txtMobNo);
        txtPassword=findViewById(R.id.txtPassword);
        txtName=findViewById(R.id.txtName);
        txtPincode=findViewById(R.id.txtPincode);
        txtState=findViewById(R.id.txtState);
        txtCity=findViewById(R.id.txtCity);
        txtAddress=findViewById(R.id.txtAdress);
        txtLandmark=findViewById(R.id.txtLandmark);
        txtBloodGroup=findViewById(R.id.txtBloodGroup);
        txtWeight=findViewById(R.id.txtWeight);
        txtAge=findViewById(R.id.txtAge);
        txtGender=findViewById(R.id.txtGender);

        rq= Volley.newRequestQueue(Registration_Activity.this);
        pd=new ProgressDialog(Registration_Activity.this);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Registration_Activity.this,Login_Activity.class));
        finish();
    }

    public void register(View view) {
        if(txtName.getText().toString().isEmpty())
        {
            txtName.setError("Please Enter The Name");
            txtName.requestFocus();
        }
        else if(txtEmail.getText().toString().isEmpty())
        {
            txtEmail.setError("Please Enter The Email");
            txtEmail.requestFocus();
        }
        else if(txtMobNo.getText().toString().isEmpty())
        {
            txtMobNo.setError("Please Enter The Mobile Number");
            txtMobNo.requestFocus();
        }
        else if(txtPincode.getText().toString().isEmpty())
        {
            txtPincode.setError("Please Enter The Pincode");
            txtPincode.requestFocus();
        }
        else if(txtState.getText().toString().isEmpty())
        {
            txtState.setError("Please Enter The State");
            txtState.requestFocus();
        }
        else if(txtCity.getText().toString().isEmpty())
        {
            txtCity.setError("Please Enter The City");
            txtCity.requestFocus();
        }
        else if(txtAddress.getText().toString().isEmpty())
        {
            txtAddress.setError("Please Enter The Address");
            txtAddress.requestFocus();
        }
        else if(txtLandmark.getText().toString().isEmpty())
        {
            txtLandmark.setError("Please Enter The Landmark");
            txtLandmark.requestFocus();
        }
        else if(txtAge.getText().toString().isEmpty())
        {
            txtAge.setError("Please Enter The Age");
            txtAge.requestFocus();
        }
        else if(txtGender.getText().toString().isEmpty())
        {
            txtGender.setError("Please Enter The Gender");
            txtGender.requestFocus();
        }
        else if(txtWeight.getText().toString().isEmpty())
        {
            txtWeight.setError("Please Enter The Weight");
            txtWeight.requestFocus();
        }
        else if(txtPassword.getText().toString().isEmpty())
        {
            txtPassword.setError("Please Enter The Password");
            txtPassword.requestFocus();
        }
        else
        {
            if(txtMobNo.getText().toString().length()>10)
            {
                txtMobNo.setError("Please Enter Valid Mobile Number");
                txtMobNo.requestFocus();
            }
            else if(txtPincode.getText().toString().length()>6)
            {
                txtPincode.setError("Please Enter Valid Pincode");
                txtPincode.requestFocus();
            }
            else
            {
                pd.setTitle("Please Wait...!");
                pd.setMessage("Registering Detail...");
                pd.show();
                final String url="https://kunalpatildemo.000webhostapp.com/BloodBank/validate_user.php";
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
                                validate=obj.getString("Email");
                                pd.dismiss();
                                Toast.makeText(Registration_Activity.this, "This Email Is Already Registered With Us!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(validate.equalsIgnoreCase("no"))
                        {
                            registerUser();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(Registration_Activity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

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
    }
    public void registerUser()
    {
        String url1="https://kunalpatildemo.000webhostapp.com/BloodBank/user_registration.php";
        strq=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Toast.makeText(Registration_Activity.this,"Registration Successfull!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registration_Activity.this,Login_Activity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Registration_Activity.this,"Error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mp=new HashMap<>();
                mp.put("NAME",txtName.getText().toString().trim());
                mp.put("MOBNO",txtMobNo.getText().toString().trim());
                mp.put("EMAIL",txtEmail.getText().toString().trim());
                mp.put("PINCODE",txtPincode.getText().toString().trim());
                mp.put("STATE",txtState.getText().toString().trim());
                mp.put("CITY",txtCity.getText().toString().trim());
                mp.put("ADDRESS",txtAddress.getText().toString().trim());
                mp.put("LANDMARK",txtLandmark.getText().toString().trim());
                mp.put("GENDER",txtGender.getText().toString().trim());
                mp.put("AGE",txtAge.getText().toString().trim());
                mp.put("BLOODGROUP",txtBloodGroup.getText().toString().trim());
                mp.put("WEIGHT",txtWeight.getText().toString().trim());
                mp.put("PASSWORD",txtPassword.getText().toString().trim());
                return mp;
            }
        };
        rq.add(strq);
    }
}