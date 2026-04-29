package com.roadto500.api.service;

import com.roadto500.api.model.AftEvent;
import com.roadto500.api.model.Exercise;
import com.roadto500.api.model.ExerciseDifficulty;
import com.roadto500.api.model.PrescriptionType;
import com.roadto500.api.repository.AftEventRepository;
import com.roadto500.api.repository.ExerciseAftEventRepository;
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
    private final ExerciseAftEventRepository exerciseAftEventRepository;

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
            seedMdlExercises();
            seedHrpExercises();
            seedSdcExercises();
            seedPlkExercises();
            seed2mrExercises();
        }
    }

    // ============================================================
    // AFTEVENT SEED METHODS
    // ============================================================

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

    // ============================================================
    // EXERCISE SEED METHODS
    // ============================================================
    // Total: 64 exercises across 5 AFT events
    // Each event has BEGINNER / INTERMEDIATE / ADVANCED tiers
    // Each tier has bodyweight + equipment options where applicable
    // Prescription types: REPS, DURATION, DISTANCE, WEIGHT_BASED, CARDIO
    // ============================================================

    private Exercise createExercise(String name, String categories, ExerciseDifficulty difficulty, PrescriptionType prescriptionType, String description) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setCategories(categories);
        exercise.setDifficulty(difficulty);
        exercise.setPrescriptionType(prescriptionType);
        exercise.setDescription(description);
        return exercise;
    }

    private void seedMdlExercises() {
        exerciseRepository.save(createExercise(
                "Bodyweight Hip Hinge",
                "POSTERIOR_CHAIN_POWER,CORE_STABILITY,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "Stand with feet hip-width apart. Push hips back while keeping a neutral spine and soft knees. Drive hips forward to return. Teaches the foundational movement pattern of the deadlift without load."
        ));
        exerciseRepository.save(createExercise(
                "Bodyweight Good Morning",
                "POSTERIOR_CHAIN_POWER,CORE_STABILITY,MAXIMAL_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "Stand with hands behind your head. Hinge at the hips with a neutral spine until torso is parallel to the floor, then drive back up. Develops posterior chain strength and teaches safe spine position under load."
        ));
        exerciseRepository.save(createExercise(
                "Romanian Deadlift",
                "POSTERIOR_CHAIN_POWER,MAXIMAL_STRENGTH,GRIP_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.WEIGHT_BASED,
                "With a barbell or dumbbells, hinge at the hips keeping the bar close to your legs. Lower until you feel a hamstring stretch, then drive hips forward. Excellent for building the posterior chain strength required for the MDL."
        ));
        exerciseRepository.save(createExercise(
                "Single-Leg Hip Hinge",
                "POSTERIOR_CHAIN_POWER,CORE_STABILITY,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "Balance on one leg and hinge at the hip, reaching toward the floor while the free leg extends behind. Develops unilateral posterior chain strength and balance critical for safe deadlift mechanics."
        ));
        exerciseRepository.save(createExercise(
                "Trap Bar Deadlift",
                "MAXIMAL_STRENGTH,POSTERIOR_CHAIN_POWER,GRIP_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.WEIGHT_BASED,
                "Using a hex/trap bar, stand inside the frame, grip the handles, and drive through the floor. More upright torso than a conventional deadlift. Excellent for building pulling strength with reduced lower back stress."
        ));
        exerciseRepository.save(createExercise(
                "Kettlebell Swing",
                "POSTERIOR_CHAIN_POWER,MAXIMAL_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "Hinge at the hips to swing a kettlebell back between your legs, then explosively drive your hips forward to swing it to chest height. Develops posterior chain power directly transferable to MDL performance."
        ));
        exerciseRepository.save(createExercise(
                "Farmer Carry",
                "GRIP_STRENGTH,CORE_STABILITY,MAXIMAL_STRENGTH,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DISTANCE,
                "Hold heavy dumbbells or kettlebells at your sides and walk for a prescribed distance. One of the most effective grip strength developers. Directly addresses the grip limitation many Soldiers experience on the MDL."
        ));
        exerciseRepository.save(createExercise(
                "Nordic Hamstring Curl",
                "POSTERIOR_CHAIN_POWER,MAXIMAL_STRENGTH,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Kneel with feet anchored. Lower your torso toward the floor as slowly as possible using hamstring strength, then push back up. One of the most demanding bodyweight posterior chain exercises. Significantly reduces hamstring injury risk."
        ));
        exerciseRepository.save(createExercise(
                "Conventional Deadlift",
                "MAXIMAL_STRENGTH,POSTERIOR_CHAIN_POWER,GRIP_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "The foundational barbell pull. Set up with mid-foot under the bar, hinge down to grip, and drive the floor away while keeping the bar close. The closest movement to the AFT MDL event. Heavy sets build maximal strength directly."
        ));
        exerciseRepository.save(createExercise(
                "Rack Pull",
                "MAXIMAL_STRENGTH,GRIP_STRENGTH,POSTERIOR_CHAIN_POWER",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "A partial range deadlift starting from knee height on a rack. Allows heavier loads than a full deadlift, overloading the lockout position. Excellent for developing the top-end strength and grip required to max the MDL."
        ));
        exerciseRepository.save(createExercise(
                "Sumo Deadlift",
                "MAXIMAL_STRENGTH,POSTERIOR_CHAIN_POWER,GRIP_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "A wide-stance deadlift variation with the hands inside the legs. Shifts emphasis to the quads, hips, and adductors. A useful variation for Soldiers with longer torsos or hip mobility limitations in the conventional stance."
        ));
        exerciseRepository.save(createExercise(
                "Barbell Good Morning",
                "POSTERIOR_CHAIN_POWER,MAXIMAL_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "With a barbell on your upper back, hinge forward at the hips keeping a rigid spine until your torso is near parallel. Heavily loads the erectors and hamstrings. Builds the posterior chain strength required for heavy MDL attempts."
        ));
        exerciseRepository.save(createExercise(
                "Hex Bar Shrug",
                "GRIP_STRENGTH,MAXIMAL_STRENGTH,POSTERIOR_CHAIN_POWER",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "Stand inside a hex bar loaded heavy and perform controlled shrugs. Develops upper trap and grip strength at loads far exceeding standard shrugs. Directly addresses the grip and upper back demands of maximal MDL attempts."
        ));
    }

