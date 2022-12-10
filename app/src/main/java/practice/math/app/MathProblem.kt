package practice.math.app

import java.io.Serializable
import kotlin.math.roundToInt

class MathProblem(mode: String, rangeObject: RangeObject): Serializable {

    companion object{
        const val serialVersionUID = 5312102145995800878;
    }

    val mode: String = mode
    val rangeObject = rangeObject
    @Transient var firstNumberRange: IntRange? = null
    @Transient var secondNumberRange: IntRange? = null
    var firstNumber: Number? = null
    var secondNumber: Number? = null
    var answer: Number? = null
    var guess: Number? = null
    var correctAnswer: Boolean? = null

    init {
        setRange()
        setNumbers()
        setAnswer()

    }
    fun setNumbers(){
        firstNumber = firstNumberRange!!.random()
        secondNumber = secondNumberRange!!.random()
    }

    fun setRange() {
        val fns = rangeObject.firstNumStart
        val fne = rangeObject.firstNumEnd
        val sns = rangeObject.secondNumStart
        val sne = rangeObject.secondNumEnd

        firstNumberRange = fns!!..fne!!
        secondNumberRange = sns!!..sne!!

    }

    fun setAnswer(){

        when(mode){
            "add" -> {
                answer = firstNumber!!.toInt() + secondNumber!!.toInt()
                answer = (((answer!!.toDouble()*100).roundToInt()).toDouble() / 100)
            }
            "subtract" -> {
                answer = firstNumber!!.toInt() - secondNumber!!.toInt()
                answer = (((answer!!.toDouble()*100).roundToInt()).toDouble() / 100)
            }
            "multiply" -> {
                answer = firstNumber!!.toInt() * secondNumber!!.toInt()
                answer = ((answer!!.toDouble() * 100).roundToInt() / 100).toDouble()
            }
            "divide" -> {
                answer = firstNumber!!.toDouble() / secondNumber!!.toDouble()
                answer = (((answer!!.toDouble()*100).roundToInt()).toDouble() / 100)
            }
        }
    }

}