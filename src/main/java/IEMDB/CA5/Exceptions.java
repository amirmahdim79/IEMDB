package IEMDB.CA5;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Exceptions {

    @ResponseStatus(value = HttpStatus.NOT_FOUND,reason="User not found!")
    public static class UserNotFound extends Exception {
        public UserNotFound() {
            super("");
        }

        @Override
        public String toString() {
            return "User not found!";
        }
    }
    @ResponseStatus(value = HttpStatus.NOT_FOUND,reason="Movie not found!")
    public static class MovieNotFound extends Exception {
        public MovieNotFound() {
            super("");
        }

        @Override
        public String toString() {
            return "Movie not found!";
        }
    }
    @ResponseStatus(value = HttpStatus.NOT_FOUND,reason="Actor not found!")
    public static class ActorNotFound extends Exception {
        public ActorNotFound() {
            super("");
        }
        @Override
        public String toString() {
            return "Actor not found!";
        }
    }
    @ResponseStatus(value = HttpStatus.NOT_FOUND,reason="Comment not found!")
    public static class CommentNotFound extends Exception {
        public CommentNotFound() {
            super("");
        }
        @Override
        public String toString() {
            return "Comment not found!";
        }
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST,reason="این فیلم قبلا به لیست تماشا اضافه شده")
    public static class MovieAlreadyExists extends Exception {
        public MovieAlreadyExists() {
            super("");
        }

        @Override
        public String toString() {
            return "این فیلم قبلا به لیست تماشا اضافه شده";
        }
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST,reason="سن شما کم تر از سن مجاز است")
    public static class AgeLimitError extends Exception {
        public AgeLimitError() {
            super("");
        }

        @Override
        public String toString() {
            return "Age limit error!";
        }
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST,reason="Something went wrong!")
    public static class InvalidCommand extends Exception {
        public InvalidCommand() {
            super("");
        }

        @Override
        public String toString() {
            return "Something went wrong!";
        }
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST,reason="Invalid rate score!")
    public static class InvalidRateScore extends Exception {
        public InvalidRateScore() {
            super("");
        }

        @Override
        public String toString() {
            return "Invalid rate score!";
        }
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST,reason="\"Invalid vote value!\"")
    public static class InvalidVoteValue extends Exception {
        public InvalidVoteValue() {
            super("");
        }

        @Override
        public String toString() {
            return "Invalid vote value!";
        }
    }
}
