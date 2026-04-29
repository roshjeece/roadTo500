package com.roadto500.api.service;

import com.roadto500.api.model.AftEvent;
import com.roadto500.api.repository.AftEventRepository;
import com.roadto500.api.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import static com.roadto500.api.model.AftEventUnit.*;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {
    // CommandLineRunner runs code after the application is fully initialized and ready
    // this is needed because we're seeding data below

    private final AftEventRepository aftEventRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    // tells Java/IntelliJ that we are fulfilling the CommandLineRunner's defined
    // run method below
    public void run(String... args) throws Exception {
        // varargs param, ... means it accepts 0+ String args
        // throws exception important to handle errors later, matches CommandLineRunner run method signature

        if (aftEventRepository.count() == 0) {

            aftEventRepository.save(createMdl());
            aftEventRepository.save(createHrp());
            aftEventRepository.save(createSdc());
            aftEventRepository.save(createPlk());
            aftEventRepository.save(create2mr());

        }

        if (exerciseRepository.count() == 0) {
            // seed exercises
        }
    }

    private AftEvent createMdl() {
        AftEvent mdl = new AftEvent();
        mdl.setName("3 Repetition Maximum Deadlift");
        mdl.setAbbreviation("MDL");
        mdl.setCategories("MAXIMAL_STRENGTH,POSTERIOR_CHAIN_POWER,GRIP_STRENGTH,CORE_STABILITY");
        mdl.setAftEventUnit(LBS);
        mdl.setDescription("The MDL assesses the Muscular Strength component of fitness by measuring your lower body, grip, and core strength. It requires well-conditioned back and leg muscles which helps Soldiers avoid hip, knee, and lower back injuries. Flexibility and balance are other aspects of fitness that are assessed by the MDL.");
        return mdl;
    }

    private AftEvent createHrp() {
        AftEvent hrp = new AftEvent();
        hrp.setName("Hand-Release Push-Up Arm Extension");
        hrp.setAbbreviation("HRP");
        hrp.setCategories("MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,FORCE_PRODUCTION,CORE_STABILITY");
        hrp.setAftEventUnit(REPS);
        hrp.setDescription("The HRP assesses the Muscular Endurance component of fitness by measuring a Soldier’s upper body endurance. The HRP is a strong driver for upper body and core strength training. Flexibility is a secondary component of fitness assessed by the HRP.");
        return hrp;
    }

    private AftEvent createSdc() {
        AftEvent sdc = new AftEvent();
        sdc.setName("Sprint-Drag-Carry");
        sdc.setAbbreviation("SDC");
        sdc.setCategories("MUSCULAR_ENDURANCE,MUSCULAR_STRENGTH,ANAEROBIC_POWER,ANAEROBIC_ENDURANCE");
        sdc.setAftEventUnit(TIME_IN_SECONDS);
        sdc.setDescription("The SDC assesses the Muscular Endurance, Muscular Strength, Anaerobic Power, and Anaerobic Endurance components of fitness by measuring a Soldier’s ability to sustain moderate- to high-intensity muscular work over a short duration. Secondary components of fitness assessed by the SDC include balance, coordination, agility, flexibility, and reaction time.");
        return sdc;
    }

    private AftEvent createPlk() {
        AftEvent plk = new AftEvent();
        plk.setName("Plank");
        plk.setAbbreviation("PLK");
        plk.setCategories("MUSCULAR_ENDURANCE,CORE_STABILITY,CORE_STRENGTH,ISOMETRIC_STRENGTH");
        plk.setAftEventUnit(TIME_IN_SECONDS);
        plk.setDescription("The PLK tests the Muscular Endurance component of fitness by measuring your core strength and endurance. Balance is a secondary component of fitness assessed by the PLK.");
        return plk;
    }

    private AftEvent create2mr() {
        AftEvent tmr = new AftEvent();
        tmr.setName("Two-Mile Run");
        tmr.setAbbreviation("2MR");
        tmr.setCategories("AEROBIC_ENDURANCE,ANAEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE,LACTATE_THRESHOLD");
        tmr.setAftEventUnit(TIME_IN_SECONDS);
        tmr.setDescription("The 2MR assesses the Aerobic Endurance component of fitness. Higher aerobic endurance enables you to work for long periods of time and recover more quickly when executing repetitive physical tasks.");
        return tmr;
    }

}
