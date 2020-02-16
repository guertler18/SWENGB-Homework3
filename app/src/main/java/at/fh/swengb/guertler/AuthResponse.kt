package at.fh.swengb.guertler

import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)

class AuthResponse(val token: String)
{

}