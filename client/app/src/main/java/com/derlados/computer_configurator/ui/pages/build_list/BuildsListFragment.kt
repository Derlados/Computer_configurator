package com.derlados.computer_configurator.ui.pages.build_list

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.models.entities.BuildData
import com.derlados.computer_configurator.ui.pages.build.build_constructor.BuildConstructorFragment
import com.derlados.computer_configurator.ui.pages.build.build_view.BuildOnlineViewFragment
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.derlados.computer_configurator.ui.adapters.BuildRecyclerAdapter
import com.derlados.computer_configurator.ui.pages.main.PageFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_build_list.view.*

class BuildsListFragment : PageFragment(), BuildsListView {
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var rvBuildRecycler: RecyclerView // Основной контейнер для бланков сборок
    private lateinit var createBt: FloatingActionButton

    private lateinit var listPresenter: BuildListPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_build_list, container, false)
        currentFragment.fragment_build_list_srl_refresh.isEnabled = false

        rvBuildRecycler = currentFragment.fragment_build_list_rv
        createBt = currentFragment.fragment_build_list_bt_add
        createBt.setOnClickListener {
            createBuild()
        }

        listPresenter = BuildListPresenter(this, App.app.resourceProvider)
        listPresenter.init()
        return currentFragment
    }

    override fun onDestroy() {
        rvBuildRecycler.adapter?.notifyItemRangeChanged(0, 0)
        listPresenter.finish()
        super.onDestroy()
    }

    /**
     * Установка данных о сборке
     * @see BuildData
     * @param buildsData - список сборок. Отправляется массив Builds, однако доступ предоставляется
     * в соответствии с интерфейсом BuildData, все элементы должны его наследовать
     */
    override fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>) {
        rvBuildRecycler.layoutManager = LinearLayoutManager(context)
        rvBuildRecycler.adapter = BuildRecyclerAdapter(buildsData, ::selectBuild, ::showDeleteDialog, ::showDialogAcceptPublish)
    }

    /**
     * Обновление списка через адаптер, если изменяется содержимое списка сборок
     * @param size - размер нового списка
     */
    override fun updateRangeBuildList(size: Int) {
        rvBuildRecycler.adapter?.notifyItemRangeChanged(0, size)
    }

    /**
     * Обновление конкретного элемента
     * @param index - индекс изменяемого элемента
     */
    override fun updateItemBuildList(index: Int) {
        rvBuildRecycler.adapter?.notifyItemChanged(index)
    }

    /**
     * Удаление конкретного элемента
     * @param index - индекс изменяемого элемента
     */
    override fun removeItemBuildList(index: Int) {
        rvBuildRecycler.adapter?.notifyItemRemoved(index)
    }

    /**
     * Диаологовое окно с предупреждением. На данный момент сообщает что нельзя по определенным
     * причинам сохранить сборку.
     */
    override fun showWarnDialog(message: String) {
        val tvDialog = layoutInflater.inflate(R.layout.inflate_dialog_text, null) as TextView
        tvDialog.text = message

        AlertDialog.Builder(context, R.style.DarkAlert)
                .setCustomTitle(tvDialog)
                .setPositiveButton("Ок") { _, _ ->  }
                .show()
    }

    /**
     * Диаологовое окно для подтверждения того, что сборка будет сохранена на сервере
     */
    private fun showDialogAcceptPublish(id: String) {
        val tvDialog = layoutInflater.inflate(R.layout.inflate_dialog_text, null) as TextView
        tvDialog.text =  getString(R.string.build_will_be_saved_on_server)

        AlertDialog.Builder(context, R.style.DarkAlert)
                .setCustomTitle(tvDialog)
                .setPositiveButton("Да") { _, _ -> listPresenter.saveBuildOnServer(id) }
                .setNegativeButton("Нет") { _, _ -> }
                .show()
    }

    private fun showDeleteDialog(id: String) {
        val tvDialog = layoutInflater.inflate(R.layout.inflate_dialog_text, null) as TextView
        tvDialog.text = getString(R.string.accept_delete)

        AlertDialog.Builder(context, R.style.DarkAlert)
                .setCustomTitle(tvDialog)
                .setPositiveButton("Да") { _, _ ->   listPresenter.removeBuild(id) }
                .setNegativeButton("Нет") { _, _ -> }
                .show()
   }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun openBuildConstructor() {
        frListener.nextFragment(this, BuildConstructorFragment(), BackStackTag.BUILD_CONSTRUCTOR)
    }

    override fun openBuildOnlineView() {
        frListener.nextFragment(this, BuildOnlineViewFragment(), BackStackTag.BUILD_VIEW)
    }

    private fun createBuild() {
        listPresenter.createNewBuild()
    }

    /** Обработчики кнопок для каждого элемента, по сути вызывают один метод, так как необходимо передать в адаптер их */
    //TODO может найдется способ передать напрямую из презентера и удалить эти методы отсюда

    private fun selectBuild(id: String) {
        listPresenter.selectBuild(id)
    }
}