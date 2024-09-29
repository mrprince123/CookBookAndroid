package com.cook.cookbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cook.cookbook.Activity.RecipeActivity;
import com.cook.cookbook.Models.Category;
import com.cook.cookbook.R;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

   private final Context context;
   private final ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories){
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.categoryName.setText(category.getName());
        Glide.with(context).load(category.getCategory_image()).into(holder.categoryImage);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageView categoryImage;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView){
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
