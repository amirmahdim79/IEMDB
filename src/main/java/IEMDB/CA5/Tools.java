package IEMDB.CA5;

import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import IEMDB.CA5.ExternalDataHandler.requestAPI;
import IEMDB.CA5.Models.*;
import IEMDB.CA5.dbHandler.Repository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.lang.Integer.parseInt;


public class Tools {
    private static Tools instance;
    private static ObjectMapper mapper = new ObjectMapper();
    static User currentUser = null;

    public static Tools getInstance() {
        if (instance == null)
            instance = new Tools();
        return instance;
    }

    public static void main() throws Exception {
        Repository.main();
    }

    public static void checkDate(String date) throws IOException, Exceptions.InvalidCommand {
        String[] splitedDate = date.split("-", 3);
        if (splitedDate.length != 3) {
            throw new Exceptions.InvalidCommand();
        }
        if (splitedDate[0].length() != 4 || splitedDate[1].length() < 1 || splitedDate[2].length() < 1) {
            throw new Exceptions.InvalidCommand();
        }
        try {
            for (String element : splitedDate) {
                parseInt(element);
            }
        }
        catch (NumberFormatException e) {
            throw new Exceptions.InvalidCommand();
        }
        if (parseInt(splitedDate[1]) > 12 || parseInt(splitedDate[2]) > 31) {
            throw new Exceptions.InvalidCommand();
        }
    }


    public static void addComment(String payload,String email) throws JsonProcessingException, Exceptions.UserNotFound, Exceptions.MovieNotFound, Exceptions.InvalidCommand, SQLException {
        Comment comment = mapper.readValue(payload, Comment.class);
        comment.setUserEmail(email);
        Integer id = Repository.getInstance().getLastCommentId();
        comment.setId(id + 1);
        Repository.getInstance().addComment(comment);
    }

    public static void rateMovie(String payload,String email) throws JsonProcessingException, Exceptions.InvalidCommand, Exceptions.InvalidRateScore, Exceptions.UserNotFound, Exceptions.MovieNotFound, SQLException {
        Rate rate = mapper.readValue(payload, Rate.class);
        rate.setUserEmail(email);
        if (rate.getScore() < 1 || rate.getScore() > 10)
            throw new Exceptions.InvalidRateScore();
        Repository.getInstance().addMovieRating(rate);
        System.out.println("{\"success\": true, \"data\": " + "movie rated successfully" + "}");
    }

    public static void voteComment(String payload,String email) throws JsonProcessingException, Exceptions.UserNotFound, Exceptions.CommentNotFound, Exceptions.InvalidCommand, Exceptions.InvalidVoteValue {
        Vote vote = mapper.readValue(payload, Vote.class);
        vote.setUserEmail(email);
        ArrayList<Integer> validNumbers = new ArrayList<Integer>();
        validNumbers.add(-1);
        validNumbers.add(0);
        validNumbers.add(1);
        if(!validNumbers.contains(vote.getVote()))
            throw new Exceptions.InvalidVoteValue();

        Repository.getInstance().addVote(vote);
        System.out.println("{\"success\": true, \"data\": " + "comment voted successfully" + "}");
    }

    public static void addToWatchList(String payload,String email) throws JsonProcessingException, Exceptions.MovieNotFound, Exceptions.UserNotFound, Exceptions.AgeLimitError, Exceptions.MovieAlreadyExists, Exceptions.InvalidCommand, SQLException {
        WatchList watchList = mapper.readValue(payload, WatchList.class);
        watchList.setUserEmail(email);
        Movie movie = getOneMovie(watchList.getMovieId());
        User user = getOneUser(email);
        LocalDate birth = LocalDate.parse(user.getBirthDate());
        LocalDate now = LocalDate.now();
        System.out.println(email);
        System.out.println(user.getEmail());
        System.out.println(user.getWatchList());
        Integer age = Period.between(birth, now).getYears();
        if (movie.getAgeLimit() > age) {
            System.out.println("age limit error");
            throw new Exceptions.AgeLimitError();
        }
        for (Movie element : user.getWatchList()) {
            if ((movie.getId().equals(element.getId()))) {
                System.out.println("movie exist error");
                throw new Exceptions.MovieAlreadyExists();
            }
        }
        Repository.getInstance().appendToWatchList(watchList.getUserEmail(), watchList.getMovieId());
        System.out.println("{\"success\": true, \"data\": " + "movie added to watchlist successfully" + "}");
    }

    public static void removeMovieFromWatchList(String payload,String userEmail) throws JsonProcessingException, Exceptions.UserNotFound, Exceptions.MovieNotFound, Exceptions.MovieAlreadyExists, Exceptions.AgeLimitError, Exceptions.InvalidCommand, SQLException {
        WatchList w = mapper.readValue(payload,WatchList.class);
        User user = getOneUser(userEmail);
        Movie movie = getOneMovie(w.getMovieId());
        Repository.getInstance().removeFromWatchList(movie,user);
        System.out.println("{\"success\": true, \"data\": " + "movie removed from watchlist successfully" + "}");
    }


