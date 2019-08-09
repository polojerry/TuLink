package com.polotechnologies.tulink.fragments


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.polotechnologies.tulink.R
import com.polotechnologies.tulink.dataModels.Profile
import com.polotechnologies.tulink.databinding.FragmentProfileBinding
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    var REQUEST_IMAGE_GET = 1

    var CAMERA = 1
    var GALLERY = 1

    private var PERMISSIONS_REQUEST_CAMERA = 1001

    lateinit var binding : FragmentProfileBinding
    lateinit var mAuth : FirebaseAuth
    lateinit var mDatabaseReference : DatabaseReference
    lateinit var mStorageReference: StorageReference

    var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        mStorageReference = FirebaseStorage.getInstance().reference

        binding.imgSignUpProfilePicture.setOnClickListener{
            selectImage()
        }

        binding.btnFinishSignUp.setOnClickListener {
            startProfileUpload()

        }

        return binding.root
    }

    private fun startProfileUpload() {
        uploadProfileImage(selectedImageUri)

    }

    private fun selectImage() {
        showSelectPictureDialog()

    }

    private fun showSelectPictureDialog() {
        val pictureDialog = context?.let { AlertDialog.Builder(it) }
        pictureDialog?.setTitle("Select Location")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog?.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> selectPhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog?.show()
    }

    private fun selectPhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS)
            }
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_CAMERA)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(intent, CAMERA)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    // Permission has already been granted
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    startActivityForResult(intent, CAMERA)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val imageURI = data.data

                selectedImageUri = imageURI
                try
                {
                    val bitmapImage = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageURI)

                    binding.imgSignUpProfilePicture.setImageBitmap(bitmapImage)



                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            if (data != null)
            {
                val contentURI = data.data
                try
                {
                    val bitmapImage = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)

                    binding.imgSignUpProfilePicture.setImageBitmap(bitmapImage)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        if(binding.etSignUpProfileName.text.isNullOrEmpty()){
            binding.etSignUpProfileName.error = "Required"
            return
        }
        val userID = mAuth.currentUser?.uid

        val storageReference = mStorageReference.child("profileImages/$userID.png")
        val uploadTask = storageReference.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageReference.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                uploadProfileDetails(downloadUri)

            } else {
                // Handle failures
                // ...
            }
        }

    }


    private fun uploadProfileDetails(downloadUri: Uri?) {
        val userName = binding.etSignUpProfileName.text.toString().trim()
        val profileImageUrl = downloadUri.toString().trim()

        val userId  = mAuth.currentUser?.uid

        val profile  = Profile(userId, profileImageUrl,userName,"", "",ServerValue.TIMESTAMP)
        if (userId != null) {
            mDatabaseReference.child(userId).setValue(profile).addOnSuccessListener {
                Toast.makeText(context,"Profile Updated",Toast.LENGTH_SHORT).show()
                //findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }.addOnFailureListener{
                Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
            }
        }


    }

}
