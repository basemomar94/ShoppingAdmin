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
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class New_item : Fragment(R.layout.add_new_item_fragment) {
    private var _binding: AddNewItemFragmentBinding? = null
    private val binding get() = _binding
    private lateinit var db: FirebaseFirestore
    lateinit var title: String
    private var price: String? = null
    private var details: String? = null
    private var amount: String? = null
    private var imageUri: Uri? = null
    private lateinit var documentID: String
    private val REQUEST_CODE = 1
    private var detailsMap: HashMap<String, Any>? = null
    private var edit: Boolean = false
    private lateinit var itemId: String
    private var firebaseUrl: String? = null
    private var category: String? = null


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
    ): View {
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
        binding!!.categoryChips.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId > -1) {
                val currentChip = view.findViewById<Chip>(checkedId)
                category = currentChip.text.toString()
            }


        }

    }

    private fun getDetails(imageUrl: String) {
        title = binding!!.itemTitle.text!!.trim().toString()
        amount = binding!!.itemAmount.text.toString()
        price = binding!!.itemPrice.text.toString()
        details = binding!!.itemDetails.text.toString()
        errorEmpty(title, binding!!.titleLayout)
        errorEmpty(amount.toString(), binding!!.amountLayout)
        errorEmpty(price!!, binding!!.priceLayout)
        errorEmpty(details!!, binding!!.detailsLayout)
        documentID = UUID.randomUUID().toString()
        detailsMap = hashMapOf()


        if (title.isNotEmpty() && amount.toString()
                .isNotEmpty() && price!!.isNotEmpty() && details!!.isNotEmpty()
        ) {
            detailsMap!!["title"] = title
            detailsMap!!["amount"] = amount!!.toInt()
            detailsMap!!["price"] = price!!
            detailsMap!!["photo"] = imageUrl
            detailsMap!!["id"] = documentID
            detailsMap!!["currentPrice"] = price!!
            detailsMap!!["details"] = details!!
            detailsMap!!["visible"] = true
            if (category != null) {
                detailsMap!!["category"] = category.toString()
            } else {
                detailsMap!!["category"] = "null"

            }

            //Edit Hashmap to avoid getting a new id
            val editHashMap = HashMap<String, Any>()
            editHashMap["title"] = title
            editHashMap["amount"] = amount!!.toInt()
            editHashMap["price"] = price!!
            editHashMap["photo"] = imageUrl
            editHashMap["details"] = details!!
            /*if (category!=null){
                editHashMap["category"] = category.toString()
            } else {
                editHashMap["category"] = "null"

            }*/
            if (edit) {
                updateDetails(editHashMap)
            } else {
                addItemDetails(detailsMap!!, documentID)

            }
        } else {
            normal()
        }


    }

    private fun addItemDetails(data: HashMap<String, Any>, id: String) {
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

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data!!.data!!
            binding!!.imageView.setImageURI(imageUri)
        } else {
            println("Failed $resultCode")
        }
    }

    private fun uploadPhoto() {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        if (imageUri != null) {
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
        } else {
            Toast.makeText(requireContext(), "please upload item photo", Toast.LENGTH_SHORT).show()
            normal()

        }


    }


    private fun loading() {
        binding!!.addBu.visibility = View.INVISIBLE
        binding!!.progressBar.visibility = View.VISIBLE
    }

    private fun normal() {
        binding!!.addBu.visibility = View.VISIBLE
        binding!!.progressBar.visibility = View.GONE
    }

    private fun clearFields() {
        binding!!.imageView.setImageResource(R.drawable.photo)
        binding!!.itemPrice.text!!.clear()
        binding!!.itemAmount.text!!.clear()
        binding!!.itemTitle.text!!.clear()
        binding!!.categoryChips.clearCheck()
        binding!!.itemDetails.text!!.clear()
        normal()
    }

    private fun fillData() {
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
                        Glide.with(requireContext()).load(firebaseUrl).into(binding!!.imageView)
                    }
                    binding!!.itemDetails.setText(it.result!!.getString("details"))
                }
            }
        }
    }

    private fun updateDetails(data: HashMap<String, Any>) {
        db = FirebaseFirestore.getInstance()
        db.collection("items").document(itemId).update(data).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "item is updated", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

    }

     fun errorEmpty(text: String, layout: TextInputLayout) {
        if (text.isEmpty()) {
            layout.error = "*required"

        } else {
            layout.isErrorEnabled = false
        }
    }

}