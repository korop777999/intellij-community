/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
 */
package com.intellij.codeInsight.slice;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.daemon.DaemonAnalyzerTestCase;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.slicer.SliceAnalysisParams;
import com.intellij.slicer.SliceForwardHandler;
import com.intellij.slicer.SliceManager;
import com.intellij.slicer.SliceUsage;
import com.intellij.util.containers.IntArrayList;
import gnu.trove.TIntObjectHashMap;

import java.util.Collection;
import java.util.Map;

/**
 * @author cdr
 */
public class SliceForwardTest extends DaemonAnalyzerTestCase {
  private final TIntObjectHashMap<IntArrayList> myFlownOffsets = new TIntObjectHashMap<IntArrayList>();

  private void dotest() throws Exception {
    configureByFile("/codeInsight/slice/forward/"+getTestName(false)+".java");
    Map<String, RangeMarker> sliceUsageName2Offset = SliceBackwardTest.extractSliceOffsetsFromDocument(getEditor().getDocument());
    PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
    PsiElement element = new SliceForwardHandler().getExpressionAtCaret(getEditor(), getFile());
    assertNotNull(element);
    SliceBackwardTest.calcRealOffsets(element, sliceUsageName2Offset, myFlownOffsets);
    Collection<HighlightInfo> errors = highlightErrors();
    assertEmpty(errors);
    SliceAnalysisParams params = new SliceAnalysisParams();
    params.scope = new AnalysisScope(getProject());
    params.dataFlowToThis = false;
    SliceUsage usage = SliceManager.createRootUsage(element, params);
    SliceBackwardTest.checkUsages(usage, false, myFlownOffsets);
  }

  public void testSimple() throws Exception { dotest();}
  public void testInterMethod() throws Exception { dotest();}
  public void testParameters() throws Exception { dotest();}
}