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

package ru.ifproject.android.afr.data.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.ifproject.android.afr.R;
import ru.ifproject.android.afr.data.WatchFaceItem;

public class WatchFaceListAdapter extends BaseAdapter
{
    private List<WatchFaceItem> faceList;
    private LayoutInflater inflater;

    public WatchFaceListAdapter(
            Context context,
            List<WatchFaceItem> faceList )
    {
        this.faceList = faceList;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public int getCount()
    {
        return faceList.size();
    }

    @Override
    public Object getItem( int i )
    {
        return faceList.get( i );
    }

    @Override
    public long getItemId( int i )
    {
        return i;
    }

    @Override
    public View getView( int i, View view, ViewGroup viewGroup )
    {
        if ( null == view )
            view = inflater.inflate( R.layout.face_list_item, viewGroup, false );

        WatchFaceItem face = (WatchFaceItem) getItem( i );
        ImageView faceImage = view.findViewById( R.id.face_image );
        TextView faceName = view.findViewById( R.id.face_name );

        Bitmap image = Bitmap.createScaledBitmap( face.getImage(), 176, 176, false );
        faceImage.setImageBitmap( image );
        faceName.setText( face.getName() );

        return view;
    }
}
