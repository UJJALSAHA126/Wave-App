package com.example.wave.model

class Messages {
    var message: String = ""
    var senderName: String = ""
    var senderUID: String = ""
    var senderPhoto: String = ""
    var receiverName: String = ""
    var receiverUID: String = ""
    var receiverPhoto: String = ""
    var time: Long = 0

    constructor() {

    }

    constructor(
        message: String,
        senderName: String,
        senderUID: String,
        senderPhoto: String,
        receiverName: String,
        receiverUID: String,
        receiverPhoto: String,
        time: Long
    ) {
        this.message = message
        this.senderName = senderName
        this.senderUID = senderUID
        this.senderPhoto = senderPhoto
        this.receiverName = receiverName
        this.receiverUID = receiverUID
        this.receiverPhoto = receiverPhoto
        this.time = time
    }


    override fun equals(other: Any?): Boolean {
        val msg = other as Messages
        return (this.message == msg.message && this.time == msg.time && this.senderUID == msg.senderUID && this.receiverUID == msg.receiverUID)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + senderUID.hashCode()
        result = 31 * result + senderPhoto.hashCode()
        result = 31 * result + receiverName.hashCode()
        result = 31 * result + receiverUID.hashCode()
        result = 31 * result + receiverPhoto.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

}