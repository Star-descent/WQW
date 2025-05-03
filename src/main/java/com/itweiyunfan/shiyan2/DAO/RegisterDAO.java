package com.itweiyunfan.shiyan2.DAO;

public interface RegisterDAO {
    boolean registerUser(String name, String gender, String birthDate, String password, String email);
}
