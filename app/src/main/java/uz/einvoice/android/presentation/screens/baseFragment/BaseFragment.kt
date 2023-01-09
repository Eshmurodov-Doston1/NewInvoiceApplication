package uz.einvoice.android.presentation.screens.baseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import uz.einvoice.android.presentation.activities.AuthActivity
import uz.einvoice.android.presentation.activities.MainActivity

abstract class BaseFragment<VB:ViewBinding>:Fragment() {
    private var _binding : VB? = null
    val binding :VB get() = _binding!!

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    val mainActivity:MainActivity get() = (activity as MainActivity)
    val authActivity: AuthActivity get() = (activity as AuthActivity)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         if (_binding == null){
            _binding = inflateViewBinding(inflater,container)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun init()
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}

