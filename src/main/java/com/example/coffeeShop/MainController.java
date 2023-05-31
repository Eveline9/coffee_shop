package com.example.coffeeShop;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Controller

public class MainController {

    public static boolean isAuth = false;
    public static boolean isAdmin = false;
    public static User currentUser = null;


    public static ArrayList<User> users;
    public static ArrayList<Item> items;


    // creates the comparator for comparing stock value
    class ItemComparator implements Comparator<Item> {

        // override the compare() method
        public int compare(Item s1, Item s2) {
            return s1.title.compareTo(s2.title);
        }
    }

    // creates the comparator for comparing stock value
    class ItemComparatorDesc implements Comparator<Item> {

        // override the compare() method
        public int compare(Item s1, Item s2) {
            return -s1.title.compareTo(s2.title);
        }
    }

    // creates the comparator for comparing stock value
    class ItemComparatorCost implements Comparator<Item> {

        // override the compare() method
        public int compare(Item s1, Item s2) {
            return s1.cost > s2.cost ? -1 : 1;
        }
    }

    // creates the comparator for comparing stock value
    class ItemComparatorCostDesc implements Comparator<Item> {

        // override the compare() method
        public int compare(Item s1, Item s2) {
            return s1.cost < s2.cost ? -1 : 1;
        }
    }


    @GetMapping({"/", ""})
    public String index(Model m, HttpServletRequest request) {
        m.addAttribute("title", "Eveline CoffeeShop");
        m.addAttribute("isAdmin", isAdmin);
        m.addAttribute("isAuth", isAuth);
        m.addAttribute("currentUser", currentUser);

        String search_string = request.getParameter("search_string");
        m.addAttribute("search_string", search_string);

        ArrayList<Item> itemsToShow = new ArrayList<Item>();
        for (Item it : items)
            if (search_string == null || search_string.isEmpty() || it.title.toUpperCase().contains(search_string.toUpperCase()))
                itemsToShow.add(it);


        String title_order = request.getParameter("title_order");
        if (("desc").equals(title_order)) {
            m.addAttribute("title_order", "asc");
            Collections.sort(itemsToShow, new ItemComparator());
        } else
            m.addAttribute("title_order", "desc");

        if (("asc").equals(title_order)) {
            Collections.sort(itemsToShow, new ItemComparatorDesc());
        }

        String cost_order = request.getParameter("cost_order");

        if (("desc").equals(cost_order)) {
            m.addAttribute("cost_order", "asc");
            Collections.sort(itemsToShow, new ItemComparatorCost());
        } else
            m.addAttribute("cost_order", "desc");

        if (("asc").equals(cost_order)) {

            Collections.sort(itemsToShow, new ItemComparatorCostDesc());
        }

        m.addAttribute("items", itemsToShow);


        ArrayList<MenuItem> menu = new ArrayList<>();
        if (currentUser != null)
            if (isAdmin) {
                menu.add(new MenuItem("/admin", "Панель администрирования"));
            } else {

                menu.add(new MenuItem("/", "Кофе"));
                menu.add(new MenuItem("/orders", "Мои заказы"));
                menu.add(new MenuItem("/logout", "Выйти"));

            }
        else
            menu.add(new MenuItem("/login", "Войти"));

        m.addAttribute("menu", menu);


        return "index";
    }


    public MainController() {
        users = new ArrayList<User>();
        users.add(new User("admin", "123", true));
        users.add(new User("user", "123", false));

        items = new ArrayList<>();
        items.add(new Item(0, "Латте", "Вкусный кофе", 100));
        items.add(new Item(1, "Гляссе", "Вкусный кофе", 200));
        items.add(new Item(2, "Капучино", "Вкусный кофе", 300));
        items.add(new Item(3, "Чёрный кофе", "Вкусный кофе", 400));
        items.add(new Item(4, "Латте большой", "Вкусный кофе", 500));
        items.add(new Item(5, "Гляссе большой", "Вкусный кофе", 300));
        items.add(new Item(6, "Капучино большой", "Вкусный кофе", 500));
        items.add(new Item(7, "Чёрный кофе большой", "Вкусный кофе", 600));

    }


    @GetMapping("/login")
    public String loginForm(Model m) {
        m.addAttribute("title", "Eveline CoffeeShop - Войти");
        return "login";
    }


    @PostMapping("/makeOrder")
    public String makeOrder(HttpServletRequest request, Model m) {
        if (currentUser == null) return "redirect:login";
        String _address = request.getParameter("address");
        currentUser.currentOrder.address = _address;
        currentUser.currentOrder.sum = 0;
        for (Order.OrderItem o : currentUser.currentOrder.items) {
            currentUser.currentOrder.sum += o.quantity * o.item.cost;
        }
        currentUser.currentOrder.itemCount = currentUser.currentOrder.items.size();
        currentUser.currentOrder.id = currentUser.orders.size() + 1;


        currentUser.orders.add(currentUser.currentOrder);
        currentUser.currentOrder = new Order("");//очистили корзину


        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem("/", "Список товаров"));


