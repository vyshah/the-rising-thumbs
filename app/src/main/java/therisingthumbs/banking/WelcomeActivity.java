package therisingthumbs.banking;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.RelativeLayout;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;


public class WelcomeActivity extends Activity {

    static int screenWidth, screenHeight; //vars to hold the size of the screen
    static boolean isPortrait; //bool used to check if in portrait or landscape

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ParseObject.registerSubclass(User.class);
        // connect to parse
        Parse.initialize(this, "fVmX21jyCA3B7ffHgU8RCJQJCls6x9wJBSdx5KHY",
                               "RxrZt3ldrgG0xilRZHrIZe5ViQQqC1OcxBl33DlK");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        /**
         * Get the screen size and check if in portrait or landscape mode
         */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        if (screenWidth <= screenHeight)
        {
            isPortrait = true;
        }
        else
            isPortrait = false;

        /**
         * Set background based on
         * portrait or landscape
         */

        /**
         * This will hide the action bar.
         */
        ActionBar actionbar = getActionBar();
        try{
            actionbar.hide();
        }
        catch (NullPointerException e){
            System.err.println("Null Pointer while hiding action bar");
        }


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

            RelativeLayout lay = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
            RelativeLayout layIn = (RelativeLayout) rootView.findViewById(R.id.relative_layout_welcome);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (isPortrait)
            {
                //set background to welcome_background
                lay.setBackgroundResource(R.drawable.welcome_background);
                params.topMargin = screenHeight / 3;
                params.leftMargin = screenWidth / 8;
                params.rightMargin = screenWidth / 8;
                layIn.setLayoutParams(params);
            }
            else
            {
                //set background to welcome_background2
                lay.setBackgroundResource(R.drawable.welcome_background2);
                params.topMargin = screenHeight / 4;
                params.leftMargin = screenWidth / 6;
                params.rightMargin = screenWidth / 6;
                layIn.setLayoutParams(params);
            }


            return rootView;
        }
    }

    /**
     * Function to start the LogIn activity
     */
    public void startLogInActivity(View view)
    {
        /**
         * create an intent used to start the LogIn activity
         * No data is needed to transfer to this activity
         */
        Intent intent  = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    /**
     * Function to start the SignUp activity
     */
    public void startSignUpActivity(View view)
    {
        /**
         * Create an intent used to start the SignUp activity
         * No data is needed to transfer to this activity
         */
        Intent intent  = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
