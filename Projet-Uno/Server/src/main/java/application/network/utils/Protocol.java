package application.network.utils;

public enum Protocol {
    CONNECTION("CONNEXION"),
    CONNECTION_OK("CONNEXION_OK"),
    DECONNECTION("DECONNEXION"),

    READY("PLAYER_IS_READY"),
    NOT_READY("PLAYER_IS_NOT_READY"),
    TO_ALL("TO_ALL"),
    LIST_USERS("LISTUSERS"),
    PUBLIC_FROM("PUBLIC_FROM"),

    DEMARRER_PARTIE("DEMARRER_PARTIE"),



    ERROR("ERROR");

    private final String protocol;

    Protocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return "@" + protocol;
    }

    @Override
    public String toString() {
        return getProtocol();
    }

}
