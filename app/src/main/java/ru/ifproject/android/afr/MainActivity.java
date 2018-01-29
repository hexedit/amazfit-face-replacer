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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends Activity
{
    private static final int PICK_FILE_ACTIVITY = 0;

    private static final String mifitFacePath =
            "/Android/data/com.xiaomi.hm.health/files/watch_skin/";
    private static final String[] staticList = {
            "Face 1",
            "Face 2",
            "Face 3",
            "Face 4",
            "Face 5",
            "Face 6",
            "Face 7",
            "Face 8",
            "Face 9",
            "Face 10"
    };
    private static final String[] fileList = {
            "9098940e097cf25a971fc917630a6ac2.bin",
            "1d489df858097f0a2c8933bdc470fb19.bin",
            "ff08dd51b4dde06a2ba1ec14d8903a34.bin",
            "065a001971f63693cc133f754abeb48f.bin",
            "0d9fece425386d2ae3dd4ec698917db4.bin",
            "e33c5a7bbae418728bf915c093d1704d.bin",
            "b1a2ee8c77659e0680f8b97c41f93fb4.bin",
            "af059d8bf4a51c409395912ac8538868.bin",
            "393e000f7c51a54015403ae199e2b68e.bin",
            "60cf0698275183353ebb66c9d15d483d.bin"
    };

    private Uri faceFile = null;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        setTitle( R.string.app_title );

        ListView faceList = findViewById( R.id.face_list );

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
        }

        faceList.setAdapter( new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, staticList ) );
        faceList.setOnItemClickListener( new FaceItemClickListener() );
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

    private class FaceItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id )
        {
            try
            {
                replaceFaceFile( faceFile, mifitFacePath + fileList[ position ] );
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
        File dstFile = new File( Environment.getExternalStorageDirectory().toString(), dst );
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
