/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 jOpenDocument, by ILM Informatique. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the GNU
 * General Public License Version 3 only ("GPL").  
 * You may not use this file except in compliance with the License. 
 * You can obtain a copy of the License at http://www.gnu.org/licenses/gpl-3.0.html
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each file.
 * 
 */

package org.jopendocument.util.cc;




import org.apache.commons.collections.Transformer;
import org.jopendocument.util.ExceptionUtils;

/**
 * Transformer able to throw an exception.
 * 
 * @author Sylvain
 * 
 * @param <E> input type
 * @param <T> return type
 * @param <X> exception type
 */
public abstract class ExnTransformer<E, T, X extends Exception> implements Transformer {

    @SuppressWarnings("unchecked")
    public final Object transform(Object input) {
        return this.transformCheckedWithExn((E) input, IllegalStateException.class);
    }

    /**
     * Execute this transformer, making sure that an exception of type <code>exnClass</code> is
     * thrown.
     * 
     * @param <Y> type of exception to throw.
     * @param input the input.
     * @param exnClass class exception to throw.
     * @return the result of this transformer.
     * @throws Y if {@link #transformChecked(Object)} throws an exception, it will be wrapped (if
     *         necessary) in an exception of class <code>exnClass</code>.
     */
    public final <Y extends Exception> T transformCheckedWithExn(E input, Class<Y> exnClass) throws Y {
        try {
            return this.transformChecked(input);
        } catch (Exception e) {
            if (exnClass.isInstance(e))
                throw exnClass.cast(e);
            else
                throw ExceptionUtils.createExn(exnClass, "executeChecked failed", e);
        }
    }

    public abstract T transformChecked(E input) throws X;

}
