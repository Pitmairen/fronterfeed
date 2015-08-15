package net.myr1.fronterfeed;

/**
 * The main application object.
 *
 * This is used to create the Features instance and upgrade storage.
 */
public class FFApplication extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Settings.upgradeStorage(this);

        Features.createInstance(this);

    }



}
