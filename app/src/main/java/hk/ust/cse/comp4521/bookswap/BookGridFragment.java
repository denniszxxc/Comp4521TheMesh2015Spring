package hk.ust.cse.comp4521.bookswap;

import android.app.Activity;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
// import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookGridFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // logcat label
    private static final String LABEL = "BookGridFragment";

    /**
     * title of the fragment
     */
    private String mtitle;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bookType title from nav draw, indicate type of book (borrow or donated)
     * @return A new instance of fragment BookGridFragment.
     */
    public static BookGridFragment newInstance(String bookType) {
        BookGridFragment fragment = new BookGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, bookType);
        fragment.setArguments(args);
        return fragment;
    }

    public BookGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtitle = getArguments().getString(ARG_PARAM1);
            Log.i(LABEL, mtitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_book_grid_list, container, false);

        // read sample data reasource
        TypedArray imgs = getResources().obtainTypedArray(R.array.img_id_array);
        String[] imgText = getResources().getStringArray(R.array.img_title_array);

        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < imgText.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", imgs.getResourceId(i, -1));
            item.put("text", imgText[i]);
            items.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                items, R.layout.grid_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});

        GridView gridView = (GridView)rootView.findViewById(R.id.main_page_gridview);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            ((MainActivity) activity).onSectionAttached(getArguments().getString(ARG_PARAM1));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
