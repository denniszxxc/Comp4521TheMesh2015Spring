package com.comp4521.bookscan.MainLayout;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


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
    private static final String ARG_PARAM5 = "bookOwner";
    private static final String ARG_PARAM6 = "bookType";

    private String imageURL;
    private String bookName;
    private String bookAuthor;
    private String bookID;
    private String bookOwner;
    private String bookType;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageURL   URLfor book cover
     * @param bookName
     * @param bookAuthor
     * @param bookType
     * @return A new instance of fragment BookDetailFragment.
     */
    public static BookDetailFragment newInstance(String imageURL, String bookName, String bookAuthor,
                                                 String bookID, String bookOwner, String bookType) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, imageURL);
        args.putString(ARG_PARAM2, bookName);
        args.putString(ARG_PARAM3, bookAuthor);
        args.putString(ARG_PARAM4, bookID);
        args.putString(ARG_PARAM5, bookOwner);
        args.putString(ARG_PARAM6, bookType);
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
            bookName = getArguments().getString(ARG_PARAM2);
            bookAuthor = getArguments().getString(ARG_PARAM3);
            bookID = getArguments().getString(ARG_PARAM4);
            bookOwner = getArguments().getString(ARG_PARAM5);
            bookType = getArguments().getString(ARG_PARAM6);
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

        booknameView.setText("Book name: \n" + bookName);
        bookAuthorView.setText("Author: " + bookAuthor);

        Picasso.with(container.getContext()).load(imageURL).error(R.drawable.no_cover).into((ImageView) bookcoverView);

        Button bgdBookBtn = (Button) rootView.findViewById(R.id.borrow_get_del_book_btn);

        if (bookType == getString(R.string.title_section2)) {
            bgdBookBtn.setText(getString(R.string.book_action_get));
        }

        if (bookType == getString(R.string.title_section3)) {
            bgdBookBtn.setText(getString(R.string.book_action_del));
            bgdBookBtn.setBackgroundColor(Color.RED);
        }

        bgdBookBtn.setOnClickListener(this);


        TextView bookOwnerView = (TextView) rootView.findViewById(R.id.bookowner);
        bookOwnerView.setOnClickListener(this);
        bookOwnerView.setText("Owner: " + bookOwner);

        return rootView;
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.borrow_get_del_book_btn) {
            if (bookType == getString(R.string.title_section1)) {
                ((Button) v).setText("Waiting to confirm");

                Context context = this.getActivity();
                CharSequence text = "Request Sent. \n Waiting for owner's confirmation.";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            if (bookType == getString(R.string.title_section2)) {
                // get donated book
            }

            if (bookType == getString(R.string.title_section3)) {
                // remove users' book
                new ButtonPressTask().execute();
            }


        }
//        else if(v.getId() == R.id.bookowner) {
//            // display sample chat interface
//
//            Intent chatIntent = new Intent(getActivity(), FullscreenActivityChat.class);
//            getActivity().startActivity(chatIntent);
//        }
    }

    private class ButtonPressTask extends AsyncTask<Void, Void, Void> {

        Boolean connectSuccess;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            String time = new Date().toString();
            WriteToServer server = new WriteToServer();
            // TODO change userID after register complete
            JSONObject  receivedJson;
            if (bookType == getString(R.string.title_section3)) {
                receivedJson = server.removeOneBook("useriduselessfornow", bookID);
                connectSuccess = true;

//            } else if(mtitle == getString(R.string.title_section2)) {
//                receivedJson = server.getBookListAll("useriduselessfornow");
            } else {
                receivedJson = null;
            }


            if (receivedJson != null) {
//                try {
//                    imgAuthor = receviedJsonTolocalArray(receivedJson,"author");
//                    imgCover = receviedJsonTolocalArray(receivedJson,"cover");
//                    imgbookIDs = receviedJsonTolocalArray(receivedJson,"server_book_id");
//                    imgText = receviedJsonTolocalArray(receivedJson,"name");
//                    imgBookOwner = receviedJsonTolocalArray(receivedJson,"user_id");
//                    imgbookIDs = receviedJsonTolocalArray(receivedJson,"server_book_id");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                connectSuccess = true;

            } else {
//                connectSuccess = false;
            }
            return null;
        }


        protected void onPostExecute(Void params) {
            if(connectSuccess) {
                Toast.makeText(getActivity(), "Request Completed.", Toast.LENGTH_SHORT).show();
                backtoMyLibrary();
            } else {
                Toast.makeText(getActivity(), "Connection failed! Please Try again.", Toast.LENGTH_SHORT).show();

            }
        }


    }

    private void backtoMyLibrary() {
        Intent intent = new Intent(getActivity(), com.comp4521.bookscan.MainLayout.MainActivity.class); // the class may be different
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("toFragment", "My Library");
        startActivity(intent);
    }
}
