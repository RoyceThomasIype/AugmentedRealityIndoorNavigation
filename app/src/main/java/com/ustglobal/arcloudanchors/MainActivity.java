package com.ustglobal.arcloudanchors;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CustomArFragment arFragment;
    private ArrayList anchorList;
    public Spinner modelOptionsSpinner;
    private static final String[] paths = {"Straight Arrow", "Right Arrow", "Left Arrow"};
    private String FROM, MODE;

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }

    private Anchor anchor;
    private AnchorNode anchorNode;
    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    private String ELECTRONICS = "electronics_DB";
    private String TOYS = "toys_DB";
    private String TV_APPLIANCES = "tv_DB";
    private String CLOTHING = "clothing_DB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                FROM = null;
            } else {
                FROM = extras.getString(LauncherActivity.FROM);
                MODE = extras.getString(LauncherActivity.MODE);
            }
        }

        setContentView(R.layout.activity_main);
        anchorList = new ArrayList();
        TinyDB tinydb = new TinyDB(getApplicationContext());
        Button resolve = findViewById(R.id.resolve);
        modelOptionsSpinner = findViewById(R.id.modelOptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelOptionsSpinner.setAdapter(adapter);
        modelOptionsSpinner.setOnItemSelectedListener(this);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            //Active only in Admin Mode
            if(MODE.equalsIgnoreCase("admin")) {
                Log.d("HIT_RESULT:", hitResult.toString());
                anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                appAnchorState = AppAnchorState.HOSTING;
                showToast("Hosting...");
                createCloudAnchorModel(anchor);
            } else {
                showToast("Anchor can be hosted only in Admin mode");
            }

        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if (appAnchorState != AppAnchorState.HOSTING)
                return;
            Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();

            if (cloudAnchorState.isError()) {
                showToast(cloudAnchorState.toString());
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                appAnchorState = AppAnchorState.HOSTED;

                String anchorId = anchor.getCloudAnchorId();
                anchorList.add(anchorId);

                if (FROM.equalsIgnoreCase(LauncherActivity.ELECTRONICS)) {
                    tinydb.putListString(ELECTRONICS, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.TOYS)) {
                    tinydb.putListString(TOYS, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.TV_APPLIANCES)) {
                    tinydb.putListString(TV_APPLIANCES, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.CLOTHING)) {
                    tinydb.putListString(CLOTHING, anchorList);
                }

                showToast("Anchor hosted successfully. Anchor Id: " + anchorId);
            }

        });


        resolve.setOnClickListener(view -> {
            ArrayList<String> stringArrayList = new ArrayList<>();
            if (FROM.equalsIgnoreCase(LauncherActivity.ELECTRONICS)) {
                stringArrayList = tinydb.getListString(ELECTRONICS);
            } else if (FROM.equalsIgnoreCase(LauncherActivity.TOYS)) {
                stringArrayList = tinydb.getListString(TOYS);
            } else if (FROM.equalsIgnoreCase(LauncherActivity.TV_APPLIANCES)) {
                stringArrayList = tinydb.getListString(TV_APPLIANCES);
            } else if (FROM.equalsIgnoreCase(LauncherActivity.CLOTHING)) {
                stringArrayList = tinydb.getListString(CLOTHING);
            }

            for (int i = 0; i < stringArrayList.size(); i++) {
                String anchorId = stringArrayList.get(i);
                if (anchorId.equals("null")) {
                    Toast.makeText(this, "No anchor Id found", Toast.LENGTH_LONG).show();
                    return;
                }

                Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorId);
                createCloudAnchorModel(resolvedAnchor);

            }


        });

        if (MODE.equalsIgnoreCase("user")) {
            modelOptionsSpinner.setVisibility(View.GONE);
        } else {
            modelOptionsSpinner.setVisibility(View.VISIBLE);
            resolve.setVisibility(View.VISIBLE);
        }


    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void createCloudAnchorModel(Anchor anchor) {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeCloudAnchorModel(anchor, modelRenderable));

    }

    private void placeCloudAnchorModel(Anchor anchor, ModelRenderable modelRenderable) {
        anchorNode = new AnchorNode(anchor);
        /*AnchorNode cannot be zoomed in or moved
        So we create a TransformableNode with AnchorNode as the parent*/
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        if (modelOptionsSpinner.getSelectedItem().toString().equals("Straight Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Right Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 135));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Left Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 315));
        }
        transformableNode.setParent(anchorNode);
        //adding the model to the transformable node
        transformableNode.setRenderable(modelRenderable);
        //adding this to the scene
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
