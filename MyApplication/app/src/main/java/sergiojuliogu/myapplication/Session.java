package sergiojuliogu.myapplication;

/**
 * Created by joseantoniocarbonell on 27/4/18.
 */

public class Session {
    private static String token = null;
    private static Boolean loged = false;
    private static String username = null;
    private static String projectSelected = null;
    private static String sprintSelected = null;
    private static boolean cambios = false;
    private static String statusSelected = null;
    private static String taskSelected = null;

    //public static String URL = "https://sergiojuliogu-tfg-2018.herokuapp.com";
    public static String URL = "http://10.0.2.2:5000";


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

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Session.username = username;
    }

    public static String getProjectSelected() {
        return projectSelected;
    }

    public static void setProjectSelected(String projectSelected) {
        Session.projectSelected = projectSelected;
    }

    public static String getSprintSelected() {
        return sprintSelected;
    }

    public static void setSprintSelected(String sprintSelected) {
        Session.sprintSelected = sprintSelected;
    }

    public static String getStatusSelected() {
        return statusSelected;
    }

    public static void setStatusSelected(String statusSelected) {
        Session.statusSelected = statusSelected;
    }

    public static String getTaskSelected() {
        return taskSelected;
    }

    public static void setTaskSelected(String taskSelected) {
        Session.taskSelected = taskSelected;
    }

    public static void setCambios(boolean cambios) {
        Session.cambios = cambios;
    }

    public static boolean isCambios() {
        return cambios;
    }

    public static  void logOut(){
        setLoged(false);
        setToken(null);
        setUsername(null);
        setProjectSelected(null);
        setSprintSelected(null);
        setCambios(false);
        setStatusSelected(null);
        setTaskSelected(null);
    }
}
