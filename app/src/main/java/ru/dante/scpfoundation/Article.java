package ru.dante.scpfoundation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Dante on 16.01.2016.
 */
public class Article implements Parcelable
{
    public static final String KEY_ARTICLE = "KEY_ARTICLE";
    private String title;
    private String URL;
    private Date creationDate = new Date(0);
    private Date updateDate = new Date(0);
    private String authorName;
    private String articlesText;
    private String imageUrl;
    private boolean isRead = false;
    private String preview;

    @Override
    public String toString()
    {
        return this.getTitle();
    }

    //    Parcel implementation/////////////////////////////
    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>()
    {

        @Override
        public Article createFromParcel(Parcel source)
        {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size)
        {
            return new Article[size];
        }
    };

    //    Parcel implementation/////////////////////////////
    private Article(Parcel in)
    {
        this.URL = in.readString();
        this.title = in.readString();

        this.creationDate = new Date(in.readLong());
        this.updateDate = new Date(in.readLong());

        this.imageUrl = in.readString();

        this.articlesText = in.readString();
        this.authorName = in.readString();
        this.isRead = (in.readInt() == 1);
        this.preview=in.readString();
    }

    /**
     * empty constructor
     */
    public Article()
    {

    }

    //    Parcel implementation/////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    //    Parcel implementation/////////////////////////////
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

        dest.writeString(URL);
        dest.writeString(title);

        dest.writeLong(creationDate.getTime());
        dest.writeLong(updateDate.getTime());

        dest.writeString(imageUrl);

        dest.writeString(articlesText);
        dest.writeString(authorName);
        dest.writeInt((isRead) ? 1 : 0);
        dest.writeString(preview);
    }


    public String getArticlesText()
    {
        return articlesText;
    }

    public void setArticlesText(String articlesText)
    {
        this.articlesText = articlesText;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getURL()
    {
        return URL;
    }

    public void setURL(String URL)
    {
        this.URL = URL;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public void setIsRead(boolean isRead)
    {
        this.isRead = isRead;
    }

    public String getPreview()
    {
        return preview;
    }

    public void setPreview(String preview)
    {
        this.preview = preview;
    }
   /* public static class PubDateComparator implements Comparator<Article>
    {
        @Override
        public int compare(Article o1, Article o2)
        {
            return o2.getPubDate().compareTo(o1.getPubDate());
        }
    }*/
}

