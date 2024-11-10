package za.co.varsitycollege.syntechsoftware.wingwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class QuizActivity : AppCompatActivity() {

    private lateinit var quizBirdImage: ImageView
    private lateinit var questionText: TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button
    private lateinit var nextButton: Button
    private lateinit var backButton: Button
    private lateinit var tryAgainButton: Button
    private lateinit var hotspotsButton: Button
    private lateinit var addBirdsButton: Button

    private var currentQuestionIndex = 0
    private var score = 0
    private var selectedAnswers = mutableMapOf<Int, String>()

    data class Question(
        val question: String,
        val options: List<String>,
        val correctAnswer: String,
        val imageResId: Int
    )

    private val questionBank = listOf(
        Question("Identify the bird in the image:", listOf("African Hoopoe", "Burchell's Starling", "Cape Turtle Dove", "Southern Red Bishop"), "African Hoopoe", R.drawable.african_hoopoe),
        Question("Identify the bird in the image:", listOf("African Hoopoe", "Burchell's Starling", "Cape Turtle Dove", "Southern Red Bishop"), "Burchell's Starling", R.drawable.burchells_starling),
        Question("Identify the bird in the image:", listOf("African Hoopoe", "Burchell's Starling", "Cape Turtle Dove", "Southern Red Bishop"), "Cape Turtle Dove", R.drawable.cape_turtle_dove),
        Question("Identify the bird in the image:", listOf("African Hoopoe", "Burchell's Starling", "Cape Turtle Dove", "Southern Red Bishop"), "Southern Red Bishop", R.drawable.southern_red_bishop)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birdquiz)

        quizBirdImage = findViewById(R.id.quizBirdImage)
        questionText = findViewById(R.id.questionText)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        tryAgainButton = findViewById(R.id.tryAgainButton)
        hotspotsButton = findViewById(R.id.hotspots)
        addBirdsButton = findViewById(R.id.birds)

        loadQuestion()

        option1.setOnClickListener { onOptionSelected(option1) }
        option2.setOnClickListener { onOptionSelected(option2) }
        option3.setOnClickListener { onOptionSelected(option3) }
        option4.setOnClickListener { onOptionSelected(option4) }

        nextButton.setOnClickListener {
            if (selectedAnswers[currentQuestionIndex].isNullOrEmpty()) {
                Toast.makeText(this, "Please select an answer before proceeding", Toast.LENGTH_SHORT).show()
            } else {
                if (selectedAnswers[currentQuestionIndex] == questionBank[currentQuestionIndex].correctAnswer) {
                    score++
                }
                currentQuestionIndex++
                if (currentQuestionIndex < questionBank.size) {
                    loadQuestion()
                } else {
                    showScore()
                }
            }
        }

        backButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                loadQuestion()
            } else {
                navigateToHome()
            }
        }

        tryAgainButton.setOnClickListener {
            restartQuiz()
        }

        // Set up navigation to HotspotsActivity and AddBirdsActivity
        hotspotsButton.setOnClickListener {
            val intent = Intent(this, HotspotsActivity::class.java)
            startActivity(intent)
        }

        addBirdsButton.setOnClickListener {
            val intent = Intent(this, AddBirdsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onOptionSelected(button: Button) {
        // Reset the background color of all options to default
        resetButtonColors()

        // Set the background color of the selected button to indicate it's been selected
        button.setBackgroundColor(resources.getColor(R.color.selected_button_color, null))

        // Save the selected answer
        selectedAnswers[currentQuestionIndex] = button.text.toString()
    }

    private fun resetButtonColors() {
        option1.setBackgroundColor(resources.getColor(R.color.default_button_color, null))
        option2.setBackgroundColor(resources.getColor(R.color.default_button_color, null))
        option3.setBackgroundColor(resources.getColor(R.color.default_button_color, null))
        option4.setBackgroundColor(resources.getColor(R.color.default_button_color, null))
    }

    private fun loadQuestion() {
        val currentQuestion = questionBank[currentQuestionIndex]
        quizBirdImage.setImageResource(currentQuestion.imageResId)
        questionText.text = currentQuestion.question
        option1.text = currentQuestion.options[0]
        option2.text = currentQuestion.options[1]
        option3.text = currentQuestion.options[2]
        option4.text = currentQuestion.options[3]

        // Reset button colors to ensure no options are highlighted when loading a new question
        resetButtonColors()

        // Highlight previously selected answer if any
        val previouslySelectedAnswer = selectedAnswers[currentQuestionIndex]
        if (previouslySelectedAnswer != null) {
            when (previouslySelectedAnswer) {
                option1.text.toString() -> onOptionSelected(option1)
                option2.text.toString() -> onOptionSelected(option2)
                option3.text.toString() -> onOptionSelected(option3)
                option4.text.toString() -> onOptionSelected(option4)
            }
        }

        tryAgainButton.visibility = View.GONE
    }

    private fun showScore() {
        val scoreMessage = "Your score: $score/${questionBank.size}"
        questionText.text = scoreMessage
        option1.isEnabled = false
        option2.isEnabled = false
        option3.isEnabled = false
        option4.isEnabled = false
        nextButton.isEnabled = false
        tryAgainButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            navigateToHome()
        }
    }

    private fun restartQuiz() {
        score = 0
        currentQuestionIndex = 0
        selectedAnswers.clear()
        option1.isEnabled = true
        option2.isEnabled = true
        option3.isEnabled = true
        option4.isEnabled = true
        nextButton.isEnabled = true
        loadQuestion()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
