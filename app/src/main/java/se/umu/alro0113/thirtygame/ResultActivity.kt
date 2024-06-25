package se.umu.alro0113.thirtygame

/* This activity simply presents the total score and scores for each selection made during the game */


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import se.umu.alro0113.thirtygame.databinding.ActivityResultBinding


class ResultActivity : AppCompatActivity() {
    private lateinit var model : GameModel
    private lateinit var binding: ActivityResultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        model = if(savedInstanceState != null){
            savedInstanceState.getParcelable("model")!!
        } else {
            intent.getParcelableExtra("model")!!
        }

        val totalScore = model.getTotalScore()
        val choiceScores = model.getChoiceScores()

        binding.totalScore.text = getString(R.string.totalScore, totalScore.toString())
        binding.round1Score.text = getString(R.string.choice1Score, "Low", choiceScores["Low"].toString())
        binding.round2Score.text = getString(R.string.choice2Score, "4", choiceScores["4"].toString())
        binding.round3Score.text = getString(R.string.choice3Score, "5", choiceScores["5"].toString())
        binding.round4Score.text = getString(R.string.choice4Score, "6", choiceScores["6"].toString())
        binding.round5Score.text = getString(R.string.choice5Score, "7", choiceScores["7"].toString())
        binding.round6Score.text = getString(R.string.choice6Score, "8", choiceScores["8"].toString())
        binding.round7Score.text = getString(R.string.choice7Score, "9", choiceScores["9"].toString())
        binding.round8Score.text = getString(R.string.choice8Score, "10", choiceScores["10"].toString())
        binding.round9Score.text = getString(R.string.choice9Score, "11", choiceScores["11"].toString())
        binding.round10Score.text = getString(R.string.choice10Score, "12", choiceScores["12"].toString())
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putParcelable("model", model)
    }
}