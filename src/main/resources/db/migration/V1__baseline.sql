-- V1__baseline.sql
-- Baseline schema for Road to 500 as of 2026-05-12
-- Captured after ON DELETE CASCADE was added to all soldier-dependent FK constraints

-- ── Reference / lookup tables (no FK dependencies) ───────────────────────────

CREATE TABLE IF NOT EXISTS aft_event (
                                         id          BIGSERIAL PRIMARY KEY,
                                         abbreviation    VARCHAR(255) NOT NULL,
                                         categories      VARCHAR(255) NOT NULL,
                                         description     TEXT,
                                         name            VARCHAR(255) NOT NULL,
                                         unit            VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS exercise (
                                        id                  BIGSERIAL PRIMARY KEY,
                                        name                VARCHAR(255) NOT NULL,
                                        description         TEXT,
                                        difficulty          VARCHAR(255) NOT NULL,
                                        categories          VARCHAR(255) NOT NULL,
                                        prescription_type   VARCHAR(255) NOT NULL
);

-- ── Soldier and direct dependents ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS soldier (
                                       id              BIGSERIAL PRIMARY KEY,
                                       name            VARCHAR(255) NOT NULL,
                                       password        VARCHAR(255),
                                       date_of_birth   DATE NOT NULL,
                                       gender          VARCHAR(255) NOT NULL,
                                       mos             VARCHAR(255) NOT NULL,
                                       created_at      DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS soldier_profile (
                                               id                      BIGSERIAL PRIMARY KEY,
                                               soldier_id              BIGINT NOT NULL UNIQUE,
                                               trap_bar_3rm            INTEGER NOT NULL,
                                               last_hrp_count          INTEGER NOT NULL,
                                               two_mile_time_seconds   INTEGER NOT NULL,
                                               bench_press_1rm         INTEGER,
                                               body_weight_lbs         INTEGER NOT NULL,
                                               height_inches           INTEGER NOT NULL,
                                               CONSTRAINT fklg4c1wxihg0v5x32datvrap3v
                                                   FOREIGN KEY (soldier_id) REFERENCES soldier(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS aft_test_result (
                                               id          BIGSERIAL PRIMARY KEY,
                                               soldier_id  BIGINT NOT NULL,
                                               test_date   DATE NOT NULL,
                                               total_score INTEGER NOT NULL,
                                               notes       TEXT,
                                               CONSTRAINT fkjjovnk3yl9k9v9pnj855uaf1a
                                                   FOREIGN KEY (soldier_id) REFERENCES soldier(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS improvement_suggestion (
                                                      id                              BIGSERIAL PRIMARY KEY,
                                                      soldier_id                      BIGINT NOT NULL,
                                                      aft_event_id                    BIGINT NOT NULL,
                                                      check_in_frequency              VARCHAR(255) NOT NULL,
                                                      date_suggestion_created         DATE NOT NULL,
                                                      event_score                     INTEGER NOT NULL,
                                                      improvement_generation_source   VARCHAR(255) NOT NULL,
                                                      max_score_gap                   INTEGER NOT NULL,
                                                      priority_level                  VARCHAR(255) NOT NULL,
                                                      CONSTRAINT fk2s3ts44dqgeu5d2vnnadkpv18
                                                          FOREIGN KEY (soldier_id) REFERENCES soldier(id) ON DELETE CASCADE,
                                                      CONSTRAINT fkjmoh9xljgejeq96esg254sqqf
                                                          FOREIGN KEY (aft_event_id) REFERENCES aft_event(id)
);

CREATE TABLE IF NOT EXISTS weekly_plan (
                                           id                          BIGSERIAL PRIMARY KEY,
                                           soldier_id                  BIGINT NOT NULL,
                                           week_start                  DATE NOT NULL,
                                           week_end                    DATE NOT NULL,
                                           week_status                 VARCHAR(255) NOT NULL,
                                           date_time_group_generation  TIMESTAMP NOT NULL,
                                           CONSTRAINT fknow9w7hb4xi6vwu6f31eq66ws
                                               FOREIGN KEY (soldier_id) REFERENCES soldier(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS soldier_exercise_progression (
                                                            id                          BIGSERIAL PRIMARY KEY,
                                                            soldier_id                  BIGINT NOT NULL,
                                                            exercise_id                 BIGINT NOT NULL,
                                                            current_weight              INTEGER,
                                                            current_reps                INTEGER,
                                                            current_distance            INTEGER,
                                                            current_duration            INTEGER,
                                                            consecutive_failure_count   INTEGER,
                                                            last_performed              DATE,
                                                            pyramid_step                INTEGER,
                                                            CONSTRAINT fklrb3tdcprm99k5vbp3krikwit
                                                                FOREIGN KEY (soldier_id) REFERENCES soldier(id) ON DELETE CASCADE,
                                                            CONSTRAINT fk26x3eguftep5ltmtrptrnhmdm
                                                                FOREIGN KEY (exercise_id) REFERENCES exercise(id)
);

-- ── Event score (child of aft_test_result) ────────────────────────────────────

CREATE TABLE IF NOT EXISTS event_score (
                                           id                  BIGSERIAL PRIMARY KEY,
                                           aft_test_result_id  BIGINT NOT NULL,
                                           aft_event_id        BIGINT NOT NULL,
                                           raw_value           INTEGER,
                                           points_earned       INTEGER NOT NULL,
                                           is_check_in         BOOLEAN NOT NULL DEFAULT FALSE,
                                           CONSTRAINT fkkp0hhdq4phbuxwg7rcs0inxs7
                                               FOREIGN KEY (aft_test_result_id) REFERENCES aft_test_result(id) ON DELETE CASCADE,
                                           CONSTRAINT fkb3602ildmypytddxqnaresgp8
                                               FOREIGN KEY (aft_event_id) REFERENCES aft_event(id)
);

-- ── Plan hierarchy ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS planned_session (
                                               id                  BIGSERIAL PRIMARY KEY,
                                               weekly_plan_id      BIGINT NOT NULL,
                                               session_type        VARCHAR(255) NOT NULL,
                                               session_date        DATE NOT NULL,
                                               day_of_week         VARCHAR(255) NOT NULL,
                                               day_status          VARCHAR(255) NOT NULL,
                                               session_description TEXT NOT NULL,
                                               check_in_set        BIGINT,
                                               planned_min_rpe     INTEGER,
                                               planned_max_rpe     INTEGER,
                                               user_rpe            INTEGER,
                                               CONSTRAINT fk7mjg63w4tepgjtrag6bv3ydpd
                                                   FOREIGN KEY (weekly_plan_id) REFERENCES weekly_plan(id) ON DELETE CASCADE,
                                               CONSTRAINT fka2uwnboq543g4ubxe3ch8ttkd
                                                   FOREIGN KEY (check_in_set) REFERENCES aft_event(id)
);

CREATE TABLE IF NOT EXISTS planned_exercise (
                                                id                      BIGSERIAL PRIMARY KEY,
                                                planned_session_id      BIGINT NOT NULL,
                                                exercise_id             BIGINT NOT NULL,
                                                sets                    INTEGER,
                                                reps                    INTEGER,
                                                weight                  INTEGER,
                                                planned_exercise_unit   VARCHAR(255),
                                                exercise_time           INTEGER,
                                                exercise_distance       INTEGER,
                                                exercise_pace           INTEGER,
                                                rest_time               INTEGER,
                                                percentage_1rm          INTEGER,
                                                CONSTRAINT fk1kgbptyufdkkj1vf9d28u5dqa
                                                    FOREIGN KEY (planned_session_id) REFERENCES planned_session(id) ON DELETE CASCADE,
                                                CONSTRAINT fkcbmiflah751vbaqtqrpuav45d
                                                    FOREIGN KEY (exercise_id) REFERENCES exercise(id)
);

-- ── Join table ────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS exercise_aft_event (
                                                  id                  BIGSERIAL PRIMARY KEY,
                                                  exercise_id         BIGINT NOT NULL,
                                                  aft_event_id        BIGINT NOT NULL,
                                                  contribution_level  VARCHAR(255) NOT NULL,
                                                  CONSTRAINT fk7qchl4p982pv5kroeoaav9y12
                                                      FOREIGN KEY (exercise_id) REFERENCES exercise(id),
                                                  CONSTRAINT fk6ttcioxm0hs893xwl10dbu99v
                                                      FOREIGN KEY (aft_event_id) REFERENCES aft_event(id)
);