package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Comment {
    private Integer id;
    private String userEmail;
    private Integer movieId;
    private String text;
    private String createdTime;
    private ArrayList<Vote> votes = new ArrayList<Vote>();

    public Integer getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public Integer getMovieId() {
        return movieId;
    }
    public String getText() {
        return text;
    }
    public String getCreatedTime() {
        return createdTime;
    }
    public ArrayList<Vote> getVotes() {
        return this.votes;
    }

    public Comment(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.createdTime = dtf.format(now);
    }

    public void setId(Integer id) throws Exceptions.InvalidCommand {
        this.id = id;
    }
    public void setCreatedTime(String createdTime) throws Exceptions.InvalidCommand {
        if(createdTime == null)
            throw new Exceptions.InvalidCommand();
        this.createdTime = createdTime;
    }
    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
    }
    public void appendVotes(Vote vote) throws Exceptions.InvalidCommand, Exceptions.InvalidVoteValue {
        for (Vote element : this.votes) {
            if ((vote.getCommentId().equals(element.getCommentId())) && (vote.getUserEmail().equals(element.getUserEmail()))) {
                element.setVote(vote.getVote());
                return;
            }
        }
        votes.add(vote);
    }

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
    public void setText(String text) throws Exceptions.InvalidCommand {
        if(text == null || text == "")
            throw new Exceptions.InvalidCommand();
        this.text = text;
    }

    public Integer getLikes(){
        int num = 0;
        for (Vote vote : votes){
            if(vote.getVote() == 1)
                num +=1 ;
        }
        return num;
    }

    public Integer getDisLikes(){
        int num = 0;
        for (Vote vote : votes){
            if(vote.getVote() == -1)
                num +=1 ;
        }
        return num;
    }

    public Integer getVoteCounts(){
        int num = 0;
        for (Vote vote : votes){
            num +=1 ;
        }
        return num;
    }

    @Override
    public String toString() {
        return "{" +
                "\"commentId\": " + id +
                ", \"userEmail\": \"" + userEmail + "\"" +
                ", \"text\": \"" + text + "\"" +
                ", \"like\": " + getLikes() +
                ", \"dislike\": " + getDisLikes() +
                '}';
    }
}