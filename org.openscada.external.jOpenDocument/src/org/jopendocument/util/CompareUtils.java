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

/*
 * Créé le 14 avr. 2005
 * 
 */
package org.jopendocument.util;

import java.util.Comparator;
import java.util.List;

/**
 * @author Sylvain CUAZ
 */
public class CompareUtils {

    /**
     * Compare 2 nombres entier avec longValue().
     * 
     * @param n1 le premier nombre.
     * @param n2 le deuxième nombre.
     * @return 0 si ==, >0 si n1>2.
     */
    public static final int compareIntNumbers(Number n1, Number n2) {
        return compareLong(n1.longValue(), n2.longValue());
    }

    static public final int compareInt(int int1, int int2) {
        if (int1 < int2)
            return -1;
        else if (int1 == int2)
            return 0;
        else
            return +1;
    }

    static public final int compareLong(long int1, long int2) {
        if (int1 < int2)
            return -1;
        else if (int1 == int2)
            return 0;
        else
            return +1;
    }

    /**
     * Renvoie un comparateur qui utilise successivement la liste passée tant que les objets sont
     * égaux.
     * 
     * @param comparators une liste de Comparator.
     * @return le Comparator demandé.
     * @param <T> type of comparator
     */
    static public final <T> Comparator<T> createComparator(final List<? extends Comparator<T>> comparators) {
        return new Comparator<T>() {
            public String toString() {
                return "CompareUtils comparator with " + comparators;
            }

            public int compare(T o1, T o2) {
                int result = 0;
                int i = 0;
                while (i < comparators.size() && result == 0) {
                    final Comparator<T> transf = comparators.get(i);
                    result = transf.compare(o1, o2);
                    i++;
                }
                return result;
            }
        };
    }

    /**
     * Compare 2 objets pouvant être <code>null</code>.
     * 
     * @param o1 the first object, can be <code>null</code>.
     * @param o2 the second object, can be <code>null</code>.
     * @return <code>true</code> if both are <code>null</code> or if o1.equals(o2).
     */
    static public final boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

}