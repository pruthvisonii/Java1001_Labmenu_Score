package ca.pruthvi_score

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {
    //variables to hold Runs and Wickets scored by both teams
    private var teamOneRun = 0
    private var teamOneWicket = 0
    private var teamTwoRun = 0
    private var teamTwoWicket = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declaring the buttons
        val teamOneUp = findViewById<Button>(R.id.TeamOneIncrease)
        val teamOneDown = findViewById<Button>(R.id.TeamOneReduce)
        val teamTwoUp = findViewById<Button>(R.id.TeamTwoIncrease)
        val teamTwoDown = findViewById<Button>(R.id.TeamTwoReduce)
        val newGame = findViewById<Button>(R.id.newGame)

        sharedPreferences = getSharedPreferences("ScoreKeeper", Context.MODE_PRIVATE)


        //calling update screen to update the button status as per the opening values
        updateScreen()

        //Events for the buttons. They are called when user clicks on the buttons
        teamOneUp.setOnClickListener(this)
        teamOneDown.setOnClickListener(this)
        teamTwoUp.setOnClickListener(this)
        teamTwoDown.setOnClickListener(this)
        newGame.setOnClickListener(this)

        //Event is triggered when any of the radiobutton is selected, it calls the update screen method
        val scoringOptions = findViewById<RadioGroup>(R.id.ScoringOptions)
        scoringOptions.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int -> updateScreen() }

        teamOneRun = sharedPreferences.getInt("teamOneRun", 0)
        teamOneWicket = sharedPreferences.getInt("teamOneWicket", 0)
        teamTwoRun = sharedPreferences.getInt("teamTwoRun", 0)
        teamTwoWicket = sharedPreferences.getInt("teamTwoWicket", 0)
    }

    override fun onClick(view: View) {
        //integer array to hold run and wicket value from the TextView
        var runsAndWicket: IntArray

        //fetching the score for team one from TextView
        val teamOneScore = findViewById<TextView>(R.id.TeamOneScore)
        //fetching the run and wicket value from the TextView
        runsAndWicket = getRunAndWickets(teamOneScore.text.toString())
        //Run value is stored in the first array position of runsAndWicket
        teamOneRun = runsAndWicket[0]
        //If wicket is equal to 10, the score will only have runs value - hence the condition
        //If wicket value is available, it will be there on the second array position of runsAndWicket
        if (runsAndWicket.size > 1) {
            teamOneWicket = runsAndWicket[1]
        }

        //fetching the score for team two from TextView
        val teamTwoScore = findViewById<TextView>(R.id.TeamTwoScore)
        //fetching the run and wicket value from the TextView
        runsAndWicket = getRunAndWickets(teamTwoScore.text.toString())
        //Run value is stored in the first array position of runsAndWicket
        teamTwoRun = runsAndWicket[0]
        //If wicket is equal to 10, the score will only have runs value - hence the condition
        //If wicket value is available, it will be there on the second array position of runsAndWicket
        if (runsAndWicket.size > 1) {
            teamTwoWicket = runsAndWicket[1]
        }

        //scoreValue is fetched as per the radiobutton selected by the user
        val scoreValue = scoringValue
        when (view.id) {
            R.id.TeamOneIncrease ->                 //if user selects wicket radiobutton then the scoreValue will be -1
                if (scoreValue != -1) {
                    //increment team one run by the scoreValue
                    teamOneRun = teamOneRun + scoreValue
                } else {
                    //increment team one wicket by 1
                    teamOneWicket = teamOneWicket + 1
                }
            R.id.TeamOneReduce ->
                if (scoreValue != -1) {
                    teamOneRun = teamOneRun - scoreValue
                } else {
                    teamOneWicket = teamOneWicket - 1
                }
            R.id.TeamTwoIncrease ->
                if (scoreValue != -1) {
                    teamTwoRun = teamTwoRun + scoreValue
                } else {
                    teamTwoWicket = teamTwoWicket + 1
                }
            R.id.TeamTwoReduce ->
                if (scoreValue != -1) {
                    teamTwoRun = teamTwoRun - scoreValue
                } else {
                    teamTwoWicket = teamTwoWicket - 1
                }
            R.id.newGame -> {
                teamOneRun = 0
                teamTwoRun = 0
                teamOneWicket = 0
                teamTwoWicket = 0
            }
        }
        updateScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about -> {
                Toast.makeText(this, "Developed by Pruthvi Soni and Sakshi Sheth", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_settings -> {
                // Handle "Settings" menu item click
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private lateinit var sharedPreferences: SharedPreferences


    fun updateScreen() {
        //declaring buttons
        val teamOneUp = findViewById<Button>(R.id.TeamOneIncrease)
        val teamOneDown = findViewById<Button>(R.id.TeamOneReduce)
        val teamTwoUp = findViewById<Button>(R.id.TeamTwoIncrease)
        val teamTwoDown = findViewById<Button>(R.id.TeamTwoReduce)
        val newGame = findViewById<Button>(R.id.newGame)

        teamOneDown.isEnabled = teamOneRun > 0 && teamOneWicket < 10
        teamTwoDown.isEnabled = teamTwoRun > 0 && teamTwoWicket < 10
        teamOneUp.isEnabled = teamOneWicket < 10
        teamTwoUp.isEnabled = teamTwoWicket < 10

        if (scoringValue == -1) {
            teamOneDown.isEnabled = teamOneWicket > 0
            teamTwoDown.isEnabled = teamTwoWicket > 0
        }

        if (teamOneRun > 0 || teamTwoRun > 0 || teamOneWicket > 0 || teamTwoWicket > 0) newGame.visibility =
            View.VISIBLE else newGame.visibility = View.GONE

        val teamOneScore = findViewById<TextView>(R.id.TeamOneScore)
        teamOneScore.text = getScore(teamOneRun, teamOneWicket)

        val teamTwoScore = findViewById<TextView>(R.id.TeamTwoScore)
        teamTwoScore.text = getScore(teamTwoRun, teamTwoWicket)

        val editor = sharedPreferences.edit()
        editor.putInt("teamOneRun", teamOneRun)
        editor.putInt("teamOneWicket", teamOneWicket)
        editor.putInt("teamTwoRun", teamTwoRun)
        editor.putInt("teamTwoWicket", teamTwoWicket)
        editor.apply()
    }


    fun getRunAndWickets(score: String): IntArray {

        val runAndWicketString: Array<String>

        runAndWicketString = score.split("/").toTypedArray()
        val runAndWicket = IntArray(runAndWicketString.size)
        for (i in runAndWicketString.indices) {
            runAndWicket[i] = runAndWicketString[i].toInt()
        }
        return runAndWicket
    }

    val scoringValue: Int
        get() {
            val scoreValue: Int
            val scoringOptions = findViewById<RadioGroup>(R.id.ScoringOptions)
            scoreValue = when (scoringOptions.checkedRadioButtonId) {
                R.id.ScoreOne -> 1
                R.id.ScoreTwo -> 2
                R.id.ScoreThree -> 3
                R.id.ScoreFour -> 4
                R.id.ScoreFive -> 6
                R.id.Wicket -> -1
                else -> 0
            }
            return scoreValue
        }

    fun getScore(run: Int, wicket: Int): String {
        var run = run
        var wicket = wicket
        if (run < 0) {
            run = 0
        }
        if (wicket < 0) {
            wicket = 0
        }
        return if (wicket == 10) {
            run.toString()
        } else
        {
            "$run/$wicket"
        }
    }
}