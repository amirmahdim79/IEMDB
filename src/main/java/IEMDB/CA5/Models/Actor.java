package IEMDB.CA5.Models;

import IEMDB.CA5.Exceptions;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Actor {
    private Integer id;
    private String name;
    private String birthDate;
    private String nationality;
    private String image;

    public void setImage(String image) throws Exceptions.InvalidCommand {
        this.image = image;
    }
    public String getImage() {
        return  this.image = image;
    }

    public void setId(Integer id) throws Exceptions.InvalidCommand {
//        if (id == null) {
//            throw new Exceptions.InvalidCommand();
//        }
        this.id = id;
    }
    public void setName(String name) throws Exceptions.InvalidCommand {
//        if (name == null) {
//            throw new Exceptions.InvalidCommand();
//        }
        this.name = name;
    }
    public void setBirthDate(String birthDate) throws Exceptions.InvalidCommand {
//        if (birthDate == null) {
//            this.birthDate = null;
//        }
        this.birthDate = birthDate;
    }
    public void setNationality(String nationality) throws Exceptions.InvalidCommand {
//        if (nationality == null) {
//            throw new Exceptions.InvalidCommand();
//        }
        this.nationality = nationality;
    }

    public Integer calcAge() {
        if(this.birthDate == null) {
            return 0;
        }
        if(this.birthDate.equals("")) {
            return 0;
        }
        LocalDate birth = LocalDate.parse(this.birthDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        LocalDate now = LocalDate.now();
        Integer age = Period.between(birth,now).getYears();
        return age;
    }

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getBirthDate() {
        return this.birthDate;
    }
    public String getNationality() {
        return this.nationality;
    }

    @Override
    public String toString() {
        return "{" +
                "\"actorId\": " + id +
                ", \"name\": \"" + name + "\"" +
                '}';
    }
}
