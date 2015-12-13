package DataModels.Business;

/**
 * Created by Scott on 8/29/15.
 */
public enum STAR_RATING {
    ZERO_STAR,
    HALF_STAR,
    ONE_STAR,
    ONE_N_HALF_STARS,
    TWO_STARS,
    TWO_N_HALF_STARS,
    THREE_STARS,
    THREE_N_HALF_STARS,
    FOUR_STARS,
    FOUR_N_HALF_STARS,
    FIVE_STARS;

    public float getFloatValue() {
        return this.ordinal() / (float)2;
    }

    public STAR_RATING getRatingFromFloat(float value) {
        if (value < 0.5)
            return ZERO_STAR;
        else if (value < 1)
            return HALF_STAR;
        else if (value < 1.5)
            return ONE_STAR;
        else if (value < 2)
            return ONE_N_HALF_STARS;
        else if (value < 2.5)
            return TWO_STARS;
        else if (value < 3)
            return TWO_N_HALF_STARS;
        else if (value < 3.5)
            return THREE_STARS;
        else if (value < 4)
            return THREE_N_HALF_STARS;
        else if (value < 4.5)
            return FOUR_STARS;
        else if (value < 5)
            return FOUR_N_HALF_STARS;
        else
            return FIVE_STARS;
    }
}
