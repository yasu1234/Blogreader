package com.kumaydevelop.rssreader.Adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kumaydevelop.rssreader.Model.BlogModel
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import java.text.SimpleDateFormat

class BlogAdapter(data: OrderedRealmCollection<BlogModel>?) : RealmBaseAdapter<BlogModel>(data) {

    inner class ViewHolder(cell: View) {
        val date = cell.findViewById<TextView>(android.R.id.text2)
        val title = cell.findViewById<TextView>(android.R.id.text1)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val viewHolder : ViewHolder

        when (convertView) {
            null -> {
                val inflater = LayoutInflater.from(parent?.context)
                view = inflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            }
            else -> {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
        }

        adapterData?.run {
            val blog = get(position)
            val df = SimpleDateFormat("yyyy/MM/dd")
            // 年月日と時間を表示する
            viewHolder.date.setText(DateFormat.format("yyyy/MM/dd", blog.lastUpdate).toString())
            viewHolder.title.setText(blog.title)

        }
        return view
    }
}