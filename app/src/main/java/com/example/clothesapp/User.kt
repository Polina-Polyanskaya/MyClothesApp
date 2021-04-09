package com.example.clothesapp

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class User {
    var login: String? = null
        get() = field
        set(value) {
            field = value
        }
    var phone: String? = null
        get() = field
        set(value) {
            field = value
        }
    var email: String? = null
        get() = field
        set(value) {
            field = value
        }
    var password: String? = null
        get() = field
        set(value) {
            field = value
        }

    fun equals(other: User): Boolean{
        if(!this.login.equals(other.login))
            return false
        if(!this.phone.equals(other.phone))
            return false
        if(!this.email.equals(other.email))
            return false
        return true
    }

    constructor() {}
    constructor(
        _login: String?,
        _phone: String?,
        _email: String?,
        _password: String?
    ) {
        login = _login
        phone = _phone
        email = _email
        password = _password
    }

}