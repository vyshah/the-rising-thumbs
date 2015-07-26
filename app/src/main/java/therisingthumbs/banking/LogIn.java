package therisingthumbs.banking;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class LogIn extends Activity {

    public final static String EXTRA_MESSAGE = "therisingthumbs.banking.MESSAGE";
    static int screenWidth, screenHeight; //vars to hold the size of the screen
    static boolean isPortrait; //bool used to check if in portrait or landscape

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_in, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);

            TextView text = (TextView) rootView.findViewById(R.id.textView);

            /**
             * Set the objects on the screen based on the layout of the screen
             */
            if( isPortrait )
                text.setPadding( 0, screenHeight/4, 0, 0);
            else
                text.setPadding( 0, screenHeight/5, 0, 0);



            return rootView;
        }


    }

    /**
     * Activity to respond to log in button click
     * attempt to access user data on parse and log in
     */
    public void LogInAttempt(View view)
    {
        final Intent intent = new Intent(this, HomePage.class);
        final Bundle b = new Bundle();
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText pass = (EditText) findViewById(R.id.pass);
        final TextView errMsg = (TextView) findViewById(R.id.errMsg);



        if(!isEmpty(email) && !isEmpty(pass))
        {
            System.err.println("LOG IN ATTEMPT!");
            //check parse data base for email and password
            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
            query.whereEqualTo("email", email.getText().toString());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        //user exists, access home page
                        errMsg.setText("");
                        String passWord = parseObject.getString ("pass");
                        if (passWord.equals(pass.getText().toString())) {
                            User u = new User(parseObject.getString("first_name"),
                                              parseObject.getString("last_name"),
                                              parseObject.getString("email"),
                                              parseObject.getString("address"),
                                              parseObject.getString("phone"),
                                              parseObject.getString("pass"),
                                              parseObject.getBoolean("isTeller"));

                            ((myApplication) getApplication()).setUser(u);
                            startActivity(intent);
                            finish();

                        } else {
                            errMsg.setText("Invalid password!");
                            errMsg.setTextColor(Color.RED);

                            //Error Message
                            errMsg.setText("Password incorrect, please try again!");
                            errMsg.setTextColor(Color.RED);
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, "Password incorrect, please try again!", duration);
                            toast.show();
                        }
                    } else {
                        errMsg.setText("Email does not exist");
                        errMsg.setTextColor(Color.RED);

                        //Error Message
                        errMsg.setText("Invalid ID or password, please try again!");
                        errMsg.setTextColor(Color.RED);
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, "Invalid ID or password, please try again!", duration);
                        toast.show();
                    }
                }
            });
        }
        else
        {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Missing information. ALL fields are required", duration);
            toast.show();

        }



    }

    protected boolean isEmpty(EditText t)
    {
        if(t.getText().toString().trim().length() == 0)
            return true;
        return false;

    }


}


