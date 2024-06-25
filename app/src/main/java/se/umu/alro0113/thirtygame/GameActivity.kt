package se.umu.alro0113.thirtygame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import se.umu.alro0113.thirtygame.databinding.ActivityGameBinding

/* This activity is the entry point of the application
*  Game state is controlled internally by a looping integer throwCounter
*  and by toggling the enabled state for 3 buttons and 1 spinner.
*  Game data is retrieved, and set in, a GameModel object. This object
*  has a reference to a list of Die objects, which is in its own file
*/

private const val FINAL_ROUND = 10 + 1
private const val MAX_THROWS_PER_ROUND = 3

class GameActivity : AppCompatActivity() {
    private lateinit var model : GameModel
    private lateinit var btnThrow : Button
    private lateinit var btnConfirmRound : Button
    private lateinit var btnConfirmSpinnerSelection : Button
    private lateinit var spinner : Spinner
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private lateinit var diceImageViews: List<ImageView>
    private lateinit var yourChoice: TextView
    private lateinit var currentRoundTextView : TextView

    private lateinit var binding: ActivityGameBinding

    private var throwCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        btnThrow = binding.throwBtn
        btnConfirmRound = binding.confirmBtn
        btnConfirmSpinnerSelection = binding.confirmSpinnerSelectionBtn
        spinner = binding.choicesSpn
        currentRoundTextView = binding.round
        yourChoice = binding.yourChoice

        diceImageViews = listOf(
            binding.dice1,
            binding.dice2,
            binding.dice3,
            binding.dice4,
            binding.dice5,
            binding.dice6
        )

        if(savedInstanceState != null){
            model = savedInstanceState.getParcelable("model")!! // restore model
            val dice : MutableList<Die> = mutableListOf() // restore dice
            for (i in 0 until 6) {
                val die = savedInstanceState.getParcelable<Die>("die_$i")
                die?.let {
                    dice.add(it)
                }
            }
            model.setDice(dice) // update restored model with restored dice

            // Restore game state
            btnThrow.isEnabled = savedInstanceState.getBoolean("btnThrow")
            btnConfirmRound.isEnabled = savedInstanceState.getBoolean("btnConfirmRound")
            btnConfirmSpinnerSelection.isEnabled = savedInstanceState.getBoolean("btnConfirmSpinnerSelection")
            spinner.isEnabled = savedInstanceState.getBoolean("spinner")
            yourChoice.visibility = savedInstanceState.getInt("yourChoice")
            throwCounter = savedInstanceState.getInt("throwCounter")

            restoreUI() // restore user interface

        } else {
            model = GameModel()
            model.setDice(MutableList(6) { Die() })

            // initial state of application
            spinner.isEnabled = false
            btnThrow.isEnabled = true
            btnConfirmRound.isEnabled= false
            btnConfirmSpinnerSelection.isEnabled = false
            yourChoice.visibility = View.VISIBLE
            yourChoice.text = resources.getString(R.string.choice_text, model.getCurrentChoice())
        }

