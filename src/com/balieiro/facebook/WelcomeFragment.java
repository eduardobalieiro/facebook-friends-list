/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 423):
 * <balieiro@kajoo.com.br> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

package com.balieiro.facebook;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.widget.LoginButton;


public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.welcome_fragment, container, false);

    	LoginButton buttonLogin = (LoginButton) view.findViewById(R.id.authButton);
        buttonLogin.setFragment(this);
        buttonLogin.setReadPermissions(Arrays.asList("user_friends", "user_status"));
        
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
}
