package therisingthumbs.banking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomePage extends Activity {

    static String userEmail,
                  userFirstName,
                  userLastName;
    static User u;
    static int screenWidth, screenHeight; //vars to hold the size of the screen
    static boolean isPortrait; //bool used to check if in portrait or landscape

    static List<ParseObject> accounts = new ArrayList<ParseObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        u = ((myApplication) this.getApplication()).getUser();
        System.err.println("HomePage: " + u);

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

        try {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException ex) {
            //Access global users
        }

    }

    /**
     * Fix system back button in the home page
     */
    @Override
    public void onBackPressed() {

        final Intent intent = new Intent(this, WelcomeActivity.class);
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                startActivity(intent);
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            System.err.println("SETTINGS CLICKED");
            return true;
        }

        //create account button pressed
        if (id == R.id.create) {
            Intent intent = new Intent(this, CreateAccount.class);
            startActivity(intent);
        }

        if (id == R.id.log_out) {
            final Intent intent = new Intent(this, WelcomeActivity.class);
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener(){
                        @Override
                    public void onClick(DialogInterface dialog, int which){
                            switch(which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    startActivity(intent);
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
            /**
             * Initialize global (file scope) user information
             **/

            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
            query.whereEqualTo("email", userEmail);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        userFirstName = parseObject.getString("first_name");
                        userLastName = parseObject.getString("last_name");

                        //TextView text = (TextView) rootView.findViewById(R.id.userEmail);
                        //text.setText("Welcome, " + userFirstName);

                    } else {
                        // error reading parse database!
                    }
                }
            });

            TextView text = (TextView) rootView.findViewById(R.id.userEmail);
            text.setText("Welcome, " + u.firstName);
            text.setTextSize(20);
            text.setPadding(0, screenHeight/8, 0, 0);


            //clear accounts list to prevent duplicate display of accounts
            accounts.clear();

            //System.err.println("Accounts empty:"+ accounts.isEmpty());

            /*Find accounts*/
            final ParseQuery<ParseObject> acc = ParseQuery.getQuery("Account");
            if (!u.isTeller)
            {
                acc.whereEqualTo("email", u.email);
            }
            acc.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> accList, ParseException e) {
                    //account(s) found
                    if (e == null)
                    {
                        //add all the accounts found
                        accounts.addAll(accList);
                        //System.err.println("Found "+accounts.size());

                    } else
                    {
                        System.err.println("ERROR Finding Accounts");
                    }
                    System.err.println("Accounts size "+accounts.size());

                    //Prompt the user to see if they want to create a new account
                    if (accounts.size() == 0)
                    {
                        final Intent intent = new Intent(rootView.getContext(), CreateAccount.class);
                        DialogInterface.OnClickListener dialogClickListener =
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        switch(which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                startActivity(intent);
                                                finish();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                        builder.setMessage("Would you like to create an account?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }

                    //sort the accounts for the teller
                    if(u.isTeller)
                        sortaccounts(0,accounts.size()-1);

                    interest();

                    //this is used to populate the ListView
                    ListView accView = (ListView)rootView.findViewById(R.id.accountList);
                    accountAdapter accAdapter = new accountAdapter( getActivity(), accounts);
                    accView.setAdapter(accAdapter);
                    ///////////////////

                    //set click function for the list view
                    accView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent( getActivity(),ManageAccount.class);

                            ParseObject acc = (ParseObject)adapterView.getItemAtPosition(i);

                            String type = acc.getString("type");
                            String title = acc.getString("title");
                            double accBal = Double.parseDouble(acc.get("balance").toString());

                            u.accountType = type;
                            u.accountTitle = title;
                            u.userBalance = accBal;

                            if(u.isTeller)
                            {
                                String email = acc.getString("email");
                                u.email = email;
                            }

                            System.err.println("User balance: "+accBal);
                            startActivity(intent);

                         }
                    });

                }
            });


            return rootView;
        }//end of onCreate


        public void sortaccounts(int left, int right)
        {
            int index = partition(left,right);

            if(left < index - 1)
                sortaccounts(left,index-1);
            if(index < right)
                sortaccounts(index,right);
        }

        public int partition(int left, int right)
        {
            int i = left,
                j = right;

            ParseObject temp;
            ParseObject pivot = accounts.get((left+right)/2);

            //System.err.println("pivot "+ (left+right)/2);

            while ( i <= j)
            {
                //System.err.println("In While i <= j");
                while(accounts.get(i).getString("email").compareToIgnoreCase(pivot.getString("email")) < 0)
                {
                    //System.err.println("In less then "+ i + " " +accounts.get(i).getString("email"));
                    //System.err.println("compare to " +pivot.getString("email"));
                    i++;
                }
                while(accounts.get(j).getString("email").compareToIgnoreCase(pivot.getString("email")) > 0)
                {
                    //System.err.println("In greater then");
                    j--;
                }

                if(i <= j)
                {
                    //System.err.println("Setting temp");
                    temp = accounts.get(i);
                    //System.err.println("setting account i to j");
                    accounts.set(i,accounts.get(j));
                    //System.err.println("setting account j to temp");
                    accounts.set(j,temp);

                    i++;
                    j--;

                }
            }

            return i;
        }

        public void interest()
        {
            /***************************************************

                            LOCAL VARIABLES

             ***************************************************/

            Calendar cal = Calendar.getInstance();
            Calendar update = Calendar.getInstance();
            Date currDate = cal.getTime();
            String type;

            /****************************************************/

            //go through all of the user's accounts
            //and check if interest has to be applied to any
            for(int i = 0; i < accounts.size();i++)
            {
                //get the date the interest should be applied on
                Date accDate = (Date)accounts.get(i).get("interestDate");
                System.err.println(" account "+accounts.get(i).getString("email"));
                System.err.println(" current time "+cal.getTime());
                System.err.println(" account time "+accDate);
                /*update.setTime(accDate);
                System.err.println(" account time "+update.getTime());
                update.add(Calendar.DAY_OF_MONTH,30);*/

                System.err.println((cal.getTime().before(accDate)));

                //check if the current date is before the date
                //the interest should be applied
                //if it is not before interest should be applied to the
                //account and the interest date should be updated
                //to the next 30 days
                if(!cal.getTime().before(accDate))
                {
                    System.err.println("interst to parse");

                    //update the date of the next
                    //interest data
                    update.setTime(accDate);
                    update.add(Calendar.DAY_OF_MONTH,30);
                    System.err.println(" account time "+update.getTime());
                    currDate = update.getTime();
                    //and store it
                    accounts.get(i).put("interestDate",currDate);
                    accounts.get(i).saveInBackground();

                    type = accounts.get(i).get("type").toString();
                    //calculate the interes
                    calculateInterest(accounts.get(i),type);

                }
                else
                {
                    continue;
                }

                //type = accounts.get(i).get("type").toString();
                //calculateInterest(accounts.get(i),type);

                //System.err.println(" account time "+accDate.getTime());
               // System.err.println(" new time "+update.getTime());

            }


        }

        public void calculateInterest(ParseObject currAccount,String type)
        {
            /************************************************************************

                                        LOCAL VARIABLES

             *************************************************************************/

            double bal = Double.parseDouble(currAccount.get("balance").toString());
            double percent = 0;
            double interest = 0;
            double newbal = 0;
            double penalty = 0;

            /*************************************************************************/

            if(bal >= 3000)
            {
                if(type.equalsIgnoreCase("checking"))
                {
                    percent = .03;
                }
                else
                {
                    percent = .04;
                }
            }
            else
                if(bal >= 2000)
                {
                    if(type.equalsIgnoreCase("checking"))
                    {
                        percent = .02;
                    }
                    else
                    {
                        percent = .03;
                    }
                }
                else
                    if(bal >= 1000)
                    {
                        if(type.equalsIgnoreCase("checking"))
                        {
                            percent = .01;
                        }
                        else
                        {
                            percent = .02;
                        }
                    }
                    else
                        if(bal < 100)
                        {
                            penalty = 25;
                        }

            System.err.println("Current bal: "+bal);
            System.err.println("Type: "+type);
            System.err.println("Percent: "+percent);
            System.err.println("Penalty: "+penalty);

            interest = bal * percent;

            //this is used so that there are no more
            //than two decimal place
            interest *= 100;
            interest = Math.ceil(interest);
            interest /= 100.0;
            ////////////////////////////////////////

            System.err.println("Interest: "+interest);

            newbal = bal - interest - penalty;
            System.err.println("New Balance: "+newbal);

            currAccount.put("balance",newbal);
            currAccount.saveInBackground();

        }

    }

}