        // Initial setup of spinner, based on saved or new GameModel object
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, model.getChoices())
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        // Sets up 6 handlers for toggling die state and views from white to red/gray, or red/gray to white
        setDieTogglingListeners()

        // Responsible for both rolling all dice or re-rolling particular dice
        btnThrow.setOnClickListener{
            if(throwCounter == 1 || throwCounter == 2){
                model.rollSelectedDice()
            } else {
                spinner.isEnabled = true
                btnConfirmSpinnerSelection.isEnabled = true
                model.rollAllDice()
            }
            model.clearSelectedDice()
            throwCounter++
            updateDieImageViewsOnThrow()
            if(throwCounter == MAX_THROWS_PER_ROUND){
                btnThrow.isEnabled = false
            }
        }

        // Responsible for ending a round and starting a new one
        btnConfirmRound.setOnClickListener{
            yourChoice.visibility = View.INVISIBLE
            spinner.isEnabled = false
            val theChoice = model.getCurrentChoice()

            // handle updating score for "low" or 4-12, depending on selectedRed die configuration and choice
            if(theChoice == "Low"){
                model.updateScoresAndRound()
                model.clearSelectedDice()
                restoreUI()
                btnThrow.isEnabled = true
                btnConfirmRound.isEnabled = false
            } else {
                if(model.assertValidDieConfig()){ // first control that the selected dice are viable
                    model.updateScoresAndRound()
                    model.clearSelectedDice()
                    restoreUI()
                    btnThrow.isEnabled = true
                    btnConfirmRound.isEnabled = false
                    //btnConfirmSpinnerSelection.isEnabled = true
                } else {
                    Toast.makeText(this, "Invalid die configuration for chosen score, try selecting other dice", Toast.LENGTH_LONG).show()
                }
            }

            // reset throwCounter before new round begins
            throwCounter = 0

            // Start ResultActivity
            if(model.getRound() == FINAL_ROUND){
                val intent = Intent(this@GameActivity, ResultActivity::class.java)
                intent.putExtra("model", model)
                startActivity(intent)
            }
        }

        // Responsible for recreating adapter with one less item
        btnConfirmSpinnerSelection.setOnClickListener{
            yourChoice.visibility = View.VISIBLE
            val selection : String = spinner.selectedItem.toString()
            model.onSpinnerConfirmed(selection)
            spinner.isEnabled = false
            btnConfirmSpinnerSelection.isEnabled = false
            btnThrow.isEnabled = false
            btnConfirmRound.isEnabled = true
            yourChoice.text = resources.getString(R.string.choice_text, model.getCurrentChoice())
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, model.getChoices())
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // GameModel
        outState.putParcelable("model", model)

        // Die
        val dice = model.getDice()
        for (i in dice.indices) {
            val die = dice[i]
            outState.putParcelable("die_$i", die)
        }

        // GameActivity
        outState.putInt("throwCounter", throwCounter)

        outState.putBoolean("btnThrow", btnThrow.isEnabled)
        outState.putBoolean("btnConfirmRound", btnConfirmRound.isEnabled)
        outState.putBoolean("btnConfirmSpinnerSelection", btnConfirmSpinnerSelection.isEnabled)
        outState.putBoolean("spinner", spinner.isEnabled)
        outState.putInt("yourChoice", yourChoice.visibility)
    }

    // Set 6 image views, 1 round textview, 1 choice textview.
    private fun restoreUI(){

        // White, red, or grey, 1-6, depending on die value and selected-state
        diceImageViews.forEachIndexed{ index, _ ->
            val dice = model.getDice()
            val die = dice[index]
            val resId : Int
            if(die.selectedRed){
                resId = when (die.value) {
                    1 -> R.drawable.red1
                    2 -> R.drawable.red2
                    3 -> R.drawable.red3
                    4 -> R.drawable.red4
                    5 -> R.drawable.red5
                    6 -> R.drawable.red6
                    else -> R.drawable.red1
                }
            } else if(die.selectedGray){
                resId = when (die.value) {
                    1 -> R.drawable.grey1
                    2 -> R.drawable.grey2
                    3 -> R.drawable.grey3
                    4 -> R.drawable.grey4
                    5 -> R.drawable.grey5
                    6 -> R.drawable.grey6
                    else -> R.drawable.grey1
                }
            } else {
                resId = when (die.value) {
                    1 -> R.drawable.white1
                    2 -> R.drawable.white2
                    3 -> R.drawable.white3
                    4 -> R.drawable.white4
                    5 -> R.drawable.white5
                    6 -> R.drawable.white6
                    else -> R.drawable.white1
                }
            }
            diceImageViews[index].setImageResource(resId)
        }

        // set the text view that shows the current choice
        yourChoice.text = resources.getString(R.string.choice_text, model.getCurrentChoice())

        // set the textview that shows the current round
        val resId = when (model.getRound()) {
            1 -> R.string.round1
            2 -> R.string.round2
            3 -> R.string.round3
            4 -> R.string.round4
            5 -> R.string.round5
            6 -> R.string.round6
            7 -> R.string.round7
            8 -> R.string.round8
            9 -> R.string.round9
            10 -> R.string.round10

            else -> R.drawable.white1
        }
        currentRoundTextView.setText(resId)
    }

    // helper for btnThrow listeners. Sets 6 die image views with white dice with correct values from the model
    private fun updateDieImageViewsOnThrow() {
        val dice = model.getDice()
        dice.forEachIndexed { index, die ->
            val resId = when (die.value) {
                1 -> R.drawable.white1
                2 -> R.drawable.white2
                3 -> R.drawable.white3
                4 -> R.drawable.white4
                5 -> R.drawable.white5
                6 -> R.drawable.white6
                else -> R.drawable.white1
            }
            diceImageViews[index].setImageResource(resId)
        }
    }

    // Sets listeners for the 6 die ImageViews for toggling dice selection
    private fun setDieTogglingListeners() {
        diceImageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val dice = model.getDice()
                val die = dice[index]
                val resId : Int

                // Based on current game state, toggle the selection state of a die at index in models list of die
                val rethrowing : Boolean = spinner.isEnabled
                if(rethrowing){
                    model.toggleSelectedGray(index)
                } else {
                    model.toggleSelectedRed(index)
                }

                // Update UI after model changes
                if(die.selectedRed){
                    resId = when (die.value) {
                        1 -> R.drawable.red1
                        2 -> R.drawable.red2
                        3 -> R.drawable.red3
                        4 -> R.drawable.red4
                        5 -> R.drawable.red5
                        6 -> R.drawable.red6
                        else -> R.drawable.white1
                    }
                } else if(die.selectedGray){
                    resId = when (die.value) {
                        1 -> R.drawable.grey1
                        2 -> R.drawable.grey2
                        3 -> R.drawable.grey3
                        4 -> R.drawable.grey4
                        5 -> R.drawable.grey5
                        6 -> R.drawable.grey6
                        else -> R.drawable.grey1
                    }
                } else {
                    resId = when (die.value) {
                        1 -> R.drawable.white1
                        2 -> R.drawable.white2
                        3 -> R.drawable.white3
                        4 -> R.drawable.white4
                        5 -> R.drawable.white5
                        6 -> R.drawable.white6
                        else -> R.drawable.white1
                    }
                }
                diceImageViews[index].setImageResource(resId)
            }
        }
    }
}