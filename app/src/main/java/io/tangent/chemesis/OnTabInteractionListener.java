package io.tangent.chemesis;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public interface OnTabInteractionListener  {
    public void onFragmentInteraction(Uri uri);
}
