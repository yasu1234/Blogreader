package com.kumaydevelop.blogreader.Adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.Model.BlogModel
import com.kumaydevelop.blogreader.R
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import kotlinx.android.synthetic.main.blog_list_content.view.*

class BlogAdapter(data: OrderedRealmCollection<BlogModel>?) : RealmBaseAdapter<BlogModel>(data) {

    // 画面の構成
    inner class ViewHolder(cell: View) {
        val title = cell.titleView
        val date = cell.dateView
        val id = cell.idView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val viewHolder : ViewHolder

        when (convertView) {
            null -> {
                val inflater = LayoutInflater.from(parent?.context)
                view = inflater.inflate(R.layout.blog_list_content, parent, false)
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
            // 年月日と時間を表示する
            viewHolder.date.setText(DateFormat.format(Constants.FROM_YEAR_TO_SECONDS, blog.lastUpdate).toString())
            viewHolder.title.setText(blog.title)
            // idはサイズ0で保持しておく
            viewHolder.id.setText(blog.id.toString())
        }
        return view
    }
}