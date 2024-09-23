package com.example.foodbits

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreateRecipe : AppCompatActivity() {

    private lateinit var recipeNameEditText: EditText
    private lateinit var recipeDescriptionEditText: EditText
    private lateinit var ingredientsList: RecyclerView
    private lateinit var addIngredientButton: Button
    private lateinit var stepsList: RecyclerView
    private lateinit var addStepButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var submitRecipeButton: Button
    private lateinit var visibilityGroup: RadioGroup
    private var imageUri: Uri? = null
    private val pickImageRequest = 1

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        // Inicializar las vistas
        recipeNameEditText = findViewById(R.id.recipe_name)
        recipeDescriptionEditText = findViewById(R.id.recipe_description)
        ingredientsList = findViewById(R.id.ingredients_list)
        addIngredientButton = findViewById(R.id.add_ingredient)
        stepsList = findViewById(R.id.steps_list)
        addStepButton = findViewById(R.id.add_step)
        uploadImageButton = findViewById(R.id.upload_image)
        submitRecipeButton = findViewById(R.id.submit_recipe)
        visibilityGroup = findViewById(R.id.visibility_group)

        // Configurar RecyclerView (por ejemplo, un LinearLayoutManager para listas verticales)
        ingredientsList.layoutManager = LinearLayoutManager(this)
        stepsList.layoutManager = LinearLayoutManager(this)

        val ingredientsAdapter = IngredientsAdapter(mutableListOf()) // Lista vacía al inicio
        ingredientsList.adapter = ingredientsAdapter

        val stepsAdapter = StepsAdapter(mutableListOf()) // Lista vacía al inicio
        stepsList.adapter = stepsAdapter


        // Manejo del botón de agregar ingrediente
        addIngredientButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Agregar Ingrediente")

            val input = EditText(this)
            input.hint = "Ingresa el ingrediente"
            builder.setView(input)

            builder.setPositiveButton("Agregar") { dialog, which ->
                val newIngredient = input.text.toString()
                if (newIngredient.isNotEmpty()) {
                    ingredientsAdapter.addIngredient(newIngredient)
                }
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        // Manejo del botón de agregar paso
        addStepButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Agregar Paso")

            val input = EditText(this)
            input.hint = "Ingresa el paso"
            builder.setView(input)

            builder.setPositiveButton("Agregar") { dialog, which ->
                val newStep = input.text.toString()
                if (newStep.isNotEmpty()) {
                    stepsAdapter.addStep(newStep)
                }
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }


        // Manejo del botón de subir imagen
        uploadImageButton.setOnClickListener {
            openImagePicker()
        }

        // Inicializa el ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                // Procesa la imagen seleccionada
                if (imageUri != null) {
                    handleSelectedImage(imageUri)
                }
            }
        }

        // Manejo del botón de subir receta
        submitRecipeButton.setOnClickListener {
            submitRecipe()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun handleSelectedImage(uri: Uri) {
        imageUri = uri  // Guarda el Uri en una variable global
        // Aquí puedes mostrar la imagen seleccionada en tu UI o hacer lo que necesites
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            // Aquí puedes hacer algo con la imagen seleccionada, como mostrarla en un ImageView
        }
    }

    private fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
            .child("recipe_images/${UUID.randomUUID()}.jpg")

        storageReference.putFile(uri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitRecipe() {
        val recipeName = recipeNameEditText.text.toString()
        val recipeDescription = recipeDescriptionEditText.text.toString()
        val visibility = when (visibilityGroup.checkedRadioButtonId) {
            R.id.public_option -> Visibility.PUBLIC
            R.id.private_option -> Visibility.PRIVATE
            else -> Visibility.PUBLIC
        }

        if (recipeName.isEmpty() || recipeDescription.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Si hay imagen, la subimos primero
        if (imageUri != null) {
            uploadImageToFirebase(imageUri!!) { imageUrl ->
                saveRecipeToFirestore(recipeName, recipeDescription, imageUrl, visibility, userId)
            }
        } else {
            // Si no hay imagen, subimos la receta sin URL de imagen
            saveRecipeToFirestore(recipeName, recipeDescription, "", visibility, userId)
        }
    }

    private fun saveRecipeToFirestore(
        name: String,
        description: String,
        imageUrl: String,
        visibility: Visibility,
        userId: String
    ) {
        val ingredients = (ingredientsList.adapter as IngredientsAdapter).getIngredientsList()
        val steps = (stepsList.adapter as StepsAdapter).getStepsList()

        val recipe = Recipe(
            name = name,
            description = description,
            ingredients = ingredients,
            steps = steps,
            imageUrl = imageUrl,
            visibility = visibility,
            userId = userId
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("recipes").add(recipe)
            .addOnSuccessListener {
                Toast.makeText(this, "Receta subida correctamente.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir la receta.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getIngredientsList(): List<String> {
        // Retorna la lista de ingredientes, ya sea desde tu adaptador o donde sea que estés guardando los ingredientes temporalmente.
        return listOf("Ingrediente 1", "Ingrediente 2")  // Este es un ejemplo
    }

    private fun getStepsList(): List<String> {
        // Retorna la lista de pasos, ya sea desde tu adaptador o donde sea que estés guardando los pasos temporalmente.
        return listOf("Paso 1", "Paso 2")  // Este es un ejemplo
    }


}
