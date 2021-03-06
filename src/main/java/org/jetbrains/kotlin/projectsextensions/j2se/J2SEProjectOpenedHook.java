/**
 * *****************************************************************************
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */
package org.jetbrains.kotlin.projectsextensions.j2se;

import org.jetbrains.kotlin.projectsextensions.j2se.buildextender.KotlinBuildExtender;
import org.jetbrains.kotlin.model.KotlinEnvironment;
import org.jetbrains.kotlin.projectsextensions.KotlinProjectHelper;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author Alexander.Baratynski
 */
public class J2SEProjectOpenedHook extends ProjectOpenedHook {

    private final Project project;

    public J2SEProjectOpenedHook(Project project) {
        this.project = project;
    }

    @Override
    protected void projectOpened() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        final ProgressHandle progressBar
                                = ProgressHandleFactory.createHandle("Loading Kotlin environment");
                        progressBar.start();
                        KotlinEnvironment.Companion.getEnvironment(project);
                        progressBar.finish();
                    }
                };
                KotlinProjectHelper.INSTANCE.postTask(run);
                KotlinBuildExtender extender = new KotlinBuildExtender(project);
                extender.addKotlinTasksToScript(project);

                J2SEProjectPropertiesModifier propsModifier = new J2SEProjectPropertiesModifier(project);
                propsModifier.turnOffCompileOnSave();
                propsModifier.addKotlinRuntime();

                KotlinProjectHelper.INSTANCE.doInitialScan(project);
            }
        };
        thread.start();
    }

    @Override
    protected void projectClosed() {
        KotlinProjectHelper.INSTANCE.removeProjectCache(project);
    }

}
