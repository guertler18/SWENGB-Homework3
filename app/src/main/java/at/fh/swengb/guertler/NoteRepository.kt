package at.fh.swengb.guertler

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NoteRepository {

    fun getNote(token: String, lastSync: Long, success:(noteResponse: NotesResponse)->Unit,error:(errorMessage:String)->Unit)
    {
        NoteApi.retrofitService.notes(token, lastSync).enqueue(object : Callback<NotesResponse>{

            override fun onFailure(call: Call<NotesResponse>, t: Throwable)
            {
                error("Call failed")
            }

            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null)
                {
                    success(responseBody)
                } else {
                    error("There is something wrong")
                }
            }
        })
    }

    fun NoteUpload (token: String, Uploaded_Note: Note, success: (note: Note)->Unit, error: (errorMessage: String)->Unit)
    {
        NoteApi.retrofitService.addupdateNote(token, Uploaded_Note).enqueue(object : Callback<Note>{

            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("Call failed! " + t.localizedMessage)
            }

            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("There is something wrong " + response.message())
                }
            }
        }
        )
    }








    fun getNoteById (context: Context, id: String):Note {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.findNoteById(id)
    }

    fun addNote(context: Context, newNote: Note) {
        val db = NoteDatabase.getDatabase(context)
        db.NoteDao.insert(newNote)
    }

    fun clearDb (context: Context) {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.deleteAllNotes()
    }

    fun getNoteAll (context: Context):List<Note> {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.getNoteAll()
    }


}