package sergiojuliogu.myapplication;

/**
 * Created by joseantoniocarbonell on 27/4/18.
 */

public class Session {
    private static String token = null;

    protected Session(){}

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Session.token = token;
    }
}
