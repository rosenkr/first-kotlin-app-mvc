package se.umu.alro0113.thirtygame

/* This activity simply presents the total score and scores for each selection made during the game */

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {
    private lateinit var totalscore: TextView
    private lateinit var choice1Score : TextView
    private lateinit var choice2Score : TextView
    private lateinit var choice3Score : TextView
    private lateinit var choice4Score : TextView
    private lateinit var choice5Score : TextView
    private lateinit var choice6Score : TextView
    private lateinit var choice7Score : TextView
    private lateinit var choice8Score : TextView
    private lateinit var choice9Score : TextView
    private lateinit var choice10Score : TextView
    private lateinit var model : GameModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        totalscore = findViewById(R.id.totalScore)
        choice1Score = findViewById(R.id.round1Score)
        choice2Score = findViewById(R.id.round2Score)
        choice3Score = findViewById(R.id.round3Score)
        choice4Score = findViewById(R.id.round4Score)
        choice5Score = findViewById(R.id.round5Score)
        choice6Score = findViewById(R.id.round6Score)
        choice7Score = findViewById(R.id.round7Score)
        choice8Score = findViewById(R.id.round8Score)
        choice9Score = findViewById(R.id.round9Score)
        choice10Score = findViewById(R.id.round10Score)

        model = if(savedInstanceState != null){
            savedInstanceState.getParcelable("model")!!
        } else {
            intent.getParcelableExtra("model")!!
        }

        val totalScore = model.getTotalScore()
        val choiceScores = model.getChoiceScores()

        totalscore.text = getString(R.string.totalScore, totalScore.toString())
        choice1Score.text = getString(R.string.choice1Score, "Low", choiceScores["Low"].toString())
        choice2Score.text = getString(R.string.choice2Score, "4", choiceScores["4"].toString())
        choice3Score.text = getString(R.string.choice3Score, "5", choiceScores["5"].toString())
        choice4Score.text = getString(R.string.choice4Score, "6", choiceScores["6"].toString())
        choice5Score.text = getString(R.string.choice5Score, "7", choiceScores["7"].toString())
        choice6Score.text = getString(R.string.choice6Score, "8", choiceScores["8"].toString())
        choice7Score.text = getString(R.string.choice7Score, "9", choiceScores["9"].toString())
        choice8Score.text = getString(R.string.choice8Score, "10", choiceScores["10"].toString())
        choice9Score.text = getString(R.string.choice9Score, "11", choiceScores["11"].toString())
        choice10Score.text = getString(R.string.choice10Score, "12", choiceScores["12"].toString())
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putParcelable("model", model)
    }
}