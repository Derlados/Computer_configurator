package com.derlados.computer_configurator.ui.pages.component_info

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.ui.components.AdMob
import com.derlados.computer_configurator.ui.decorators.AnimOnTouchListener
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_component_data.*
import kotlinx.android.synthetic.main.fragment_component_data.view.*
import kotlinx.android.synthetic.main.inflate_attribute_string.view.*


class ComponentInfoFragment : Fragment(), ComponentInfoView, MainActivity.OnBackPressedListener {
    private lateinit var attributeList: LinearLayout // Контейнер в который помещается все характеристики товара
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var presenter: ComponentInfoPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    private var mAdView: AdView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_component_data, container, false)
        attributeList = currentFragment.findViewById(R.id.fragment_component_data_attributes)
        presenter = ComponentInfoPresenter(this, App.app.resourceProvider)
        presenter.init()

        MobileAds.initialize(requireContext()) {}

        mAdView = currentFragment.adView
        ++AdMob.adCount
        if (AdMob.adCount % 2 == 0) {
            mAdView?.loadAd(AdMob.adRequest)
        } else {
            mAdView?.visibility =View.GONE
        }


        return currentFragment
    }

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initMarkBt(text: String, onClickAction: () -> Unit) {
        val btMark = currentFragment.findViewById<Button>(R.id.fragment_component_data_bt_mark)
        btMark.text = text
        btMark.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
            onClickAction()
            return@OnTouchListener true
        }))
    }

    override fun disableMarkBt() {
        val btMark = currentFragment.findViewById<Button>(R.id.fragment_component_data_bt_mark)
        btMark.visibility = View.GONE
    }

    override fun setComponentInfo(component: Component) {
        activity?.title = component.name
        val container: LinearLayout = currentFragment.fragment_component_data_attributes

        currentFragment.fragment_component_data_name.text = component.name
        currentFragment.fragment_component_data_price.text = App.app.resources.getString(
            R.string.component_price,
            component.price
        )

        if (component.image != null) {
            currentFragment.fragment_component_data_img.setImageBitmap(component.image)
        } else {
            Picasso.get().load(component.img).into(currentFragment.fragment_component_data_img)
        }

        for ((_, attribute) in component.attributes.toSortedMap()) {
            val dataString: LinearLayout = layoutInflater.inflate(
                R.layout.inflate_attribute_string,
                container,
                false
            ) as LinearLayout
            dataString.inflate_attribute_string_name.text = attribute.name
            dataString.inflate_attribute_string_value.text = attribute.value
            container.addView(dataString)
        }

        currentFragment.fragment_component_data_shop.text = component.shop
        currentFragment.fragment_component_data_link.text = component.url
    }

    override fun returnToBuild() {
        fragmentListener.popBackStack(BackStackTag.BUILD_CONSTRUCTOR)
    }

    override fun setDefaultImage(defaultId: Int) {
        currentFragment.fragment_component_data_img.setImageDrawable(
            ResourcesCompat.getDrawable(App.app.resources, defaultId, App.app.theme)
        )
    }


}