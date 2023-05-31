package com.example.coffeeShop;

import java.util.ArrayList;

public class Order {
    public static class OrderItem {
        public Item item;
        public int quantity;

        public  OrderItem(){

        }
    }

    public ArrayList<OrderItem> items;
    public String address;
    public float sum=0;
    public int itemCount=0;
    public int id=0;

    public Order(String _address){
        items = new ArrayList<>();
        address=_address;
    }

}
