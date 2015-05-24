package com.comp4521.bookscan.MainLayout;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookscan.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "imageURL";
    private static final String ARG_PARAM2 = "bookName";
    private static final String ARG_PARAM3 = "bookAuthor";
    private static final String ARG_PARAM4 = "bookID";
    private static final String ARG_PARAM5 = "bookType";

    private String imageURL;
    private String bookName;
    private String bookAuthor;
    private String bookID;
    private String bookType;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageURL URLfor book cover
     * @param bookName
     * @param bookAuthor
     * @param bookType
     * @return A new instance of fragment BookDetailFragment.
     */
    public static BookDetailFragment newInstance(String imageURL, String bookName, String bookAuthor,
                                                 String bookID, String bookType) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, imageURL);
        args.putString(ARG_PARAM2, bookName);
        args.putString(ARG_PARAM3, bookAuthor);
        args.putString(ARG_PARAM4, bookAuthor);
        args.putString(ARG_PARAM5, bookType);
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
            imageURL = getArguments().getString(ARG_PARAM1);
            bookName= getArguments().getString(ARG_PARAM2);
            bookAuthor= getArguments().getString(ARG_PARAM3);
            bookID= getArguments().getString(ARG_PARAM4);
            bookType= getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

          rootView = inflater.inflate(R.layout.activity_book_details, container, false);


        ImageView bookcoverView = (ImageView) rootView.findViewById(R.id.bookcover);
        TextView booknameView = (TextView) rootView.findViewById(R.id.bookname);
        TextView bookAuthorView = (TextView) rootView.findViewById(R.id.bookAuthor);

        booknameView.setText("Book name: " + bookName);
        bookAuthorView.setText("Author: " + bookAuthor);

        Picasso.with(container.getContext()).load(imageURL).error(R.drawable.no_cover).into((ImageView) bookcoverView);


        if (bookType == getString(R.string.title_section2)) {
            ((TextView) rootView.findViewById(R.id.borrow_get_book_btn)).setText(getString(R.string.book_action_get));
        }

        Button getBookBtn = (Button) rootView.findViewById(R.id.borrow_get_book_btn);
        getBookBtn.setOnClickListener(this);


        rootView.findViewById(R.id.bookowner).setOnClickListener(this);

        return rootView;
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.borrow_get_book_btn) {
            ((Button) v).setText("Waiting to confirm");

            Context context = this.getActivity();
            CharSequence text = "Request Sent. \n Waiting for owner's confirmation.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if(v.getId() == R.id.bookowner) {
            // display sample chat interface

            Intent chatIntent = new Intent(getActivity(), FullscreenActivityChat.class);
            getActivity().startActivity(chatIntent);
        }
    }
}
