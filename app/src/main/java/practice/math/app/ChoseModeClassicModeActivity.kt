package practice.math.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.math.app.R

class ChoseModeClassicModeActivity : AppCompatActivity() {

    var addButton: Button? = null
    var subtractButton: Button? = null
    var multiplyButton: Button? = null
    var divideButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_chose_mode)


        addButton = findViewById(R.id.add_button)
        subtractButton = findViewById(R.id.subtract_button)
        multiplyButton = findViewById(R.id.multiply_button)
        divideButton = findViewById(R.id.divide_button)

        setListeners()

    }

    fun setListeners(){
        addButton!!.setOnClickListener {
            val intent = Intent(this, GameClassicMode::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }
        subtractButton!!.setOnClickListener {
            val intent = Intent(this, GameClassicMode::class.java)
            intent.putExtra("mode", "subtract")
            startActivity(intent)
        }
        multiplyButton!!.setOnClickListener {
            val intent = Intent(this, GameClassicMode::class.java)
            intent.putExtra("mode", "multiply")
            startActivity(intent)
        }
        divideButton!!.setOnClickListener {
            val intent = Intent(this, GameClassicMode::class.java)
            intent.putExtra("mode", "divide")
            startActivity(intent)
        }
    }
}