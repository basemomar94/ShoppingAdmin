package com.bassem.shoppingadmin.ui.items

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.shoppingadmin.R
import com.bassem.shoppingadmin.databinding.AddNewItemFragmentBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

class New_item : Fragment(R.layout.add_new_item_fragment) {
    var _binding: AddNewItemFragmentBinding? = null
    val binding get() = _binding
    lateinit var db: FirebaseFirestore
    lateinit var title: String
    var price: String? = null
    var amount: Int? = null
    var imageUri: Uri? = null
    lateinit var documentID: String
    val REQUEST_CODE = 1
    var detailsMap: HashMap<String, Any>? = null
    var edit: Boolean = false
    lateinit var itemId: String
    var firebaseUrl: String? = null
    lateinit var mListener: EventListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            edit = bundle.getBoolean("edit")
            itemId = bundle.getString("item")!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddNewItemFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDetach() {
        super.onDetach()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillData()

        //listners
        binding!!.addBu.setOnClickListener {
            loading()
            if (imageUri == null && edit) {
                getDetails(firebaseUrl!!)
            } else {
                uploadPhoto()
            }
        }
        binding!!.addImage.setOnClickListener {
            pickPhoto()
        }

    }

    fun getDetails(imageUrl: String) {
        title = binding!!.itemTitle.text!!.trim().toString()
        amount = binding!!.itemAmount.text.toString().toInt()
        price = binding!!.itemPrice.text.toString()
        documentID = UUID.randomUUID().toString()
        detailsMap = hashMapOf()
        detailsMap!!["title"] = title
        detailsMap!!["amount"] = amount!!
        detailsMap!!["price"] = price!!
        detailsMap!!["photo"] = imageUrl
        detailsMap!!["id"] = documentID

        //Edit Hashmap to avoid getting a new id
        val editHashMap = HashMap<String, Any>()
        editHashMap["title"] = title
        editHashMap["amount"] = amount!!
        editHashMap["price"] = price!!
        editHashMap["photo"] = imageUrl

        if (edit) {
            updateDetails(editHashMap)
        } else {
            addItemDetails(detailsMap!!, documentID)

        }


    }

    fun addItemDetails(data: HashMap<String, Any>, id: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("items").document(id).set(data).addOnCompleteListener {
            if (it.isSuccessful) {

                Snackbar.make(
                    requireView(),
                    "Item is added",
                    Snackbar.LENGTH_LONG
                )
                    .show()
                clearFields()


            } else {
                Snackbar.make(
                    requireView(),
                    it.exception!!.message.toString(),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                normal()

            }
        }
    }

    fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data!!.data!!
            binding!!.imageView.setImageURI(imageUri)
            println("Gpp")
        } else {
            println("Failed $resultCode")
        }
    }

    fun uploadPhoto() {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storage = FirebaseStorage.getInstance().reference.child("items/$fileName")
        storage.putFile(imageUri!!).addOnSuccessListener { it ->
            val reuslt = it.metadata!!.reference!!.downloadUrl
            reuslt.addOnSuccessListener {
                val photoUrl = it.toString()
                getDetails(photoUrl)
            }


        }.addOnFailureListener {
            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
            normal()
        }

    }


    fun loading() {
        binding!!.addBu.visibility = View.INVISIBLE
        binding!!.progressBar.visibility = View.VISIBLE
    }

    fun normal() {
        binding!!.addBu.visibility = View.VISIBLE
        binding!!.progressBar.visibility = View.GONE
    }

    fun clearFields() {
        binding!!.imageView.setImageResource(R.drawable.photo)
        binding!!.itemPrice.text!!.clear()
        binding!!.itemAmount.text!!.clear()
        binding!!.itemTitle.text!!.clear()
        normal()
    }

    fun fillData() {
        if (edit) {
            db = FirebaseFirestore.getInstance()
            db.collection("items").document(itemId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    binding!!.itemPrice.setText(it.result!!.getString("price"))
                    binding!!.itemAmount.setText(
                        it.result!!.getDouble("amount")!!.toInt().toString()
                    )
                    binding!!.itemTitle.setText(it.result!!.getString("title"))
                    firebaseUrl = it.result!!.getString("photo")!!
                    if (imageUri == null) {
                        Glide.with(context!!).load(firebaseUrl).into(binding!!.imageView)
                    }
                }
            }
        }
    }

    fun updateDetails(data: HashMap<String, Any>) {
        db = FirebaseFirestore.getInstance()
        db.collection("items").document(itemId).update(data).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "item is updated", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

    }

}