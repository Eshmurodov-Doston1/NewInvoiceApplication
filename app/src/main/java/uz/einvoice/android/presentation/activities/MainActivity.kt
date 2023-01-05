package uz.einvoice.android.presentation.activities

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import uz.einvoice.android.BuildConfig.EIMZO_API_KEY
import uz.einvoice.android.R
import uz.einvoice.android.adapters.genericRvAdapter.GenericRvAdapter
import uz.einvoice.android.databinding.ActivityMainBinding
import uz.einvoice.android.databinding.BottomSheetMainBinding
import uz.einvoice.android.utils.appConstant.AppConstant.EN
import uz.einvoice.android.utils.appConstant.AppConstant.MENU_ERROR
import uz.einvoice.android.utils.appConstant.AppConstant.NO_INTERNET
import uz.einvoice.android.utils.appConstant.AppConstant.RU
import uz.einvoice.android.utils.appConstant.AppConstant.UZ
import uz.einvoice.android.utils.container.ContainerApplication
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.utils.language.LocaleManager
import uz.einvoice.android.utils.uiStates.UIController
import uz.einvoice.android.vm.containerVm.ContainerViewModel
import uz.einvoice.android.vm.mainVM.MainViewModel
import uz.einvoice.domain.models.menuModel.Children
import uz.einvoice.domain.models.menuModel.Data
import uz.einvoice.domain.models.menuModel.MenuDataModel
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.sicnt.horcrux.Constants.*
import uz.sicnt.horcrux.Horcrux
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),UIController{
    private val binding:ActivityMainBinding by viewBinding()
    // mainViewModel
    private val mainViewModel:MainViewModel by viewModels()
    // mainViewModel
    val containerViewModel:ContainerViewModel by viewModels()
    // container application
    lateinit var containerApplication: ContainerApplication
    //click menu data
    val liveData = MutableLiveData<Data>()
    // childrenList
    private val childrenList:LinkedList<Data> = LinkedList()
    lateinit var horcrux: Horcrux
    // generic main menu item
    private val genericRvAdapter:GenericRvAdapter<Data> by lazy {
        GenericRvAdapter(R.layout.item_menu){ data, position, clickType ->
            genericRvAdapter.itemClick(position)
            binding.drawerLayout.closeDrawer(GravityCompat.START,true)
            liveData.postValue(data)

            containerViewModel.setData(childrenList[0].children?.get(0))
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navHostFragment: NavHostFragment
    lateinit var packages:List<ApplicationInfo>
    lateinit var pm: PackageManager
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        horcrux = Horcrux(this, EIMZO_API_KEY)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        // container appplication object
        containerApplication = ContainerApplication(this@MainActivity,this@MainActivity,navHostFragment.navController)


        saveMeasureData()
        binding.apply {
            binding.appBarMain.bottomNavigationView.background = null
            binding.appBarMain.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
            statusBarColor(R.color.white)
            setSupportActionBar(binding.appBarMain.toolbar)
            // save measure database
            // fab icon tint
            binding.appBarMain.fab.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.primary_color))
            lifecycleScope.launchWhenCreated {
                containerViewModel.children.collect { children->
                    liveData.observe(this@MainActivity) { data ->
                        when(LocaleManager.getLanguage(this@MainActivity)){
                            UZ -> {
                                supportActionBar?.title = data.title_uz + " | ${children?.title_uz}"
                            }
                            RU->{
                                supportActionBar?.title = data.title + " | ${children?.title}"
                            }
                            EN->{
                                supportActionBar?.title = data.title_uz + " | ${children?.title_uz}"
                            }
                            else->{
                                supportActionBar?.title = data.title + " | ${children?.title}"
                            }
                        }
                    }
                }
            }


            binding.appBarMain.fab.setOnClickListener {
                if (navHostFragment.navController.currentDestination?.id==R.id.home){
                    bottomSheetDocument(liveData.value)
                }else{
                    navHostFragment.navController.popBackStack()
                    binding.appBarMain.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
                }
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
            navUiController()
            menuList()
        }
    }


    fun saveMeasureData(){
        containerViewModel.getMeasure(getLanguage(this@MainActivity))
    }

    private lateinit var categoryAdapter:GenericRvAdapter<Children>

    private fun bottomSheetDocument(data: Data?){
        if (data?.children != null)  {
            val bottomSheetDialog = BottomSheetDialog(this)
            val bindingBottom = BottomSheetMainBinding.inflate(LayoutInflater.from(this))

            bindingBottom.titleMenu.text = when(LocaleManager.getLanguage(this)){
                UZ->{
                    data.title_uz
                }
                RU->{
                    data.title
                }
                EN->{
                    data.title_uz
                }
                else-> data.title
            }

            categoryAdapter = GenericRvAdapter(R.layout.item_bottomsheet_data){ children, position, _ ->
                categoryAdapter.itemClick(position)
                containerViewModel.setData(children)
                bottomSheetDialog.dismiss()
            }
            categoryAdapter.submitList(data.getMenuList())
            bindingBottom.rvCategory.adapter = categoryAdapter

            bottomSheetDialog.setContentView(bindingBottom.root)
            bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (!bottomSheetDialog.isShowing){
                bottomSheetDialog.show()
            }
        }else{
            toasty(getString(R.string.no_data))
        }
    }


    fun toasty(message:String){
        Toasty.info(this, message, Toast.LENGTH_SHORT,true).show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?: emptyList()) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun navUiController(){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.appBarMain.apply {
                if (controller.currentDestination?.id == R.id.home){
                  fab.setImageResource(R.drawable.ic_add)
                } else {
                    fab.setImageResource(R.drawable.ic_baseline_home_24)
                }
                // fab icon tint
                binding.appBarMain.fab.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.primary_color))
                if (controller.currentDestination?.id == R.id.home ||
                    controller.currentDestination?.id == R.id.filter ||
                    controller.currentDestination?.id == R.id.settings){
                    if (!bottomAppBar.isVisible && !fab.isVisible){
                        slideUp(bottomAppBar)
                        slideUp(fab)
                    }
                }else{
                    bottomAppBar.gone()
                    fab.gone()
                    if (bottomAppBar.isVisible && fab.isVisible){
                        slideDown(bottomAppBar)
                        slideDown(fab)
                    }
                }
            }
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
                            containerViewModel.setData(menuData?.data?.get(0)?.children?.get(0))

                            childrenList.clear()
                            childrenList.addAll(menuData?.data?: emptyList())
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



    fun motionAnimation(status:String?,message:String?){
        MotionToast.createColorToast(this, message = message.toString(),
            style = if (status != "error") MotionToastStyle.SUCCESS else MotionToastStyle.ERROR,
            position = MotionToast.GRAVITY_TOP,
            duration = MotionToast.LONG_DURATION,
            font =  ResourcesCompat.getFont(this,R.font.inter_medium)
        )
    }

    override fun bottomBarView(isVisible: Boolean) {
        binding.appBarMain.apply {
            if (!bottomAppBar.isVisible && !fab.isVisible){
                if (isVisible){
                    slideUp(bottomAppBar)
                    slideUp(fab)
                }
            } else {
                if (!isVisible) {
                    slideDown(bottomAppBar)
                    slideDown(fab)
                }
            }
        }
    }

    private fun statusBarColor(color:Int){
        window.statusBarColor = ContextCompat.getColor(this,color)
    }

    override fun loadingSave(isLoading: Boolean) = containerApplication.loadingSaved(isLoading)

    override fun clickMenu():LiveData<Data> = liveData

}