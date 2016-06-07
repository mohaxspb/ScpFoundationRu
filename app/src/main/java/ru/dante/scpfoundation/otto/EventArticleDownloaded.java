package ru.dante.scpfoundation.otto;

/**
 * Created by Dante on 20.02.2016.
 */
public class EventArticleDownloaded
{
    private String link;

    public EventArticleDownloaded(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
        return link;
    }
}
