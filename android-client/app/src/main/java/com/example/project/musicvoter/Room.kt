package com.example.project.musicvoter

import com.google.gson.internal.LinkedTreeMap

data class Room(val id: String, val name: String, val owner: User, val password: String?, val users: List<User>, val votes: MutableMap<String, List<User>>) {



    override fun toString(): String {
        return name
    }

}