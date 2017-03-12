package ru.dante.scpfoundation.monetization.model;

import java.util.List;

/**
 * Created by mohax on 24.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class OurApplicationsResponse {

    public List<OurApplication> items;

    @Override
    public String toString() {
        return "OurApplicationsResponse{" +
                "items=" + items +
                '}';
    }
}