// ─── HRP EXERCISES (13) ──────────────────────────────────────

    private void seedHrpExercises() {
        exerciseRepository.save(createExercise(
                "Incline Push-Up",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,CORE_STABILITY",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "Perform a push-up with hands elevated on a bench or wall. Reduces effective bodyweight load. The entry-level push-up variation for Soldiers building baseline upper body pushing strength toward the HRP standard."
        ));
        exerciseRepository.save(createExercise(
                "Standard Push-Up",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,CORE_STABILITY,FORCE_PRODUCTION",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "The foundational push-up. Maintain a rigid plank position throughout. Lower chest to the floor and press back up. The closest bodyweight movement to the HRP test. Volume is the primary driver of improvement."
        ));
        exerciseRepository.save(createExercise(
                "Dumbbell Bench Press",
                "UPPER_BODY_STRENGTH,MUSCULAR_ENDURANCE,FORCE_PRODUCTION",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.WEIGHT_BASED,
                "Lying on a bench, press dumbbells from chest height to full extension. Greater range of motion than a barbell press. Builds the chest and tricep strength that underpins HRP performance."
        ));
        exerciseRepository.save(createExercise(
                "Hand-Release Push-Up",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,FORCE_PRODUCTION,CORE_STABILITY",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "The exact AFT HRP movement. Lower to the floor, lift hands off the ground briefly, then press back up. Practice at test pace to build specific endurance. Each rep must start from a dead stop — no bouncing."
        ));
        exerciseRepository.save(createExercise(
                "Wide-Grip Push-Up",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,FORCE_PRODUCTION",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "Push-up with hands placed wider than shoulder width. Increases chest involvement and range of motion. Develops the lateral chest strength that supports high-volume HRP performance."
        ));
        exerciseRepository.save(createExercise(
                "Tricep Dip",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,FORCE_PRODUCTION",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "Using parallel bars or a bench, lower your body by bending the elbows and press back up. Directly targets the triceps, which are a primary limiter in HRP performance during the lockout phase."
        ));
        exerciseRepository.save(createExercise(
                "Barbell Bench Press",
                "UPPER_BODY_STRENGTH,FORCE_PRODUCTION,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.WEIGHT_BASED,
                "The standard barbell press. Lower the bar to the chest and drive back up. Builds maximal horizontal pushing strength that transfers to the HRP. Lower reps and heavier loads build the strength reserve that makes high HRP reps feel easier."
        ));
        exerciseRepository.save(createExercise(
                "Deficit Push-Up",
                "MUSCULAR_ENDURANCE,UPPER_BODY_STRENGTH,FORCE_PRODUCTION,CORE_STABILITY",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Perform push-ups with hands elevated on plates or blocks, allowing chest to drop below hand level. Increases range of motion significantly. Builds the chest, shoulder, and tricep strength at extreme depth not possible in a standard push-up."
        ));
        exerciseRepository.save(createExercise(
                "Archer Push-Up",
                "UPPER_BODY_STRENGTH,MUSCULAR_ENDURANCE,FORCE_PRODUCTION",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "In a wide push-up position, shift weight to one arm while the other arm extends straight. Dramatically increases unilateral load. Builds the raw pushing strength that gives a large reserve for high-volume HRP sets."
        ));
        exerciseRepository.save(createExercise(
                "Push-Up Isometric Hold",
                "MUSCULAR_ENDURANCE,CORE_STABILITY,UPPER_BODY_STRENGTH",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Hold the bottom position of a push-up for a prescribed duration. Develops the isometric strength and endurance at the weakest point of the HRP. Particularly effective for Soldiers who collapse under fatigue during the test."
        ));
        exerciseRepository.save(createExercise(
                "Weighted Push-Up",
                "UPPER_BODY_STRENGTH,FORCE_PRODUCTION,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "Standard push-up performed with a weight plate on your back. Increases resistance while maintaining the exact movement pattern of the HRP. Builds maximal strength in the test-specific movement without changing mechanics."
        ));
        exerciseRepository.save(createExercise(
                "Close-Grip Bench Press",
                "UPPER_BODY_STRENGTH,FORCE_PRODUCTION,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.WEIGHT_BASED,
                "Barbell bench press with a narrow grip. Shifts primary load to the triceps. Directly targets the push-to-lockout phase of the HRP. Useful for Soldiers whose tricep endurance is the limiting factor in their HRP score."
        ));
        exerciseRepository.save(createExercise(
                "Ring Push-Up",
                "UPPER_BODY_STRENGTH,MUSCULAR_ENDURANCE,CORE_STABILITY,FORCE_PRODUCTION",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Perform push-ups with hands in gymnastics rings. The unstable surface demands significant stabilizer activation throughout the chest, shoulder, and core. Builds the raw strength reserve that makes high-rep HRP sets sustainable."
        ));
    }

