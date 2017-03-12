package ru.dante.scpfoundation.monetization.model;

/**
 * Created by mohax on 24.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class OurApplication extends BaseModel {
    public String id;
    public String name;
    public String description;

    /**
     * use it to check lists for containing app with specifik package
     */
    public OurApplication(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OurApplication{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OurApplication that = (OurApplication) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}