package com.example.project.musicvoter

import com.google.gson.internal.LinkedTreeMap

class Room() {

     var id: String =""
     var name: String =""
     var owner: String =""
     var password: String =""
     var users: String =""

    fun parse(hashMap: LinkedTreeMap<String, String>){
        id = hashMap.getValue("id")
        name = hashMap.getValue("name")
        for(user in hashMap.getValue("owner") as LinkedTreeMap<String, String>){
            owner = user.value
        }

        password = hashMap.getValue("password")
        for(user in hashMap.getValue("users") as MutableList<LinkedTreeMap<String, String>>){
            users = user.getValue("username")
        }

    }

    override fun toString(): String {
        return name
    }

}