// ─── SDC EXERCISES (13) ──────────────────────────────────────

    private void seedSdcExercises() {
        exerciseRepository.save(createExercise(
                "Lateral Shuffles",
                "ANAEROBIC_POWER,MUSCULAR_ENDURANCE,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.DURATION,
                "In an athletic stance, shuffle laterally for a prescribed distance or time. Low-impact entry point for developing the lateral agility and change-of-direction speed required in the SDC carry portion."
        ));
        exerciseRepository.save(createExercise(
                "Broad Jump",
                "ANAEROBIC_POWER,MUSCULAR_STRENGTH,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "From a two-foot stance, swing arms and jump forward as far as possible, landing softly. Develops the explosive lower body power required for the sprint and drag components of the SDC."
        ));
        exerciseRepository.save(createExercise(
                "Slider Drag",
                "MUSCULAR_STRENGTH,ANAEROBIC_ENDURANCE,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.DISTANCE,
                "Attach a band or rope to a weighted object on a smooth floor. Backpedal and drag it for a set distance. A low-load introduction to the pulling mechanics of the SDC drag lane without requiring a full sled."
        ));
        exerciseRepository.save(createExercise(
                "Shuttle Run",
                "ANAEROBIC_POWER,ANAEROBIC_ENDURANCE,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DURATION,
                "Sprint between two cones set 25 meters apart, touching the line on each turnaround. Directly replicates the repeated sprint and direction change pattern of the SDC. Set up to mirror the SDC distance and rest intervals."
        ));
        exerciseRepository.save(createExercise(
                "Sandbag Front Carry",
                "MUSCULAR_STRENGTH,ANAEROBIC_ENDURANCE,CORE_STABILITY,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DISTANCE,
                "Carry a sandbag at chest height for a set distance. Replicates the loaded carry lane of the SDC. The unstable load demands core stability while moving at pace — exactly what the SDC requires."
        ));
        exerciseRepository.save(createExercise(
                "Sled Drag",
                "MUSCULAR_STRENGTH,ANAEROBIC_POWER,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DISTANCE,
                "Attach a harness or rope to a loaded sled. Backpedal and drag the sled for a set distance. The most specific exercise for the SDC drag lane. Load the sled at 90 lbs to replicate the exact SDC standard."
        ));
        exerciseRepository.save(createExercise(
                "Kettlebell Farmer Carry",
                "MUSCULAR_STRENGTH,ANAEROBIC_ENDURANCE,CORE_STABILITY,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DISTANCE,
                "Carry two kettlebells at your sides for a set distance at a fast pace. Replicates the kettlebell carry lane of the SDC. Use 40 lb kettlebells to match the SDC standard for male Soldiers."
        ));
        exerciseRepository.save(createExercise(
                "Burpee to Sprint",
                "ANAEROBIC_POWER,ANAEROBIC_ENDURANCE,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Perform a burpee, then immediately sprint 10 meters. Combines full body explosive movement with immediate sprint effort. Replicates the metabolic demand of transitioning between SDC lanes at maximal effort."
        ));
        exerciseRepository.save(createExercise(
                "Box Jump",
                "ANAEROBIC_POWER,MUSCULAR_STRENGTH,FUNCTIONAL_STRENGTH",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Explosively jump onto a box, land softly, and step down. Develops the reactive lower body power required for the sprint lanes of the SDC. Reset fully between reps to train maximal power output."
        ));
        exerciseRepository.save(createExercise(
                "Sled Push",
                "ANAEROBIC_POWER,MUSCULAR_STRENGTH,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DISTANCE,
                "Drive a loaded sled forward for a set distance. Heavily loads the quads, glutes, and calves while demanding cardiovascular output. One of the highest-ROI exercises for SDC performance across all lanes."
        ));
        exerciseRepository.save(createExercise(
                "Prowler Sprint Interval",
                "ANAEROBIC_POWER,ANAEROBIC_ENDURANCE,MUSCULAR_STRENGTH",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Sprint with a prowler for 25 meters, rest, and repeat. Closely replicates the all-out effort and brief rest pattern of the SDC. One of the most effective SDC-specific conditioning tools available."
        ));
        exerciseRepository.save(createExercise(
                "Trap Bar Carry",
                "MUSCULAR_STRENGTH,FUNCTIONAL_STRENGTH,CORE_STABILITY,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DISTANCE,
                "Load a hex/trap bar and carry it for a set distance at pace. Combines grip, core, and lower body strength demands in a single loaded carry. Excellent overall SDC preparation exercise."
        ));
        exerciseRepository.save(createExercise(
                "Bear Crawl",
                "ANAEROBIC_POWER,FUNCTIONAL_STRENGTH,CORE_STABILITY,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DISTANCE,
                "On all fours with knees hovering just off the ground, crawl forward at a fast pace. Full body conditioning that directly replicates the low-to-ground demands of the SDC drag lane and develops total body coordination under fatigue."
        ));
    }

