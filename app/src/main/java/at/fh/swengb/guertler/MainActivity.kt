package at.fh.swengb.guertler

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
companion object
{
    val TOKEN = "TOKEN"
}
    override fun onCreate(savedInstanceState: Bundle?) 
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       
        val shared_Preferences= getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if(shared_Preferences.getString(TOKEN,null)!= null)
        {
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
        }
        else
        {
            Log.e("Token","No Token found!")
        }


        login_button.setOnClickListener {
            if(login_username.text.toString().isNotEmpty() and login_password.text.toString().isNotEmpty()) 
            {
                val authRequest =
                    AuthRequest(login_username.text.toString(), login_password.text.toString())
                login(authRequest,
                    success =
                    {
                        shared_Preferences.edit().putString(TOKEN, it.token).apply()
                        //Log.e("Token", it.token)
                        val intent = Intent(this, NoteListActivity::class.java)
                        startActivity(intent)

                    },
                    error = {
                        Log.e("Error", it)
                    }
                )
            }
            else 
            {
                Toast.makeText(this, "You need your username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login (
        request: AuthRequest,
        success: (response: AuthResponse) -> Unit,
        error: (errorMessage: String) -> Unit)
        {
             NoteApi.retrofitService.login(request).enqueue(object: retrofit2.Callback<AuthResponse>{
                 override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                 error("Login failed")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>)
            {
                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null)
                {
                    success(responseBody)
                }
                else
                {
                    error("Login failed")
                }

            }
        })
    }
}