package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Movie {
    private Integer id;
    private String name;
    private String summary;
    private String releaseDate;
    private String director;
    private ArrayList<String> writers = new ArrayList<String>();
    private ArrayList<String> genres = new ArrayList<String>();
    private ArrayList<Integer> cast = new ArrayList<Integer>();
    private Float imdbRate;
    private String duration;
    private Integer ageLimit;
    private ArrayList<Rate> ratings = new ArrayList<Rate>();
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    private Float rating;
    private String image;
    private String coverImage;

    public void setImage(String image) throws Exceptions.InvalidCommand {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setCoverImage(String coverImage) throws Exceptions.InvalidCommand {
        this.coverImage = coverImage;
    }

    public String getCoverImage() {
        if(this.coverImage == null)
            return "";
        return this.coverImage;
    }

    public void setId(Integer id) throws Exceptions.InvalidCommand {
        if (id == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.id = id;
    }
    public void setName(String name) throws Exceptions.InvalidCommand {
        if (name == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.name = name;
    }
    public void setSummary(String summary) throws Exceptions.InvalidCommand {
        if (summary == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.summary = summary;
    }
    public void setReleaseDate(String releaseDate) throws Exceptions.InvalidCommand {
        if (releaseDate == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.releaseDate = releaseDate;
    }
    public void setDirector(String director) throws Exceptions.InvalidCommand {
        if (director == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.director = director;
    }
    public void setWriters(ArrayList<String> writers) throws Exceptions.InvalidCommand {
        if (writers == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.writers = (ArrayList<String>) writers.clone();
    }
    public void setGenres(ArrayList<String> genres) throws Exceptions.InvalidCommand {
        if (genres == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.genres = (ArrayList<String>) genres.clone();
    }
    public void setCast(ArrayList<Integer> cast) throws Exceptions.InvalidCommand {
        if (cast == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.cast = (ArrayList<Integer>) cast.clone();
    }
    public void setImdbRate(Float imdbRate) throws Exceptions.InvalidCommand {
        if (imdbRate == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.imdbRate = imdbRate;
    }
    public void setDuration(String duration) throws Exceptions.InvalidCommand {
        if (duration == null) {
            throw new Exceptions.InvalidCommand();
        }
        try {
            parseInt(duration);
        }
        catch (NumberFormatException e) {
            throw new Exceptions.InvalidCommand();
        }
        this.duration = duration;
    }
    public void setAgeLimit(Integer ageLimit) throws Exceptions.InvalidCommand {
        if (ageLimit == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.ageLimit = ageLimit;
    }
    public void setRatings(ArrayList<Rate> ratings) throws Exceptions.InvalidCommand {
        if (ratings == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.ratings = (ArrayList<Rate>) ratings.clone();
    }
    public void setComments(ArrayList<Comment> comments) throws Exceptions.InvalidCommand {
        if (comments == null) {
            throw new Exceptions.InvalidCommand();
        }
        this.comments = (ArrayList<Comment>) comments.clone();
    }
    public void setRating(Float rating) { this.rating = rating; }

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getSummary() {
        return this.summary;
    }
    public String getReleaseDate() {
        return this.releaseDate;
    }
    public String getDirector() {
        return this.director;
    }
    public ArrayList<String> getWriters() {
        return this.writers;
    }
    public String getWritersString() {
        return String.join(", ", this.writers);
    }

    public ArrayList<String> getGenres() {
        return this.genres;
    }
    public String getGenresString() {
        return String.join(", ", this.genres);
    }

    public ArrayList<Integer> getCast() {
        return this.cast;
    }
    public String getCastString(Map<Integer, Actor> actors) {
        ArrayList<String> resCast = new ArrayList<String>();
        for (Integer elem : cast) {
            Actor temp = actors.get(elem);
            resCast.add(temp.getName());
        }

        return String.join(", ", resCast);
    }
    public Map<Integer, Actor> getCastObject(Map<Integer, Actor> actors) {
        Map<Integer, Actor> resCast = new HashMap<Integer, Actor>();
        for (Integer elem : cast) {
            Actor temp = actors.get(elem);
            resCast.put(temp.getId(), temp);
        }
        return resCast;
    }

    public Float getImdbRate() {
        return this.imdbRate;
    }
    public String getDuration() {
        return this.duration;
    }
    public Integer getAgeLimit() {
        return this.ageLimit;
    }
    public ArrayList<Rate> getRatings() {
        return ratings;
    }
    public ArrayList<Comment> getComments() {
        return comments;
    }
    public Integer getRatingCount() {
        return ratings.size();
    }
    public Float getRating() { return rating; }

    public void appendRate(Rate rate) throws Exceptions.InvalidCommand, Exceptions.InvalidRateScore {
        for (Rate element : this.ratings) {
            if ((rate.getUserEmail().equals(element.getUserEmail())) && (rate.getMovieId().equals(element.getMovieId()))) {
                element.setScore(rate.getScore());
                updateRating();
                return;
            }
        }
        ratings.add(rate);
        updateRating();
    }

    public void appendComment(Comment comment) {
        comments.add(comment);
    }

    public void updateRating() {
        float mean = 0;
        for (Rate element: ratings) {
            mean += element.getScore();
        }
        mean = mean / getRatingCount();
        this.setRating(mean);
    }

    public String toStringMovieList() {
        return "{\"movieId\": " + id + ", " +
                "\"name\": \"" + name + "\", " +
                "\"director\": \"" + director + "\", " +
                "\"genres\": " + genres + ", " +
                "\"rating\": " + rating + "}";

    }

    public String toStringGetMovieByIdPart1() {
        return "{" +
                "\"movieId\": " + id +
                ", \"name\": \"" + name + '\"' +
                ", \"summary\": \"" + summary + '\"' +
                ", \"releaseDate\": \"" + releaseDate + '\"' +
                ", \"director\": \"" + director + '\"' +
                ", \"writers\": " + writers +
                ", \"genres\": " + genres +
                ", \"cast\": ";
    }

    public String toStringGetMovieByIdPart2() {
        return
                ", \"rating\": " + rating +
                ", \"duration\": " + duration +
                ", \"ageLimit\": " + ageLimit +
                ", \"comments\": ";
    }

    @Override
    public String toString() {
        return "{" +
                "\"movieId\"=" + id +
                ", \"name\"='" + name + '\'' +
                ", \"releaseDate\"='" + releaseDate + '\'' +
                ", \"director\"='" + director + '\'' +
                ", \"writers\"=" + writers +
                ", \"genres\"=" + genres +
                ", \"cast\"=" + cast +
                ", \"imdbRate\"=" + imdbRate +
                ", \"duration\"='" + duration + '\'' +
                ", \"ageLimit\"=" + ageLimit +
                ", \"ratings\"=" + ratings +
                ", \"comments\"=" + comments +
                ", \"rating\"=" + rating +
                '}';
    }
}
