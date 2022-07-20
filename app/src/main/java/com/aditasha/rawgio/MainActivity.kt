package com.aditasha.rawgio

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aditasha.rawgio.databinding.ActivityMainBinding
import com.aditasha.rawgio.ui.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_released, R.id.navigation_popularity
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)

        val searchManager = getSystemService<SearchManager>()
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager?.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                lifecycleScope.launchWhenCreated {
                    sharedViewModel.searchQuery = query
                    sharedViewModel.requestedList.emit(SEARCH)
                }
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                installFavoritesModule()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun installFavoritesModule() {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        val moduleName = getString(R.string.module_name)
        if (splitInstallManager.installedModules.contains(moduleName)) {
            moveToFavoritesActivity()
        } else {
            val request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()

            splitInstallManager.startInstall(request)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.success_module), Toast.LENGTH_SHORT)
                        .show()
                    moveToFavoritesActivity()
                }
                .addOnFailureListener {
                    Toast.makeText(this, getString(R.string.error_module), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun moveToFavoritesActivity() {
        startActivity(
            Intent(
                this@MainActivity,
                Class.forName(getString(R.string.module_class_name))
            )
        )
    }

    companion object {
        const val RELEASED = "released"
        const val ADDED = "added"
        const val SEARCH = "search"
        const val QUERY = "query"
        const val DEFAULT = "default"
    }
}