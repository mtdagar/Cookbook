package com.example.cookbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cookbook.database.RecipeDatabase
import com.example.cookbook.databinding.ActivitySplashBinding
import com.example.cookbook.entities.Category
import com.example.cookbook.entities.Meal
import com.example.cookbook.entities.MealsItems
import com.example.cookbook.interfaces.GetDataService
import com.example.cookbook.retrofitclient.RetrofitClientInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : BaseActivity(), EasyPermissions.RationaleCallbacks,
    EasyPermissions.PermissionCallbacks{

    private var READ_STORAGE_PERM = 123

    lateinit var binding: ActivitySplashBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readStorageTask()

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this@SplashActivity, SignupActivity::class.java))
            finish()
        }

        binding.btnSkip.setOnClickListener{
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

    }

    fun getCategories() {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getCategoryList()
        call.enqueue(object : retrofit2.Callback<Category> {
            override fun onFailure(call: Call<Category>, t: Throwable) {

                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
            }

            override fun onResponse(
                    call: Call<Category>,
                    response: Response<Category>
            ) {

                for (arr in response.body()!!.categorieitems!!) {
                    getMeal(arr.strcategory)
                }
                insertDataIntoRoomDb(response.body())
            }

        })
    }

    fun getMeal(categoryName: String) {
        val service = RetrofitClientInstance.retrofitInstance!!.create(GetDataService::class.java)
        val call = service.getMealList(categoryName)
        call.enqueue(object : Callback<Meal> {
            override fun onFailure(call: Call<Meal>, t: Throwable) {

                binding.loader.visibility = View.INVISIBLE
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
            }

            override fun onResponse(
                    call: Call<Meal>,
                    response: Response<Meal>
            ) {

                insertMealDataIntoRoomDb(categoryName, response.body())
            }

        })
    }

    fun insertDataIntoRoomDb(category: Category?) {

        launch {
            this.let {

                for (arr in category!!.categorieitems!!) {
                    RecipeDatabase.getDatabase(this@SplashActivity)
                            .recipeDao().insertCategory(arr)
                }
            }
        }


    }


    fun insertMealDataIntoRoomDb(categoryName: String, meal: Meal?) {

        launch {
            this.let {


                for (arr in meal!!.mealsItem!!) {
                    var mealItemModel = MealsItems(
                            arr.id,
                            arr.idMeal,
                            categoryName,
                            arr.strMeal,
                            arr.strMealThumb
                    )
                    RecipeDatabase.getDatabase(this@SplashActivity)
                            .recipeDao().insertMeal(mealItemModel)
                    Log.d("mealData", arr.toString())
                }

                binding.btnSkip.visibility = View.VISIBLE
            }
        }


    }

    fun clearDataBase() {
        launch {
            this.let {
                RecipeDatabase.getDatabase(this@SplashActivity).recipeDao().clearDb()
            }
        }
    }

    private fun hasReadStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun readStorageTask() {
        if (hasReadStoragePermission()) {
            clearDataBase()
            getCategories()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your storage,",
                    READ_STORAGE_PERM,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }


}