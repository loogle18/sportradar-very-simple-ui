package com.example.sportradar

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sportradar.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var curentGame: Game
    private var isGameInProgress = false
    private lateinit var updateButton: Button
    private lateinit var summaryButton: Button
    private var history = ArrayList<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        updateButton = findViewById(R.id.update_score)
        summaryButton = findViewById(R.id.summary)
        if (history.size < 1) {
            summaryButton.visibility = View.INVISIBLE
        }
        binding.fab.setOnClickListener { view ->
            if (this.isGameInProgress) {
                Snackbar.make(view, "Current game is finished!", Snackbar.LENGTH_LONG).show()
                this.isGameInProgress = false
                (this.findViewById(R.id.fab) as FloatingActionButton).setImageResource(android.R.drawable.ic_media_play)
                (this.findViewById(R.id.main_text) as TextView).text = "Start new game!"
                this.updateButton.visibility = View.INVISIBLE
                history.add(this.curentGame)
                this.history.sortByDescending { it -> it.home.score + it.away.score }
                summaryButton.visibility = View.VISIBLE
            } else {
                showNewGameDialog()
            }
        }
        this.updateButton.setOnClickListener { view ->
            showeUpdateScoreDialog()
        }

        this.summaryButton.setOnClickListener { view ->
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.summary_table)
            val btnClose = dialog.findViewById(R.id.close) as Button
            val table = dialog.findViewById(R.id.summary_table_layout) as TableLayout
            for ((index, game) in this.history.withIndex()) {
                var tableRow = TableRow(this)
                var matchView = TextView(this)
                matchView.setBackgroundColor(Color.WHITE)
                matchView.setTextColor(Color.BLACK)
                matchView.text = "${index + 1}. ${game.home.name} ${game.home.score} - ${game.away.name} ${game.away.score}"
                tableRow.addView(matchView)
                table.addView(tableRow)
            }
            dialog.show()
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun showNewGameDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.new_game_modal)
        val btnStart = dialog.findViewById(R.id.btn_update) as Button
        val btnClose = dialog.findViewById(R.id.btn_close) as TextView
        val homeName = dialog.findViewById(R.id.home_score) as EditText
        val awayName = dialog.findViewById(R.id.away_score) as EditText
        btnStart.setOnClickListener {
            this.isGameInProgress = true
            this.curentGame = Game(Team(homeName.text.toString()), Team(awayName.text.toString()))
            (this.findViewById(R.id.main_text) as TextView).text = "Current game: ${this.curentGame.home.name} ${this.curentGame.home.score} - ${this.curentGame.away.name} ${this.curentGame.away.score}"
            (this.findViewById(R.id.fab) as FloatingActionButton).setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            this.updateButton.visibility = View.VISIBLE
            dialog.dismiss()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showeUpdateScoreDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.update_score_modal)
        val btnUpdate = dialog.findViewById(R.id.btn_update) as Button
        val btnClose = dialog.findViewById(R.id.btn_close) as TextView
        val homeScore = dialog.findViewById(R.id.home_score) as EditText
        val awayScore = dialog.findViewById(R.id.away_score) as EditText
        btnUpdate.setOnClickListener {
            this.isGameInProgress = true
            this.curentGame.home.score = this.curentGame.home.score + homeScore.text.toString().toUInt()
            this.curentGame.away.score = this.curentGame.away.score + awayScore.text.toString().toUInt()
            (this.findViewById(R.id.main_text) as TextView).text = "Current game: ${this.curentGame.home.name} ${this.curentGame.home.score} - ${this.curentGame.away.name} ${this.curentGame.away.score}"
            (this.findViewById(R.id.fab) as FloatingActionButton).setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            this.updateButton.visibility = View.VISIBLE
            dialog.dismiss()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}