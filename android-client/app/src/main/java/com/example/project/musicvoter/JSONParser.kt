package com.example.project.musicvoter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


class JSONParser {

    fun parseJSON(input: String): List<Room> {
        val mapper = jacksonObjectMapper()
        val rooms = mapper.readValue<Rooms>(input)


        return rooms.rooms
    }
}