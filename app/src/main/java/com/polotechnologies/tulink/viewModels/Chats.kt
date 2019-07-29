package com.polotechnologies.tulink.viewModels


class Chats {
    var profilePictureChats : Int? = null
    var profileNameChats : String? = null
    var lastMessageChats : String? = null
    var lastMessageChatsTime : Long? = null

    constructor()

    constructor(
        profilePictureChats: Int?,
        profileNameChats: String?,
        lastMessageChats: String?,
        lastMessageChatsTime: Long?
    ) {
        this.profilePictureChats = profilePictureChats
        this.profileNameChats = profileNameChats
        this.lastMessageChats = lastMessageChats
        this.lastMessageChatsTime = lastMessageChatsTime
    }


}