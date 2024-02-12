package com.example.chatapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.databinding.ActivitySetupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.UUID

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding
    var selectedBitmap : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var permissionRegisterLauncher: (ActivityResultLauncher<String>)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var database : FirebaseFirestore = Firebase.firestore
    private lateinit var storage: FirebaseStorage
    private lateinit var name : String
    private var doc  =  database.collection("users").document("info")

    private lateinit var about : String
    private lateinit var selectedImage : Uri
    var imageReference : StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage
        auth = FirebaseAuth.getInstance()

        handlePermission() // this should be here

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        imageReference = reference.child("images").child("$imageName.jpg")
        // we have just created the file name as of now.


    }

    fun onButtonClicked(view: View) {
        // TODO: CHECK IF USER ENTERED NAME
        intent = Intent(this@SetupActivity, MainActivity::class.java)
        startActivity(intent)
    }

    //  make sure these match with the onclicks
    fun uploadImageClicked(view: View) {
        // get the permission to upload images.

        handleImagePermissions()
        loadDataToDatabase()
    }

    fun handleImagePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(binding.root, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionRegisterLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionRegisterLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    // registers the activty request and launchers at the CREATE stage. -> you have to initialize them in onCreate
    // variable.launch(intent)
    private fun handlePermission(){
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                run {
                    if (result.resultCode != RESULT_OK) {
                        var intentFromResult = result.data
                        if(intentFromResult != null) {
                             selectedImage = intentFromResult.data as Uri
                            imageReference?.putFile(selectedImage)
                            try {
                                var selectedBitmap : Bitmap? = null
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        this@SetupActivity.contentResolver,
                                        selectedImage!!
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)

                                    binding.profileImage.setImageBitmap(selectedBitmap)

                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        this@SetupActivity.contentResolver,
                                        selectedImage
                                    )
                                    binding.profileImage.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                    }
                }

            }

        permissionRegisterLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                result ->
            run {
                if (result) {
                    val intentToGallery = Intent(Intent.ACTION_ALL_APPS,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
                else {
                    Toast.makeText(this@SetupActivity, "Permission is required.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    fun changeNameClicked(view: View) {
        name = binding.varNameText.text.toString()
        doc.update(
            "name",name
        )
}

    private fun loadDataToDatabase() {
        val map :HashMap<String,String> = hashMapOf(
            "imageUri" to selectedImage.toString(),
            "name" to name,
            "phone" to auth.currentUser!!.phoneNumber.toString(),
            "about" to binding.varAboutText.text.toString()
        )
        doc.set(map).addOnSuccessListener {
            Toast.makeText(this,"Your data data has successfully been saved",Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Log.d(it.localizedMessage,"error")
        }
    }

    fun AboutChangeClicked(view: View) {
        about = binding.varAboutText.text.toString()
        doc.update(
            "about",about
        )
}

}