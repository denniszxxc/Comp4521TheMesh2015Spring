package hk.ust.comp4521.instant_messaging;

import com.example.bookscan.R;
import hk.ust.comp4521.storage_handle.DataProvider;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ChatMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{


	@Override
	public void onResume() {
		super.onResume();
		
	}

	//private CustomSimpleCursorAdapter adapter;
	private SimpleCursorAdapter adapter;
	//private Activity mActivity;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getLoaderManager().initLoader(0, null, this);
	}
	
	// Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned
	public void onViewCreated (View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);		
		view.setBackgroundColor(Color.DKGRAY);		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//adapter = new CustomSimpleCursorAdapter(mActivity, R.layout.chat_menu_list_item, null, new String[]{DataProvider.USERS_COL_UID, DataProvider.USERS_COL_COUNT, DataProvider.USERS_COL_COUNT}, new int[]{R.id.uid, R.id.message_count, R.id.message_tag}, 0, new OnListTouchListener(getListView(),getActivity(),DataProvider.CONTENT_URI_USERS));	
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.chat_menu_list_item, null, new String[]{DataProvider.USERS_COL_UID, DataProvider.USERS_COL_COUNT, DataProvider.USERS_COL_COUNT}, new int[]{R.id.uid, R.id.message_count, R.id.message_tag}, 0);	
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				switch(view.getId()) {
				case R.id.message_count:
					{
						
						int count = cursor.getInt(columnIndex);
						TextView v = (TextView)view;
						if (count > 0){
							v.setText(String.valueOf(count));
							v.setVisibility(View.VISIBLE);
						}
						else 
							v.setVisibility(View.GONE);
						return true;
					}
				case R.id.message_tag:
					{
						int count = cursor.getInt(columnIndex);
						TextView v = (TextView)view;
						if(count > 0){
							v.setText(String.format("new message%s", count==1 ? "" : "s"));
							v.setVisibility(View.VISIBLE);
						}
						else 
							v.setVisibility(View.GONE);
						return true;
					}	
				}
			
					
				return false;
			}
		});	
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();		
		fragmentTransaction.replace(R.id.container, ChatFragment.newInstance(id));
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
	

	//----------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity().getApplicationContext(), DataProvider.CONTENT_URI_USERS, new String[]{DataProvider.COL_ID, DataProvider.USERS_COL_UID, DataProvider.USERS_COL_COUNT}, null, null, DataProvider.USERS_COL_COUNT + " DESC, " + DataProvider.COL_ID + " DESC"); 
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.i("ChatMenuFragment","onLoadFinished");
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
