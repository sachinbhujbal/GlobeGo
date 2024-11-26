package com.example.globego.Activity;

import static com.example.globego.Manager.CartManager.cartList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.globego.Domain.CartItem;
import com.example.globego.Domain.ItemDomain;
import com.example.globego.R;
import com.example.globego.databinding.ActivityTicketBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class TicketActivity extends BaseActivity {

    ActivityTicketBinding binding;
    private ItemDomain object;

    private static final int STORAGE_PERMISSION_CODE = 1;
    private LinearLayout ticketLayout;
    Button btnDownload;

    //
    private ArrayList<CartItem> cartList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ticketLayout = findViewById(R.id.ticketLayout);
        btnDownload=findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(TicketActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(TicketActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    createPdfFromView(ticketLayout);
                }
                else{
                    ActivityCompat.requestPermissions(TicketActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            }
        });

        getIntentExtra();
        setVariable();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with PDF creation
                createPdfFromView(ticketLayout);
            } else {
                // Permission denied
                Toast.makeText(this, "Storage Permission is required to download the ticket.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPdfFromView(View view) {
        // Create a bitmap of the view
        Bitmap bitmap = getBitmapFromView(view);

        // Create a PDF document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the bitmap on the PDF page
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // Write the PDF to external storage
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ticket.pdf";

        File file = new File(filePath);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                document.writeTo(Files.newOutputStream(file.toPath()));
            }
            Toast.makeText(this, "Ticket downloaded as PDF", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error downloading ticket", Toast.LENGTH_SHORT).show();
        }

        // Close the document
        document.close();
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void setVariable() {

        Glide.with(TicketActivity.this)
                .load(object.getPic())
                .into(binding.pic);

        Glide.with(TicketActivity.this)
                .load(object.getTourGuidePic())
                .into(binding.profile);

        //imp
        binding.backBtn.setOnClickListener(v -> finish());
        binding.titleTxt.setText(object.getTitle());
        binding.durationTxt.setText(object.getDuration());
        binding.tourGuideTxt.setText(object.getDateTour());
        binding.timeTxt.setText(object.getTimeTour());
        binding.tourGuideNameTxt.setText(object.getTourGuideName());
        binding.guestTxt.setText(String.valueOf(object.getBed()));

        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=object.getTourGuidePhone();
                Intent intent=new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel",phone,null));
                startActivity(intent);
            }
        });

        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sentIntent =new Intent(Intent.ACTION_VIEW);
                sentIntent.setData(Uri.parse("sms:"+object.getTourGuidePhone()));
                sentIntent.putExtra("sms_body","Type Your Message");
                startActivity(sentIntent);


            }
        });
    }

    private void getIntentExtra() {
        object= (ItemDomain) getIntent().getSerializableExtra("object");
        //
        if (object == null) {
            Toast.makeText(this, "Ticket data is not available.", Toast.LENGTH_SHORT).show();
        }
    }
}