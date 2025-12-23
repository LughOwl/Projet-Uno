package application.model.card.special.draw;

import application.model.card.special.SpecialCard;
import application.model.enumUno.Color;
import application.model.enumUno.ScoreValue;

public abstract class Draw extends SpecialCard {

    protected int drawAmount;

    public Draw(String description, Color color, ScoreValue score, int drawAmount) {
        super(description, color, score);
        this.drawAmount = drawAmount;
    }

    public int getDrawAmount() {
        return drawAmount;
    }

    public void setDrawAmount(int drawAmount) {
        this.drawAmount = drawAmount;
    }

    @Override
    public String toString() {
        return "Draw";
    }
}
