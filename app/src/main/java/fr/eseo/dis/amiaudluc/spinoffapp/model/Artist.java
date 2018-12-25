package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.ArrayList;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class Artist implements Media{

    private int id;
    private String name;
    private String profilePath;
    private String creditId;
    private String department;
    private String job;
    private String character;
    private int gender;
    private ArrayList<Movie> movies = new ArrayList<>();
    private ArrayList<Serie> series =  new ArrayList<>();
    private String biography;
    private String placeOfBirth;
    private String type;

    public Artist() {
        this.id = 0;
        this.name = "";
        this. profilePath = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }


    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public ArrayList<Serie> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Serie> series) {
        this.series = series;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    @Override
    public String getMediaType() {
        return this.type;
    }

    @Override
    public void setMediaType(String mediaType) {
        this.type = mediaType;
    }
}
