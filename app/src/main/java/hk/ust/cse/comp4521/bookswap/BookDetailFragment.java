package hk.ust.cse.comp4521.bookswap;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "imageID";
    private static final String ARG_PARAM2 = "bookName";
    private static final String ARG_PARAM3 = "bookAuthor";

    // TODO: Rename and change types of parameters
    private int imageID;
    private String bookName;
    private String bookAuthor;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageID drawable id for book cover
     * @param bookName
     * @param bookAuthor
     * @return A new instance of fragment BookDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookDetailFragment newInstance(int imageID, String bookName, String bookAuthor) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, imageID);
        args.putString(ARG_PARAM2, bookName);
        args.putString(ARG_PARAM3, bookAuthor);
        fragment.setArguments(args);
        return fragment;
    }

    public BookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageID = getArguments().getInt(ARG_PARAM1);
            bookName= getArguments().getString(ARG_PARAM2);
            bookAuthor= getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_book_details, container, false);

        ImageView bookcoverView = (ImageView) rootView.findViewById(R.id.bookcover);
        TextView booknameView = (TextView) rootView.findViewById(R.id.bookname);
        TextView bookAuthorView = (TextView) rootView.findViewById(R.id.bookAuthor);

        booknameView.setText("Book name: " +bookName);
        bookAuthorView.setText("Author: " + bookAuthor);

        bookcoverView.setImageResource(imageID);

        return rootView;
    }


}
