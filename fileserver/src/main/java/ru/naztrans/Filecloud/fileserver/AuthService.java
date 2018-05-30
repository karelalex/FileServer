package ru.naztrans.Filecloud.fileserver;

public class AuthService {
    public static boolean  checkUser(String name, String  password) {
        return name.equals(SQLService.getUser(name, password));
    }
    public static boolean addUser(String name, String password) {
        return SQLService.addUser(name,password);
    }
}
