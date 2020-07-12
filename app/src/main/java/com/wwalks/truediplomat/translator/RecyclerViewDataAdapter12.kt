package com.wwalks.truediplomat.translator

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView

import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.R


class RecyclerViewDataAdapter12(var context: Context, var languageName: List<String>, var pos: String, internal var rv: RecyclerView) : RecyclerView.Adapter<RecyclerViewDataAdapter12.SingleCheckViewHolder>() {
    private var onItemClickListener: AdapterView.OnItemClickListener? = null
    private var mSelectedItem = -1
    var selected = RecyclerView.NO_POSITION
        private set


    init {
        selected = -1
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleCheckViewHolder {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.single_row_choice, viewGroup, false)
        return SingleCheckViewHolder(view, this)

    }


    override fun onBindViewHolder(holder: SingleCheckViewHolder, position: Int) {
        holder.languageText.text = languageName[position]

        holder.languageText.typeface = MyFonts.getFont(context,
                MyFonts.GORDITALIGHT)
        holder.checkSymbol.visibility = View.GONE



        if (selected >= 0 && selected == position) {
            holder.wholeLayout.setBackgroundResource(R.color.text_grey)

        } else {
            holder.wholeLayout.setBackgroundResource(R.color.transparent)
        }






        holder.wholeLayout.setOnClickListener {
            notifyItemChanged(selected)
            selected = holder.layoutPosition
            notifyItemChanged(selected)

            if (pos == "1") {
                TranslatorActivity.fromPos = selected
                TranslatorFragment.dummyFrom = selected
                TranslatorActivity.setSharedPrefs(context, "fromPos", "" + selected)

            } else {
                TranslatorActivity.toPos = selected
                TranslatorFragment.dummyTo = selected
            }
        }
    }

    override fun getItemCount(): Int {

        return languageName.size
    }


    fun onItemClickListener(onItemClickListener: AdapterView.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    fun onItemHolderClick(holder: SingleCheckViewHolder) {
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
        }
    }


    inner class SingleCheckViewHolder(view: View, private val mAdapter: RecyclerViewDataAdapter12) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var languageText: TextView
        var checkSymbol: RelativeLayout
        var wholeLayout: RelativeLayout

        init {

            languageText = view.findViewById<View>(R.id.textLanguage) as TextView
            wholeLayout = view.findViewById<View>(R.id.wholeLayout) as RelativeLayout
            checkSymbol = view.findViewById<View>(R.id.checkSymbol) as RelativeLayout

            //layout.setOnClickListener(this);


        }

        override fun onClick(v: View) {


            mSelectedItem = adapterPosition
            notifyItemChanged(mSelectedItem)
            val focussedItem = layoutPosition
            notifyItemChanged(focussedItem)

            notifyItemRangeChanged(0, languageName.size)
            Log.e("Selected position", "" + mSelectedItem)
            mAdapter.onItemHolderClick(this@SingleCheckViewHolder)
        }
    }
}
