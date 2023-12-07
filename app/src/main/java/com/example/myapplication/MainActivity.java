package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Product;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Product> productList;

    List<Product> filteredProductList;

    Spinner categoryLIst;

    String URL = "https://dummyjson.com/products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.productList);
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryLIst = findViewById(R.id.category_List);

        filteredProductList = new ArrayList<>();


        ProductAdapter adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        categoryLIst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();


                updateFilteredProductList(selectedCategory);

                updateRecyclerView(filteredProductList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Nothing is selected",Toast.LENGTH_LONG).show();
            }
        });


        fetchProductData();
    }


    void fetchProductData() {

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);


        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("VolleyResponse", "Success: " + response);


                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                try {
                    parseJson(jsonResponse);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                updateRecyclerView(productList);
                updateCategoryList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(request);
    }


    void parseJson(JSONObject response) throws JSONException {
        try {
            JSONArray jsonArray = response.getJSONArray("products");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectResponse = jsonArray.getJSONObject(i);
                Product product = new Product();
                product.setId(objectResponse.getInt("id"));
                product.setTitle(objectResponse.getString("title"));
                product.setDescription(objectResponse.getString("description"));
                product.setPrice(objectResponse.getInt("price"));
                product.setThumbnail(objectResponse.getString("thumbnail"));
                product.setRating(objectResponse.getDouble("rating"));
                product.setBrand(objectResponse.getString("brand"));
                product.setCategory(objectResponse.getString("category"));
                JSONArray imagesarray = objectResponse.getJSONArray("images");
                List<String> imageList = new ArrayList<>();
                for (int j = 0; j < imagesarray.length(); j++) {
                    String imageUrl = imagesarray.getString(j);
                    imageList.add(imageUrl);
                }
                product.setImages(imageList);

                productList.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateRecyclerView(List<Product> productList) {
        ProductAdapter adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        private List<Product> productList;


        public ProductAdapter(List<Product> productList) {
            this.productList = productList;

        }


//        public void setProductList(List<Product> productList) {
//            this.productList = productList;
//            notifyDataSetChanged();
//        }


        @NonNull
        @Override
        public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            Product product = productList.get(position);
            holder.title.setText(product.getTitle());
            holder.description.setText(product.getDescription());
            String price1="Rs."+String.valueOf(product.getPrice());
            holder.price.setText(price1);
            holder.rating.setText(String.valueOf(product.getRating()));

            Picasso.get().load(product.getThumbnail()).into(holder.thumbnail);


        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title, description, rating, price;
            ImageView thumbnail;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
                rating = itemView.findViewById(R.id.rating);
                price = itemView.findViewById(R.id.price);

                thumbnail = itemView.findViewById(R.id.thumbnailImageView);

                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                int position = this.getAdapterPosition();
                Product product = productList.get(position);

                openProductDetailsActivity(product);
            }
        }
    }

    void openProductDetailsActivity(Product product) {
        Intent intent = new Intent(MainActivity.this, Product_Details_Activity.class);
        intent.putExtra("product", product);
        startActivity(intent);

    }

    public void updateCategoryList() {
        Set<String> Product_category = new HashSet<>();
        for (Product product : productList) {
           // Log.d("ProductList", "Category: " + String.valueOf(product.getCategory()));
            Product_category.add(product.getCategory());
        }
        //Log.d("categories", String.valueOf(Product_category));
        List<String> catergories = new ArrayList<>(Product_category);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, catergories);
        arrayAdapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        categoryLIst.setAdapter(arrayAdapter);
    }


    private void updateFilteredProductList(String selectedCategory) {
        filteredProductList.clear();
        for (Product product : productList) {
            if (product.getCategory().equals(selectedCategory)) {
                filteredProductList.add(product);
            }
        }
    }


}