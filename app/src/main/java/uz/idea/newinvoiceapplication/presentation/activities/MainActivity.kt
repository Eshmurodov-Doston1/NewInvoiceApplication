package uz.idea.newinvoiceapplication.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.domain.models.menuModel.Data
import uz.idea.domain.models.menuModel.MenuDataModel
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.newinvoiceapplication.databinding.ActivityMainBinding
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.MENU_ERROR
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.newinvoiceapplication.utils.container.ContainerApplication
import uz.idea.newinvoiceapplication.utils.extension.*
import uz.idea.newinvoiceapplication.utils.uiStates.UIController
import uz.idea.newinvoiceapplication.vm.mainVM.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),UIController{
    private val binding:ActivityMainBinding by viewBinding()
    // mainViewModel
    private val mainViewModel:MainViewModel by viewModels()
    // container application
    lateinit var containerApplication: ContainerApplication
    //click menu data
    private val liveData = MutableLiveData<Data>()
    // generic main menu item
    private val genericRvAdapter:GenericRvAdapter<Data> by lazy {
        GenericRvAdapter(R.layout.item_menu){ data, position, clickType ->
            genericRvAdapter.itemClick(position)
            binding.drawerLayout.closeDrawer(GravityCompat.START,true)
            liveData.postValue(data)
        }
    }


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            binding.appBarMain.bottomNavigationView.background = null
            binding.appBarMain.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            statusBarColor(R.color.white)
            setSupportActionBar(binding.appBarMain.toolbar)
            // fab icon tint
            binding.appBarMain.fab.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.primary_color))

            navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
            // container appplication object
            containerApplication = ContainerApplication(this@MainActivity,this@MainActivity,navHostFragment.navController)

            binding.appBarMain.fab.setOnClickListener {
                navHostFragment.navController.popBackStack()
                binding.appBarMain.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            }

            val drawerLayout: DrawerLayout = binding.drawerLayout
            val navView: NavigationView = binding.navView
            appBarConfiguration = AppBarConfiguration(setOf(
                R.id.filter,
                R.id.home,
                R.id.settings
            ), drawerLayout)
            setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
            binding.appBarMain.bottomNavigationView.setupWithNavController(navHostFragment.navController)
            navView.setupWithNavController(navHostFragment.navController)
            menuList()

        }
    }

    private fun menuList(){
        binding.apply {
            // swipe refresh menu
            binding.swipeRefresh.setOnRefreshListener {
                menuList()
            }
            binding.swipeRefresh.setColorSchemeResources(R.color.secondary_color)
            mainViewModel.getMenuList(getLanguage(this@MainActivity))
            lifecycleScope.launchWhenCreated {
                mainViewModel.menuState.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            binding.rvMenu.gone()
                            binding.progress.visible()
                        }
                        is ResponseState.Success->{
                            binding.rvMenu.visible()
                            binding.progress.gone()
                            val menuData = result.data?.parseClass(MenuDataModel::class.java)
                            genericRvAdapter.submitList(menuData?.data?: emptyList())
                            binding.rvMenu.adapter = genericRvAdapter
                            binding.rvMenu.setItemViewCacheSize(menuData?.data?.size?:0)
                            liveData.postValue(menuData?.data?.get(0))
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is ResponseState.Error->{
                            binding.rvMenu.visible()
                            binding.progress.gone()
                            if (result.exception.localizedMessage.isNotEmptyOrNull()){
                                val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                containerApplication.dialogData(MENU_ERROR,errorAuth){ clickType->
                                    if(clickType==1) menuList()
                                }
                            }else{
                                containerApplication.dialogData(NO_INTERNET,null){ clickType ->
                                    if(clickType==1) menuList()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun statusBarColor(color:Int){
        window.statusBarColor = ContextCompat.getColor(this,color)
    }

    override fun clickMenu():LiveData<Data> = liveData
}