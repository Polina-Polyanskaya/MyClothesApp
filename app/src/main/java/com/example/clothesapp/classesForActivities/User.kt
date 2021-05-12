package com.example.clothesapp.classesForActivities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {

    companion object {
        var tableName: String = ""
            get() = field
            set(value) {
                field = value
            }

        var wasLoaded: Boolean = false
            get() = field
            set(value) {
                field = value
            }
    }

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

    fun equals(other: User): Boolean {
        if (this.login.equals(other.login))
            return true
        if (this.phone.equals(other.phone))
            return true
        if (this.email.equals(other.email))
            return true
        return false
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