        menu.add(new MenuItem("/orders", "Мои заказы"));
        menu.add(new MenuItem("/logout", "Выход"));

        m.addAttribute("menu", menu);

        return "makeOrder";
    }


    @PostMapping("/dologin")
    public String loginProcess(HttpServletRequest request, Model m) {
        String _login = request.getParameter("login");
        String _password = request.getParameter("password");

        currentUser = null;

        String _encryptedPassword = User.passEncrypt(_password);

        for (User u : users)
            if (u.login.equals(_login))
                if (u.encryptedPassword.equals(_encryptedPassword) && !u.isAdmin) {
                    currentUser = u;
                    break;

                }

        if (currentUser != null) {
            isAdmin = currentUser.isAdmin;
            isAuth = true;
        }


        if (isAdmin)
            return "redirect:/admin/items";
        if (isAuth)
            return "redirect:/";


        return "login";
    }

    @GetMapping("/item/{id}")
    public String item(Model m, @PathVariable("id") int id) {
        Item item = new Item();
        for (Item it : items)
            if (it.id == id) {
                item = it;
                break;
            }
        m.addAttribute("item", item);
        m.addAttribute("title", "Eveline CoffeeShop - " + item.title);
        ArrayList<Integer> cartItems = new ArrayList<Integer>();

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem("/", "Кофе"));
        if (isAuth) {
            if (isAdmin) {
                menu.add(new MenuItem("/admin", "Панель администрирования"));

            } else {
//            menu.add(new MenuItem("/cart", "Корзина"));
                menu.add(new MenuItem("/orders", "Мои заказы"));
                menu.add(new MenuItem("/logout", "Выход"));
                cartItems.add(id);
            }
        }
        m.addAttribute("menu", menu);
        m.addAttribute("cartItems", cartItems);
        return "item";
    }

    @GetMapping("/logout")
    public String logout(Model m) {
        isAdmin = false;
        isAuth = false;
        currentUser = null;

        return "redirect:/";
    }

    @GetMapping("/cart")
    public String cart(Model m) {
        if (currentUser == null) return "redirect:/login";
        m.addAttribute("orderItems", currentUser.currentOrder.items);
        m.addAttribute("title", "Eveline CoffeeShop - Корзина");

        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem("/", "Кофе"));

        menu.add(new MenuItem("/orders", "Мои заказы"));
        menu.add(new MenuItem("/logout", "Выход"));
        m.addAttribute("menu", menu);

        return "cart";
    }


    @GetMapping("/deleteCart/{id}")
    public String deleteCart(Model m, @PathVariable("id") int id) {
        for (Order.OrderItem it : currentUser.currentOrder.items)
            if (it.item.id == id) {
                currentUser.currentOrder.items.remove(it);
                break;
            }
        return "redirect:/cart";
    }

    @GetMapping("/addCart/{id}")
    public String cart(Model m, @PathVariable("id") int id) {
        if (currentUser == null) return "redirect:/login";


        for (Item it : items)
            if (it.id == id) {
                Order.OrderItem o = new Order.OrderItem();
                o.quantity = 1;
                o.item = it;
                currentUser.currentOrder.items.add(o);
                break;
            }
        return "redirect:/cart";
    }

    @GetMapping("/orders")
    public String orders(Model m) {
        if (currentUser == null) return "redirect:/login";
        m.addAttribute("title", "Eveline CoffeeShop - Мои заказы");
        m.addAttribute("orders", currentUser.orders);


        ArrayList<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem("/", "Список товаров"));
        menu.add(new MenuItem("/cart", "Корзина"));

        menu.add(new MenuItem("/logout", "Выйти из аккаунта"));
        m.addAttribute("menu", menu);


        return "orders";
    }

    @GetMapping("/photos/{id}.jpg")
    public ResponseEntity<?> picture(Model m, @PathVariable("id") int id) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(
                    new File("src\\main\\resources\\photos\\" + String.valueOf(id) + ".jpg")//
            ));//                get();


            return ResponseEntity.ok()

                    .contentType(MediaType.IMAGE_JPEG)
                    //.contentLength(100000)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
        }
        return null;
    }

    @GetMapping("/css/{filename}.css")
    public ResponseEntity<?> fileCSS(Model m, @PathVariable("filename") String filename) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(
                    new File("src\\main\\resources\\css\\" + filename + ".css")//
            ));//


            return ResponseEntity.ok()
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
        }
        return null;
    }

    @GetMapping("/js/{filename}.js")
    public ResponseEntity<?> fileJS(Model m, @PathVariable("filename") String filename) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(
                    new File("src\\main\\resources\\js\\" + filename + ".js")//
            ));//


            return ResponseEntity.ok()
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
        }
        return null;
    }

}
