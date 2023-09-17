package IEMDB.CA5.dbHandler;


import IEMDB.CA5.Exceptions;
import IEMDB.CA5.ExternalDataHandler.requestAPI;
import IEMDB.CA5.Models.*;
import IEMDB.CA5.Tools;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Repository {
    private static Repository instance;
    private static ObjectMapper mapper = new ObjectMapper();
    static Integer lastCommentId = 1;
    ComboPooledDataSource dataSource;

    private Repository() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/iemdb?useUnicode=true&characterEncoding=UTF-8");
        dataSource.setUser("root");
        dataSource.setPassword("amir2011");

        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(25);
        dataSource.setMaxStatements(150);
    }

    public static Repository getInstance() {
        if (instance == null)
            instance = new Repository();
        return instance;
    }

    public static void main() throws Exception {
        Repository rep = getInstance();
        rep.createTables();
        rep.InitData();
    }

    private void createMoviesTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `movies` (\n" +
                    "  `id` int(11) NOT NULL,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  `ageLimit` int(11) DEFAULT NULL,\n" +
                    "  `summary` longtext DEFAULT NULL,\n" +
                    "  `releaseDate` date DEFAULT NULL,\n" +
                    "  `director` varchar(255) DEFAULT NULL,\n" +
                    "  `imdbRate` float DEFAULT NULL,\n" +
                    "  `duration` varchar(255) DEFAULT NULL,\n" +
                    "  `rating` float DEFAULT NULL,\n" +
                    "  `image` varchar(500) DEFAULT NULL,\n" +
                    "  `coverImage` varchar(500) DEFAULT NULL,\n" +
                    "  `writers` longtext DEFAULT NULL,\n" +
                    "  `genres` longtext DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Movies Table");
        }
    }

    private void createActorsTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `actors` (\n" +
                    "  `id` int(11) NOT NULL,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  `birthDate` varchar(100) DEFAULT NULL,\n" +
                    "  `nationality` varchar(100) DEFAULT NULL,\n" +
                    "  `image` varchar(500) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Actors Table");
        }
    }

    private void createUsersTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `users` (\n" +
                    "  `email` varchar(255) NOT NULL,\n" +
                    "  `password` varchar(255) DEFAULT NULL,\n" +
                    "  `nickname` varchar(255) DEFAULT NULL,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  `birthDate` varchar(100) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`email`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Users Table");
        }
    }

    private void createGenresTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `genres` (\n" +
                    "  `genre` varchar(255) NOT NULL,\n" +
                    "  PRIMARY KEY (`genre`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Genres Table");
        }
    }

    private void createActorMoviesTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `actormovies` (\n" +
                    "  `actorId` int(11) NOT NULL,\n" +
                    "  `movieId` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`actorId`,`movieId`),\n" +
                    "  KEY `movieidd` (`movieId`),\n" +
                    "  CONSTRAINT `actor` FOREIGN KEY (`actorId`) REFERENCES `actors` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `movieidd` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building ActorMovies Table");
        }
    }

    private void createCommentsTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `comments` (\n" +
                    "  `id` int(11) NOT NULL,\n" +
                    "  `userEmail` varchar(255) DEFAULT NULL,\n" +
                    "  `movieId` int(11) DEFAULT NULL,\n" +
                    "  `text` varchar(255) DEFAULT NULL,\n" +
                    "  `createdTime` varchar(100) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `email` (`userEmail`),\n" +
                    "  KEY `movieId` (`movieId`),\n" +
                    "  CONSTRAINT `email` FOREIGN KEY (`userEmail`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `movieId` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Comments Table");
        }
    }

    private void createMovieGenresTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `moviegenres` (\n" +
                    "  `movieId` int(11) NOT NULL,\n" +
                    "  `genre` varchar(255) NOT NULL,\n" +
                    "  PRIMARY KEY (`movieId`,`genre`),\n" +
                    "  KEY `genre` (`genre`),\n" +
                    "  CONSTRAINT `genre` FOREIGN KEY (`genre`) REFERENCES `genres` (`genre`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `movieIdentity` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building movieGenres Table");
        }
    }

    private void createRatesTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `rates` (\n" +
                    "  `userEmail` varchar(255) NOT NULL,\n" +
                    "  `movieId` int(11) NOT NULL,\n" +
                    "  `score` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`userEmail`,`movieId`),\n" +
                    "  KEY `movie` (`movieId`),\n" +
                    "  CONSTRAINT `movie` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `userEmail` FOREIGN KEY (`userEmail`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Rates Table");
        }
    }

    private void createVotesTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `votes` (\n" +
                    "  `userEmail` varchar(255) NOT NULL,\n" +
                    "  `commentId` int(11) NOT NULL,\n" +
                    "  `vote` int(11) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`userEmail`,`commentId`),\n" +
                    "  KEY `commentid` (`commentId`),\n" +
                    "  CONSTRAINT `commentid` FOREIGN KEY (`commentId`) REFERENCES `comments` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `emailUser` FOREIGN KEY (`userEmail`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Votes Table");
        }
    }

    private void createWatchListTable() {
        try {
            Connection connection = this.dataSource.getConnection();
            String query = "CREATE TABLE `watchlist` (\n" +
                    "  `userEmail` varchar(255) NOT NULL,\n" +
                    "  `movieId` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`userEmail`,`movieId`),\n" +
                    "  KEY `IDmovie` (`movieId`),\n" +
                    "  CONSTRAINT `IDmovie` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                    "  CONSTRAINT `user` FOREIGN KEY (`userEmail`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println("Error in Building Watchlist Table");
        }
    }

    private static void closeConnections(Connection con, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) { /* Ignored */}
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) { /* Ignored */}
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) { /* Ignored */}
        }
    }

    public boolean checkTableExists(String tableName) throws SQLException {
        Connection connection = this.dataSource.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
        ArrayList<String> tables = new ArrayList<String>();
        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_NAME");
            tables.add(name);
        }
        boolean result = false;
        for (String name : tables) {
            if (name.equals(tableName))
                result = true;
        }
        return result;
    }

    public void createTables() throws SQLException {
        if (!checkTableExists("movies"))
            createMoviesTable();
        if (!checkTableExists("users"))
            createUsersTable();
        if (!checkTableExists("actors"))
            createActorsTable();
        if (!checkTableExists("genres"))
            createGenresTable();
        if (!checkTableExists("moviegenres"))
            createMovieGenresTable();
        if (!checkTableExists("actormovies"))
            createActorMoviesTable();
        if (!checkTableExists("comments"))
            createCommentsTable();
        if (!checkTableExists("rates"))
            createRatesTable();
        if (!checkTableExists("votes"))
            createVotesTable();
        if (!checkTableExists("watchlist"))
            createWatchListTable();
    }

    public void InitData() throws Exception {
        String moviesData = requestAPI.main("http://138.197.181.131:5000/api/v2/movies");
        String actorsData = requestAPI.main("http://138.197.181.131:5000/api/v2/actors");
        String usersData = requestAPI.main("http://138.197.181.131:5000/api/users");
        String commentsData = requestAPI.main("http://138.197.181.131:5000/api/comments");
        Movie[] movieObjects = mapper.readValue(moviesData, Movie[].class);
        Actor[] actorObjects = mapper.readValue(actorsData, Actor[].class);
        User[] userObjects = mapper.readValue(usersData, User[].class);
        Comment[] commentObjects = mapper.readValue(commentsData, Comment[].class);
        Connection connection = this.dataSource.getConnection();
        // Insert Movies & Genres
        PreparedStatement stmt = this.createMovieStmt(connection);
        PreparedStatement genreStmt = this.createGenreStmt(connection);

        for (Movie movie : movieObjects) {
            for (String genre : movie.getGenres()) {
                genreStmt = this.setGenreValues(genreStmt, genre);
                genreStmt.addBatch();
            }
            genreStmt.executeBatch();
            stmt = this.setMovieValues(stmt, movie);
            stmt.addBatch();

        }

        // Insert Movies Relation with Genres
        PreparedStatement movieGenresStmt = this.createMovieGenresStmt(connection);
        for (Movie movie : movieObjects) {
            for (String genre : movie.getGenres()) {
                movieGenresStmt = this.setMovieGenresValues(movieGenresStmt, movie.getId(), genre);
                movieGenresStmt.addBatch();
            }
            movieGenresStmt.executeBatch();
        }

        // Insert Actors
        PreparedStatement actorStmt = this.createActorStmt(connection);
        for (Actor actor : actorObjects) {
            actorStmt = this.setActorValues(actorStmt, actor);
            actorStmt.addBatch();
        }
        // Insert Actors Relation with Movies
        PreparedStatement movieActorsStmt = this.createMovieActorsStmt(connection);
        for (Movie movie : movieObjects) {
            if (movie.getCast() != null) {
                for (Integer id : movie.getCast()) {
                    movieActorsStmt = this.setMovieActorsValues(movieActorsStmt, id, movie.getId());
                    movieActorsStmt.addBatch();
                }
                movieActorsStmt.executeBatch();
            }

            stmt = this.setMovieValues(stmt, movie);
            stmt.addBatch();

        }
        // Insert Users & Watchlist

        PreparedStatement userStmt = this.createUserStmt(connection);
        PreparedStatement watchlistStmt = this.createWatchlistStmt(connection);
        for (User user : userObjects) {
            userStmt = this.setUserValues(userStmt, user);
            for (Movie movie : user.getWatchList()) {
                watchlistStmt = this.setWatchListValues(watchlistStmt, movie, user);
                watchlistStmt.addBatch();
            }
            int[] watchListUpdateCounts = watchlistStmt.executeBatch();
            userStmt.addBatch();

        }

        // Insert Comments & Votes
        PreparedStatement commentStmt = this.createCommentStmt(connection);
        PreparedStatement votesStmt = this.createVoteStmt(connection);
        for (int i = 1; i <= commentObjects.length; i++) {
            commentObjects[i - 1].setId(lastCommentId);
            lastCommentId += 1;
            commentStmt = this.setCommentValues(commentStmt, commentObjects[i - 1]);
            for (Vote vote : commentObjects[i - 1].getVotes()) {
                votesStmt = this.setVoteValues(votesStmt, vote);
                votesStmt.addBatch();
            }
            int[] voteUpdateCounts = votesStmt.executeBatch();
            commentStmt.addBatch();

        }

        int[] movieUpdateCounts = stmt.executeBatch();
        int[] actorUpdateCounts = actorStmt.executeBatch();
        int[] userUpdateCounts = userStmt.executeBatch();
        int[] commentUpdateCounts = commentStmt.executeBatch();

        genreStmt.close();
        movieGenresStmt.close();
        movieActorsStmt.close();
        votesStmt.close();
        watchlistStmt.close();
        stmt.close();
        actorStmt.close();
        userStmt.close();
        commentStmt.close();
        connection.close();

    }

    private PreparedStatement createMovieStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO movies (id,name,summary,releaseDate,director,writers,imdbRate,duration,ageLimit,rating,image,coverImage)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?);"
        );
        return stmt;
    }

    private PreparedStatement setMovieValues(PreparedStatement stmt, Movie movie) throws SQLException {
        stmt.setInt(1, movie.getId());
        stmt.setString(2, movie.getName());
        stmt.setString(3, movie.getSummary());
        stmt.setString(4, movie.getReleaseDate());
        stmt.setString(5, movie.getDirector());
        stmt.setString(6, movie.getWritersString());
        stmt.setFloat(7, movie.getImdbRate());
        stmt.setString(8, movie.getDuration());
        stmt.setInt(9, movie.getAgeLimit());
        stmt.setObject(10, movie.getRatings().size() == 0 ? null : movie.getRating(), java.sql.Types.FLOAT);
        stmt.setString(11, movie.getImage());
        stmt.setString(12, movie.getCoverImage());
        return stmt;
    }

    private PreparedStatement createActorStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO actors (id,name,birthDate,nationality,image)" +
                        " VALUES (?,?,?,?,?);"
        );
        return stmt;
    }

    private PreparedStatement setActorValues(PreparedStatement stmt, Actor actor) throws SQLException {
        stmt.setInt(1, actor.getId());
        stmt.setString(2, actor.getName());
        stmt.setString(3, actor.getBirthDate());
        stmt.setString(4, actor.getNationality());
        stmt.setString(5, actor.getImage());
        return stmt;
    }

    private PreparedStatement createUserStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO users (email,password,nickname,name,birthDate)" +
                        " VALUES (?,?,?,?,?);"
        );
        return stmt;
    }

    private PreparedStatement setUserValues(PreparedStatement stmt, User user) throws SQLException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        stmt.setString(1, user.getEmail());
        if (user.getPassword() == null) {
            stmt.setString(2, null);
        } else {
            stmt.setString(2, bCryptPasswordEncoder.encode(user.getPassword()));
        }
        stmt.setString(3, user.getNickname());
        stmt.setString(4, user.getName());
        stmt.setString(5, user.getBirthDate());
        return stmt;
    }

    private PreparedStatement createCommentStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO comments (id,userEmail,movieId,text,createdTime)" +
                        " VALUES (?,?,?,?,?);"
        );
        return stmt;
    }

    private PreparedStatement setCommentValues(PreparedStatement stmt, Comment comment) throws SQLException {
        stmt.setInt(1, comment.getId());
        stmt.setString(2, comment.getUserEmail());
        stmt.setInt(3, comment.getMovieId());
        stmt.setString(4, comment.getText());
        stmt.setString(5, comment.getCreatedTime());
        return stmt;
    }

    private PreparedStatement createWatchlistStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO watchlist (userEmail,movieId)" +
                        " VALUES (?,?);"
        );
        return stmt;
    }

    private PreparedStatement setWatchListValues(PreparedStatement stmt, Movie movie, User user) throws SQLException {
        stmt.setString(1, user.getEmail());
        stmt.setInt(2, movie.getId());
        return stmt;
    }

    private PreparedStatement createVoteStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO votes (userEmail,commentId,vote)" +
                        " VALUES (?,?,?);"
        );
        return stmt;
    }

    private PreparedStatement setVoteValues(PreparedStatement stmt, Vote vote) throws SQLException {
        stmt.setString(1, vote.getUserEmail());
        stmt.setInt(2, vote.getCommentId());
        stmt.setInt(3, vote.getVote());
        return stmt;
    }

    private PreparedStatement createMovieActorsStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO actorMovies (actorId,movieId)" +
                        " VALUES (?,?);"
        );
        return stmt;
    }

    private PreparedStatement setMovieActorsValues(PreparedStatement stmt, Integer actorId, Integer movieId) throws SQLException {
        stmt.setInt(1, actorId);
        stmt.setInt(2, movieId);
        return stmt;
    }

    private PreparedStatement createGenreStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO genres (genre)" +
                        " VALUES (?);"
        );
        return stmt;
    }

    private PreparedStatement setGenreValues(PreparedStatement stmt, String genre) throws SQLException {
        stmt.setString(1, genre);
        return stmt;
    }

    private PreparedStatement createMovieGenresStmt(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO movieGenres (movieId,genre)" +
                        " VALUES (?,?);"
        );
        return stmt;
    }

    private PreparedStatement setMovieGenresValues(PreparedStatement stmt, Integer movieId, String genre) throws SQLException {
        stmt.setInt(1, movieId);
        stmt.setString(2, genre);
        return stmt;
    }

    private Movie getMovieValues(Movie movie, ResultSet res, Connection connection) throws SQLException, Exceptions.InvalidCommand {
        movie.setId(res.getInt("id"));
        movie.setName(res.getString("name"));
        movie.setSummary(res.getString("summary"));
        movie.setReleaseDate(res.getString("releaseDate"));
        movie.setDirector(res.getString("director"));
        movie.setImdbRate(res.getFloat("imdbRate"));
        movie.setDuration(res.getString("duration"));
        movie.setAgeLimit(res.getInt("ageLimit"));
        movie.setRating(res.getFloat("rating"));
        movie.setImage(res.getString("image"));
        movie.setCoverImage(res.getString("coverImage"));
        movie.setWriters(stringToArray(res.getString("writers")));
        movie.setComments(getMovieComments(movie.getId(), connection));
        movie.setRatings(getMovieRates(movie.getId(), connection));
        movie.setGenres(getMovieGenres(movie.getId(), connection));
        movie.setCast(getMovieCast(movie.getId(), connection));
        return movie;
    }

    private User getUserValues(User user, ResultSet res) throws SQLException, Exceptions.InvalidCommand {
        user.setEmail(res.getString("email"));
        user.setPassword(res.getString("password"));
        user.setNickname(res.getString("nickname"));
        user.setName(res.getString("name"));
        user.setBirthDate(res.getString("birthDate"));
        user.setWatchList(getWatchlist(user));
        return user;
    }

    private Actor getActorValues(Actor actor, ResultSet res) throws SQLException, Exceptions.InvalidCommand {
        actor.setId(res.getInt("id"));
        actor.setName(res.getString("name"));
        actor.setBirthDate(res.getString("birthDate"));
        actor.setNationality(res.getString("nationality"));
        actor.setImage(res.getString("image"));
        return actor;
    }


    private Vote getVoteValues(Vote vote, ResultSet res) throws SQLException, Exceptions.InvalidCommand, Exceptions.InvalidVoteValue {
        vote.setCommentId(res.getInt("commentId"));
        vote.setUserEmail(res.getString("userEmail"));
        vote.setVote(res.getInt("vote"));
        return vote;
    }

    public ArrayList<Vote> getCommentsVotes(String commentId, Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM votes WHERE commentId = ?;"
            );
            stmt.setString(1, commentId);
            ResultSet res = stmt.executeQuery();
            ArrayList<Vote> votes = new ArrayList<Vote>();
            while (res.next()) {
                Vote vote = new Vote();
                vote = getVoteValues(vote, res);
                votes.add(vote);
            }
            return votes;
        } catch (Exception e) {
            System.out.println("Problem in Getting Votes");
            return null;
        }
    }

    private Comment getCommentValues(Comment comment, ResultSet res, Connection connection) throws SQLException, Exceptions.InvalidCommand {
        comment.setId(res.getInt("id"));
        comment.setUserEmail(res.getString("userEmail"));
        comment.setMovieId(res.getInt("movieId"));
        comment.setText(res.getString("text"));
        comment.setCreatedTime(res.getString("createdTime"));
        comment.setVotes(getCommentsVotes(Integer.toString(comment.getId()), connection));
        return comment;
    }

    public Map<Integer, Comment> getComments() {
        try {
            Connection connection = this.dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM comments;";
            ResultSet res = stmt.executeQuery(query);
            Map<Integer, Comment> comments = new HashMap<Integer, Comment>();
            while (res.next()) {
                Comment comment = new Comment();
                comment = getCommentValues(comment, res, connection);
                comments.put(comment.getId(), comment);
            }
            closeConnections(connection, stmt, res);
            return comments;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Problem in Getting Comments");
            return null;
        }
    }

    public Comment getOneComment(String id) throws Exceptions.CommentNotFound {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM comments WHERE id = ?;"
            );
            stmt.setString(1, id);
            ResultSet res = stmt.executeQuery();
            Comment comment = new Comment();
            while (res.next()) {
                comment = getCommentValues(comment, res, connection);
            }
            closeConnections(connection, stmt, res);
            return comment;
        } catch (Exception e) {
            throw new Exceptions.CommentNotFound();
        }
    }

    public Map<Integer, Actor> getActors() {
        try {
            Connection connection = this.dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM actors;";
            ResultSet res = stmt.executeQuery(query);
            Map<Integer, Actor> actors = new HashMap<Integer, Actor>();
            while (res.next()) {
                Actor actor = new Actor();
                actor = getActorValues(actor, res);
                actors.put(actor.getId(), actor);
            }
            closeConnections(connection, stmt, res);
            return actors;
        } catch (Exception e) {
            System.out.println("Problem in Getting Actors");
            return null;
        }
    }

    public Map<Integer, Movie> getMovies() throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM movies;";
        ResultSet res = stmt.executeQuery(query);
        Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
        while (res.next()) {
            Movie movie = new Movie();
            movie = getMovieValues(movie, res, connection);
            movies.put(movie.getId(), movie);
        }
        closeConnections(connection, stmt, res);
        return movies;
    }

    public Map<String, User> getUsers() throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM users;";
        ResultSet res = stmt.executeQuery(query);
        Map<String, User> users = new HashMap<String, User>();
        while (res.next()) {
            User user = new User();
            user = getUserValues(user, res);
            users.put(user.getEmail(), user);
        }
        closeConnections(connection, stmt, res);
        return users;
    }

    public Actor getOneActor(Integer id) throws Exceptions.ActorNotFound {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM actors WHERE id = ?;"
            );
            stmt.setInt(1, id);
            ResultSet res = stmt.executeQuery();
            Actor actor = new Actor();
            while (res.next()) {
                actor = getActorValues(actor, res);
            }
            closeConnections(connection, stmt, res);
            return actor;
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.ActorNotFound();
        }
    }

    public User getOneUser(String email) throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM users WHERE email=?;"
        );
        stmt.setString(1, email);
        ResultSet res = stmt.executeQuery();
        System.out.println(res);
        User user = new User();
        while (res.next()) {
            user = getUserValues(user, res);
        }
        closeConnections(connection, stmt, res);
        return user;
    }

    public ArrayList<Movie> getWatchlist(User user) throws SQLException, Exceptions.InvalidCommand {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        Connection connection = this.dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM watchlist WHERE userEmail =  ?;"
        );
        stmt.setString(1, user.getEmail());
        ResultSet res = stmt.executeQuery();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        while (res.next()) {
            ids.add(res.getInt("movieId"));
        }
        for (Integer id : ids) {
            movies.add(getOneMovie(id));
        }
        closeConnections(connection, stmt, res);
        return movies;
    }

    public Movie getOneMovie(Integer id) throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM movies WHERE id = ?;"
        );
        stmt.setInt(1, id);
        ResultSet res = stmt.executeQuery();
        Movie movie = new Movie();
        while (res.next()) {
            movie = getMovieValues(movie, res, connection);
        }
        closeConnections(connection, stmt, res);
        return movie;
    }

    public ArrayList<String> stringToArray(String s) {
        String[] elements = s.split(",");
        List<String> newElements = Arrays.asList(elements);
        ArrayList<String> res = new ArrayList<String>(newElements);
        return res;
    }

    public ArrayList<Comment> getMovieComments(Integer movieId, Connection connection) throws SQLException, Exceptions.InvalidCommand {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM comments WHERE movieId = ?;"
        );
        stmt.setInt(1, movieId);
        ResultSet res = stmt.executeQuery();
        ArrayList<Comment> comments = new ArrayList<Comment>();
        while (res.next()) {
            Comment comment = new Comment();
            comment = getCommentValues(comment, res, connection);
            comments.add(comment);
        }
        closeConnections(null, stmt, res);
        return comments;
    }

    public ArrayList<Rate> getMovieRates(Integer movieId, Connection connection) throws SQLException, Exceptions.InvalidCommand {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM rates WHERE movieId = ?;"
        );
        stmt.setInt(1, movieId);
        ResultSet res = stmt.executeQuery();
        ArrayList<Rate> rates = new ArrayList<Rate>();
        while (res.next()) {
            Rate rate = new Rate();
            rate.setMovieId(res.getInt("movieId"));
            rate.setScore(res.getInt("score"));
            rate.setUserEmail(res.getString("userEmail"));
            rates.add(rate);
        }
        closeConnections(null, stmt, res);
        return rates;
    }

    public ArrayList<String> getMovieGenres(Integer movieId, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM moviegenres WHERE movieId = ?;"
        );
        stmt.setInt(1, movieId);
        ResultSet res = stmt.executeQuery();
        ArrayList<String> genres = new ArrayList<String>();
        while (res.next())
            genres.add(res.getString("genre"));
        closeConnections(null, stmt, res);
        return genres;
    }

    public ArrayList<Integer> getMovieCast(Integer movieId, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM actorMovies WHERE movieId = ?;"
        );
        stmt.setInt(1, movieId);
        ResultSet res = stmt.executeQuery();
        ArrayList<Integer> cast = new ArrayList<Integer>();
        while (res.next())
            cast.add(res.getInt("actorId"));
        closeConnections(null, stmt, res);
        return cast;
    }

    public void appendToWatchList(String userEmail, Integer movieId) throws Exceptions.AgeLimitError, Exceptions.MovieAlreadyExists, SQLException, Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO watchlist (movieId,userEmail)" +
                            " VALUES (?,?);"
            );
            stmt.setInt(1, movieId);
            stmt.setString(2, userEmail);
            stmt.execute();
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public void removeFromWatchList(Movie movie, User user) throws Exceptions.MovieNotFound, SQLException {
        System.out.println("================================================");
        System.out.println(user.getEmail());
        Connection connection = this.dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM watchlist WHERE movieId = ? and userEmail = ?;"
        );
        stmt.setInt(1, movie.getId());
        stmt.setString(2, user.getEmail());
        int numRowsAffected = stmt.executeUpdate();
        System.out.println(numRowsAffected);
        closeConnections(connection, stmt, null);
        if (numRowsAffected == 0)
            throw new Exceptions.MovieNotFound();

    }

    public ArrayList<Movie> getMoviesSortedByImdb() throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM movies ORDER BY imdbRate DESC;";
        ResultSet res = stmt.executeQuery(query);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        while (res.next()) {
            Movie movie = new Movie();
            movie = getMovieValues(movie, res, connection);
            movies.add(movie);
        }
        closeConnections(connection, stmt, res);
        return movies;
    }

    public ArrayList<Movie> getMoviesSortedByDate() throws SQLException, Exceptions.InvalidCommand {
        Connection connection = this.dataSource.getConnection();
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM movies ORDER BY releaseDate;";
        ResultSet res = stmt.executeQuery(query);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        while (res.next()) {
            Movie movie = new Movie();
            movie = getMovieValues(movie, res, connection);
            movies.add(movie);
        }
        closeConnections(connection, stmt, res);
        return movies;
    }

    public void addMovieRating(Rate rate) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO rates (userEmail,movieId,score)" +
                            " VALUES (?,?,?)  ON DUPLICATE KEY UPDATE " +
                            " score= " + rate.getScore() + ";");
            stmt.setString(1, rate.getUserEmail());
            stmt.setInt(2, rate.getMovieId());
            stmt.setInt(3, rate.getScore());
            stmt.executeUpdate();
            Movie movie = getOneMovie(rate.getMovieId());
            movie.updateRating();
            setMovieRating(movie);
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println();
            throw new Exceptions.InvalidCommand();
        }
    }

    public Map<Integer, Movie> getMoviesByGenre(String Genre) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM movieGenres WHERE genre = ?;"
            );
            stmt.setString(1, Genre);
            ResultSet res = stmt.executeQuery();
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while (res.next()) {
                Integer id = 0;
                id = res.getInt("movieId");
                ids.add(id);
            }
            Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
            for (Integer id : ids) {
                Movie movie = getOneMovie(id);
                movies.put(movie.getId(), movie);
            }
            closeConnections(connection, stmt, res);
            return movies;
        } catch (SQLException e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public Map<Integer, Movie> getMoviesByDate(Integer first, Integer second) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM movies WHERE YEAR(releaseDate) <  ? AND YEAR(releaseDate) >= ?;"
            );
            stmt.setInt(1, second);
            stmt.setInt(2, first);
            ResultSet res = stmt.executeQuery();
            Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
            while (res.next()) {
                Movie movie = new Movie();
                movie = getMovieValues(movie, res, connection);
                movies.put(movie.getId(), movie);
            }
            closeConnections(connection, stmt, res);
            return movies;
        } catch (SQLException e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }


    public Map<Integer, Movie> searchName(String name) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM movies WHERE name LIKE ?;"
            );
            stmt.setString(1, "%" + name + "%");
            ResultSet res = stmt.executeQuery();
            Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
            while (res.next()) {
                Movie movie = new Movie();
                movie = getMovieValues(movie, res, connection);
                movies.put(movie.getId(), movie);
            }
            closeConnections(connection, stmt, res);
            return movies;
        } catch (SQLException e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public Map<Integer, Movie> getActorMovies(String actorId) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM actormovies WHERE actorId = ?;"
            );
            stmt.setString(1, actorId);
            ResultSet res = stmt.executeQuery();
            ArrayList<Integer> ids = new ArrayList<Integer>();
            while (res.next()) {
                Integer id = 0;
                id = res.getInt("movieId");
                ids.add(id);
            }
            Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
            for (Integer id : ids) {
                Movie movie = getOneMovie(id);
                movies.put(movie.getId(), movie);
            }
            closeConnections(connection, stmt, res);
            return movies;
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public void addComment(Comment comment) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO comments (id,movieId,userEmail,text,createdTime)" +
                            " VALUES (?,?,?,?,?);"
            );
            stmt.setInt(1, comment.getId());
            stmt.setInt(2, comment.getMovieId());
            stmt.setString(3, comment.getUserEmail());
            stmt.setString(4, comment.getText());
            stmt.setString(5, comment.getCreatedTime());
            int numRowsAffected = stmt.executeUpdate();
            closeConnections(connection, stmt, null);
            if (numRowsAffected == 0)
                throw new Exceptions.InvalidCommand();
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public Integer getLastCommentId() throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String query = "SELECT id FROM comments ORDER BY id DESC LIMIT 1;";
            ResultSet res = stmt.executeQuery(query);
            res.next();
            Integer result = res.getInt("id");
            closeConnections(connection, stmt, null);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public void addVote(Vote vote) throws Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO votes (commentId,userEmail,vote)" +
                            " VALUES (?,?,?)  ON DUPLICATE KEY UPDATE" +
                            " vote = " + vote.getVote() + ";"
            );
            stmt.setInt(1, vote.getCommentId());
            stmt.setInt(3, vote.getVote());
            stmt.setString(2, vote.getUserEmail());
            int numRowsAffected = stmt.executeUpdate();
            closeConnections(connection, stmt, null);
            if (numRowsAffected == 0)
                throw new Exceptions.InvalidCommand();
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public void setMovieRating(Movie movie) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE movies SET rating = ? WHERE id = ?;"
            );
            stmt.setFloat(1, movie.getRating());
            stmt.setInt(2, movie.getId());
            stmt.execute();
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void createUser(User user) throws SQLException, Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (email,password,nickname,name,birthDate)" +
                            " VALUES (?,?,?,?,?);"
            );
            stmt = setUserValues(stmt, user);
            stmt.execute();
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }

    public void createOrUpdateUser(User user) throws SQLException, Exceptions.InvalidCommand {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (email,password,nickname,name,birthDate)" +
                            " VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE \n" +
                            "  name = VALUES(name), \n" +
                            "  nickname = VALUES(nickname)," +
                            "birthDate = VALUES(birthDate);"
            );
            stmt = setUserValues(stmt, user);
            stmt.execute();
            closeConnections(connection, stmt, null);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exceptions.InvalidCommand();
        }
    }
}