    public static Map<Integer, Movie> getMoviesByGenre(String payload) throws JsonProcessingException, Exceptions.InvalidCommand {
        Map<String, Object> map = mapper.readValue(payload, new TypeReference<Map<String,Object>>(){});
        Map<Integer, Movie> movies = Repository.getInstance().getMoviesByGenre((String) map.get("genre"));
        return movies;
    }

    public static Map<Integer, Movie> getMoviesByDate(String payload) throws JsonProcessingException, Exceptions.InvalidCommand {
        try
        {
            Map<String, Object> map = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            Map<Integer, Movie> new_movies = Repository.getInstance().getMoviesByDate((Integer) map.get("start_year"), (Integer) map.get("end_year"));
            System.out.println("{\"success\": true, \"data\": " + new_movies.toString() + "}");
            return new_movies;
        }
        catch (Exception e){
            throw new Exceptions.InvalidCommand();
        }
    }

    public static ArrayList<Movie> getWatchList(String payload) throws Exceptions.InvalidCommand, JsonProcessingException, Exceptions.UserNotFound, SQLException {
        Map<String, Object> map = mapper.readValue(payload, new TypeReference<Map<String,Object>>(){});
        ArrayList<Movie> watchList  = Repository.getInstance().getWatchlist(getOneUser((String) map.get("userEmail")));
        return watchList;
    }

    public static Map<Integer, Movie> getStarred(String id) throws Exceptions.ActorNotFound, Exceptions.InvalidCommand {
        Actor actor = Repository.getInstance().getOneActor(Integer.valueOf(id));
        if(actor.getId() == null)
            throw new Exceptions.ActorNotFound();
        Map<Integer, Movie> movies = Repository.getInstance().getActorMovies(id);
        return movies;
    }

    public static Map<Integer, Movie> searchName(String name) throws Exceptions.InvalidCommand {
        Map<Integer, Movie> new_movies = new HashMap<Integer, Movie>();
        new_movies = Repository.getInstance().searchName(name);
        return new_movies;
    }

    public static Map<Integer,Movie> sortMoviesByImdbRating() throws Exceptions.InvalidCommand, SQLException {
        ArrayList<Movie> movies = Repository.getInstance().getMoviesSortedByImdb();
        Map<Integer,Movie> result = new HashMap<Integer,Movie>();
        int counter = 1;
        for(Movie movie : movies)
        {
            result.put(counter,movie);
            counter +=1 ;
        }
        return result;
    }

    public static Map<Integer,Movie> sortMoviesByDate() throws Exceptions.InvalidCommand, SQLException {
        ArrayList<Movie> movies = Repository.getInstance().getMoviesSortedByDate();
        Map<Integer,Movie> result = new HashMap<Integer,Movie>();
        int counter = 1;
        for(Movie movie : movies)
        {
            result.put(counter,movie);
            counter +=1 ;
        }
        return result;
    }

    public static Set<Map.Entry<Integer, Float>> sortMoviesByScore(HashMap<Integer,Float> scores){
        Set<Map.Entry<Integer, Float>> entries = scores.entrySet();
        Comparator<Map.Entry<Integer, Float>> valueComparator =
                new Comparator<Map.Entry<Integer, Float>>() {
                    @Override public int compare(Map.Entry<Integer, Float> e1,Map.Entry<Integer, Float> e2)
                    { Float v1 = e1.getValue();
                        Float v2 = e2.getValue();
                        return v2.compareTo(v1);
                    }
                };

        List<Map.Entry<Integer, Float>> listOfEntries= new ArrayList<Map.Entry<Integer, Float>>(entries);
        Collections.sort(listOfEntries, valueComparator);
        LinkedHashMap<Integer, Float> sortedByValue= new LinkedHashMap<Integer, Float>(listOfEntries.size());
        for(Map.Entry<Integer, Float> entry : listOfEntries)
            sortedByValue.put(entry.getKey(), entry.getValue());
        Set<Map.Entry<Integer, Float>> entrySetSortedByValue = sortedByValue.entrySet();
        return entrySetSortedByValue;
    }

