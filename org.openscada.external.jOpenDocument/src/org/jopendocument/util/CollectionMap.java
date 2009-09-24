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

package org.jopendocument.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

/**
 * Une MultiMap qui permet de ne pas renvoyer <code>null</code>. De plus elle permet de choisir
 * le type de Collection utilisé.
 * 
 * @author ILM Informatique 8 sept. 2004
 * @param <K> type of the keys
 * @param <V> type of elements in collections
 */
@SuppressWarnings("unchecked")
public class CollectionMap<K, V> extends MultiHashMap {

    private final Class<? extends Collection<V>> collectionClass;
    private final Collection<V> collectionSpecimen;

    /**
     * Une nouvelle map avec ArrayList comme collection.
     */
    public CollectionMap() {
        this(ArrayList.class);
    }

    /**
     * Une nouvelle map. <code>collectionClass</code> doit descendre de Collection, et posséder un
     * constructeur prenant une Collection (c'est le cas de la majorité des classes de java.util).
     * 
     * @param aCollectionClass le type de collection utilisé.
     */
    public CollectionMap(Class aCollectionClass) {
        this.collectionClass = aCollectionClass;
        this.collectionSpecimen = null;
    }

    /**
     * A map that creates new collections by cloning collectionSpecimen. Allow one to customize an
     * instance, contrary to the constructor which only takes a class.
     * 
     * @param collectionSpecimen the collection from which to all others will be cloned.
     * @throws IllegalArgumentException is not a Cloneable.
     */
    public CollectionMap(Collection<V> collectionSpecimen) {
        this.collectionClass = null;
        this.collectionSpecimen = collectionSpecimen;
        if (!(collectionSpecimen instanceof Cloneable))
            throw new IllegalArgumentException(collectionSpecimen + " not a cloneable.");
        this.collectionSpecimen.clear();
    }

    /**
     * Renvoie la collection associée à la clef passée. Si la clef n'existe pas, renvoie une
     * collection vide.
     * 
     * @param key la clef.
     * @return le collectionClass (par défaut ArrayList) associé à la clef passée.
     * @see #getCollectionClass()
     */
    public Collection<V> getNonNull(K key) {
        Collection<V> res = (Collection<V>) super.get(key);
        return res == null ? this.createCollection(res) : res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.collections.MultiHashMap#createCollection(java.util.Collection)
     */
    protected Collection<V> createCollection(Collection coll) {
        if (this.collectionClass != null)
            try {
                if (coll == null) {
                    return this.collectionClass.newInstance();
                } else {
                    return this.collectionClass.getConstructor(new Class[] { Collection.class }).newInstance(new Object[] { coll });
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        else if (this.collectionSpecimen != null) {
            final Method m;
            try {
                m = this.collectionSpecimen.getClass().getMethod("clone");
            } catch (NoSuchMethodException e) {
                throw ExceptionUtils.createExn(IllegalStateException.class, "Cloneable w/o clone()", e);
            }
            try {
                final Collection<V> res = (Collection<V>) m.invoke(this.collectionSpecimen);
                if (coll != null)
                    res.addAll(coll);
                return res;
            } catch (Exception e) {
                throw ExceptionUtils.createExn(IllegalStateException.class, "clone() failed", e);
            }
        } else
            return super.createCollection(coll);
    }

    public Class getCollectionClass() {
        return this.collectionClass;
    }

    /**
     * Fusionne la MultiMap avec celle-ci. C'est à dire rajoute les valeurs de mm à la suite des
     * valeurs de cette map (contrairement à putAll(Map) qui ajoute les valeurs de mm en tant que
     * valeur scalaire et non en tant que collection).
     * 
     * @param mm la MultiMap à fusionner.
     */
    public void merge(MultiMap mm) {
        // copied from super ctor
        for (Iterator it = mm.entrySet().iterator(); it.hasNext();) {
            final Map.Entry entry = (Map.Entry) it.next();
            Collection<V> coll = (Collection<V>) entry.getValue();
            Collection newColl = createCollection(coll);
            this.putAll(entry.getKey(), newColl);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map. This method is equivalent to
     * {@link MultiHashMap#MultiHashMap(Map)}. NOTE: cannot use Map<? extends K, ? extends V>
     * since java complains (MultiHashMap not being generic).
     * 
     * @param m mappings to be stored in this map
     */
    @Override
    public void putAll(Map mapToCopy) {
        if (mapToCopy instanceof MultiMap) {
            this.merge((MultiMap) mapToCopy);
        } else {
            super.putAll(mapToCopy);
        }
    }

    public boolean putAll(K key, V... values) {
        return this.putAll(key, Arrays.asList(values));
    }

    // generics : MultiHashMap is not generic but it extends HashMap who does
    // so just override

    @Override
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return super.entrySet();
    }

    @Override
    public Set<K> keySet() {
        return super.keySet();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }
}