package com.example.wave.model

class User {
    var uid: String = ""
    var name: String = ""
    var dob: String = ""
    var phone: String = ""
    var email: String = ""
    var photo: String = ""
    var about: String = ""

    constructor() {

    }

    constructor(
        uid: String,
        name: String,
        DOB: String,
        phone: String,
        email: String,
        Photo: String,
        about: String
    ) {
        this.uid = uid
        this.name = name
        this.dob = DOB
        this.phone = phone
        this.email = email
        this.photo = Photo
        this.about = about
    }
}
