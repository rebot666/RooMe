package com.rebot.roomme.MeProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.*;
import com.rebot.roomme.R;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Strike on 4/5/14.
 */
public class MeProfileLogin extends SherlockFragmentActivity {
    public ArrayList<String> interestList;
    private ProgressWheel loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilelogin_layout);
        ParseFacebookUtils.initialize(getString(R.string.app_id));

        this.loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        this.loader.spin();
        login_facebook();
    }

    public void login_facebook() {
        List<String> permissions = Arrays.asList("basic_info", "user_birthday", "user_likes");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    //Mensaje de error
                    //NavUtils.navigateUpFromSameTask(MeProfileLogin.this);
                    onBackPressed();
                } else if (user.isNew()) {
                    Session session = ParseFacebookUtils.getSession();
                    if (session != null && session.isOpened()) {
                        makeMeRequest();
                    }
                } else {
                    //Mensaje de error
                    //NavUtils.navigateUpFromSameTask(MeProfileLogin.this);
                    //requestPicture();
                    onBackPressed();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());

                                if (user.getLocation() != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",
                                            (String) user.getProperty("gender"));
                                }
                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",
                                            user.getBirthday());
                                }
                                if (user.getProperty("relationship_status") != null) {
                                    userProfile
                                            .put("relationship_status",
                                                    (String) user
                                                            .getProperty("relationship_status"));
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("profile", userProfile);
                                current.saveInBackground();

                                requestBook();
                            } catch (JSONException e) {
                                Log.d("Error",
                                        "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {
                            // handle error
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestBook() {
        interestList = new ArrayList<String>();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/books",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("books", interestList);
                                current.saveInBackground();
                                requestMusic();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }


    private void requestMusic() {
                        interestList = new ArrayList<String>();
                        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/music",
                                new Request.Callback() {
                                    @Override
                                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("music", interestList);
                                current.saveInBackground();
                                requestMovie();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestMovie() {
        interestList = new ArrayList<String>();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/movies",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("movies", interestList);
                                current.put("esRoomie", true);
                                current.saveInBackground();
                                onBackPressed();
                                //requestPicture();


                                //NavUtils.navigateUpFromSameTask(MeProfileLogin.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestPicture() {
        interestList = new ArrayList<String>();
        Session session = ParseFacebookUtils.getSession();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/picture?redirect=false",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONObject info = userBooks.getJSONObject("data");
                                String imageUrl = info.getString("url");
                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("urlImage", interestList);
                                current.saveInBackground();

                                loader.stopSpinning();
                                onBackPressed();
                                //NavUtils.navigateUpFromSameTask(MeProfileLogin.this);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }
}
