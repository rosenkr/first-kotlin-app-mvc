package se.umu.alro0113.thirtygame

import android.os.Parcel
import android.os.Parcelable

data class GameModel(private var totalScore : Int = 0) : Parcelable{
    private var dice: MutableList<Die> = mutableListOf()
    private var choiceScores: MutableMap<String, Int> = mutableMapOf() // maps a round (1-10) to a score
    private var round : Int = 1 // first round is 1
    private var currentChoice : String = "Low"
    private var choices : MutableList<String> = mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")


    constructor(parcel: Parcel) : this(parcel.readInt()) {
        val size = parcel.readInt()
        for (i in 0 until size) {
            val key = parcel.readString() ?: ""
            val value = parcel.readInt()
            choiceScores[key] = value
        }
        round = parcel.readInt()
        currentChoice = parcel.readString().toString()
        parcel.readStringList(choices)
    }

    fun setDice(list: MutableList<Die>){
        this.dice = list
    }

    // save current choice before removing it from the choices collection
    fun onSpinnerConfirmed(selection : String){
        currentChoice = selection
        choices.remove(selection)
    }

    // randomize values 1-6 for all die
    fun rollAllDice() {
        dice.forEach { die ->
            die.value = (1..6).random()
        }
    }

    // randomize values 1-6 for those die that are selected (gray)
    fun rollSelectedDice(){
        dice.forEach { die ->
            if(die.selectedGray){
                die.value = (1..6).random()
            }
        }
    }

    // calculates a round's score, increments scores/round
    fun updateScoresAndRound(){
        val roundScore : Int

        if(currentChoice == "Low"){  // roundScore for Low
            roundScore = dice.filter { it.value <= 3 }.sumOf { it.value }
        } else {  // roundScore for 4-12
            val selectedDice = dice.filter { it.selectedRed }
            roundScore = selectedDice.sumOf { it.value }

        }
        choiceScores[currentChoice] = roundScore
        totalScore += roundScore
        round++
    }

    // makes all dice not selected in any way
    fun clearSelectedDice(){
        dice.forEach { it.selectedRed = false }
        dice.forEach { it.selectedGray = false }
    }

    // tests selectedRed dice against currentChoice according to game rules
    fun assertValidDieConfig() : Boolean{

        // for those dice that are selectedRed, sum their values
        val selectedDice = dice.filter { it.selectedRed }
        val totalValue = selectedDice.sumOf { it.value }

        // if any selected die has a value higher than the choice,
        // or modulo of their values together against the choice is not 0, return false, otherwise true
        // for example, choice is 4:
        // (6 + 6) == 12.
        // 12 % 4 == 0
        // but this will NOT pass because 6 > 4 leads to false expression
        // which is the expected result
        return !((selectedDice.any { it.value > currentChoice.toInt() }) || (totalValue % currentChoice.toInt() != 0))
    }

    // toggles selectedRed state for the Die at some index
    fun toggleSelectedRed(index : Int){
        dice[index].selectedRed = !dice[index].selectedRed
    }

    // toggles selectedGray state for the Die at some index
    fun toggleSelectedGray(index : Int){
        dice[index].selectedGray = !dice[index].selectedGray
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(totalScore)
        parcel.writeInt(choiceScores.size)
        choiceScores.forEach { (key, value) ->
            parcel.writeString(key)
            parcel.writeInt(value)
        }

        // round, choice, and choices saved
        parcel.writeInt(round)
        parcel.writeString(currentChoice)
        parcel.writeStringList(choices)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameModel> {
        override fun createFromParcel(parcel: Parcel): GameModel {
            return GameModel(parcel)
        }

        override fun newArray(size: Int): Array<GameModel?> {
            return arrayOfNulls(size)
        }
    }

    /* Getters */
    fun getTotalScore() : Int{
        return totalScore
    }

    fun getDice() : List<Die>{
        return dice
    }

    fun getChoiceScores() : MutableMap<String, Int>{
        return choiceScores
    }

    fun getRound() : Int{
        return round
    }

    fun getCurrentChoice() : String{
        return currentChoice
    }

    fun getChoices() : List<String>{
        return choices
    }
}
