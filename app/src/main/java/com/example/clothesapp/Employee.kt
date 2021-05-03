package com.example.clothesapp

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Employee {
    var login: String? = null
        get() = field
        set(value) {
            field = value
        }

    var password: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor() {}
    constructor(
        _login: String?,
        _password: String?
    ) {
        login = _login
        password = _password
    }
}