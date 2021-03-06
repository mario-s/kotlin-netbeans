/*******************************************************************************
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
 *******************************************************************************/
package org.jetbrains.kotlin.hints.fixes

import org.jetbrains.kotlin.diagnostics.netbeans.parser.KotlinParserResult
import org.jetbrains.kotlin.diagnostics.netbeans.parser.KotlinError
import org.jetbrains.kotlin.hints.KotlinRule
import org.netbeans.modules.csl.api.Hint
import org.netbeans.modules.csl.api.HintFix
import org.netbeans.modules.csl.api.HintSeverity
import org.netbeans.modules.csl.api.OffsetRange

abstract class KotlinQuickFix(val kotlinError: KotlinError,
                              val parserResult: KotlinParserResult) : HintFix {

    private val PRIORITY = 10
    
    abstract val hintSeverity: HintSeverity
    
    override fun isSafe() = true
    
    override fun isInteractive() = false
    
    abstract fun isApplicable(): Boolean
    
    abstract fun createFixes(): List<KotlinQuickFix>
    
    fun createHint() = Hint(
            KotlinRule(hintSeverity),
            description,
            parserResult.snapshot.source.fileObject,
            OffsetRange(kotlinError.startPosition, kotlinError.endPosition),
            createFixes(), 
            PRIORITY
    )
    
}