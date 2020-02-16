package at.fh.swengb.guertler

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*

class NoteListActivity : AppCompatActivity()
{
    companion object
    {

        val EXTRA_ADDED_EDITED_RESULT = 0
        val LASTSYNC = "LASTSYNC"
        val TOKEN = "TOKEN"
        val NOTEiD = "NOTEiD"

    }

    val noteAdapter = NoteAdapter()
    {
        val intent = Intent(this, AddEditNoteActivity::class.java)
        intent.putExtra(NOTEiD, it.id)
        startActivityForResult(intent, EXTRA_ADDED_EDITED_RESULT)
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val shared_Preferences= getSharedPreferences(packageName, Context.MODE_PRIVATE)

        val lastSync = shared_Preferences.getLong(LASTSYNC, 0)
        val token = shared_Preferences.getString(TOKEN, null)

        if (token != null){
            NoteRepository.getNote(

                token,
                lastSync,

                success =
                {
                    it.notes.map { NoteRepository.addNote(this, it) }
                    shared_Preferences.edit().putLong(LASTSYNC, it.lastSync).apply()
                    noteAdapter.updateList(NoteRepository.getNoteAll(this))
                },
                error = {
                   // Log.e("Error", it)

                    noteAdapter.updateList(NoteRepository.getNoteAll(this))
                })
            note_recyclerView.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recyclerView.adapter = noteAdapter

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item?.itemId)
        {
            R.id.logout -> {
                val shared_Preferences= getSharedPreferences(packageName, Context.MODE_PRIVATE)
                shared_Preferences.edit().clear().apply()
                NoteRepository.clearDb(this)
                finish()
            true}


            R.id.newnote -> {
                val uuidString = UUID.randomUUID().toString()
                val intent = Intent(this, AddEditNoteActivity::class.java)
                intent.putExtra(NOTEiD, uuidString)
                startActivityForResult(intent, EXTRA_ADDED_EDITED_RESULT)
            true}


            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == EXTRA_ADDED_EDITED_RESULT  && resultCode == Activity.RESULT_OK){
            noteAdapter.updateList(NoteRepository.getNoteAll(this))
            note_recyclerView.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recyclerView.adapter = noteAdapter
        }
    }

    override fun onResume() {
        super.onResume()
    }


}
