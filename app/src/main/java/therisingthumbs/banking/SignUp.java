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

import com.parse.Parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends Activity {

    static int screenWidth, screenHeight; //vars to hold the size of the screen
    static boolean isPortrait; //bool used to check if in portrait or landscape

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // connect to parse
        Parse.initialize(this, "fVmX21jyCA3B7ffHgU8RCJQJCls6x9wJBSdx5KHY",
                                "RxrZt3ldrgG0xilRZHrIZe5ViQQqC1OcxBl33DlK");

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
         * This will hide the action bar.
         */
        ActionBar actionbar = getActionBar();
        try{
            actionbar.hide();
        }
        catch (NullPointerException e){
            System.err.println("Null Pointer while hiding action bar");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

            TextView text = (TextView) rootView.findViewById(R.id.textView);

            /**
             * Set the objects on the screen based on the layout of the screen
             */


            return rootView;
        }
    }

    /**
     * Activity to respond to sign up button click
     * attempt to access user data on parse and log in
     */
    public void SignUpAttempt(View view)
    {



        final Intent intent = new Intent(this, HomePage.class);
        final Bundle b = new Bundle();
        System.err.println("SIGN UP ATTEMPT!");

        final EditText first_name = (EditText) findViewById(R.id.first_name);
        final EditText last_name = (EditText) findViewById(R.id.last_name);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText address = (EditText) findViewById(R.id.address);
        final EditText phone = (EditText) findViewById(R.id.phone);
        final EditText pass = (EditText) findViewById(R.id.pass);
        final EditText pass_confirm = (EditText) findViewById(R.id.pass_confirm);
        final TextView err = (TextView) findViewById(R.id.errMsg);



        //check if all fields are non-empty
        if (!isEmpty(first_name) &&
            !isEmpty(last_name) &&
            !isEmpty(email) &&
            !isEmpty(address) &&
            !isEmpty(phone) &&
            !isEmpty(pass) &&
            !isEmpty(pass_confirm))
        {
            System.err.println("All values non empty");


           //check for valid email and valid password
           if (emailValidator(email.getText().toString())) {

               System.err.println("Valid email");

               if (passwordValidator(pass.getText().toString())) {

                   System.err.println("Valid Password");
                   //check passwords match
                   if (pass.getText().toString().equals(pass_confirm.getText().toString())) {
                       //passwords match
                       System.err.println("pass match!\nCheck Parse for user");

                       // Check parse database to see if user exists (by email)
                       // if does not, create new object. otherwise show error message
                       User u = new User(first_name.getText().toString(),
                               last_name.getText().toString(),
                               email.getText().toString(),
                               address.getText().toString(),
                               phone.getText().toString(),
                               pass.getText().toString(),
                               false);
                       u.printData();

                       System.err.println("User in SignUp: " + u);

                       String e = u.addToDatabase();
                       System.err.println("e: " + e);
                       if (e == null) {
                           //Add successful! start new activity with user object
                           ((myApplication) this.getApplication()).setUser(u);
                           System.err.println("Add Successful!");
                           b.putParcelable("user_object", u);
                           intent.putExtras(b);

                           startActivity(intent);
                       } else {
                           err.setText(e);

                       }

                   } else {
                       //error passwords don't match
                       err.setText("Passwords do not match!");
                       err.setTextColor(Color.RED);

                       Context context = getApplicationContext();
                       int duration = Toast.LENGTH_SHORT;
                       Toast toast = Toast.makeText(context, "Passwords do not match, please try again.", duration);
                       toast.show();
                   }

               }
               else
               {
                   err.setText("Password not in valid format. Must be between 8-15 characters," +
                           "include a digit, a lowercase character, uppercase character, and special character");
                   err.setTextColor(Color.RED);
                   Context context = getApplicationContext();
                   int duration = Toast.LENGTH_SHORT;
                   Toast toast = Toast.makeText(context, "Password not in valid format. Must be between 8-15 characters"
                           + "include a digit, a lowercase character, uppercase character, and special character", duration);
                   toast.show();
               }
           }
            else
            {
                //not valid email format
                err.setText("Email not in valid format (example@example.com)");
                err.setTextColor(Color.RED);
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Email not in valid format (example@example.com)", duration);
                toast.show();
            }

        }//end empty string check
        else
        {
            // empty field(s)
            err.setText("Missing information. ALL fields are required");
            err.setTextColor(Color.RED);
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
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
    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\." +
                "                      [A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean passwordValidator(String pass)
    {
        Pattern pattern;
        Matcher matcher;
        // between 8-15 characters, 1 special char, 1 upper, 1 lower, 1 digit
        final String PASSWORD_PATTERN = "^(?=.{8,15}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[\\W]).*$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pass);
        return matcher.matches();
    }

}
