package com.work.cafe.model;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.work.cafe.R;
import com.work.cafe.data.detail.Photo;

import java.io.File;
import java.util.ArrayList;

public class ImageModel {

    private static final String IMAGE_BASE_URL = "images/";

    public static ArrayList<Photo> uploadImage(ArrayList<Image> images) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        ArrayList<Photo> photos = new ArrayList<>();
        for (Image image : images) {
            Uri file = image.getUri();
            String reference = IMAGE_BASE_URL + file.getLastPathSegment();

            Photo photo = new Photo();
            photo.setFirebase(true);
            photo.setPhotoReference(reference);

            photos.add(photo);

            StorageReference riversRef = storageRef.child(reference);
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
        return photos;
    }

    public static void downloadPhoto(Context context, Photo photos, ImageView imageView) {
        if (photos.isFirebase()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            Glide.with(context)
                    .load(storageReference.child(photos.getPhotoReference()))
                    .fitCenter()
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(handlingPhoto(photos))
                    .fitCenter()
                    .into(imageView);
        }
    }

    private static String handlingPhoto(Photo photo) {

        String paramPhoto = "photoreference=" + photo.getPhotoReference();
        String key = "key=" + HTTPManager.PLACE_API_KEY;
        String maxHeight = "maxheight=" + 300;
        String url = "https://maps.googleapis.com/maps/api/place/photo?";

        return url + paramPhoto + "&" + key + "&" + maxHeight;
    }
}
