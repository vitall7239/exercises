package org.vpetrovych.exercises.battleships;

import java.util.Locale;
import java.util.Objects;

public class MyBattleship8x8 {
    private final long ships;
    private long shots = 0L;

    public MyBattleship8x8(final long ships) {
        this.ships = ships;
    }

    public boolean shoot(String shot) {
        if(Objects.isNull(shot) || shot.length() != 2) {
            System.out.println("Error: shot has wrong format");
            return false;
        }
        int colNumber = shot.toLowerCase(Locale.ROOT).charAt(0) - 'a';
        int rowNumber = Character.getNumericValue(shot.charAt(1)) - 1;
        int shift = 63 - (colNumber + rowNumber * 8);
        long currentShot = 1L << shift;
        shots = currentShot | shots;
        return (ships & currentShot) != 0;
    }

    public String state() {
        StringBuilder state = new StringBuilder();
        for(int i = 0; i < 64; i++){
            if(i % 8 == 0 && i != 0){
                state.append('\n');
            }
            int shift = 63 - i;
            long mask = 1L << shift;
            if((ships & mask) == 0){
                if((shots & mask) == 0){
                    state.append('.');
                }else{
                    state.append('×');
                }
            }else{
                if((shots & mask) == 0){
                    state.append('☐');
                }else{
                    state.append('☒');
                }
            }
        }
        return state.toString();
    }
}