    public static ArrayList<Movie> getRecommendedMovies(String email) throws Exceptions.InvalidCommand, Exceptions.UserNotFound, JsonProcessingException, SQLException, Exceptions.MovieNotFound {

        String payload = "{\"userEmail\" : " + email + "}";
        ArrayList<Movie> watchlist = getWatchList(payload);
        HashMap <Integer,Float> movie_scores = new HashMap<Integer,Float>();
        for (Map.Entry mapElement : getMovies().entrySet()) {
            Integer key = (Integer) mapElement.getKey();
            Movie movie = ((Movie) mapElement.getValue());
            for (Movie watchlist_movie : watchlist)
                if(mapElement.getKey() == watchlist_movie.getId())
                    continue;
            float score = 0;
            float imdbRate = movie.getImdbRate();
            float rating = 0;
            if (movie.getRating() != null)
                rating = movie.getRating();

            int genreSimilarity = 0;
            for (Movie watchlist_movie : watchlist)
            {
                ArrayList<String> genres = watchlist_movie.getGenres();
                ArrayList<String> temp = (ArrayList<String>) genres.clone();
                temp.retainAll(movie.getGenres());
                genreSimilarity += temp.size();
            }
            score = imdbRate + rating + 3 * genreSimilarity;
            movie_scores.put(key,score);
        }
        Set<Map.Entry<Integer, Float>> sortedValues = sortMoviesByScore(movie_scores);
        ArrayList<Movie> result = new ArrayList<Movie>();
        int counter = 0;
        int flag = 0;
        for (Map.Entry mapElement : sortedValues){
            flag = 0;
            if(counter >= 3)
                break;
            Integer key = (Integer) mapElement.getKey();
            for (Movie movie : getWatchList(payload)) {
                if(movie.getId() == getOneMovie(key).getId()) {
                    flag = 1;
                }
            }
            if(flag == 0) {
                result.add(getOneMovie(key));
                counter += 1;
            }
        }
        return result;
    }

    public static String createToken(String userEmail) throws Exceptions.InvalidCommand {
        try {
            Algorithm algorithm = Algorithm.HMAC256("iemdb1401");
            String token = JWT.create()
                    .withIssuer("iemdb")
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                    .withClaim("userEmail",userEmail)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            throw new Exceptions.InvalidCommand();
        }
    }

    public static String authenticate(String token){
        try {
            if(token == null)
                return "false";
            Algorithm algorithm = Algorithm.HMAC256("iemdb1401"); //use more secure key
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("iemdb")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getClaim("userEmail").toString();
        } catch (Exception exception){
            return "false";
        }
    }

//
    public static String addUser(Map<String, Object> payload) throws Exceptions.InvalidCommand, SQLException {
        User user = new User();
        user.setName((String) payload.get("name"));
        user.setEmail((String) payload.get("email"));
        user.setPassword((String) payload.get("password"));
        user.setBirthDate((String) payload.get("birthDate"));
        user.setNickname((String) payload.get("username"));
        Repository.getInstance().createUser(user);
        String token = Tools.createToken(user.getEmail());
        return token;

    }

    public static boolean hasCurrentUser() {
        if (currentUser != null) {
            return true;
        }
        return false;
    }

    public static User getCurrentUser() {
        if (hasCurrentUser() == true) {
            return currentUser;
        }
        return null;
    }

    public static void setCurrentUser(String email) throws Exceptions.InvalidCommand, SQLException, Exceptions.UserNotFound {
        if (email != null && email != "") {
            if (getUsers().containsKey(email)) {
                currentUser = getOneUser(email);
            }
        }
    }

    public static void logout() {
        currentUser = null;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static Map<Integer, Actor> getActors() {
        return Repository.getInstance().getActors();
    }

    public static Actor getOneActor(Integer id) throws  Exceptions.ActorNotFound {
        Actor actor = Repository.getInstance().getOneActor(id);
        if(actor.getId() == null)
            throw new Exceptions.ActorNotFound();
        return actor;
    }

    public static Map<Integer, Movie> getMovies() throws Exceptions.InvalidCommand, SQLException {
        return Repository.getInstance().getMovies();
    }

    public static Movie getOneMovie(Integer id) throws Exceptions.InvalidCommand, SQLException, Exceptions.MovieNotFound {
        Movie movie = Repository.getInstance().getOneMovie(id);
        if(movie.getId() == null)
            throw new Exceptions.MovieNotFound();
        return movie;
    }

    public static Map<String, User> getUsers() throws Exceptions.InvalidCommand, SQLException {
        return Repository.getInstance().getUsers();
    }

    public static User getOneUser(String email) throws Exceptions.InvalidCommand, SQLException, Exceptions.UserNotFound {
        User user = Repository.getInstance().getOneUser(email);
        if(user.getEmail() == null)
            throw new Exceptions.UserNotFound();
        return user;
    }


    public static Map<Integer, Comment> getComments() {
        return Repository.getInstance().getComments();
    }

    public static Comment getOneComment(String id) throws Exceptions.InvalidCommand, Exceptions.CommentNotFound {
        Comment comment = Repository.getInstance().getOneComment(id);
        if(comment.getId() == null)
            throw new Exceptions.CommentNotFound();
        return comment;
    }


}