package com.nareshkumarrao.eiweblog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nareshkumarrao.eiweblog.ui.main.ItemArticleAdapter
import com.nareshkumarrao.eiweblog.ui.main.ItemGradesAdapter


class GradesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)


        val myToolbar = findViewById<View>(R.id.grades_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<RecyclerView>(R.id.grades_recycler).apply {
            layoutManager = LinearLayoutManager(this@GradesActivity)
            adapter = ItemArticleAdapter(listOf())
        }
        findViewById<RecyclerView>(R.id.grades_recycler).addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.grades_swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener {
            HISUtility.fetchExamRows(this, ::updateExamRows)
        }

        val sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        )

        val username = sharedPref?.getString(getString(R.string.username_key), null)
        if( username == null ){
            val loginDialog = LoginDialogFragment(this, false) {
                HISUtility.fetchExamRows(this, ::updateExamRows)
            }
            loginDialog.show(supportFragmentManager, "loginDialog")
        }else{
            val savedRows = HISUtility.getSavedExamRows(this)
            if(savedRows == null){
                HISUtility.fetchExamRows(this, ::updateExamRows)
            }else{
                updateExamRows(savedRows)
            }
        }
    }

    private fun updateExamRows(examRows: List<ExamRow>?){
        examRows ?: run {
            val loginDialog = LoginDialogFragment(this, true){
                HISUtility.fetchExamRows(this, ::updateExamRows)
            }
            loginDialog.show(supportFragmentManager, "loginDialog")
            return
        }
        this@GradesActivity.runOnUiThread {
            findViewById<RecyclerView>(R.id.grades_recycler)?.apply {
                layoutManager = LinearLayoutManager(this@GradesActivity)
                adapter = ItemGradesAdapter(examRows)
            }
            findViewById<SwipeRefreshLayout>(R.id.grades_swipe_refresh).isRefreshing=false
            findViewById<ProgressBar>(R.id.gradesProgressBar).visibility=View.GONE
        }
    }
}

class LoginDialogFragment(val context: GradesActivity, private val isError: Boolean, val loginCallback: () -> Unit?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_login, null)
            if(isError){
                dialogView.findViewById<TextView>(R.id.errorText).visibility=View.VISIBLE
            }else{
                dialogView.findViewById<TextView>(R.id.errorText).visibility=View.INVISIBLE
            }
            builder.setView(dialogView)
                .setPositiveButton(
                        R.string.login
                ) { _, _ ->
                    val username = dialogView.findViewById<EditText>(R.id.loginUsername).text.toString()
                    val password = dialogView.findViewById<EditText>(R.id.loginPassword).text.toString()
                    HISUtility.setUsernamePassword(context, username, password)
                    loginCallback()
                }
                .setNegativeButton(
                        R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                    context.finish()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}
