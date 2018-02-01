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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity
{

    @SuppressLint( "SetTextI18n" )
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about );
        setTitle( R.string.app_title );

        TextView copyright = findViewById( R.id.copyright );
        copyright.setText( "Copyright \u00a9 2018, IFProject" );

        TextView credits = findViewById( R.id.credits );
        credits.setText( "HexEdit\nDriffeX\nNitroOxid" );

        ActionBar actionBar = getActionBar();
        if ( null != actionBar )
            actionBar.setDisplayHomeAsUpEnabled( true );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }
}
