package core.Models

import java.io.Serializable

class Channel : Serializable {
    var id: String? = null
    var member: Member? = null
    var admin: Member? = null
    var message: Message? = null
    var active: Boolean = true
    var read: Boolean = false

    constructor()

    constructor(
        id: String?,
        member: Member?,
        admin: Member?,
        message: Message?,
        active: Boolean,
        read: Boolean
    ) {
        this.id = id
        this.member = member
        this.admin = admin
        this.message = message
        this.active = active
        this.read = read
    }
}

class Member : Serializable {
    var id: String? = null
    var name: String? = null
    var type: String? = null

    constructor()

    constructor(id: String?, name: String?, type: String?) {
        this.id = id
        this.name = name
        this.type = type
    }
}