package core.UI.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.app.fooddeliverysystem.R
import core.UI.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TextFragment : BaseFragment() {
    private var text: String? = null
    private var color: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(ARG_PARAM1)
            color = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(text: String, color: Int) =
            TextFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, text)
                    putInt(ARG_PARAM2, color)
                }
            }
    }

    override fun initializeComponents(rootView: View) {
        rootView.findViewById<TextView>(R.id.textView).setText(text)
        rootView.setBackgroundColor(resources.getColor(color!!))
    }

    override fun setupListeners(rootView: View?) {

    }
}
