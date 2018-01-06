package com.example.marcus.grouponadministrator.One;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marcus.grouponadministrator.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VendorPageReviewFragment.OnReviewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VendorPageReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VendorPageReviewFragment extends Fragment implements View.OnClickListener
{
    //parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REVIEW_NUM = "reviewNum";

    //types of parameters
    private int reviewNum;

    private OnReviewFragmentInteractionListener mListener;

    public VendorPageReviewFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static VendorPageReviewFragment newInstance(int reviewNum)
    {
        VendorPageReviewFragment fragment = new VendorPageReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REVIEW_NUM, reviewNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            reviewNum = getArguments().getInt(ARG_REVIEW_NUM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.one_fragment_vendor_page_review, container, false);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnReviewFragmentInteractionListener)
        {
            mListener = (OnReviewFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public void setReviewNum(int reviewNum)
    {
        this.reviewNum = reviewNum;
    }

    public int getReviewNum()
    {
        return reviewNum;
    }

    @Override
    public void onClick(View v) {
        mListener.onReviewFragmentInteraction(reviewNum);
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
    public interface OnReviewFragmentInteractionListener
    {
        void onReviewFragmentInteraction(int reviewNum);
    }
}
