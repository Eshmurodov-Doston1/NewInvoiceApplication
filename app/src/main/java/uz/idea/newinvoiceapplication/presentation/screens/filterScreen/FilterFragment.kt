package uz.idea.newinvoiceapplication.presentation.screens.filterScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentFilterBinding
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment

class FilterFragment : BaseFragment<FragmentFilterBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentFilterBinding.inflate(inflater,container,false)

    override fun init() {

    }

}