package controllers

import models.Note
import persistence.Serializer

//pass serializer object from NoteAPI to Main.kt
class NoteAPI(serializerType: Serializer){

    private var serializer: Serializer = serializerType

    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean {
        return notes.add(note)
    }

    //delete functionality
    fun deleteNote(indexToDelete: Int): Note? {
        return if (isValidListIndex(indexToDelete, notes)) {
            notes.removeAt(indexToDelete)
        } else null
    }

    //update functionality
    fun updateNote(indexToUpdate: Int, note: Note?): Boolean {
        //find the note object by the index number
        val foundNote = findNote(indexToUpdate)

        //if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if ((foundNote != null) && (note != null)) {
            foundNote.noteTitle = note.noteTitle
            foundNote.notePriority = note.notePriority
            foundNote.noteCategory = note.noteCategory
            return true
        }

        //if the note was not found, return false, indicating that the update was not successful
        return false
    }

    //archiveNote functionality
    fun archiveNote(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val noteToArchive = notes[indexToArchive]
            if (!noteToArchive.isNoteArchived) {
                noteToArchive.isNoteArchived = true
                return true
            }
        }
        return false
    }

    //listallactivenotes funtionality
    fun listActiveNotes(): String =
        if  (numberOfActiveNotes() == 0)  "No active notes stored"
        else formatListString(notes.filter { note -> !note.isNoteArchived})
    fun listAllNotes(): String =
        if  (notes.isEmpty()) "No notes stored"
        else formatListString(notes)

    fun listArchivedNotes(): String =
        if  (numberOfArchivedNotes() == 0) "No archived notes stored"
        else formatListString(notes.filter { note -> note.isNoteArchived})



    fun listNotesBySelectedPriority(priority: Int): String =
        //if notes is empty "no notes stored" is returned
        if (notes.isEmpty()) "No notes stored"
        else {
            val listOfNotes = formatListString(notes.filter{ note -> note.notePriority == priority})
            if (listOfNotes.equals("")) "No notes with priority: $priority"
            else "${numberOfNotesByPriority(priority)} notes with priority $priority: $listOfNotes"
        }

    fun listNotesBySelectedCategory(cat: String): String =
        if (notes.isEmpty()) "No Notes Stored"
    else {
        val listOfNotes = formatListString(notes.filter{ note -> note.noteCategory == cat})
            if (listOfNotes.equals("")) "No notes with category: $cat"
            else "${numberOfNotesByCategory(cat)} notes with category $cat: $listOfNotes"
        }
    fun searchByTitle (searchString : String) =
        formatListString(
            notes.filter { note -> note.noteTitle.contains(searchString, ignoreCase = true) })
    fun numberOfNotes(): Int = notes.size

    fun numberOfArchivedNotes(): Int = notes.count { note: Note -> note.isNoteArchived }

    fun numberOfActiveNotes(): Int = notes.count{note: Note -> !note.isNoteArchived}

    fun numberOfNotesByPriority(priority: Int): Int = notes.count { p: Note -> p.notePriority == priority }
    fun numberOfNotesByCategory(cat: String): Int = notes.count { p: Note -> p.noteCategory == cat }

    fun findNote(index: Int): Note? {
        return if (isValidListIndex(index, notes)) {
            notes[index]
        } else null
    }

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, notes);
    }

    @Throws(Exception::class)
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }


//joinString becomes helper code
    private fun formatListString(notesToFormat : List<Note>) : String =
        notesToFormat
            .joinToString (separator = "\n") { note ->
                notes.indexOf(note).toString() + ": " + note.toString() }
}