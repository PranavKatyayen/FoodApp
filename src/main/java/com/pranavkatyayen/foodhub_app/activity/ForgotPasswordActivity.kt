package com.pranavkatyayen.foodhub_app.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pranavkatyayen.foodhub_app.R
import com.pranavkatyayen.foodhub_app.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotPasswordMobileNumber: EditText
    private lateinit var forgotPasswordEmail: EditText
    lateinit var forgotPasswordButton: Button
    private lateinit var forgotPasswordBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotPasswordMobileNumber = findViewById(R.id.forgotPasswordMobileNumber)
        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail)
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
        forgotPasswordBack = findViewById(R.id.forgotPasswordBack)

        forgotPasswordBack.setOnClickListener {
            finish()
        }

        forgotPasswordButton.setOnClickListener {

            forgotPasswordButton.visibility = View.INVISIBLE

            if (forgotPasswordMobileNumber.text.isNullOrEmpty() || forgotPasswordEmail.text.isNullOrEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Fill the details First!",
                    Toast.LENGTH_SHORT
                ).show()
                forgotPasswordButton.visibility = View.VISIBLE
            } else if (forgotPasswordMobileNumber.text.length != 10) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Enter Valid Mobile Number",
                    Toast.LENGTH_SHORT
                ).show()
                forgotPasswordButton.visibility = View.VISIBLE
            } else {

                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", forgotPasswordMobileNumber.text.toString())
                jsonParams.put("email", forgotPasswordEmail.text.toString())

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(
                                Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {

                                    try {

                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {
                                            val intent = Intent(
                                                this@ForgotPasswordActivity,
                                                ResetPasswordActivity::class.java
                                            )
                                            intent.putExtra(
                                                "mobile_number",
                                                forgotPasswordMobileNumber.text.toString()
                                            )
                                            if (data.getBoolean("first_try")) {
                                                Toast.makeText(
                                                    this@ForgotPasswordActivity,
                                                    "OTP has been sent to the registered Email",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this@ForgotPasswordActivity,
                                                    "OTP has already been sent to the registered Email",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            startActivity(intent)

                                        } else {
                                            val msg = data.getString("errorMessage")
                                            Toast.makeText(
                                                this@ForgotPasswordActivity,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            forgotPasswordButton.visibility = View.VISIBLE
                                        }

                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "JSON error occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        forgotPasswordButton.visibility = View.VISIBLE
                                    }
                                },
                                Response.ErrorListener {

                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Volley error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    forgotPasswordButton.visibility = View.VISIBLE
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "6f5311403e6661"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                } else {
                    forgotPasswordButton.visibility = View.VISIBLE
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->

                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finishAffinity()

                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity((this@ForgotPasswordActivity))
                    }
                    dialog.setCancelable(false)
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}

