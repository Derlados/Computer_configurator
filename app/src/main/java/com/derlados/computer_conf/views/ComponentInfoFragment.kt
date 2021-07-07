package com.derlados.computer_conf.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.view_interfaces.ComponentInfoView
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.ComponentInfoPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_component_data.view.*
import kotlinx.android.synthetic.main.inflate_attribute_string.view.*

class ComponentInfoFragment : Fragment(), ComponentInfoView {
    private lateinit var attributeList: LinearLayout // Контейнер в который помещается все характеристики товара
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var presenter: ComponentInfoPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_component_data, container, false)
        attributeList = currentFragment.findViewById(R.id.fragment_component_data_attributes)
        presenter = ComponentInfoPresenter(this, App.resourceProvider)
        presenter.init()

        return currentFragment
    }

    override fun initMarkBt(text: String, onClickAction: () -> Unit ) {
        val btMark = currentFragment.findViewById<Button>(R.id.fragment_component_data_bt_mark)
        btMark.text = text
        btMark.setOnClickListener {
            onClickAction()
        }
    }

    override fun setComponentInfo(component: Component) {
        val container: LinearLayout = currentFragment.fragment_component_data_attributes

        currentFragment.fragment_component_data_name.text = component.name
        currentFragment.fragment_component_data_price.text = App.app.resources.getString(R.string.component_price, component.price)
        if (component.image != null) {
            currentFragment.fragment_component_data_img.setImageBitmap(component.image)
        } else {
            Picasso.get().load(component.imageUrl).into(currentFragment.fragment_component_data_img)
        }

        for (attribute in component.attributes) {
            val dataString: LinearLayout = layoutInflater.inflate(R.layout.inflate_attribute_string, container, false) as LinearLayout
            dataString.inflate_attribute_string_name.text = attribute.name
            dataString.inflate_attribute_string_value.text = attribute.value
            container.addView(dataString)
        }
    }

    override fun returnToBuild() {
        fragmentListener.popBackStack(BackStackTag.BUILD)
    }

    override fun setDefaultImage(defaultId: Int) {
        currentFragment.fragment_component_data_img.setImageDrawable(
                ResourcesCompat.getDrawable(App.app.resources, defaultId, App.app.theme)
        )
    }
}