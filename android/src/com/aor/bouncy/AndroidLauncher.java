package com.aor.bouncy;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.aor.bouncy.MyBouncyBird;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		config.useGyroscope = true;
		config.useGLSurfaceView20API18 = true;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;
		initialize(new MyBouncyBird(), config);
	}
}
