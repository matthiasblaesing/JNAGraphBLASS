/* Copyright (c) 2019, Matthias Bläsing, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2 alternative Open Source
 * /Free licenses: LGPL 2.1 or later and Apache License 2.0.
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 * http://www.gnu.org/licenses/licenses.html
 *
 *
 * You may obtain a copy of the Apache License at:
 * http://www.apache.org/licenses/
 *
 */
package eu.doppel_helix.dev.jna.graphblas;

import com.sun.jna.ptr.LongByReference;
import static eu.doppel_helix.dev.jna.graphblas.GraphBLAS.GrB_BOOL;
import eu.doppel_helix.dev.jna.graphblas.GraphBLAS.GrB_Matrix_ByReference;
import eu.doppel_helix.dev.jna.graphblas.GraphBLAS.GrB_Mode;
import eu.doppel_helix.dev.jna.graphblas.GraphBLAS.GrB_Type_ByReference;
import org.junit.jupiter.api.Test;

public class GraphBLASTest {

    public GraphBLASTest() {
    }

    @Test
    public void testSampleRun() {
        System.out.println(GraphBLAS.INSTANCE.GrB_init(GrB_Mode.GrB_BLOCKING));

        GrB_Matrix_ByReference F = new GrB_Matrix_ByReference();
        System.out.println(GraphBLAS.INSTANCE.GrB_Matrix_new(F, GrB_BOOL, 1000, 1000));

        GrB_Type_ByReference type = new GrB_Type_ByReference();
        System.out.println(GraphBLAS.INSTANCE.GxB_Matrix_type(type, F.getValue()));
        System.out.println(type.getValue().equals(GrB_BOOL));

        LongByReference rows = new LongByReference();
        LongByReference cols = new LongByReference();
        LongByReference vals = new LongByReference();

        System.out.println(GraphBLAS.INSTANCE.GrB_Matrix_ncols(cols, F.getValue()));
        System.out.println(cols.getValue());
        System.out.println(GraphBLAS.INSTANCE.GrB_Matrix_nrows(rows, F.getValue()));
        System.out.println(rows.getValue());
        System.out.println(GraphBLAS.INSTANCE.GrB_Matrix_nvals(vals, F.getValue()));
        System.out.println(vals.getValue());

        System.out.println(GraphBLAS.INSTANCE.GrB_finalize());
    }

}
