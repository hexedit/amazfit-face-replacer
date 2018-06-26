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

package ru.ifproject.android.afr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import ru.ifproject.android.afr.data.WatchFaceItem;
import ru.ifproject.android.afr.data.widget.WatchFaceListAdapter;

public class MainActivity extends Activity
{
    private static final int PICK_FILE_ACTIVITY = 1;
    private static final int ABOUT_OPTIONS_ITEM = 1;

    private static final String mifitFacePath =
            "/Android/data/com.xiaomi.hm.health/files/watch_skin/";
    private static final String[] faceFiles = {
            "39e3c0a9f99eecdf4872ac53f359093d.bin",
            "05963d61d975f5776825fa0285a4bf48.bin",
            "4742a3b64747a2f0b1f3f82adaa9a4a9.bin",
            "6a7850c336ddd8104dd5373adcd9250a.bin",
            "346e945ff8ec88f73ac03877e34d50dd.bin",
            "0a6226125223e580973ca446fbda4e8f.bin",
            "ebdf8d01ae673814b4b929829da36962.bin",
            "5edac8fd56205124d86f9e5c54857405.bin",
            "831488f976d2cdd3f967fdd052de860d.bin",
            "3a8cdd67067acdcf34fef1faa3563f2f.bin",
            "4a5a805390abe287d644b59da1683802.bin",
            "48c728bc39ffbe5ab304f57665289b28.bin",
            "974658b4ad337a81a8c9843e1aee3a52.bin",
            "f522ea87b0927f4468258d4cc326c0f9.bin",
            "7bff10fc654d8d08873e59898fc15fb9.bin"
    };
    private static final String[] faceNames = {
            "Shade",
            "Cube",
            "Reverse",
            "Number",
            "Simple",
            "Winter",
            "Card",
            "City_Peking",
            "Pointer",
            "Number",
            "Pointer 2",
            "Simple_12h",
            "Number_12h",
            "City_Sydney",
            "Game"
    };
    private static final int[] faceImages = {
            R.drawable.face_shade,
            R.drawable.face_cube,
            R.drawable.face_reverse,
            R.drawable.face_number,
            R.drawable.face_simple,
            R.drawable.face_winter,
            R.drawable.face_card,
            R.drawable.face_city_peking,
            R.drawable.face_pointer,
            R.drawable.face_number_2,
            R.drawable.face_pointer_2,
            R.drawable.face_simple_12h,
            R.drawable.face_number_12h,
            R.drawable.face_city_sydney,
            R.drawable.face_game
    };

    private Uri faceFile = null;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        setTitle( R.string.app_title );

        GridView faceList = findViewById( R.id.face_list );

        int perm = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE );
        if ( perm != PackageManager.PERMISSION_GRANTED )
        {
            final String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions( this, permissions, 0 );
        }

        Intent intent = getIntent();
        if ( Intent.ACTION_SEND.equals( intent.getAction() ) )
            faceFile = intent.getParcelableExtra( Intent.EXTRA_STREAM );
        else if ( Intent.ACTION_VIEW.equals( intent.getAction() ) )
            faceFile = intent.getData();
        else
        {
            Intent chooser = new Intent( Intent.ACTION_GET_CONTENT );
            chooser.setType( "application/octet-stream" );
            chooser.addCategory( Intent.CATEGORY_OPENABLE );
            try
            {
                startActivityForResult(
                        Intent.createChooser( chooser, getString( R.string.chooser_title ) ),
                        PICK_FILE_ACTIVITY );
            }
            catch ( android.content.ActivityNotFoundException e )
            {
                Toast.makeText( this, R.string.no_available_chooser, Toast.LENGTH_LONG ).show();
                finish();
            }
        }

        if ( ( null != faceFile ) && !checkFaceFile( faceFile ) )
        {
            Toast.makeText( this, R.string.invalid_face_file, Toast.LENGTH_LONG )
                 .show();
            finish();
            return;
        }

        faceList.setAdapter( new WatchFaceListAdapter( this, faceFiles, faceNames, faceImages ) );
        faceList.setOnItemClickListener( new FaceItemClickListener() );

        Toast.makeText( this, R.string.choose_replacing, Toast.LENGTH_LONG ).show();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        MenuItem about = menu.add( 0, ABOUT_OPTIONS_ITEM, 0, R.string.about );
        about.setIcon( R.drawable.ic_menu_info );
        about.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case ABOUT_OPTIONS_ITEM:
                Intent intent = new Intent( this, AboutActivity.class );
                startActivity( intent );
                return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        switch ( requestCode )
        {
            case PICK_FILE_ACTIVITY:
                if ( ( resultCode == Activity.RESULT_OK ) && ( null != data ) )
                {
                    faceFile = data.getData();
                    if ( !checkFaceFile( faceFile ) )
                    {
                        Toast.makeText( this, R.string.invalid_face_file, Toast.LENGTH_LONG )
                             .show();
                        finish();
                    }
                }
                else
                    finish();
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    private class FaceItemClickListener implements GridView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id )
        {
            try
            {
                WatchFaceItem face = (WatchFaceItem) parent.getAdapter().getItem( position );
                replaceFaceFile( faceFile, face.getFile() );
                Toast.makeText( getBaseContext(), R.string.replace_finished, Toast.LENGTH_LONG )
                     .show();
            }
            catch ( FileNotFoundException e )
            {
                Toast.makeText( getBaseContext(), R.string.replace_target_not_found,
                                Toast.LENGTH_SHORT ).show();
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                Toast.makeText( getBaseContext(), R.string.replace_internal_error,
                                Toast.LENGTH_SHORT ).show();
                e.printStackTrace();
            }
            finally
            {
                onReplaceActionFinished();
            }
        }
    }

    private boolean checkFaceFile( Uri file )
    {
        boolean res = false;
        InputStream is;
        try
        {
            is = getContentResolver().openInputStream( file );
            if ( null != is )
            {
                byte[] in = new byte[ 6 ];
                if ( is.read( in, 0, 6 ) == 6 )
                {
                    if ( "HMDIAL".equals( new String( in ) ) )
                        res = true;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return res;
    }

    private void replaceFaceFile( Uri src, String dst ) throws IOException
    {
        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor( src, "r" );
        if ( null == pfd )
            throw new FileNotFoundException();
        FileDescriptor srcFile = pfd.getFileDescriptor();

        File path = new File( Environment.getExternalStorageDirectory().toString(), mifitFacePath );
        if ( !path.exists() )
            if ( !path.mkdirs() )
                throw new FileNotFoundException();

        File dstFile = new File( path, dst );
        if ( !dstFile.exists() )
            if ( !dstFile.createNewFile() )
                throw new FileNotFoundException();

        FileChannel source = null;
        FileChannel destination = null;
        try
        {
            source = new FileInputStream( srcFile ).getChannel();
            destination = new FileOutputStream( dstFile ).getChannel();
            destination.transferFrom( source, 0, source.size() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( null != source )
                source.close();
            if ( null != destination )
                destination.close();
        }
    }

    private void onReplaceActionFinished()
    {
        if ( !Intent.ACTION_MAIN.equals( getIntent().getAction() ) )
            finish();
    }
}
