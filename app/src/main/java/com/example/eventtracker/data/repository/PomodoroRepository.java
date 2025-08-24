package com.example.eventtracker.data.repository;

import com.example.eventtracker.data.dao.PomodoroDao;
import com.example.eventtracker.data.model.DayPomodoroStats;

import java.util.List;

public class PomodoroRepository {
    private PomodoroDao pomodoroDao;

    public PomodoroRepository(PomodoroDao dao) {
        this.pomodoroDao = dao;
    }

}


