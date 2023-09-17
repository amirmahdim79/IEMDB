package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String birthDate;
    private ArrayList<Movie> watchList = new ArrayList<Movie>();


//TODO: Email is Unique Primary Key !!!! (Don't Update it)


    public void setEmail(String email) throws Exceptions.InvalidCommand {
        if(email == null)
            throw new Exceptions.InvalidCommand();
        this.email = email;
    }
    public void setPassword(String password) throws Exceptions.InvalidCommand {
//        if(password == null)
//            throw new Exceptions.InvalidCommand();
        this.password = password;
    }
    public void setNickname(String nickname) throws Exceptions.InvalidCommand {
        if(nickname == null)
            throw new Exceptions.InvalidCommand();
        this.nickname = nickname;
    }
    public void setName(String name) throws Exceptions.InvalidCommand {
        if(name == null)
            throw new Exceptions.InvalidCommand();
        this.name = name;
    }
    public void setBirthDate(String birthDate) throws Exceptions.InvalidCommand {
        if(birthDate == null)
            throw new Exceptions.InvalidCommand();
        this.birthDate = birthDate;
    }
    public void setWatchList(ArrayList<Movie> movies) throws Exceptions.InvalidCommand {
        if(movies == null)
            throw new Exceptions.InvalidCommand();
        this.watchList = movies;
    }
    public void appendWatchList(Movie movie) throws Exceptions.MovieAlreadyExists, Exceptions.AgeLimitError {
        LocalDate birth = LocalDate.parse(this.birthDate);
        LocalDate now = LocalDate.now();
        Integer age = Period.between(birth,now).getYears();
        if(movie.getAgeLimit() > age) {
            System.out.println("age limit error");
            throw new Exceptions.AgeLimitError();
        }

        for (Movie element : this.watchList) {
            if ((movie.getId().equals(element.getId()))) {
                System.out.println("movie exist error");
                throw new Exceptions.MovieAlreadyExists();
            }
        }

        watchList.add(movie);
    }

    public void removeWatchList(Movie movie) throws Exceptions.MovieNotFound {
        for (Movie element : this.watchList) {
            if ((movie.getId().equals(element.getId()))) {
                watchList.remove(element);
                return;
            }
        }
        throw new Exceptions.MovieNotFound();
    }

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getNickname() {
        return this.nickname;
    }
    public String getName() {
        return this.name;
    }
    public String getBirthDate() {
        return this.birthDate;
    }
    public ArrayList<Movie> getWatchList() {
        return watchList;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", watchList=" + watchList +
                '}';
    }
}