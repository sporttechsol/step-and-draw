package by.step.draw.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(getFragmentLayout(), container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return fragmentView
    }

    override fun onResume() {
        if (isHeightWrapContent()) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        super.onResume()
    }

    override fun onDestroyView() {
        // https://code.google.com/p/android/issues/detail?id=17423
        val dialog = dialog
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    protected abstract fun getFragmentLayout(): Int

    protected abstract fun isHeightWrapContent(): Boolean
}