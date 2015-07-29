package net.myr1.fronterfeed;

/**
 * The main application object.
 *
 * This is used to create the Features instance.
 */
public class FFApplication extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Features.createInstance(this);

    }



}
