package com.javarush.khasanov.controller;

import com.javarush.khasanov.config.Components;
import com.javarush.khasanov.entity.Answer;
import com.javarush.khasanov.entity.Game;
import com.javarush.khasanov.entity.Question;
import com.javarush.khasanov.service.GameService;
import com.javarush.khasanov.service.QuestService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.javarush.khasanov.config.Constants.QUEST_PAGE;
import static com.javarush.khasanov.config.Constants.QUEST_RESOURCE;

@WebServlet(QUEST_RESOURCE)
public class QuestServlet extends HttpServlet {
    private final GameService gameService = Components.get(GameService.class);
    private final QuestService questService = Components.get(QuestService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Long questId = getQuestId(req);
        Game game = getSessionGame(session, questId);

        Question question = game.getCurrentQuestion();
        List<Answer> answers = gameService.getAnswers(game, question);

        session.setAttribute("question", question);
        session.setAttribute("answers", answers);

        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(QUEST_PAGE);
        requestDispatcher.forward(req, resp);
    }

    private static Long getQuestId(HttpServletRequest req) {
        String stringQuestId = req.getParameter("id");
        HttpSession session = req.getSession();
        return Objects.isNull(stringQuestId)
                ? (Long) session.getAttribute("questId")
                : Long.parseLong(stringQuestId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Long questId = getQuestId(req);
        Game game = getSessionGame(session, questId);

        sendAnswer(req, game);
        resp.sendRedirect("/quest");
    }

    private void sendAnswer(HttpServletRequest req, Game game) {
        String stringAnswerId = req.getHeader("answerId");

        if (Objects.nonNull(stringAnswerId)) {
            Long answerId = Long.parseLong(stringAnswerId);
            gameService.sendAnswer(game, answerId);
        }
    }

    private Game getSessionGame(HttpSession session, Long questId) {
        Long gameId = (Long) session.getAttribute("gameId");
        Game game = gameService.getSessionGame(gameId, questId);
        session.setAttribute("gameId", game.getId());
        session.setAttribute("questId", questId);
        return game;
    }
}