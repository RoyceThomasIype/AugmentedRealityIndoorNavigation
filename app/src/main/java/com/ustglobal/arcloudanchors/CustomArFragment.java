package com.ustglobal.arcloudanchors;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {

        Config config = new Config(session);
        config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
        config.setFocusMode(Config.FocusMode.AUTO);
        session.configure(config);

        this.getArSceneView().setupSession(session);
        return config;
    }


}
