package be.corentinvanhaeren.sandwix.data

import android.content.Context

class TokenStore(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        "sandwix_auth",
        Context.MODE_PRIVATE
    )

    fun saveSession(
        token: String,
        gebruikerId: Int,
        rol: String
    ) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_GEBRUIKER_ID, gebruikerId)
            .putString(KEY_ROL, rol)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun getGebruikerId(): Int? {
        val id = preferences.getInt(KEY_GEBRUIKER_ID, -1)
        return if (id == -1) null else id
    }

    fun getRol(): String? {
        return preferences.getString(KEY_ROL, null)
    }

    fun clearSession() {
        preferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_GEBRUIKER_ID)
            .remove(KEY_ROL)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_GEBRUIKER_ID = "gebruiker_id"
        const val KEY_ROL = "rol"
    }
}