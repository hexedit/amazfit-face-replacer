package ru.ifproject.android.afr;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadActivity extends Activity
{
    private static final String baseUrl = "https://amazfitwatchfaces.com/bip/";

    private WebView mWebView;

    @SuppressLint( "SetJavaScriptEnabled" )
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        mWebView = new WebView( this );
        setContentView( mWebView );
        ActionBar actionBar = getActionBar();
        if ( null != actionBar )
            actionBar.hide();

        mWebView.getSettings().setJavaScriptEnabled( true );
        mWebView.setWebViewClient( new MyWebViewClient() );
        mWebView.loadUrl( baseUrl );
    }

    @Override
    public void onBackPressed()
    {
        if ( mWebView.canGoBack() )
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        @SuppressWarnings( "deprecation" ) // Android 5.x, 6.x compatibility
        public boolean shouldOverrideUrlLoading( WebView view, String url )
        {
            return processUrlOverride( Uri.parse( view.getUrl() ), Uri.parse( url ) );
        }

        @Override
        public boolean shouldOverrideUrlLoading( WebView view, WebResourceRequest request )
        {
            return processUrlOverride( Uri.parse( view.getUrl() ), request.getUrl() );
        }

        private boolean processUrlOverride( Uri now, Uri request )
        {
            final String url = request.toString();

            if ( !now.getHost().equals( request.getHost() ) )
            {
                Intent intent = new Intent( Intent.ACTION_VIEW, request );
                startActivity( intent );
                return true;
            }

            if ( url.endsWith( ".bin" ) )
            {
                new DownloadTask( DownloadActivity.this ).execute( request, now );
                return true;
            }

            return false;
        }
    }

    @SuppressLint( "StaticFieldLeak" ) // TODO needs to be fixed in future
    private class DownloadTask extends AsyncTask<Uri, Integer, Uri>
    {
        private static final int bufferSize = 8192;

        private Context context;

        DownloadTask( Context context )
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground( Uri... params )
        {
            File cache = new File( context.getCacheDir(), params[ 0 ].getLastPathSegment() );

            if ( !cache.exists() )
            {
                try
                {
                    URL url = new URL( params[ 0 ].toString() );
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty( "Referer", params[ 1 ].toString() );
                    conn.connect();

                    //int size = conn.getContentLength();
                    InputStream in = new BufferedInputStream( conn.getInputStream(), bufferSize );
                    OutputStream out = new FileOutputStream( cache );

                    //long read = 0;
                    byte data[] = new byte[ bufferSize ];
                    int cb;
                    while ( ( cb = in.read( data ) ) != -1 )
                    {
                        //read += cb;
                        out.write( data, 0, cb );
                    }
                    out.flush();

                    out.close();
                    in.close();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    return null;
                }
            }

            return Uri.parse( "file://" + cache.getAbsolutePath() );
        }

        @Override
        protected void onPostExecute( Uri file )
        {
            if ( null != file )
            {
                Intent intent = new Intent( context, MainActivity.class );
                intent.setAction( Intent.ACTION_VIEW );
                intent.setData( file );
                startActivity( intent );
            }
            else
            {
                Toast.makeText( context, R.string.download_failed, Toast.LENGTH_LONG ).show();
            }
        }
    }
}
