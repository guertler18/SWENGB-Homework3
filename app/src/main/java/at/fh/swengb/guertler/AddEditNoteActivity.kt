package at.fh.swengb.guertler

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_note.*

class AddEditNoteActivity : AppCompatActivity() {

    companion object
    {
        val EXTRA_ADDED_EDITED_RESULT = "ADD_OR_EDITED_RESULT"
        val TOKEN = "TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        val extra: String? = intent.getStringExtra(NoteListActivity.NOTEiD)

        if(extra != null){
            val note:Note? = NoteRepository.getNoteById(this, extra)
            if(note != null) {
                edit_title.setText(note.title)
                edit_text.setText(note.text)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item?.itemId) {
            R.id.savenote -> {

                val extra: String? = intent.getStringExtra(NoteListActivity.NOTEiD)
                val shared_Preferences= getSharedPreferences(packageName, Context.MODE_PRIVATE)
                val token = shared_Preferences.getString(TOKEN, null)

                if (
                    (extra != null) &&
                    (edit_text.text.toString().isNotEmpty() || edit_title.text.toString().isNotEmpty()) &&
                    (token != null))
                {
                    val note = Note(extra, edit_title.text.toString(), edit_text.text.toString(), true)
                    NoteRepository.addNote(this, note)
                    NoteRepository.NoteUpload(
                        token,
                        note,
                        success = {
                        NoteRepository.addNote(this, it)
                    },
                        error = {

                    })

                    val resultIntent = intent
                    resultIntent.putExtra(EXTRA_ADDED_EDITED_RESULT, "ADDED")

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, this.getString(R.string.fill_message) , Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
