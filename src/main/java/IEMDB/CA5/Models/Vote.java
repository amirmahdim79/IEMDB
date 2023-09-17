package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

import java.util.ArrayList;

public class Vote {
    private String userEmail;
    private Integer commentId;
    private Integer vote;

    public void setUserEmail(String userEmail) throws Exceptions.InvalidCommand {
        if(userEmail == null)
            throw new Exceptions.InvalidCommand();
        this.userEmail = userEmail;
    }
    public void setCommentId(Integer commentId) throws Exceptions.InvalidCommand {
        if(commentId == null)
            throw new Exceptions.InvalidCommand();
        this.commentId = commentId;
    }
    public void setVote(Integer vote) throws Exceptions.InvalidCommand, Exceptions.InvalidVoteValue {
        if(vote == null)
            throw new Exceptions.InvalidCommand();
        this.vote = vote;
    }



    public String getUserEmail() {
        return userEmail;
    }
    public Integer getCommentId() {
        return commentId;
    }
    public Integer getVote() {
        return vote;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "userEmail='" + userEmail + '\'' +
                ", commentId=" + commentId +
                ", vote=" + vote +
                '}';
    }
}