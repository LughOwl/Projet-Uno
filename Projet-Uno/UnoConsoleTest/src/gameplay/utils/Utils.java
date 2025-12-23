package gameplay.utils;

import gameplay.card.Card;
import gameplay.card.Number;
import gameplay.card.special.DrawFour;
import gameplay.card.special.Wild;
import gameplay.enumUno.Color;
import gameplay.enumUno.NumberEnum;
import gameplay.enumUno.Type;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

    // TODO: A ameliorer car actuellement on peut pas facilement avoir de nouvel carte
    public static ArrayList<Card> generateData(int nbr) {
        ArrayList<Card> discardPile = new ArrayList<>();
        for(int i = 0; i < nbr; i++){
            Random random = new Random();
            Type[] tabType = Type.values();
            Type type = tabType[random.nextInt(tabType.length)];
            Color[] tabColor = Color.values();
            Color color;

            if (type == Type.WILD || type == Type.WILD_DRAW_FOUR) {
                if (type == Type.WILD)
                    discardPile.add(new Wild());
                else
                    discardPile.add(new DrawFour());
                continue;
            }
            else
                color = tabColor[random.nextInt(tabColor.length-2)]; // -2 pour ne pas prendre WILD et WILD_DRAW_FOUR


            switch(type) {
                case NUMBER:
                    NumberEnum[] tabNumber = NumberEnum.values();
                    discardPile.add(new Number(color, tabNumber[random.nextInt(tabNumber.length)]));
                    break;
                case SKIP:
                    discardPile.add(new gameplay.card.special.Skip(color));
                    break;
                case REVERSE:
                    discardPile.add(new gameplay.card.special.Reverse(color));
                    break;
                case DRAW_TWO:
                    discardPile.add(new gameplay.card.special.DrawTwo(color));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type");
            }
        }
//    return discardPile;
        return discardPile;
    }

    public static void random(ArrayList<Card> discardPile) {
        for (int i = 0; i < discardPile.size(); i++) {
            int randomIndex = (int) (Math.random() * discardPile.size());
            Card temp = discardPile.get(i);
            discardPile.set(i, discardPile.get(randomIndex));
            discardPile.set(randomIndex, temp);
        }
    }
}

