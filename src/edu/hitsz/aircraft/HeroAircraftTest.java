package edu.hitsz.aircraft;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


class HeroAircraftTest {

    private HeroAircraft heroAircraft;
    @org.junit.jupiter.api.BeforeEach
    void setUp() { heroAircraft = new HeroAircraft(50,50,0,0,50);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {heroAircraft = null;
    }

    @DisplayName("Test decreaseHP method")
    @org.junit.jupiter.api.Test
    void decreaseHP() {
        heroAircraft.decreaseHp(20);
        int HP = heroAircraft.getHp();
        assertEquals(HP,30);
    }

    @DisplayName("Test increaseHP method")
    @org.junit.jupiter.api.Test
    void increaseHp() {
        heroAircraft.decreaseHp(20);
        heroAircraft.increaseHp(10);
        int HP = heroAircraft.getHp();
        assertEquals(HP,40);
    }

    @DisplayName("Test getHP method")
    @org.junit.jupiter.api.Test
    void getHp() {
        int HP = heroAircraft.getHp();
        assertEquals(HP,50);
    }
}