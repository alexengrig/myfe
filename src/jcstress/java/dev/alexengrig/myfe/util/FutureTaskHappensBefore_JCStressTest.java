/*
 * Copyright 2021 Alexengrig Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.alexengrig.myfe.util;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * From JavaDoc of {@link java.util.concurrent.Future}:
 *
 * <blockquote>Memory consistency effects:
 * Actions taken by the asynchronous computation
 * happen-before actions following the corresponding Future.get()in another thread.
 */
@JCStressTest
@Description("FutureTask#get happens-before")
@Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Before FutureTask#get")
@Outcome(id = "10", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Exception")
@Outcome(id = "100", expect = Expect.ACCEPTABLE, desc = "After FutureTask#get")
@State
public class FutureTaskHappensBefore_JCStressTest {

    int value = 1;

    final FutureTask<String> future = new FutureTask<>(() -> {
        value = 100;
        return null;
    });

    @Actor
    public void actor1() {
        future.run();
    }

    @Actor
    public void actor2(I_Result r) {
        try {
            future.get();
            r.r1 = value;
        } catch (InterruptedException | ExecutionException e) {
            r.r1 = 10;
        }
    }

}
