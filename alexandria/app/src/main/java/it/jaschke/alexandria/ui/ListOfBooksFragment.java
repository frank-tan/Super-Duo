package it.jaschke.alexandria.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.BookListItemCallback;
import it.jaschke.alexandria.data.AlexandriaContract;


public class ListOfBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookListAdapter bookListAdapter;
    private ListView bookList;
    private final int position = ListView.INVALID_POSITION;
    private EditText searchText;
    private Cursor mCursor;
    private FloatingActionButton mFloatingActionButton;

    private static final int LOADER_ID = 10;

    public ListOfBooksFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.books);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.books);
        mCursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );


        bookListAdapter = new BookListAdapter(getActivity(), mCursor, 0);
        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s){

            }
            public void  beforeTextChanged(CharSequence s, int start, int count, int after){

            }
            public void  onTextChanged (CharSequence s, int start, int before,int count) {
                ListOfBooksFragment.this.restartLoader();
            }
        };

        searchText.addTextChangedListener(textWatcher);

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);
        bookList.setAdapter(bookListAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((BookListItemCallback) getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });

        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof NavigationDrawerFragment.NavigationDrawerCallbacks) {
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigationDrawerCallbacks = (NavigationDrawerFragment.NavigationDrawerCallbacks) activity;
                    navigationDrawerCallbacks.selectNavigationDrawerItem(1);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Close the cursor when the fragment is destroyed.
        if(mCursor!= null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString =searchText.getText().toString();

        if(searchString.length()>0){
            searchString = "%"+searchString+"%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString,searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            bookList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

}
