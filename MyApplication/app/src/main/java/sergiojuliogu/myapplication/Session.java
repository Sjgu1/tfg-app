package sergiojuliogu.myapplication;

/**
 * Created by joseantoniocarbonell on 27/4/18.
 */

public class Session {
    private static String token = null;
    private static Boolean loged = false;

    protected Session(){}

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Session.token = token;
    }

    public static void setLoged(Boolean loged) {
        Session.loged = loged;
    }

    public static Boolean getLoged() {
        return loged;
    }
}
