package ru.ifproject.android.afr.data;

public class WatchFaceItem
{
    private String faceFile;
    private String faceName;
    private int faceImage;

    public WatchFaceItem( String faceFile, String faceName, int faceImage )
    {
        this.faceFile = faceFile;
        this.faceName = faceName;
        this.faceImage = faceImage;
    }

    public String getFile()
    {
        return faceFile;
    }

    public String getName()
    {
        return faceName;
    }

    public int getImage()
    {
        return faceImage;
    }
}
