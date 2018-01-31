package ru.ifproject.android.afr.data.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifproject.android.afr.R;
import ru.ifproject.android.afr.data.WatchFaceItem;

public class WatchFaceListAdapter extends BaseAdapter
{
    private List<WatchFaceItem> faceList = new ArrayList<>();
    private LayoutInflater inflater;

    public WatchFaceListAdapter(
            Context context,
            final String[] faceFiles,
            final String[] faceNames,
            final int[] faceImages )
    {
        for ( int fx = 0; fx < faceFiles.length; fx++ )
        {
            faceList.add( new WatchFaceItem( faceFiles[ fx ],
                                             faceNames[ fx ],
                                             faceImages[ fx ] ) );
            inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        }
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

        faceImage.setImageResource( face.getImage() );
        faceName.setText( face.getName() );

        return view;
    }
}
