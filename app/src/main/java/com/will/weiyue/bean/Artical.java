package com.will.weiyue.bean;

public class Artical {

    public Artical(String title, String description, String link, String imageUrl, String pubDate, String category, String comments, String source, String sourceLink, String enclosure, String author, String articalKeyWords, String recordId)
    {
        Title = title;
        Description = description;

        Link = link;
        ImageUrl = imageUrl;
        PubDate = pubDate;
        Category= category;
        Comments = comments;
        Source = source;
        SourceLink = sourceLink;
        Enclosure = enclosure;
        Author = author;
        ArticalKeyWords = articalKeyWords;
        RecordId = recordId;
    }

    private String Title;
    private String Description;
    private String Author;
    private String Link;
    private String ImageUrl;
    private String PubDate;
    private String Category;
    private String Comments;
    private String Source;
    private String SourceLink;
    private String Enclosure;
    private String ArticalKeyWords;
    private String RecordId;

    public String getTitle(){
        return Title;
    }
    public String getDescription(){
        return Description;
    }
    public String getLink()
    {
        return Link;
    }
    public String getImageUrl(){ return ImageUrl; }
    public String getPubDate() { return PubDate; }
    public String getCategory() { return Category; }
    public String getComments() { return Comments; }
    public String getSource() { return Source; }
    public String getSourceLink() { return SourceLink; }
    public String getEnclosure() { return Enclosure; }
    public String getAuthor() { return Author; }
    public String getArticalKeyWords() { return ArticalKeyWords; }
    public String getRecordId() { return RecordId; }
}
