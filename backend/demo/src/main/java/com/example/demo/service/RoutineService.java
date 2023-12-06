package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.dao.RoutineRepository;
import com.example.demo.entity.Routine;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    public Routine getByRoutineName(String routineName){
        Routine routine = routineRepository.findByRoutineName(routineName);
        return routine;
    }

    public void saveAll(List<Routine> routines){
        for (Routine routine: routines){
            routineRepository.save(routine);
        }
    }

    public List<String> getUserRoutines(String userEmail) {
        List<Routine> routines = routineRepository.findByUserEmail(userEmail);
        List<String> routineNames = new ArrayList<>();

        for(Routine  rou : routines){
            String name = rou.getRoutineName();
            routineNames.add(name);
        }

        if(userEmail!=""){
            routines = routineRepository.findByUserEmail("");
            for(Routine  rou : routines){
                String name = rou.getRoutineName();
                routineNames.add(name);
            }
        }

        return routineNames;
    }

    public void saveRoutine(Routine routine) {
        Routine rou = routineRepository.save(routine);
        System.out.println(rou);
    }
    
}
