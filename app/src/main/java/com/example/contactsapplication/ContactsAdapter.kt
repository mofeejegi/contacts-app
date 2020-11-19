package com.example.contactsapplication

import android.database.Cursor
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mofeejegi on 11/17/20.
 */
class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    var cursor: Cursor? = null
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val contactsView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_contact_item, parent, false)

        return ContactsViewHolder(contactsView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        cursor?.apply {
            if (moveToPosition(position) ) {
                holder.item = this
            }
        }
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item: Cursor? = null
            set(value) {
                value?.let {
                    itemView.findViewById<TextView>(R.id.textView).text = it.getString(
                        it.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                        )
                    )
                    itemView.findViewById<TextView>(R.id.textView2).text = it.getString(
                        it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                }
                field = value
            }
    }
}