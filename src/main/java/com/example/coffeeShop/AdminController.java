package com.example.coffeeShop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping({"/", ""})
    public String index(Model m){
        if (!MainController.isAdmin) return "redirect:/admin/login";
        return "redirect:/admin/items";
    }


    @GetMapping("/login")
    public String loginForm(Model m){
        return "adminLogin";
    }

    @PostMapping("/dologin")
    public String loginProcess(HttpServletRequest request, Model m){
        String _login= request.getParameter("login");
        String _password= request.getParameter("password");

        MainController.currentUser = null;

        String _encryptedPassword=User.passEncrypt(_password);

        for(User u:MainController.users)
            if (u.login.equals(_login)) {
                if (u.encryptedPassword.equals(_encryptedPassword) && u.isAdmin) {
                    MainController.currentUser = u;
                    break;
                }
            }
        if (MainController.currentUser!=null){
            MainController.isAdmin = MainController.currentUser.isAdmin;
            MainController.isAuth=true;
        }


        if (MainController.isAdmin)
            return "redirect:/admin/items";
        if (MainController.isAuth)
            return "redirect:/admin";

        m.addAttribute("title", "Логин");
        return "adminLogin";
    }



    @GetMapping({"items"})
    public String items(Model m,  HttpServletRequest request){
        if (!MainController.isAdmin) return "redirect:/login";



        m.addAttribute("items", MainController.items);



        ArrayList<MenuItem> menu = new ArrayList<>();


        menu.add(new MenuItem("/logout", "Выйти"));
        menu.add(new MenuItem("/admin/edit/-1", "Добавить товар"));
        m.addAttribute("menu", menu);
        m.addAttribute("title", "Управление товарами");
        return "adminItems";
    }

    @GetMapping({"delete/{id}"})
    public String delete(@PathVariable("id") int id, Model m){

        if (!MainController.isAdmin) return "redirect:/login";


        for(Item it :MainController.items){
            if(it.id==id){
                MainController.items.remove(it);
                break;
            }
        }

        //m.addAttribute("items", MainController.items);
        return "redirect:/admin/items";
    }

    @GetMapping({"edit/{id}"})
    public String edit(@PathVariable("id") int id,Model m){

        if (!MainController.isAdmin) return "redirect:/login";


        Item item = new Item();
        item.id=id;

        if (id!=-1)
        for(Item it :MainController.items){
            if(it.id==id){
                item=it;
                break;

            }
        }


        m.addAttribute("item", item);

        ArrayList<MenuItem> menu = new ArrayList<>();


        menu.add(new MenuItem("/logout", "Выйти"));
        menu.add(new MenuItem("/admin", "Панель администрирования"));
        m.addAttribute("menu", menu);

        m.addAttribute("title", "Редактировать товар");
        return "adminEdit";
    }


    @PostMapping({"edit/{id}"})
    public String editDo(@PathVariable("id") int id,Model m, HttpServletRequest request,  @RequestParam("file") MultipartFile file){
        if (!MainController.isAdmin) return "redirect:/login";


        Item item=new Item();
        item.title=request.getParameter("title");
        try {
            item.cost = Float.parseFloat(request.getParameter("cost"));
        }catch(Exception e){

        }
        item.descr=request.getParameter("descr");

        if (id!=-1)
            for(Item it :MainController.items){
                if(it.id==id){
                    it.title=item.title;
                    it.descr=item.descr;
                    it.cost=item.cost;
                    break;
                }
            }
        else {
            int max_id=0;
            for(Item it :MainController.items) if (max_id<it.id) max_id=it.id;
            max_id++;
            id=item.id=max_id;
            MainController.items.add(item);
        }


        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("src\\main\\resources\\photos\\"+String.valueOf(id) +".jpg")));
                stream.write(bytes);
                stream.close();
                //return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
            } catch (Exception e) {
                //return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            //return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }

        return "redirect:/admin";///edit/"+String.valueOf(id)
    }
}
