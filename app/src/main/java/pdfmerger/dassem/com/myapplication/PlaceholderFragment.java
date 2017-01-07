package pdfmerger.dassem.com.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView directory;
    private EditText filenameInput;
    private String filename;
    private Pattern pattern;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            rootView = inflater.inflate(R.layout.fragment_merge, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            // Setup D&D feature and RecyclerView
            RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

            dragMgr.setInitiateOnMove(false);
            dragMgr.setInitiateOnLongPress(true);

            Snackbar snackbar = Snackbar
                    .make(rootView, "To change the order long-click on the file and then drag the item to the place you want", Snackbar.LENGTH_LONG);

            snackbar.show();

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(dragMgr.createWrappedAdapter(((MainActivity) getContext()).getAdapter()));

            dragMgr.attachRecyclerView(recyclerView);

        } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
            rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            directory = (TextView) rootView.findViewById(R.id.current_dir);
            directory.setText("Current directory: " + Environment.getExternalStorageDirectory().getAbsolutePath());
            String current_dir = getArguments().getString("current_dir");

            String filenameRegex = "^[a-zA-Z0-9]+$";
            pattern = Pattern.compile(filenameRegex);

            filenameInput = (EditText) rootView.findViewById(R.id.filename_input);
            filenameInput.addTextChangedListener(new TextValidator(filenameInput) {
                @Override
                public void validate(TextView textView, String text) {
                    if (!validateText(text)) {
                        textView.setError("Please enter a valid filename. Allowed characters: letters and numbers.");
                    } else {
                        filename = filenameInput.getText().toString();
                        ((MainActivity) getContext()).setCurrentFile(filename);
                    }
                }
            });

            Button change_dir = (Button) rootView.findViewById(R.id.change_dir);
            change_dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getContext()).pickDirectory();
                }
            });

            if (current_dir != null) {
                directory.setText("Current directory: " + current_dir);
            }
        }
        return rootView;
    }

    void setCurrentDirectory(String directoryPath) {
        directory.setText("Current directory: " + directoryPath);
    }

    private boolean validateText(String value) {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

}