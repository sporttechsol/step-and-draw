package by.step.draw.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import by.step.draw.R
import by.step.draw.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_info.*

class InfoDialog : BaseDialogFragment(),
    View.OnClickListener {

    private var cancelListener: InfoDialogButtonListener? = null
    private var option1Listener: InfoDialogButtonListener? = null
    private var option2Listener: InfoDialogButtonListener? = null

    override fun getFragmentLayout() = R.layout.dialog_info

    override fun isHeightWrapContent() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenAnimDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cvContent.setOnTouchListener { v, event -> true }

        arguments?.let {
            tvTitle.setText(it.getInt(KEY_TITLE))
            tvDescr.setText(it.getInt(KEY_DESCR))
            bCancel.setText(it.getInt(KEY_TEXT_BUTTON_CANCEL))
            ivReward.setImageResource(it.getInt(KEY_ICON))
            val textBOption1 = it.getInt(KEY_TEXT_BUTTON_OPTION_1, -1)
            if (textBOption1 != -1) {
                bOption1.visibility = View.VISIBLE
                bOption1.setText(textBOption1)
                bOption1.setOnClickListener(this)
            }

            val textBOption2 = it.getInt(KEY_TEXT_BUTTON_OPTION_2, -1)
            if (textBOption2 != -1) {
                bOption2.visibility = View.VISIBLE
                bOption2.setText(textBOption2)
                bOption2.setOnClickListener(this)
            }
        }

        mlParent.setTransitionListener(object : MotionLayout.TransitionListener {

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.step_3 -> dismiss()
                }
            }

            override fun onTransitionTrigger(
                p0: MotionLayout?,
                p1: Int,
                p2: Boolean,
                p3: Float
            ) {
            }
        }
        )

        vBackground.setOnClickListener(this)
        bCancel.setOnClickListener(this)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                cancelClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mlParent.setTransitionListener(null)
        mlParent.clearAnimation()
        cancelListener = null
        option1Listener = null
        option2Listener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.vBackground, R.id.bCancel -> cancelClicked()
            R.id.bOption1 -> option1Clicked()
            R.id.bOption2 -> option2Clicked()
        }
    }

    fun setCancelListener(buttonListener: InfoDialogButtonListener) {
        this.cancelListener = buttonListener
    }

    fun setOption1Listener(buttonListener: InfoDialogButtonListener) {
        this.option1Listener = buttonListener
    }

    fun setOption2Listener(buttonListener: InfoDialogButtonListener) {
        this.option2Listener = buttonListener
    }

    private fun cancelClicked() {
        cancelListener?.onClicked()
        showTransition(R.id.transition_hide)
    }

    private fun option1Clicked() {
        option1Listener?.onClicked()
        showTransition(R.id.transition_hide)
    }

    private fun option2Clicked() {
        option2Listener?.onClicked()
        showTransition(R.id.transition_hide)
    }

    private fun showTransition(id: Int) {
        if (mlParent.progress == 1.0f) {
            mlParent.setTransition(id)
            mlParent.transitionToEnd()
        }
    }

    companion object {

        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_DESCR = "KEY_DESCR"
        const val KEY_ICON = "KEY_ICON"
        const val KEY_TEXT_BUTTON_CANCEL = "KEY_TEXT_BUTTON_CANCEL"
        const val KEY_TEXT_BUTTON_OPTION_1 = "KEY_TEXT_BUTTON_OPTION_1"
        const val KEY_TEXT_BUTTON_OPTION_2 = "KEY_TEXT_BUTTON_OPTION_2"

        fun newInstance(
            title: Int, descr: Int, bCancelText: Int, resIcon: Int,
            bOption1Text: Int? = null, bOption2Text: Int? = null
        ): InfoDialog {
            return InfoDialog().apply {
                arguments = Bundle().apply {
                    putInt(KEY_TITLE, title)
                    putInt(KEY_DESCR, descr)
                    putInt(KEY_ICON, resIcon)
                    putInt(KEY_TEXT_BUTTON_CANCEL, bCancelText)
                    bOption1Text?.let {
                        putInt(KEY_TEXT_BUTTON_OPTION_1, it)
                    }
                    bOption2Text?.let {
                        putInt(KEY_TEXT_BUTTON_OPTION_2, it)
                    }
                }
            }
        }
    }
}

interface InfoDialogButtonListener {
    fun onClicked()
}