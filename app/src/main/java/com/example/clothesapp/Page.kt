package com.example.clothesapp

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Page {
    var path: String? = null
        get() = field
        set(value) {
            field = value
        }

    var type: String? = null
        get() = field
        set(value) {
            field = value
        }

    var comment: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor() {}
    constructor(
        _path: String?,
        _type: String?,
        _comment: String?
    ) {
        path = _path
        type = _type
        comment = _comment
    }

}
