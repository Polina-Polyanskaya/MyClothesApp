package com.example.clothesapp.classesForActivities

import java.util.regex.Pattern

class Errors {

    fun errorsInLogin(fieldLogin: String): String {
        if (fieldLogin.isEmpty())
            return "Логин не может быть пустым."
        if (fieldLogin.length !in 5..15)
            return "Логин не может быть длиной меньше 5 или больше 15"
        if (!Pattern.compile("^[_A-z0-9]+$").matcher(fieldLogin).matches())
            return "Логин может содержать только символы латницы, цифры и нижнее подчеркивание."
        return ""
    }

    fun errorsInPassword(fieldPassword: String): String {
        if (fieldPassword.isEmpty())
            return "Пароль не может быть пустым."
        if (fieldPassword.length !in 8..15)
            return "Пароль не может быть длиной меньше 8 или больше 15."
        if (!Pattern.compile("^[_A-z0-9]+$").matcher(fieldPassword).matches()) {
            return "Пароль может содержать только символы латиницы, цифры и нижнее подчеркивание."
        }
        return ""
    }

    fun errorsInPhone(fieldPhone: String): String {
        if (fieldPhone.length != 12)
            return "Номер телефона не может быть длиной не равной 12."
        if (!Pattern.compile("^[0-9]+$").matcher(fieldPhone.substring(3)).matches())
            return "Номер телефона может содержать только цифры и один плюс в начале."
        return ""
    }

    fun errorsInEmail(fieldEmail: String): String {
        if (fieldEmail.isEmpty())
            return "Почта не может быть пустой."
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(fieldEmail).matches())
            return "Это не адрес электронной почты."
        return ""
    }

}