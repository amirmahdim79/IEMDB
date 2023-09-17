package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

public class Rate {
    private String userEmail;
    private Integer movieId;
    private Integer score;

    public void setUserEmail(String userEmail) throws Exceptions.InvalidCommand {
        if(userEmail == null)
            throw new Exceptions.InvalidCommand();

        this.userEmail = userEmail;
    }
    public void setMovieId(Integer movieId) throws Exceptions.InvalidCommand {
        if(movieId == null)
            throw new Exceptions.InvalidCommand();

        this.movieId = movieId;
    }
    public void setScore(Integer score) throws Exceptions.InvalidCommand {
        if(score == null)
            throw new Exceptions.InvalidCommand();

        this.score = score;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public Integer getMovieId() {
        return movieId;
    }
    public Integer getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "userEmail='" + userEmail + '\'' +
                ", movieId=" + movieId +
                ", score=" + score +
                '}';
    }
}
