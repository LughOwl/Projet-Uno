package application.utils;


import javafx.scene.image.Image;


public class UtilsCardDisplay {
    /*
     * outils permettant de converture les attribut d une carte en chemin vers le fichier .png
     * de l affichage de la carte.
     * */
    public static String textToPath(String[] card) {
        String imgPath = "",color= "",number ="";

        imgPath = switch (card[0]) {
            case "Reverse" -> "/images/carte_change/carte_Change_";
            case "NumberCard" -> "/images/carte_numero/carte_";
            case "Skip" -> "/images/carte_passe/carte_Passe_";
            case "Wild" -> "/images/carte_change/carte_Change_Couleur";
            case "WildDrawFour" -> "/images/carte_plus/carte_Plus4";
            case "DrawTwo" -> "/images/carte_plus/carte_Plus2_";
            default -> imgPath;
        };



        if(card.length >= 2) {

            color = switch (card[1]) {
                case "BLUE" -> "Bleu";
                case "YELLOW" -> "Jaune";
                case "RED" -> "Rouge";
                case "GREEN" -> "Vert";
                default -> color;
            };

        }

        if(card.length >2  ) {

            number = switch (card[2]) {
                case "ONE" -> "1_";
                case "TWO" -> "2_";
                case "THREE" -> "3_";
                case "FOUR" -> "4_";
                case "FIVE" -> "5_";
                case "SIX" -> "6_";
                case "SEVEN" -> "7_";
                case "EIGHT" -> "8_";
                case "NINE" -> "9_";
                case "ZERO" -> "0_";
                default -> number;
            };


        }





        if(card[0].equals("NumberCard")){

            return imgPath+number+color+".png";

        }
        return imgPath+color+".png";


    }

    /*
     * Permer de convertire les images de chaque carte en text permetant au serveur de comprendre
     * lorsqu on jou une carte par exemple .
     * */
    public static String pathToText(Image image) {
        String[] imgPath =image.getUrl().split("/");
        String[] fileName = imgPath[imgPath.length-1].split("_");

        if(imgPath[imgPath.length-1].equals("carte_Change_Couleur.png" )){
            return "WILD";
        }else if(imgPath[imgPath.length-1].equals("carte_Plus4.png")){ return "WILD_DRAW_FOUR";}


        String cardType = switch(fileName[1]){
            case "Change" -> "REVERSE";
            case "Plus2" -> "DRAW_TWO";
            case "Passe" -> "SKIP";
            default -> "NUMBER";
        };

        String color = switch (fileName[2]){
            case "Bleu.png" -> "BLUE";
            case "Jaune.png" -> "YELLOW";
            case "Rouge.png" -> "RED";
            case "Vert.png" -> "GREEN";
            default -> "";
        };

        String number = switch (fileName[1]){
            case "0" -> "ZERO";
            case "1" -> "ONE";
            case "2" -> "TWO";
            case "3" -> "THREE";
            case "4" -> "FOUR";
            case "5" -> "FIVE";
            case "6" -> "SIX";
            case "7" -> "SEVEN";
            case "8" -> "EIGHT";
            case "9" -> "NINE";
            default -> "";
        };

        if(cardType.equals("NUMBER")){
            return cardType+","+color+","+number;
        }else {
            return cardType+","+color;
        }





    }



}