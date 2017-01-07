package pdfmerger.dassem.com.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int FILE_CODE = 1337;
    private final int DIR_CODE = 1339;
    private MyAdapter myAdapter;
    private ArrayList<String> files;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String currentFile = "temp.pdf", currentDir = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        files = new ArrayList<>();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        myAdapter = new MyAdapter();


    }


    public MyAdapter getAdapter() {
        return myAdapter;
    }

    public void pickDirectory() {
        startChooseFileIntent(true);
    }

    private void loadFile(Uri uri) {
        myAdapter.addItem(new MyItem(myAdapter.getSize(), uri.getPath()));
    }

    private void mergePDF() {
        Document document = new Document();
        try {
            File outputFile = new File(currentDir + "/" + currentFile);
            outputFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PdfCopy copy = null;

            copy = new PdfSmartCopy(document, outputStream);

            document.open();
            ArrayList<MyItem> mItems = myAdapter.getList();
            for (MyItem item : mItems) {
                PdfReader reader = new PdfReader(new File(item.text).getAbsolutePath());
                copy.addDocument(reader);
                reader.close();
            }
            document.close();
            Toast.makeText(this, "File sucessfully saved in " + currentDir + "/" + currentFile, Toast.LENGTH_SHORT).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            loadFile(uri);

                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            loadFile(uri);


                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                loadFile(uri);

            }
        } else if (requestCode == DIR_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            saveFile(uri);

                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            saveFile(uri);


                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                saveFile(uri);

            }
        }
    }

    private void saveFile(Uri uri) {
        currentDir = uri.getPath();
        ((PlaceholderFragment) (mSectionsPagerAdapter.getCurrentFragment())).setCurrentDirectory(currentDir);
    }

    public void setCurrentFile(String filename) {
        currentFile = filename + ".pdf";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            startChooseFileIntent(false);
        } else if (id == R.id.remove) {
            myAdapter.empty();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startChooseFileIntent(boolean isDir) {
        Intent i = new Intent(this, FilePicker.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        if (isDir) {
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        } else {
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        }

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        if (isDir) {
            startActivityForResult(i, DIR_CODE);
        } else {
            startActivityForResult(i, FILE_CODE);
        }
    }

}