// ─── PLK EXERCISES (12) ──────────────────────────────────────

    private void seedPlkExercises() {
        exerciseRepository.save(createExercise(
                "Kneeling Plank",
                "CORE_STABILITY,MUSCULAR_ENDURANCE,ISOMETRIC_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.DURATION,
                "Hold a plank position with knees on the floor. Reduced bodyweight load introduces the spinal bracing and core endurance required for the full plank test. Progress to full plank when 60 second holds feel easy."
        ));
        exerciseRepository.save(createExercise(
                "Standard Plank",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.DURATION,
                "Forearm plank with toes and elbows on the ground. Maintain a rigid, neutral spine. The foundational position for the PLK event. Accumulate time in this exact position. Progressive overload through increased hold duration."
        ));
        exerciseRepository.save(createExercise(
                "Dead Bug",
                "CORE_STABILITY,MUSCULAR_ENDURANCE,ISOMETRIC_STRENGTH",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.REPS,
                "Lying on your back with arms and legs raised, slowly lower opposite arm and leg toward the floor while maintaining a flat lower back. Builds deep core stability essential for sustaining a rigid plank position under fatigue."
        ));
        exerciseRepository.save(createExercise(
                "Side Plank",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DURATION,
                "Side-lying plank supported by one forearm and the edge of the feet. Develops lateral core stability. Addresses the obliques and quadratus lumborum — muscles that sustain proper spinal alignment during long PLK holds."
        ));
        exerciseRepository.save(createExercise(
                "Hollow Body Hold",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DURATION,
                "Lying on your back, press your lower back into the floor and raise arms and legs slightly off the ground. Hold the position. Develops the anterior core tension and body control that make long plank holds sustainable."
        ));
        exerciseRepository.save(createExercise(
                "Plank Shoulder Tap",
                "CORE_STABILITY,MUSCULAR_ENDURANCE,ISOMETRIC_STRENGTH",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "In a high plank position, alternate touching each shoulder with the opposite hand while resisting rotation. Develops anti-rotational core stability and conditions the stabilizers that prevent form breakdown during long PLK holds."
        ));
        exerciseRepository.save(createExercise(
                "Ab Wheel Rollout",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.REPS,
                "Kneeling with hands on an ab wheel, roll forward until your body is near parallel to the floor, then pull back. Builds the anti-extension core strength required to hold a rigid plank under increasing fatigue."
        ));
        exerciseRepository.save(createExercise(
                "RKC Plank",
                "ISOMETRIC_STRENGTH,CORE_STABILITY,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "A maximally tensioned forearm plank: squeeze glutes hard, pull elbows toward toes, and drive toes into the floor. Far more demanding than a standard plank in the same position. Builds extreme core tension rapidly. 20-30 second holds are sufficient."
        ));
        exerciseRepository.save(createExercise(
                "Long-Lever Plank",
                "ISOMETRIC_STRENGTH,CORE_STABILITY,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "A forearm plank with elbows placed further forward than standard, increasing the mechanical disadvantage and load on the core. Significantly harder than a standard plank. Excellent for building the reserve needed for max PLK scores."
        ));
        exerciseRepository.save(createExercise(
                "Dragon Flag",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "Grip a fixed object behind your head, brace your core, and lower your straight body down in a controlled manner. One of the most demanding core exercises available. Builds extreme anti-extension strength far beyond what the PLK requires."
        ));
        exerciseRepository.save(createExercise(
                "Weighted Plank",
                "ISOMETRIC_STRENGTH,CORE_STABILITY,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Standard forearm plank with a weight plate placed on your upper back. Directly overloads the PLK position. Builds the core endurance reserve needed to hold a rigid plank well past the max scoring threshold."
        ));
        exerciseRepository.save(createExercise(
                "Stir the Pot",
                "CORE_STABILITY,ISOMETRIC_STRENGTH,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.REPS,
                "In a forearm plank on a stability ball, make small circles with your elbows. The unstable surface demands constant core adjustment. Builds the deep stabilizer endurance that sustains a rock-solid plank position under prolonged fatigue."
        ));
    }

