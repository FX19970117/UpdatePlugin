/*
 * Copyright (C) 2017 Haoge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lzh.framework.updatepluginlib.creator;

import android.app.Activity;
import android.app.Dialog;

import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.callback.UpdateCheckCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.strategy.InstallStrategy;
import org.lzh.framework.updatepluginlib.util.ActivityManager;
import org.lzh.framework.updatepluginlib.util.Recyclable;
import org.lzh.framework.updatepluginlib.util.UpdatePreference;
import org.lzh.framework.updatepluginlib.util.Utils;

public abstract class InstallCreator implements Recyclable {

    private UpdateBuilder builder;
    private Update update;

    public void setBuilder(UpdateBuilder builder) {
        this.builder = builder;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public abstract Dialog create(Update update, String path, Activity activity);

    /**
     * request to install this apk file
     * @param filename the absolutely file name that downloaded
     */
    public void sendToInstall(String filename) {
        if (builder.getFileChecker().checkAfterDownload(update,filename)) {
            builder.getInstallStrategy().install(ActivityManager.get().getApplicationContext(), filename);
        } else {
            builder.getCheckCB().onCheckError(new RuntimeException(String.format("apk %s checked failed",filename)));
        }
        release();
    }

    /**
     * request cancel install action
     */
    public void sendUserCancel() {
        if (builder.getCheckCB() != null) {
            builder.getCheckCB().onUserCancel();
        }

        release();
    }

    public void sendCheckIgnore(Update update) {
        if (builder.getCheckCB() != null) {
            builder.getCheckCB().onCheckIgnore(update);
        }
        UpdatePreference.saveIgnoreVersion(update.getVersionCode());
        release();
    }

    @Override
    public void release() {
        this.builder = null;
        this.update = null;
    }
}
