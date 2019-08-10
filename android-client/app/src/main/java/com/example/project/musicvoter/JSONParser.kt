package com.example.project.musicvoter

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken



class JSONParser {
    fun parseJSON(input: String): MutableList<Room> {
        val roomsToBePassed = mutableListOf<Room>()
        val retMap = Gson().fromJson(input, object : TypeToken<HashMap<String, Any>>() {}.type) as HashMap<String, String>
        for(hashMap in retMap){
            val rooms = hashMap.value as MutableList<LinkedTreeMap<String, String>>
            println(rooms)
        for (room in rooms){
            var newRoom = Room()
            newRoom.parse(room)
            roomsToBePassed.add(newRoom)
        }
        }

        return roomsToBePassed

    }
}