package com.example.coffeeShop;

import java.util.ArrayList;

public class User {
    public String login;
    public String encryptedPassword;
    public boolean isAdmin;
    public ArrayList<Order> orders;
    public Order currentOrder;

    public static String passEncrypt(String s){
        String s1="`1234567890-=\\~!@#$%^&*()_+|qwertyuiop[]asdfghjkl;'zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,йцукенгшщзхъфывапролджэячсмитьбю.";
        String s2="йцукенгшщзхъфывапролджэячсмитьбю.йцукенгшщзхъфывапролджэячсмитьqwertyuiop[]asdfghjkl;'zxcvbnmйцукенгшщзхъфывапролджэячсмитьбю.йцукенгшщзхъфывапролджэячсмитьqwerterwfgt54t54t5";
        String res="";
        for(int i=0; i<s.length();i++) {
            int s1pos=s1.indexOf(s.charAt(i));
            res += s1pos == -1 ? s.charAt(i): s2.charAt(s1pos);
        }
        return res;
    }

    public User(String _login, String _password, boolean _isAdmin) {
        login=_login;
        encryptedPassword=passEncrypt(_password);
        orders = new ArrayList<>();
        isAdmin=_isAdmin;
        currentOrder = new Order("");
    }
}
