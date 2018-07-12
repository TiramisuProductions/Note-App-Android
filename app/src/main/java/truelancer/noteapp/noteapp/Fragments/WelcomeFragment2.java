package truelancer.noteapp.noteapp.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import truelancer.noteapp.noteapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment2 extends Fragment {
    //AlphaAnimation fadeIn = new AlphaAnimation( 0.0f , 1.0f );
    AnimationSet fadeIn1=new AnimationSet(true);
    AnimationSet fadeIn2=new AnimationSet(true);
    AnimationSet fadeIn3=new AnimationSet(true);
    AnimationSet fadeIn4=new AnimationSet(true);
    AnimationSet fadeIn5=new AnimationSet(true);
    AnimationSet fadeIn6=new AnimationSet(true);

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS=100;

    CardView cardView1,cardView2,cardView3;
    TextView textView1,textView2,textView3;
    ImageView imageView1,imageView2,imageView3;

    public WelcomeFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_welcome_fragment2, container, false);
        // Inflate the layout for this fragment
        textView1=(TextView)rootView.findViewById(R.id.text_view1);
        textView2=(TextView)rootView.findViewById(R.id.text_view2);
        textView3=(TextView)rootView.findViewById(R.id.text_view3);
        imageView1= (ImageView) rootView.findViewById(R.id.imageView1);
        imageView2= (ImageView) rootView.findViewById(R.id.imageView2);
        imageView3= (ImageView) rootView.findViewById(R.id.imageView3);

        fadeIn1.addAnimation(new AlphaAnimation(0.0f,1.0f));
        fadeIn2.addAnimation(new AlphaAnimation(0.0f,1.0f));
        fadeIn3.addAnimation(new AlphaAnimation(0.0f,1.0f));
        fadeIn4.addAnimation(new AlphaAnimation(0.0f,1.0f));
        fadeIn5.addAnimation(new AlphaAnimation(0.0f,1.0f));
        fadeIn6.addAnimation(new AlphaAnimation(0.0f,1.0f));



        fadeIn1.setDuration(2200);
        fadeIn2.setDuration(2400);
        fadeIn3.setDuration(2600);
        fadeIn4.setDuration(2800);
        fadeIn5.setDuration(3000);
        fadeIn6.setDuration(3200);

        imageView1.startAnimation(fadeIn1);
        textView1.startAnimation(fadeIn2);
        imageView2.startAnimation(fadeIn3);
        textView2.startAnimation(fadeIn4);
        imageView3.startAnimation(fadeIn5);
        textView3.startAnimation(fadeIn6);
        return rootView;
    }
    private void permissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)){


                // You can show your dialog message here but instead I am
                // showing the grant permission dialog box
                ActivityCompat.requestPermissions(getActivity(), new String[] {
                                Manifest.permission.CAMERA,
                                Manifest.permission.CAMERA },
                        10);



            }
            else{

                //Requesting permission
                ActivityCompat.requestPermissions(getActivity(), new String[] {
                                Manifest.permission.CAMERA,
                                Manifest.permission.CAMERA },
                        10);
            }
        }
    }
}
