package com.hctrom.romcontrol.licenseadapter;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.mail.Mail;
import com.software.shell.fab.ActionButton;

/**
 * Created by Ivan on 13/12/2015.
 */
public class LicenseDialogoAlerta extends DialogFragment implements View.OnClickListener {
    private Button licenses;
    private TextView titulo;
    private ImageView email;
    private ActionButton buttonBack;
    private TextView apache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getActivity());
        theme.onActivityCreateSetTheme(getActivity());
        final View view = inflater.inflate(R.layout.license_app, container, false);
        titulo = (TextView) view.findViewById(R.id.textViewTitulo);
        licenses =(Button) view.findViewById(R.id.imageButtonCompartir);
        buttonBack = (ActionButton) view.findViewById(R.id.boton_flotante_back);
        email = (ImageView) view.findViewById(R.id.mail);
        apache = (TextView) view.findViewById(R.id.textViewApache);
        Linkify.addLinks(apache, Linkify.WEB_URLS);
        applyBlurMaskFilter(titulo, BlurMaskFilter.Blur.OUTER);
        getDialog().setCanceledOnTouchOutside(false);
        licenses.setOnClickListener(this);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.mail);
                        mDrawable.setColorFilter(new PorterDuffColorFilter(getActivity().getResources().getColor(R.color.selector), PorterDuff.Mode.MULTIPLY));

                        email.setAnimation( AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close));
                        email.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.mail);
                        mDrawable.setColorFilter(new PorterDuffColorFilter(getActivity().getResources().getColor(R.color.selector), PorterDuff.Mode.MULTIPLY));
                        email.setAnimation( AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open));
                        email.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Mail.class));
            }
        });
        return view;
    }
/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                getActivity().finish();
            }
        };
    }
*/
    @Override
    public void onClick(View v) {
        if(v == licenses){
            startActivity(new Intent(getActivity(), LicenseMain.class));
        }
    }

    // Custom method to apply BlurMaskFilter to a TextView text
    public void applyBlurMaskFilter(TextView tv, BlurMaskFilter.Blur style){
        /*
            MaskFilter
                Known Direct Subclasses
                    BlurMaskFilter, EmbossMaskFilter

                MaskFilter is the base class for object that perform transformations on an
                alpha-channel mask before drawing it. A subclass of MaskFilter may be installed
                into a Paint. Blur and emboss are implemented as subclasses of MaskFilter.

        */
        /*
            BlurMaskFilter
                This takes a mask, and blurs its edge by the specified radius. Whether or or not to
                include the original mask, and whether the blur goes outside, inside, or straddles,
                the original mask's border, is controlled by the Blur enum.
        */
        /*
            public BlurMaskFilter (float radius, BlurMaskFilter.Blur style)
                Create a blur maskfilter.

            Parameters
                radius : The radius to extend the blur from the original mask. Must be > 0.
                style : The Blur to use
            Returns
                The new blur maskfilter
        */
        /*
            BlurMaskFilter.Blur
                INNER : Blur inside the border, draw nothing outside.
                NORMAL : Blur inside and outside the original border.
                OUTER : Draw nothing inside the border, blur outside.
                SOLID : Draw solid inside the border, blur outside.
        */
        /*
            public float getTextSize ()
                Returns the size (in pixels) of the default text size in this TextView.
        */

        // Define the blur effect radius
        float radius = tv.getTextSize()/10;

        // Initialize a new BlurMaskFilter instance
        BlurMaskFilter filter = new BlurMaskFilter(radius,style);

        /*
            public void setLayerType (int layerType, Paint paint)
                Specifies the type of layer backing this view. The layer can be LAYER_TYPE_NONE,
                LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE.

                A layer is associated with an optional Paint instance that controls how the
                layer is composed on screen.

            Parameters
                layerType : The type of layer to use with this view, must be one of
                    LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE
                paint : The paint used to compose the layer. This argument is optional and
                    can be null. It is ignored when the layer type is LAYER_TYPE_NONE
        */
        /*
            public static final int LAYER_TYPE_SOFTWARE
                Indicates that the view has a software layer. A software layer is backed by
                a bitmap and causes the view to be rendered using Android's software rendering
                pipeline, even if hardware acceleration is enabled.
        */

        // Set the TextView layer type
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        /*
            public MaskFilter setMaskFilter (MaskFilter maskfilter)
                Set or clear the maskfilter object.

                Pass null to clear any previous maskfilter. As a convenience, the parameter
                passed is also returned.

            Parameters
                maskfilter : May be null. The maskfilter to be installed in the paint
            Returns
                maskfilter
        */

        // Finally, apply the blur effect on TextView text
        tv.getPaint().setMaskFilter(filter);
    }
}
