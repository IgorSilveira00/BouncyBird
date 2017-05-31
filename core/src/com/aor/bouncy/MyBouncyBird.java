package com.aor.bouncy;

import com.aor.bouncy.view.MainMenuView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

	/**
	 * The game music.
	 */
	private Music BACKGROUND_MUSIC;

	/**
	 * Is the FX enable in the game.
	 */
	private static boolean FX_ENABLED = true;

	/**
	 * Is the music enabled in the game.
	 */
	private static boolean MUSIC_ENABLED = true;

	/**
	 * Number of lives of the RED player.
	 */
	private static int PLAYER_ONE_LIVES = 3;

	/**
	 * Number os lives of the BLUE player.
	 */
	private static int PLAYER_TWO_LIVES = 3;

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

	/**
	 * Returns the value of the FX enabled flag.
	 * @return true if FX are enabled, false otherwise.
	 */
	public boolean isFX_ENABLED() {
		return FX_ENABLED;
	}

	/**
	 * Sets the value of the FX enabled flag.
	 * @param FX_ENABLED value to set the flag to.
	 */
	public void setFX_ENABLED(boolean FX_ENABLED) {
		this.FX_ENABLED = FX_ENABLED;
	}

	/**
	 * Returns the value of the Music enabled flag.
	 * @return true if the background music is enabled, false otherwise.
	 */
	public static boolean isMusicEnabled() {
		return MUSIC_ENABLED;
	}

	/**
	 * Sets the value of the Music enabled flag.
	 * @param musicEnabled value to set the flag to.
	 */
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

	/**
	 * Sets the Music to be the game's background music.
	 * @param BACKGROUND_MUSIC Music to set the background music to be.
	 */
	public void setBACKGROUND_MUSIC(Music BACKGROUND_MUSIC) {
		this.BACKGROUND_MUSIC = BACKGROUND_MUSIC;
	}

	/**
	 * Gets the game's Music.
	 * @return the game's Music.
	 */
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

	/**
	 * Gets the current amount of the RED player lives.
	 * @return the current amount of the RED player lives.
	 */
	public static int getPLAYER_ONE_LIVES() {
		return PLAYER_ONE_LIVES;
	}

	/**
	 * Sets the current amount of the RED player's lives.
	 * @param PLAYER_ONE_LIVES the amount to set the RED player's lives to.
	 */
	public static void setPLAYER_ONE_LIVES(int PLAYER_ONE_LIVES) {
		MyBouncyBird.PLAYER_ONE_LIVES = PLAYER_ONE_LIVES;
	}

	/**
	 * Gets the current amount of the BLUE player lives.
	 * @return the current amount of the BLUE player lives.
	 */
	public static int getPLAYER_TWO_LIVES() {
		return PLAYER_TWO_LIVES;
	}

	/**
	 * Sets the current amount of the BLUE player's lives.
	 * @param PLAYER_TWO_LIVES the amount to set the BLUE player's lives to.
	 */
	public static void setPLAYER_TWO_LIVES(int PLAYER_TWO_LIVES) {
		MyBouncyBird.PLAYER_TWO_LIVES = PLAYER_TWO_LIVES;
	}
}