
/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.yaon.test;





import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.controller.yaon.internal.YaonImpl;


public class YaonTest extends TestCase  {

        @Test
        public void testYaonTest() {

                YaonImpl ah = null;
                ah = new YaonImpl();
                Assert.assertTrue(ah != null);
            
                /* Start HashDb Table Test */
                //HashTableTest.initTest();
                /* Start SqlDb Table Test */
                //DbTableTest.initTest("D://Test");
           
        }

}
