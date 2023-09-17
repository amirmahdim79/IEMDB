package IEMDB.CA5.Models;

public class WatchList {
    private String userEmail;
    private int movieId;

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public int getMovieId() {
        return movieId;
    }

    @Override
    public String toString() {
        return "WatchList{" +
                "userEmail='" + userEmail + '\'' +
                ", movieId=" + movieId +
                '}';
    }
}
