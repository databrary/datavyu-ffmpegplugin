package org.datavyu.plugins;

import java.util.HashMap;
import java.util.Map;

public class PlaybackRateController {

  // Defines the rate symbols with mapping to the float value
  public enum Rate {
    UNKNOWN(Float.NaN),
    MINUS_32(-32f),
    MINUS_16(-16f),
    MINUS_8(-8f),
    MINUS_4(-4f),
    MINUS_2(-2f),
    MINUS_1(-1f),
    MINUS_1_DIV_2(-1f / 2f),
    MINUS_1_DIV_4(-1f / 4f),
    MINUS_1_DIV_8(-1f / 8f),
    MINUS_1_DIV_16(-1f / 16f),
    MINUS_1_DIV_32(-1f / 32f),
    ZERO(+0f),
    PLUS_1_DIV_32(+1f / 32f),
    PLUS_1_DIV_16(+1f / 16f),
    PLUS_1_DIV_8(+1f / 8f),
    PLUS_1_DIV_4(+1f / 4f),
    PLUS_1_DIV_2(+1f / 2f),
    PLUS_1(+1f),
    PLUS_2(+2f),
    PLUS_4(+4f),
    PLUS_8(+8f),
    PLUS_16(+16f),
    PLUS_32(+32f);

    Rate(float value) {
      this.value = value;
    }

    private final float value;

    private static final Map<Float, Rate> VALUE_TO_RATE =
        new HashMap<Float, Rate>() {
          {
            put(MINUS_32.getValue(), MINUS_32);
            put(MINUS_16.getValue(), MINUS_16);
            put(MINUS_8.getValue(), MINUS_8);
            put(MINUS_4.getValue(), MINUS_4);
            put(MINUS_2.getValue(), MINUS_2);
            put(MINUS_1.getValue(), MINUS_1);
            put(MINUS_1_DIV_2.getValue(), MINUS_1_DIV_2);
            put(MINUS_1_DIV_4.getValue(), MINUS_1_DIV_4);
            put(MINUS_1_DIV_8.getValue(), MINUS_1_DIV_8);
            put(MINUS_1_DIV_16.getValue(), MINUS_1_DIV_16);
            put(MINUS_1_DIV_32.getValue(), MINUS_1_DIV_32);
            put(ZERO.getValue(), ZERO);
            put(PLUS_1_DIV_32.getValue(), PLUS_1_DIV_32);
            put(PLUS_1_DIV_16.getValue(), PLUS_1_DIV_16);
            put(PLUS_1_DIV_8.getValue(), PLUS_1_DIV_8);
            put(PLUS_1_DIV_4.getValue(), PLUS_1_DIV_4);
            put(PLUS_1_DIV_2.getValue(), PLUS_1_DIV_2);
            put(PLUS_1.getValue(), PLUS_1);
            put(PLUS_2.getValue(), PLUS_2);
            put(PLUS_4.getValue(), PLUS_4);
            put(PLUS_8.getValue(), PLUS_8);
            put(PLUS_16.getValue(), PLUS_16);
            put(PLUS_32.getValue(), PLUS_32);
          }
        };

    // Maps to next upper rate
    private static final Map<Rate, Rate> NEXT_UPPER =
        new HashMap<Rate, Rate>() {
          {
            put(MINUS_32, MINUS_16);
            put(MINUS_16, MINUS_8);
            put(MINUS_8, MINUS_4);
            put(MINUS_4, MINUS_2);
            put(MINUS_2, MINUS_1);
            put(MINUS_1, MINUS_1_DIV_2);
            put(MINUS_1_DIV_2, MINUS_1_DIV_4);
            put(MINUS_1_DIV_4, MINUS_1_DIV_8);
            put(MINUS_1_DIV_8, MINUS_1_DIV_16);
            put(MINUS_1_DIV_16, MINUS_1_DIV_32);
            put(MINUS_1_DIV_32, ZERO);
            put(ZERO, PLUS_1_DIV_32);
            put(PLUS_1_DIV_32, PLUS_1_DIV_16);
            put(PLUS_1_DIV_16, PLUS_1_DIV_8);
            put(PLUS_1_DIV_8, PLUS_1_DIV_4);
            put(PLUS_1_DIV_4, PLUS_1_DIV_2);
            put(PLUS_1_DIV_2, PLUS_1);
            put(PLUS_1, PLUS_2);
            put(PLUS_2, PLUS_4);
            put(PLUS_4, PLUS_8);
            put(PLUS_8, PLUS_16);
            put(PLUS_16, PLUS_32);
            put(PLUS_32, UNKNOWN);
          }
        };

    // Maps to the next lower rate
    private static final Map<Rate, Rate> NEXT_LOWER =
        new HashMap<Rate, Rate>() {
          {
            put(MINUS_32, UNKNOWN);
            put(MINUS_16, MINUS_32);
            put(MINUS_8, MINUS_16);
            put(MINUS_4, MINUS_8);
            put(MINUS_2, MINUS_4);
            put(MINUS_1, MINUS_2);
            put(MINUS_1_DIV_2, MINUS_1);
            put(MINUS_1_DIV_4, MINUS_1_DIV_2);
            put(MINUS_1_DIV_8, MINUS_1_DIV_4);
            put(MINUS_1_DIV_16, MINUS_1_DIV_8);
            put(MINUS_1_DIV_32, MINUS_1_DIV_16);
            put(ZERO, MINUS_1_DIV_32);
            put(PLUS_1_DIV_32, ZERO);
            put(PLUS_1_DIV_16, PLUS_1_DIV_32);
            put(PLUS_1_DIV_8, PLUS_1_DIV_16);
            put(PLUS_1_DIV_4, PLUS_1_DIV_8);
            put(PLUS_1_DIV_2, PLUS_1_DIV_4);
            put(PLUS_1, PLUS_1_DIV_2);
            put(PLUS_2, PLUS_1);
            put(PLUS_4, PLUS_2);
            put(PLUS_8, PLUS_4);
            put(PLUS_16, PLUS_8);
            put(PLUS_32, PLUS_16);
          }
        };

    public static Rate getRate(Float value) {
      return VALUE_TO_RATE.get(value);
    }

    public Rate getNextLower() {
      Rate next = NEXT_LOWER.get(this);
      return next == UNKNOWN ? this : next;
    }

    public Rate getNextUpper() {
      Rate next = NEXT_UPPER.get(this);
      return next == UNKNOWN ? this : next;
    }

    public Float getValue() {
      return value;
    }
  }

  public static Float stepToFaster(float value) {
    return Rate.getRate(value).getNextUpper().getValue();
  }

  public static Float stepToSlower(float value) {
    return Rate.getRate(value).getNextLower().getValue();
  }
}
