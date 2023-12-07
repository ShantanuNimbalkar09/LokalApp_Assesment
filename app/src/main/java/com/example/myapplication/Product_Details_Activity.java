package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import model.Product;

public class Product_Details_Activity extends AppCompatActivity {
    ImageView images,leftarrow,rightarrow;
    TextView title, description, brand, rating, price;

    ViewPager2 viewpager;
    ImageSliderAdapter imageSliderAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        images = findViewById(R.id.images);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        brand = findViewById(R.id.Brand);
        rating = findViewById(R.id.rating);
        price = findViewById(R.id.Price);
        viewpager=findViewById(R.id.viewPager);
        leftarrow=findViewById(R.id.arrowLeft);
        rightarrow=findViewById(R.id.arrowRight);


        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("product")) {
            Product product = (Product) intent.getSerializableExtra("product");


            Picasso.get().load(product.getThumbnail()).into(images);

            title.setText(product.getTitle());
            description.setText(product.getDescription());

            String company = "Company: " + product.getBrand();
            brand.setText(company);

            String stars = "Stars: " + String.valueOf(product.getRating());
            rating.setText(stars);

            String money="Rs."+String.valueOf(product.getPrice());
            price.setText(money);


            Log.d("images", String.valueOf(product.getImages()));

            imageSliderAdapter=new ImageSliderAdapter(product.getImages());
            viewpager.setAdapter(imageSliderAdapter);


            leftarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = viewpager.getCurrentItem();
                    if (currentItem > 0) {
                        viewpager.setCurrentItem(currentItem - 1);
                    }
                }
            });

            rightarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = viewpager.getCurrentItem();
                    if (currentItem < imageSliderAdapter.getItemCount() - 1) {
                        viewpager.setCurrentItem(currentItem + 1);
                    }
                }
            });





        }


    }

    public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {

        private List<String> imageUrls;

        public ImageSliderAdapter(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }
        @NonNull
        @Override
        public ImageSliderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageSliderAdapter.ViewHolder holder, int position) {
            String imageUrl = imageUrls.get(position);
            Picasso.get().load(imageUrl).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.sliderImage);
            }
        }
    }
}