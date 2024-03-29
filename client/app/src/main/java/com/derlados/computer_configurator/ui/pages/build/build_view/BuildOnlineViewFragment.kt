package com.derlados.computer_configurator.ui.pages.build.build_view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.marginStart
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.Comment
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.entities.build.BuildComponent
import com.derlados.computer_configurator.ui.pages.build.BuildViewFragment
import com.derlados.computer_configurator.ui.decorators.AnimOnTouchListener
import com.derlados.computer_configurator.ui.pages.component_info.ComponentInfoFragment
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_build_section.view.*
import kotlinx.android.synthetic.main.inflate_comment.view.*
import kotlinx.android.synthetic.main.inflate_comment_template.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class BuildOnlineViewFragment : BuildViewFragment(), BuildOnlineView {
    private val CHILD_COMMENT_MARGIN = 20f
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var llComments: LinearLayout
    private lateinit var tvCommentsHead: TextView
    private lateinit var btAddComment: Button
    private lateinit var etNewComment: EditText
    private lateinit var imgNewComment: ImageView
    private var createdTemplate: View? = null


    private var newCommentPhotoUrl: String? = null
    private var isActiveAddCommentsMode = true
    private lateinit var presenter: OnlineBuildPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        currentFragment = inflater.inflate(R.layout.fragment_build, container, false)

        initFields()

        presenter = OnlineBuildPresenter(this, App.app.resourceProvider)
        presenter.init()
        return currentFragment
    }

    override fun onDestroy() {
        presenter.finish()
        super.onDestroy()
    }

    override fun initFields() {
        super.initFields()
        // Отключение полей EditText которые используются в конструкторе
        disableEditText(etName)
        disableEditText(etDesc)

        currentFragment.fragment_build_tv_status_or_user_head.setText(R.string.creator)
        tvCompatibility.visibility = View.GONE
        currentFragment.fragment_build_tv_status_or_user_value.setTextColor(Color.WHITE)

        llComments = currentFragment.fragment_build_ll_comments
        currentFragment.fragment_build_ll_comments_container.visibility = View.VISIBLE
        tvCommentsHead = currentFragment.fragment_build_tv_comments_head

        imgNewComment = currentFragment.fragment_build_inc_new_comment.inflate_comment_template_img_new_comment
        etNewComment = currentFragment.fragment_build_inc_new_comment.inflate_comment_template_et_new_comment_text
        btAddComment = currentFragment.fragment_build_inc_new_comment.inflate_comment_template_bt_add_comment
        btAddComment.setOnClickListener {
            val text = etNewComment.text.toString()
            etNewComment.setText("")
            presenter.addComment(0, text)
        }
    }

    /**
     * Удаление пустых групп комплектующих
     */
    override fun deleteEmptyLists() {
        for ((_, value) in componentContainers) {
            val componentBt = value.inflate_build_section_bt
            val componentContainer = value.inflate_build_section_ell_components

            val componentList = componentContainer.getChildAt(0) as LinearLayout

            if (componentList.childCount == 0) {
                componentContainer.visibility = View.GONE
                componentBt.visibility = View.GONE
            }
        }
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Отключение всего функционала, а так же стилистических особенностей EditText
     * @param editText - элемент EditText
     */
    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null

        if (editText.background is InsetDrawable) {
            val insetDrawable = editText.background as InsetDrawable
            val originalDrawable = insetDrawable.drawable!!
            editText.background = InsetDrawable(originalDrawable, 0, 0, 0, 0)
            editText.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildComponent, parent: LinearLayout): View {
        val card = super.createComponentCard(category, isMultiple, buildComponent, parent)
        val component = buildComponent.component

        card.inflate_component_item_bt_favorite.visibility = View.GONE

        card.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
            openComponentInfo(category, component)
            return@OnTouchListener true
        }))

        // Открытие блока на изменение количества комплектующего (для ОЗУ, накопителей и т.д.)
        if (isMultiple) {
            card.inflate_component_item_bt_increase.visibility = View.GONE
            card.inflate_component_item_bt_reduce.visibility = View.GONE
        }

        return card
    }

    private fun openComponentInfo(category: ComponentCategory, component: Component) {
        presenter.selectComponentToVIew(category, component)
        frListener.nextFragment(this, ComponentInfoFragment(), BackStackTag.COMPONENT_INFO)
    }

    override fun setUsername(username: String) {
        currentFragment.fragment_build_tv_status_or_user_value.text = username
    }

    /**
     * Инициализация аватарки текущего пользователя
     * @param photoUrl - url аватарки пользователя
     */
    override fun setUserPhoto(photoUrl: String) {
        newCommentPhotoUrl = photoUrl
        Picasso.get().load(photoUrl).into(imgNewComment)
    }

    /**
     * Отрисовка всех комментариев. Комментарии отрисовуются в соответствии с иерархией, сначала
     * комментарий у которого нету родителя, а потом все его дочерние с отступом
     * @param comments - массив с комментариями
     */
    override fun setComments(comments: ArrayList<Comment>) {
        currentFragment.fragment_build_tv_comments_head.text = App.app.getString(R.string.comments, comments.size)

        for (comment in comments) {
            if (comment.parentId == null) {
                val commentView = createCommentView(comment)
                val childComments = comments.filter { c -> c.parentId == comment.id  }
                llComments.addView(commentView)

                // Добавление дочерних комментариев
                for (childComment in childComments) {
                    val childCommentView = createCommentView(childComment)
                    childCommentView.inflate_comment_tv_answer.visibility = View.GONE
                    (childCommentView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = dpToPx(CHILD_COMMENT_MARGIN)
                    llComments.addView(childCommentView)
                }
            }
        }
    }

    override fun disableCommentsAddMode() {
        currentFragment.fragment_build_inc_new_comment.visibility = View.GONE
        isActiveAddCommentsMode = false
    }

    /**
     * Добавление нового комментария по нужному индексу
     * @param newComment - новый комментарий
     * @param index - индекс
     */
    override fun appendComment(newComment: Comment, index: Int, isChild: Boolean) {
        createdTemplate?.let {
            llComments.removeView(createdTemplate)
            createdTemplate = null
        }

        val commentView = createCommentView(newComment)
        if (isChild) {
            (commentView.layoutParams as ViewGroup.MarginLayoutParams).marginStart = dpToPx(CHILD_COMMENT_MARGIN)
        }

        llComments.addView(commentView, index)
        currentFragment.fragment_build_tv_comments_head.text = App.app.getString(R.string.comments, llComments.childCount)
    }

    /**
     * Диаологовое окно для подтверждения того, что сборка будет сохранена на сервере
     */
    private fun showDialogReportComment(commentId: Int, comment: View) {
        val reportedCommentIndex = llComments.indexOfChild(comment)

        val tvDialog = layoutInflater.inflate(R.layout.inflate_dialog_text, null) as TextView
        tvDialog.text =  "Пожаловаться на нарушение правил ?"

        AlertDialog.Builder(context, R.style.DarkAlert)
            .setCustomTitle(tvDialog)
            .setPositiveButton("Да") { _, _ -> presenter.reportComment(commentId, reportedCommentIndex) }
            .setNegativeButton("Нет") { _, _ -> }
            .show()
    }

    // TODO Удаление работает тупо, смотрит по отступу являет ли комментарий главным и удаляет его дочерние так же смотря по оступу
    override fun hideComment(index: Int) {
        val childMargin = dpToPx(CHILD_COMMENT_MARGIN)
        val isParent = llComments.getChildAt(index).marginStart != childMargin
        llComments.removeViewAt(index)

        if (isParent) {
            while (llComments.getChildAt(index) != null && llComments.getChildAt(index).marginStart == childMargin) {
                llComments.removeViewAt(index)
            }
        }

        Toast.makeText(context, R.string.report_was_sended, Toast.LENGTH_SHORT).show()
    }

    /**
     * Создание шаблона для ввода ответа на комментарий. Одновременно может существовать только один
     * шаблон, потому он удаляется если уже существует.
     * @param commentToAnswer - элемент комментария, к которому нужно прицепить ответ
     */
    private fun createAnswerTemplate(commentId: Int, commentToAnswer: View) {
        createdTemplate?.let {
            llComments.removeView(createdTemplate)
        }
        val indexToAdd = llComments.indexOfChild(commentToAnswer) + 1

        createdTemplate = layoutInflater.inflate(R.layout.inflate_comment_template, llComments, false)
        newCommentPhotoUrl?.let {
            Picasso.get().load(newCommentPhotoUrl).into(createdTemplate?.inflate_comment_template_img_new_comment)
        }
        createdTemplate?.inflate_comment_template_bt_add_comment?.setOnClickListener {
            val text = createdTemplate?.inflate_comment_template_et_new_comment_text?.text.toString()
            presenter.addComment(indexToAdd, text, commentId)
        }
        createdTemplate?.inflate_comment_template_et_new_comment_text?.requestFocus()
        createdTemplate?.inflate_comment_template_et_new_comment_text?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = createdTemplate?.inflate_comment_template_et_new_comment_text?.text.toString()
                presenter.addComment(indexToAdd, text, commentId)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        createdTemplate?.inflate_comment_template_et_new_comment_text?.setRawInputType(InputType.TYPE_CLASS_TEXT)

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        llComments.addView(createdTemplate, indexToAdd)
    }

    /**
     * Создание элемента комментария из шаблона и наполнение данными
     * @param comment - данные комментария
     */
    private fun createCommentView(comment: Comment): View {
        val commentView = layoutInflater.inflate(R.layout.inflate_comment, llComments, false)
        comment.user.photo?.let {
            Picasso.get().load(it).into(commentView.inflate_comment_img)
        }
        commentView.inflate_comment_tv_username.text = comment.user.username
        commentView.inflate_comment_tv_text.text = comment.text

        val formatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        commentView.inflate_comment_tv_date.text = formatter.format(comment.creationDate)

        if (presenter.isAuth()) {
            commentView.inflate_comment_tv_answer.setOnClickListener {
                createAnswerTemplate(comment.id, commentView)
            }

            commentView.inflate_comment_img_report.visibility = View.VISIBLE
            commentView.inflate_comment_img_report.setOnClickListener {
                showDialogReportComment(comment.id, commentView)
            }
        } else {
            commentView.inflate_comment_tv_answer.visibility = View.GONE
        }

        return commentView
    }

    private fun dpToPx(dp: Float): Int {
        val r: Resources = resources

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.displayMetrics
        ).toInt()
    }
}