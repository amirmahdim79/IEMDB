package IEMDB.CA5;
import IEMDB.CA5.Models.*;


import IEMDB.CA5.dbHandler.Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//@CrossOrigin(origins = "*")
@RestController
@SpringBootApplication
public class Ca5Application {
	public static void main(String[] args) throws Exception {
		Tools.main();
		SpringApplication.run(Ca5Application.class, args);
	}

	@GetMapping("/api")
	String home() {
		return "Welcome to API Service !!";
	}

	@PostMapping("/api/login")
	String login(@RequestBody Map<String, Object> payload) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, SQLException {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		User user = Tools.getOneUser((String) payload.get("email"));
		boolean passwordIsValid = bCryptPasswordEncoder.matches((String) payload.get("password"), user.getPassword());
		String token = Tools.createToken(user.getEmail());
		if(passwordIsValid)
			return token;
		return "Wrong Credentials !!";
	}
	@PostMapping("/api/signUp")
	String signUp(@RequestBody Map<String, Object> payload) throws Exceptions.InvalidCommand, SQLException {
		String token = Tools.addUser(payload);
		return token;
	}

	@PostMapping("/api/callback")
	public String callback(@RequestParam("code") String code) throws IOException, InterruptedException, Exceptions.InvalidCommand, JSONException, SQLException {
		String client_id = "59f2d7785b298f48379a";
		String client_secret = "1252f63cae759adf17f5b3c42c15e81858963740";
		String accessTokenURL = String.format("https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",client_id,client_secret,code);
		HttpClient client = HttpClient.newHttpClient();
		URI accessTokenURI = URI.create(accessTokenURL);
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(accessTokenURI);
		HttpRequest req = builder.POST(HttpRequest.BodyPublishers.noBody()).header("Accept","application/json").build();
		HttpResponse<String> res = client.send(req,HttpResponse.BodyHandlers.ofString());
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,Object> resultBody = mapper.readValue(res.body(),HashMap.class);
		String accessToken = (String) resultBody.get("access_token");


		URI userDataURI = URI.create("https://api.github.com/user");
		HttpRequest.Builder userDatabuilder = HttpRequest.newBuilder().uri(userDataURI);
		HttpRequest request = userDatabuilder.GET().header("Authorization",String.format("token %s",accessToken)).build();
		HttpResponse<String> userRes = client.send(request,HttpResponse.BodyHandlers.ofString());
		System.out.println(userRes.body());
		JSONObject body = new JSONObject(userRes.body());
		User user = new User();
		user.setEmail(body.getString("email"));
		user.setPassword(null);
		user.setNickname(body.getString("login"));
		user.setName(body.getString("name"));
		String date = body.getString("created_at");
		LocalDate age = LocalDate.parse(date, DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())).minusYears(18);
		user.setBirthDate(age.toString());
		Repository.getInstance().createOrUpdateUser(user);
		String token = Tools.createToken(user.getEmail());
		return token;
	}

	// Users APIs --------------------------------------------------------
	@GetMapping("/api/users")
	Map<String, User> getUsers() throws Exceptions.InvalidCommand, SQLException {
		Map<String, User> users = Tools.getUsers();
		return users;
	}

	@GetMapping("/api/users/{email}")
	User getOneUser(@PathVariable String email) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, SQLException {
		User user = Tools.getOneUser(email);
		return user;
	}

	@GetMapping("/api/users/watchlist")
	ArrayList<Movie> getWatchList(@RequestAttribute String userEmail) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, JsonProcessingException, SQLException {
		String payload = "{\"userEmail\" : " + userEmail  + "}";
		ArrayList<Movie> watchList = Tools.getWatchList(payload);
		return watchList;
	}

	@PostMapping("/api/users/watchlist")
	void addMovieToWatchList(HttpEntity<String> httpEntity,@RequestAttribute String userEmail) throws Exceptions.UserNotFound, JsonProcessingException, Exceptions.MovieNotFound, Exceptions.AgeLimitError, Exceptions.MovieAlreadyExists, Exceptions.InvalidCommand, SQLException {
		Tools.addToWatchList(httpEntity.getBody().toString(),userEmail.substring(1, userEmail.length() - 1));
	}

	@PostMapping("/api/users/watchlist/delete")
	void deleteMovieFromWatchList(HttpEntity<String> httpEntity,@RequestAttribute String userEmail) throws Exceptions.UserNotFound, JsonProcessingException, Exceptions.MovieNotFound, Exceptions.AgeLimitError, Exceptions.MovieAlreadyExists, Exceptions.InvalidCommand, SQLException {
		System.out.println(userEmail);
		Tools.removeMovieFromWatchList(httpEntity.getBody().toString(),userEmail.substring(1, userEmail.length() - 1));
	}

	@GetMapping("/api/users/recommended")
	ArrayList<Movie> getRecommendedMovies(@RequestAttribute String userEmail) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, JsonProcessingException, SQLException, Exceptions.MovieNotFound {
		ArrayList<Movie> watchList = Tools.getRecommendedMovies(userEmail);
		return watchList;
	}

	// Movies APIs --------------------------------------------------------
	@GetMapping("/api/movies")
	Map<Integer, Movie> getMovies() throws Exceptions.InvalidCommand, SQLException {
		Map<Integer, Movie> movies = Tools.getMovies();
		return movies;
	}

	@GetMapping("/api/movies/sort/{type}")
	Map<Integer, Movie> getMoviesSorted(@PathVariable String type) throws Exceptions.InvalidCommand, SQLException {
		Map<Integer, Movie> movies = null;
		if(type.equals("imdb"))
			movies = Tools.sortMoviesByImdbRating();
		else if(type.equals("date"))
			movies = Tools.sortMoviesByDate();
		return movies;
	}

	@GetMapping("/api/movies/{id}")
	Movie getOneMovie(@PathVariable Integer id) throws Exceptions.InvalidCommand, Exceptions.MovieNotFound, JsonProcessingException, SQLException {
		Movie movie = Tools.getOneMovie(id);
		return movie;
	}

	@PostMapping("/api/movies/rate")
	void rateMovie(HttpEntity<String> httpEntity,@RequestAttribute String userEmail) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, Exceptions.InvalidRateScore, Exceptions.MovieNotFound, JsonProcessingException, SQLException {
		Tools.rateMovie(httpEntity.getBody().toString(),userEmail.substring(1, userEmail.length() - 1));
	}

	@GetMapping("/api/movies/search/genre/{genre}")
	Map<Integer,Movie> searchMoviesByGenre(@PathVariable String genre) throws Exceptions.InvalidCommand, JsonProcessingException {
		Map<Integer, Movie> movies = Tools.getMoviesByGenre("{\"genre\": \"" + genre + "\" }");
		System.out.println(movies);
		return movies;
	}

	@GetMapping("/api/movies/search/date/{start_year}/{end_year}")
	Map<Integer,Movie> searchMoviesByDate(@PathVariable String start_year, @PathVariable String end_year) throws Exceptions.InvalidCommand, JsonProcessingException {
		Map<Integer, Movie> movies = Tools.getMoviesByDate("{\"start_year\": "  + start_year  +  ", \"end_year\": " + end_year  + "}");
		return movies;
	}

	@GetMapping("/api/movies/search/name/{name}")
	Map<Integer,Movie> searchMoviesByName(@PathVariable String name) throws Exceptions.InvalidCommand, JsonProcessingException {
		Map<Integer, Movie> movies = Tools.searchName(name);
		return movies;
	}

	// Actors APIs --------------------------------------------------------
	@GetMapping("/api/actors")
	Map<Integer, Actor> getActors(){
		Map<Integer, Actor> actors = Tools.getActors();
		return actors;
	}

	@GetMapping("/api/actors/{id}")
	Actor getOneActor(@PathVariable String id) throws Exceptions.ActorNotFound {
		Actor actor = Tools.getOneActor(Integer.valueOf(id));
		return actor;
	}

	@GetMapping("/api/actors/starred/{id}")
	Map<Integer, Movie> getStarred(@PathVariable String id) throws Exceptions.InvalidCommand, Exceptions.ActorNotFound {
		Map<Integer, Movie> starred = Tools.getStarred(id);
		return starred;
	}

	@GetMapping("/api/actors/age/{id}")
	Integer getActorAge(@PathVariable String id) throws Exceptions.ActorNotFound {
		Actor actor = Tools.getOneActor(Integer.valueOf(id));
		if(actor.calcAge() != null)
			return actor.calcAge();
		return 0;
	}

	// Comments APIs --------------------------------------------------------
	@GetMapping("/api/comments")
	Map<Integer, Comment> getComments(){
		Map<Integer, Comment> comments = Tools.getComments();
		return comments;
	}

	@GetMapping("/api/comments/{id}")
	Comment getOneComment(@PathVariable String id) throws Exceptions.InvalidCommand, Exceptions.CommentNotFound {
		Comment comment = Tools.getOneComment(id);
		return comment;
	}

	@PostMapping("/api/comments")
	void addComment(HttpEntity<String> httpEntity,@RequestAttribute String userEmail) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, JsonProcessingException, Exceptions.MovieNotFound, SQLException {
		Tools.addComment(httpEntity.getBody().toString(),userEmail.substring(1, userEmail.length() - 1));
	}

	@PostMapping("/api/comments/vote")
	void voteComment(HttpEntity<String> httpEntity,@RequestAttribute String userEmail) throws Exceptions.UserNotFound, Exceptions.InvalidCommand, JsonProcessingException, Exceptions.InvalidVoteValue, Exceptions.CommentNotFound {
		Tools.voteComment(httpEntity.getBody().toString(),userEmail.substring(1, userEmail.length() - 1));
	}

}
