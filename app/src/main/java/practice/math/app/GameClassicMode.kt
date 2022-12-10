package practice.math.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.math.app.R
import java.io.*
import kotlin.math.roundToInt

class GameClassicMode : AppCompatActivity() {

    var backButton: ImageButton? = null
    var mode: String? = null
    var submitButton: Button? = null

    var currentMathProblem: MathProblem? = null
    var previousMathProblem: MathProblem? = null
    var previousMathProblemTextView: TextView? = null
    var previousMathProblemAnswerTextView: TextView? = null
    var previousMathProblemGuessTextView: TextView? = null

    var firstNumberTextView: TextView? = null
    var operatorTextView: TextView? = null
    var secondNumberTextView: TextView? = null
    var editText: EditText? = null
    var classicModeSettingsButton: ImageButton? = null
    var settingsLayout: LinearLayout? = null
    var settingsLayoutBG: LinearLayout? = null
    var settingsBackButton: ImageButton? = null
    var settingsSaveButton: Button? = null

    var streakCorrect: Int? = null
    var rangeObject: RangeObject? = null
    var settingsFirstNumStartEditText: EditText? = null
    var settingsFirstNumEndEditText: EditText? = null
    var settingsSecondNumStartEditText: EditText? = null
    var settingsSecondNumEndEditText: EditText? = null
    var settingsErrorMessageTextview: TextView? = null
    var settingsSummaryTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_classic_mode)

        backButton = findViewById(R.id.backButtonClassicMode)
        submitButton = findViewById(R.id.submit_button)
        firstNumberTextView = findViewById(R.id.first_number_textview)
        operatorTextView = findViewById(R.id.operator_textview)
        secondNumberTextView = findViewById(R.id.second_number_textview)
        editText = findViewById(R.id.editText)
        previousMathProblemTextView = findViewById(R.id.previousMathProblemTextView)
        previousMathProblemAnswerTextView = findViewById(R.id.previousMathProblemAnswerTextView)
        previousMathProblemGuessTextView = findViewById(R.id.previousMathProblemGuessTextView)
        classicModeSettingsButton = findViewById(R.id.classic_mode_settings_button)
        settingsLayout = findViewById(R.id.settings_layout1)
        settingsLayoutBG = findViewById(R.id.settings_paper_bg)
        settingsBackButton = findViewById(R.id.backButtonClassicModeSettings)
        settingsSaveButton = findViewById(R.id.settings_save_button)
        settingsFirstNumStartEditText = findViewById(R.id.settings_first_number_min_edittext)
        settingsFirstNumEndEditText = findViewById(R.id.settings_first_number_max_edittext)
        settingsSecondNumStartEditText = findViewById(R.id.settings_second_number_min_edittext)
        settingsSecondNumEndEditText = findViewById(R.id.settings_second_number_max_edittext)
        settingsErrorMessageTextview = findViewById(R.id.settings_error_message_textview)
        settingsSummaryTextView = findViewById(R.id.settings_summary)

        mode = intent.extras!!.getString("mode")

        setListeners()
        setMode()
        beginGame()


    }

    fun beginGame(){

        rangeObject = RangeObject(mode!!)
        setSettingsEditTextValues()
        streakCorrect = 0
        createFiles()
        readStreakFromFile()
        try {
            readLastMathProblemFromFile()
        }catch (e: NullPointerException){ }

        if (previousMathProblem != null){
            setPreviousMathProblem()
        }

        beginRound()


    }

    fun setSettingsEditTextValues(){
        settingsFirstNumStartEditText!!.setText(rangeObject!!.firstNumStart!!.toString())
        settingsFirstNumEndEditText!!.setText(rangeObject!!.firstNumEnd!!.toString())
        settingsSecondNumStartEditText!!.setText(rangeObject!!.secondNumStart!!.toString())
        settingsSecondNumEndEditText!!.setText(rangeObject!!.secondNumEnd!!.toString())

        setSettingsSummaryTextView()
    }

    fun beginRound(){

        currentMathProblem = MathProblem(mode!!, rangeObject!!)

        Log.e("answer", currentMathProblem!!.answer.toString())

        firstNumberTextView!!.setText(currentMathProblem!!.firstNumber.toString())
        secondNumberTextView!!.setText(currentMathProblem!!.secondNumber.toString())

    }

    fun setListeners(){
        backButton!!.setOnClickListener{
            val intent = Intent(this, ChoseModeClassicModeActivity::class.java)
            startActivity(intent)
        }
        submitButton!!.setOnClickListener {
            submitAnswer()

        }

        editText!!.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE) {
                    submitAnswer()
                }
                return true;
            }
        })

        classicModeSettingsButton!!.setOnClickListener {
            settingsLayout!!.visibility = View.VISIBLE
            settingsLayoutBG!!.visibility = View.VISIBLE
            closeKeyboard()
        }
        settingsBackButton!!.setOnClickListener{
            settingsLayout!!.visibility = View.GONE
            settingsLayoutBG!!.visibility = View.GONE
        }
        settingsSaveButton!!.setOnClickListener {
            closeKeyboard()
            saveSettings()
        }

    }

    fun saveSettings(){

        var firstNumStart = 0
        var firstNumEnd = 0

        var secondNumStart = 0
        var secondNumEnd = 0

        firstNumStart = settingsFirstNumStartEditText!!.text.toString().toInt()
        firstNumEnd = settingsFirstNumEndEditText!!.text.toString().toInt()
        secondNumStart = settingsSecondNumStartEditText!!.text.toString().toInt()
        secondNumEnd = settingsSecondNumEndEditText!!.text.toString().toInt()

        // Check if settings valid
        if ((firstNumStart <= firstNumEnd) && (secondNumStart <= secondNumEnd)){
            rangeObject!!.firstNumStart = firstNumStart
            rangeObject!!.firstNumEnd = firstNumEnd
            rangeObject!!.secondNumStart = secondNumStart
            rangeObject!!.secondNumEnd = secondNumEnd

            setSettingsSummaryTextView()
            beginRound()
            settingsErrorMessageTextview!!.setText("")
        }
        else{
            settingsErrorMessageTextview!!.setText("Error. First number must be less than second.")
        }


    }

    fun setSettingsSummaryTextView(){

        var operator: String? = null
        when(mode){
            "add" -> operator = "+"
            "subtract" -> operator = "-"
            "divide" -> operator = "/"
            "multiply" -> operator = "*"
        }

        var newText: String = "(${rangeObject!!.firstNumStart}:${rangeObject!!.firstNumEnd}) $operator (${rangeObject!!.secondNumStart}:${rangeObject!!.secondNumEnd})"
        settingsSummaryTextView!!.setText(newText)
    }

    fun closeKeyboard(){

        val view = this.currentFocus

        if (view != null) {
            val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }

    }

    fun writePreviousMathProblemToFile(){

        var FOS: FileOutputStream? = null

        when(mode){
            "add" -> FOS = openFileOutput("previous math problem add.txt", MODE_PRIVATE)
            "subtract" -> FOS = openFileOutput("previous math problem subtract.txt", MODE_PRIVATE)
            "divide" -> FOS = openFileOutput("previous math problem divide.txt", MODE_PRIVATE)
            "multiply" -> FOS = openFileOutput("previous math problem multiply.txt", MODE_PRIVATE)
        }


        var OS: ObjectOutputStream = ObjectOutputStream(FOS)
        OS.writeObject(currentMathProblem)
        OS.close()



    }

    fun readLastMathProblemFromFile(){
        var FIS: FileInputStream? = null

        when(mode){
            "add" -> FIS = openFileInput("previous math problem add.txt")
            "subtract" -> FIS = openFileInput("previous math problem subtract.txt")
            "divide" -> FIS = openFileInput("previous math problem divide.txt")
            "multiply" -> FIS = openFileInput("previous math problem multiply.txt")
        }

        var objectInputStream: ObjectInputStream = ObjectInputStream(FIS)
        previousMathProblem = objectInputStream.readObject() as MathProblem?
        objectInputStream.close()
    }

    fun submitAnswer(){

        if (checkAnswer() != false){
            setPreviousMathProblem()

            editText!!.setText("")

            beginRound()
        }


    }

    fun setPreviousMathProblem(){


        readLastMathProblemFromFile()

        var operator: String? = null
        when(mode){
            "add" -> operator = "+"
            "subtract" -> operator = "-"
            "divide" -> operator = "/"
            "multiply" -> operator = "*"
        }


        if (previousMathProblem == null){
            previousMathProblem = currentMathProblem
        } else{
            var text = previousMathProblem!!.firstNumber.toString() + " " + operator + " " + previousMathProblem!!.secondNumber.toString()
            previousMathProblemTextView!!.setText(text)
        }

        val previousProblemString = "${previousMathProblem!!.firstNumber} $operator ${previousMathProblem!!.secondNumber}"
        previousMathProblemTextView!!.setText(previousProblemString)

        if (previousMathProblem!!.correctAnswer != null){


            if (previousMathProblem!!.correctAnswer!!){
                findViewById<TextView>(R.id.answerLabel).setText("Streak")
                findViewById<TextView>(R.id.correctOrIncorrectLabel).setText("Correct")
                readStreakFromFile()

                previousMathProblemAnswerTextView!!.setText(streakCorrect.toString())
            }else {
                findViewById<TextView>(R.id.answerLabel).setText("Answer")
                findViewById<TextView>(R.id.correctOrIncorrectLabel).setText("Incorrect")
                var answerString = previousMathProblem!!.answer.toString()
                if (answerString.substring(answerString.length-2,answerString.length) == ".0"){
                    answerString = answerString.substring(0, answerString.length-2)
                }

                previousMathProblemAnswerTextView!!.setText(answerString)
            }
            setResultsLayoutBackgroundColor(previousMathProblem!!.correctAnswer!!)


            when(mode){
                "add" -> previousMathProblemGuessTextView!!.setText(previousMathProblem!!.guess.toString().toDouble().roundToInt().toString())
                "subtract" -> previousMathProblemGuessTextView!!.setText(previousMathProblem!!.guess.toString().toDouble().roundToInt().toString())
                "divide" -> previousMathProblemGuessTextView!!.setText(previousMathProblem!!.guess.toString())
                "multiply" -> previousMathProblemGuessTextView!!.setText(previousMathProblem!!.guess.toString().toDouble().roundToInt().toString())
            }

        }

    }

    fun checkAnswer(): Boolean{

        if (editText!!.text.toString() != "") {
            var userInput = editText!!.text.toString().toDouble()
            userInput = (((userInput!!.toDouble() * 100).roundToInt()).toDouble() / 100)

            currentMathProblem!!.guess = userInput

            if (currentMathProblem!!.answer == userInput) {
                correctAnswer()
            } else {
                incorrectAnswer()
            }
            writeStreakToFile()

            writePreviousMathProblemToFile()
            return true
        }else return false

    }

    fun setResultsLayoutBackgroundColor(isCorrectAnswer: Boolean){

        val bg = findViewById<ConstraintLayout>(R.id.bg_color)

        if (isCorrectAnswer){
            bg.setBackgroundColor(ContextCompat.getColor(this, R.color.correct_green))
            bg.animate().apply{
                duration = 10.toLong()
                alpha(1f)
            }.withEndAction {
                bg.animate().apply {
                    duration = 15.toLong()
                    alpha(0f)
                }
            }
        }else{
            bg.setBackgroundColor(ContextCompat.getColor(this, R.color.incorrect_red))
            bg.animate().apply{
                duration = .55.toLong()
                alpha(1f)
            }.withEndAction {
                bg.animate().apply {
                    duration = .55.toLong()
                    alpha(0f)
                }
            }
        }

    }

    fun correctAnswer(){
        currentMathProblem!!.correctAnswer = true
        streakCorrect = streakCorrect!! + 1

        }

    fun incorrectAnswer(){
        currentMathProblem!!.correctAnswer = false
        streakCorrect = 0

        }

    fun createFiles(){

        var FIS: FileInputStream? = null
        try {
            when(mode){
                "add" -> FIS = openFileInput("streak classic add.txt")
                "subtract" -> FIS = openFileInput("streak classic subtract.txt")
                "divide" -> FIS = openFileInput("streak classic divide.txt")
                "multiply" -> FIS = openFileInput("streak classic multiply.txt")
            }
        }
        catch (e: FileNotFoundException){
            writeStreakToFile()
        }

        try {
            when(mode){
                "add" -> FIS = openFileInput("previous math problem add.txt")
                "subtract" -> FIS = openFileInput("previous math problem subtract.txt")
                "divide" -> FIS = openFileInput("previous math problem divide.txt")
                "multiply" -> FIS = openFileInput("previous math problem multiply.txt")
            }

        }
        catch (e: FileNotFoundException){

            var FOS: FileOutputStream? = null

            when(mode){
                "add" -> FOS = openFileOutput("previous math problem add.txt", MODE_PRIVATE)
                "subtract" -> FOS = openFileOutput("previous math problem subtract.txt", MODE_PRIVATE)
                "divide" -> FOS = openFileOutput("previous math problem divide.txt", MODE_PRIVATE)
                "multiply" -> FOS = openFileOutput("previous math problem multiply.txt", MODE_PRIVATE)
            }

            var OS: ObjectOutputStream = ObjectOutputStream(FOS)
            OS.writeObject(currentMathProblem)
            OS.close()
        }


    }

    fun writeStreakToFile(){
        var FOS: FileOutputStream? = null

        when(mode){

            "add" -> FOS = openFileOutput("streak classic add.txt", MODE_PRIVATE)
            "subtract" -> FOS = openFileOutput("streak classic subtract.txt", MODE_PRIVATE)
            "divide" -> FOS = openFileOutput("streak classic divide.txt", MODE_PRIVATE)
            "multiply" -> FOS = openFileOutput("streak classic multiply.txt", MODE_PRIVATE)

        }

        FOS!!.write(streakCorrect!!.toString().encodeToByteArray())
        FOS!!.close()
    }

    fun readStreakFromFile(){
        var FIS: FileInputStream? = null



        when(mode){
            "add" -> FIS = openFileInput("streak classic add.txt")
            "subtract" -> FIS = openFileInput("streak classic subtract.txt")
            "divide" -> FIS = openFileInput("streak classic divide.txt")
            "multiply" -> FIS = openFileInput("streak classic multiply.txt")
        }

        var inputStreamReader: InputStreamReader = InputStreamReader(FIS)
        var bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        var stringBuffer: StringBuffer = StringBuffer()

        var streak = 0
        streak = bufferedReader.readLine().toInt()
        stringBuffer.append(streak)

        streakCorrect = stringBuffer.toString().toInt()
    }

    fun setMode(){
        when(mode){
            "add" -> {
                operatorTextView!!.setText(" + ")
            }
            "subtract" -> {
                operatorTextView!!.setText(" - ")
            }
            "multiply" -> {
                operatorTextView!!.setText(" * ")
            }
            "divide" -> {
                operatorTextView!!.setText(" / ")
            }
        }
    }
}

class RangeObject(var mode: String): Serializable {

    companion object{
        const val serialVersionUID = 5312102145995800879
    }

    var firstNumStart: Int? = null
    var firstNumEnd: Int? = null
    var secondNumStart: Int? = null
    var secondNumEnd: Int? = null

    init {
        when (mode) {
            "add" -> {
                firstNumStart = 1
                firstNumEnd = 100
                secondNumStart = 1
                secondNumEnd = 100
            }
            "subtract" -> {
                firstNumStart = 1
                firstNumEnd = 100
                secondNumStart = 1
                secondNumEnd = 50
            }
            "multiply" -> {
                firstNumStart = 1
                firstNumEnd = 20
                secondNumStart = 2
                secondNumEnd = 25
            }
            "divide" -> {
                firstNumStart = 2
                firstNumEnd = 60
                secondNumStart = 2
                secondNumEnd = 20
            }

        }
    }

}