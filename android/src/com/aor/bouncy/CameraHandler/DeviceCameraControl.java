package com.aor.bouncy.CameraHandler;
/*
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

        import com.badlogic.gdx.files.FileHandle;
        import com.badlogic.gdx.graphics.Pixmap;

public interface DeviceCameraControl {

    // Synchronous interface
    void prepareCamera();

    void startPreview();

    void stopPreview();

    void takePicture();

    byte[] getPictureData();

    // Asynchronous interface - need when called from a non platform thread (GDX OpenGl thread)
    void startPreviewAsync();

    void stopPreviewAsync();

    byte[] takePictureAsync(long timeout);

    void saveAsJpeg(FileHandle jpgfile, Pixmap cameraPixmap);

    boolean isReady();

    void prepareCameraAsync();
}