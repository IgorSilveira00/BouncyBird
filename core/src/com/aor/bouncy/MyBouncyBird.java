package com.aor.bouncy;

import com.aor.bouncy.view.MainMenuView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//HERE
/**
 * The game main class.
 */
public class MyBouncyBird extends Game {
	private SpriteBatch batch;
	private AssetManager assetManager;

	private Music BACKGROUND_MUSIC;
	private static boolean FX_ENABLED = true;
	private static boolean MUSIC_ENABLED = true;

	private static int PLAYER_ONE_LIFES = 3;
	private static int PLAYER_TWO_LIFES = 3;

	private static boolean IS_NET = false;

	/**
	 * Creates the game. Initializes the sprite batch and asset manager.
	 * Also starts the game until we have a main menu.
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		startGame();
	}

	public boolean isFX_ENABLED() {
		return FX_ENABLED;
	}

	public void setFX_ENABLED(boolean FX_ENABLED) {
		this.FX_ENABLED = FX_ENABLED;
	}

	public static boolean isMusicEnabled() {
		return MUSIC_ENABLED;
	}

	public static void setMusicEnabled(boolean musicEnabled) {
		MUSIC_ENABLED = musicEnabled;
	}

	/**
	 * Starts the game.
	 */
	private void startGame() {
		setScreen(new MainMenuView(this, true));
	}

	/**
	 * Disposes of all assets.
	 */
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}

	public void setBACKGROUND_MUSIC(Music BACKGROUND_MUSIC) {
		this.BACKGROUND_MUSIC = BACKGROUND_MUSIC;
	}

	public Music getBACKGROUND_MUSIC() {
		return BACKGROUND_MUSIC;
	}

	/**
	 * Returns the asset manager used to load all textures and sounds.
	 *
	 * @return the asset manager
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * Returns the sprite batch used to improve drawing performance.
	 *
	 * @return the sprite batch
	 */
	public SpriteBatch getBatch() {
		return batch;
	}

	public static int getPLAYER_ONE_LIFES() {
		return PLAYER_ONE_LIFES;
	}

	public static void setPLAYER_ONE_LIFES(int PLAYER_ONE_LIFES) {
		MyBouncyBird.PLAYER_ONE_LIFES = PLAYER_ONE_LIFES;
	}

	public static int getPLAYER_TWO_LIFES() {
		return PLAYER_TWO_LIFES;
	}

	public static void setPLAYER_TWO_LIFES(int PLAYER_TWO_LIFES) {
		MyBouncyBird.PLAYER_TWO_LIFES = PLAYER_TWO_LIFES;
	}

	public static boolean isIS_NET() {
		return IS_NET;
	}

	public static void setIS_NET(boolean is_net) {
		IS_NET = is_net;
	}
}