package com.zeruk.java_fragment_artbook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ContentFragment extends Fragment {

    Button saveArtButton;
    Button deleteArtButton;
    ImageView imageView;
    View _view;
    Bitmap selectedImage;

    EditText artNameText;
    EditText artistText;
    EditText yearText;
    int id;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    ArtDatabase artDatabase;
    ArtDao artDao;
    Art artFromMain;
    SQLiteDatabase database;

    private ActivityResultLauncher<String> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher2;

    public ContentFragment() {
        // Required empty public constructor
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = requireActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null);

        //button declaration
        saveArtButton = view.findViewById(R.id.saveArtButton);
        deleteArtButton = view.findViewById(R.id.deleteArtButton);
        imageView = view.findViewById(R.id.selectImage);
        artNameText = view.findViewById(R.id.artName);
        artistText = view.findViewById(R.id.artist);
        yearText = view.findViewById(R.id.year);

        _view = view;

        if(getArguments().getInt("addOrSee")==0)
        {
            saveArtButton.setVisibility(View.VISIBLE);
            deleteArtButton.setVisibility(View.GONE);
        }
        else
        {
            deleteArtButton.setVisibility(View.VISIBLE);
            saveArtButton.setVisibility(View.GONE);
            id = getArguments().getInt("id");

            mDisposable.add(artDao.getArtById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(ContentFragment.this::putDataOnFields));

        }

        //save button onclick listener
        saveArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveArt(view);
            }
        });

        //delete button onclick listener
        deleteArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteArt(view);
            }
        });

        //select image button onclick listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(view);
            }
        });





    }

    public void putDataOnFields(Art art)
    {
        artFromMain = art;
        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image, 0, art.image.length);
        imageView.setImageBitmap(bitmap);
        artNameText.setText(art.artname);
        artistText.setText(art.artistName);
        yearText.setText(art.year);
    }
    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public void SaveArt(View view)
    {
        String artName = artNameText.getText().toString();
        String artist = artistText.getText().toString();
        String year = yearText.getText().toString();
        Bitmap smallImage = makeSmallerImage(selectedImage, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        mDisposable.add(artDao.insert(new Art(artName, artist, year, byteArray)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(ContentFragment.this::HandleResponse));




    }

    public void HandleResponse()
    {
        NavDirections navDirections = ContentFragmentDirections.actionContentFragmentToRecyclerFragment();
        Navigation.findNavController(_view).navigate(navDirections);
    }

    public void DeleteArt(View view)
    {
        mDisposable.add(artDao.delete(artFromMain).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(ContentFragment.this::HandleResponse));

    }

    public void SelectImage(View view)
    {
        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(view, "Permission needed for gallery!", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Ask for permission
                        activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else
            {
                //Ask for permission
                activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else
        {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher2.launch(intentToGallery);
            //Go to gallery
        }
    }



    public void SetLauncher()
    {
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent intentFromResult = result.getData();
                    if(intentFromResult!=null)
                    {
                        Uri imageData = intentFromResult.getData();
                        try {
                            if(Build.VERSION.SDK_INT >= 28)
                            {
                                ImageDecoder.Source source = ImageDecoder.createSource(_view.getContext().getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                imageView.setImageBitmap(selectedImage);
                            }
                            else
                            {
                                selectedImage = MediaStore.Images.Media.getBitmap(_view.getContext().getContentResolver(), imageData);
                                imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result)
                {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher2.launch(intentToGallery);
                }
                else
                {
                    Toast.makeText(_view.getContext(), "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLauncher();
        artDatabase = Room.databaseBuilder(requireActivity(), ArtDatabase.class, "Arts").build();
        artDao = artDatabase.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content, container, false);
    }
}