// ─── 2MR EXERCISES (13) ──────────────────────────────────────

    private void seed2mrExercises() {
        exerciseRepository.save(createExercise(
                "Walk-Run Interval",
                "AEROBIC_ENDURANCE,LACTATE_THRESHOLD,LOWER_BODY_ENDURANCE",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.CARDIO,
                "Alternate between walking and jogging at a comfortable pace. Entry-level aerobic conditioning for Soldiers who struggle to run continuously. Builds the cardiovascular base needed before progressing to continuous running."
        ));
        exerciseRepository.save(createExercise(
                "Easy Conversational Run",
                "AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE,LACTATE_THRESHOLD",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.CARDIO,
                "Run at a pace where you can hold a full conversation. The cornerstone of aerobic base building. Most 2MR improvement comes from consistent easy running volume, not hard intervals. Effort should feel sustainable for 30-60 minutes."
        ));
        exerciseRepository.save(createExercise(
                "Stationary Bike — Steady State",
                "AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE",
                ExerciseDifficulty.BEGINNER,
                PrescriptionType.CARDIO,
                "Cycle at a moderate, steady effort for a prescribed duration. Low-impact aerobic conditioning. Useful for Soldiers with lower leg or foot injuries who cannot run. Builds the cardiovascular base that transfers to 2MR performance."
        ));
        exerciseRepository.save(createExercise(
                "Tempo Run",
                "LACTATE_THRESHOLD,AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.CARDIO,
                "Run at comfortably hard effort — roughly a 7/10 effort — for 20-40 minutes. This is your lactate threshold pace. The single most effective training tool for improving 2MR time. Should feel sustainably hard but not maximal."
        ));
        exerciseRepository.save(createExercise(
                "400m Repeats",
                "ANAEROBIC_ENDURANCE,LACTATE_THRESHOLD,AEROBIC_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DISTANCE,
                "Run 400 meters at a hard effort, rest for 90-120 seconds, and repeat. Builds the ability to sustain fast paces over multiple efforts. Directly develops the speed and lactate tolerance required for a fast 2MR."
        ));
        exerciseRepository.save(createExercise(
                "Hill Run",
                "AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.DURATION,
                "Run up a moderate hill at a hard effort, walk or jog back down, and repeat. Builds running-specific lower body strength and cardiovascular capacity simultaneously. Effective for Soldiers in areas without a flat track."
        ));
        exerciseRepository.save(createExercise(
                "Rowing Machine — Steady State",
                "AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE,MUSCULAR_ENDURANCE",
                ExerciseDifficulty.INTERMEDIATE,
                PrescriptionType.CARDIO,
                "Row at a moderate, sustained effort for a prescribed duration. Full-body aerobic conditioning with low impact. Builds cardiovascular capacity and muscular endurance that transfers well to 2MR performance."
        ));
        exerciseRepository.save(createExercise(
                "800m Repeats",
                "LACTATE_THRESHOLD,ANAEROBIC_ENDURANCE,AEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DISTANCE,
                "Run 800 meters at a strong sustained effort, rest 2-3 minutes, and repeat 4-6 times. One of the most effective workouts for improving 2MR time. Builds the ability to hold a demanding pace for a sustained duration."
        ));
        exerciseRepository.save(createExercise(
                "Fartlek Run",
                "LACTATE_THRESHOLD,AEROBIC_ENDURANCE,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.CARDIO,
                "Unstructured speed play — alternate between easy jogging and hard surges based on feel or landmarks. Develops both aerobic and anaerobic systems simultaneously. Excellent for Soldiers who find structured interval workouts monotonous."
        ));
        exerciseRepository.save(createExercise(
                "1-Mile Repeat",
                "LACTATE_THRESHOLD,AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DISTANCE,
                "Run 1 mile at goal 2MR pace, rest 3-4 minutes, and repeat 2-3 times. The closest training simulation to the actual 2MR test. Builds pace awareness and the ability to sustain goal speed under fatigue."
        ));
        exerciseRepository.save(createExercise(
                "Treadmill Speed Interval",
                "ANAEROBIC_ENDURANCE,LACTATE_THRESHOLD,AEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Alternate between fast treadmill intervals at above-race pace and easy recovery jogs. The treadmill forces honest pacing and removes the temptation to slow down. Set speed above current 2MR pace to develop speed reserve."
        ));
        exerciseRepository.save(createExercise(
                "Jump Rope Interval",
                "AEROBIC_ENDURANCE,ANAEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Jump rope at a fast pace for 30-60 seconds, rest briefly, and repeat. Develops foot speed, cardiovascular capacity, and lower leg endurance. A space-efficient alternative to track intervals for Soldiers in confined environments."
        ));
        exerciseRepository.save(createExercise(
                "Stair Climb",
                "AEROBIC_ENDURANCE,LOWER_BODY_ENDURANCE,ANAEROBIC_ENDURANCE",
                ExerciseDifficulty.ADVANCED,
                PrescriptionType.DURATION,
                "Climb stairs continuously at a steady pace for a prescribed duration. Builds the quad and glute endurance that directly transfers to 2MR performance. Available to Soldiers in barracks or any multi-story building — no track required."
        ));
    }
}