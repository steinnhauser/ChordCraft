package com.chordcraft.utils

import com.chordcraft.data.models.Note
import org.json.JSONArray

/**
 * Utility object for parsing JSON data.
 */
object JsonParser {
    
    /**
     * Parse notes from JSON string.
     * 
     * Expected JSON format:
     * [
     *   {"note": "C4", "time": 0.0, "duration": 1.0},
     *   {"note": "D4", "time": 2.0, "duration": 1.0}
     * ]
     * 
     * @param jsonString The JSON string containing note data
     * @return List of parsed Note objects
     */
    fun parseNotes(jsonString: String): List<Note> {
        return try {
            val jsonArray = JSONArray(jsonString)
            val notes = mutableListOf<Note>()
            
            for (i in 0 until jsonArray.length()) {
                val noteObj = jsonArray.getJSONObject(i)
                val note = Note(
                    note = noteObj.getString("note"),
                    time = noteObj.getDouble("time"),
                    duration = noteObj.getDouble("duration")
                )
                notes.add(note)
            }
            
            notes
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
