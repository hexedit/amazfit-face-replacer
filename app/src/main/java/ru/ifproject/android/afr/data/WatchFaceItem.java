/*
    Amazfit Face Replacer - a tool for replacing Amazfit Bip faces in Mi Fit
    Copyright (C) 2018, IFProject / HexEdit, DriffeX, NitroOxid

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
