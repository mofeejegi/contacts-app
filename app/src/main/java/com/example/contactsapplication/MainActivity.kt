package com.example.contactsapplication

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.Data._ID,
        ContactsContract.Data.CONTACT_ID,
        // The contact's LOOKUP_KEY, to construct a content URI
        ContactsContract.Data.LOOKUP_KEY,
        // The primary display name
        ContactsContract.Data.DISPLAY_NAME_PRIMARY,
        // The primary phone number
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_IS_USER_PROFILE
    )

    // Defines the text expression
    private val SELECTION: String =
        "" +
            "(" +
            "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'" +
            " OR " +
            "(${ContactsContract.Data.HAS_PHONE_NUMBER} = 0 AND ${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}') " +
            ")" +
            " AND " +
            "(" +
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER} LIKE ?" +
            ")"

    // Use this query to avoid duplicate entries due to multi-accounts
    private fun lollipopNoDuplicateQuery() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            " AND " +
                "(" +
                "${ContactsContract.Data.RAW_CONTACT_ID} = ${ContactsContract.Data.NAME_RAW_CONTACT_ID}" +
                ")"
        } else ""

    // Defines a variable for the search string
    private var searchString: String = ""

    // Defines the array to hold values that replace the ?
    private var selectionArgs = arrayOf<String>()

    // Define global mutable variables
    // Define a ListView object
    lateinit var editText: EditText
    lateinit var recycler: RecyclerView

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0

    // The contact's LOOKUP_KEY
    var contactKey: String? = null

    // A content URI for the selected contact
    var contactUri: Uri? = null

    private var contactsAdapter: ContactsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializes the loader
        LoaderManager.getInstance(this).initLoader(0, null, this)

        editText = findViewById(R.id.editText)
        recycler = findViewById(R.id.recyclerView)

        contactsAdapter = ContactsAdapter()

        // Sets the adapter for the Recycler
        recycler.adapter = contactsAdapter

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove leading Zero (0)
                searchString = s.toString().let {
                    if (it.startsWith("0") && it.length > 1) it.substring(1) else it
                }

                LoaderManager.getInstance(this@MainActivity)
                    .restartLoader(0, null, this@MainActivity)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        /*
         * Makes search string into pattern and
         * stores it in the selection array.
         * Searching by Name, Number and Normalized Number
         */
        selectionArgs = arrayOf("%$searchString%", "%$searchString%", "%$searchString%")

        // Starts the query
        return CursorLoader(
            this,
            ContactsContract.Data.CONTENT_URI,
            PROJECTION,
            SELECTION + lollipopNoDuplicateQuery(),
            selectionArgs,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Put the result Cursor in the adapter for the ListView
        //cursorAdapter?.swapCursor(data)
        contactsAdapter?.cursor = data
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        // cursorAdapter?.swapCursor(null)
        contactsAdapter?.cursor = null
    }
}