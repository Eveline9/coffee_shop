package com.example.coffeeShop;

public class Item {
    public int id;
    public String title;
    public String descr;
    public float cost;


    public Item(int _id, String _title, String _descr, int _cost) {
        id=_id;
        title=_title;
        descr=_descr;
        cost=_cost;

    }

    public Item() {

    }
}
