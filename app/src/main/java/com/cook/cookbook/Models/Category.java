package com.cook.cookbook.Models;

public class Category {
    private String name;
    private String category_image;

    public  Category(String name, String category_image){
        this.name = name;
        this.category_image = category_image;
    }

    public String getName(){
        return name;
    }

    public  String getCategory_image(){
        return category_image;
    }

    public void setName(String name){
        this.name = name;
    }

    public  void setCategory_image(String category_image){
        this.category_image = category_image;
    }

}
