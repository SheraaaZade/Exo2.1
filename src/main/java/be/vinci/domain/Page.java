package be.vinci.domain;

import java.util.Arrays;
import java.util.Objects;

public class Page {
    private int id;
    private String titre;
    private String URI;
    private String contenu;
    private String auteur;
    private String statut;
    private final String[] statutPublication = {"hidden", "published"};

    public Page(int id, String titre, String URI, String contenu, String auteur, String statut) {
        this.id = id;
        this.titre = titre;
        this.URI = URI;
        this.contenu = contenu;
        this.auteur = auteur;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getURI() {
        return URI;
    }

    public String getContenu() {
        return contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getStatut() {
        return statut;
    }

    public String[] getStatutPublication() {
        return statutPublication;
    }

    public void setStatut(String statut) {
        for (int i = 0; i < statutPublication.length; i++) {
            if (statut.equals(statutPublication[i]))
                this.statut = statut;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id == page.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", titre='" + titre + '\'' +
                ", URI='" + URI + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur;
    }
}
