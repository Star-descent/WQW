package com.itweiyunfan.shiyan2.Servlet;

import com.google.gson.Gson;
import com.itweiyunfan.shiyan2.DAO.CharacterDAO;
import com.itweiyunfan.shiyan2.DAO.impl.CharacterDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Character;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.List;

@WebServlet("/CharacterServlet")
public class CharacterServlet extends HttpServlet {
    private CharacterDAO characterDAO = new CharacterDAOImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addCharacter(request, response);
        } else if ("update".equals(action)) {
            updateCharacter(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getCharacters".equals(action)) {
            listCharacters(request, response);
        } else if ("delete".equals(action)) {
            deleteCharacter(request, response);
        }
    }

    private void addCharacter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int novelId = Integer.parseInt(request.getParameter("novelId"));
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        Character character = new Character();
        character.setNovelId(novelId);
        character.setName(name);
        character.setDescription(description);

        characterDAO.addCharacter(character);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void updateCharacter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int characterId = Integer.parseInt(request.getParameter("characterId"));
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        Character character = new Character();
        character.setCharacterId(characterId);
        character.setName(name);
        character.setDescription(description);

        characterDAO.updateCharacter(character);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void deleteCharacter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int characterId = Integer.parseInt(request.getParameter("characterId"));
        characterDAO.deleteCharacter(characterId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void listCharacters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int novelId = Integer.parseInt(request.getParameter("novelId"));
        List<Character> characters = characterDAO.getCharactersByNovelId(novelId);
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(characters));
    }
}

