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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.LongByReference;

public interface GraphBLAS extends Library {

    NativeLibrary LIBRARY = NativeLibrary.getInstance("graphblas");
    GraphBLAS INSTANCE = Native.load("graphblas", GraphBLAS.class);

    GrB_Type GrB_BOOL = new GrB_Type(LIBRARY.getGlobalVariableAddress("GrB_BOOL").getPointer(0));
    GrB_Type GrB_INT8 = new GrB_Type(LIBRARY.getGlobalVariableAddress("GrB_INT8").getPointer(0));

    /**
     * GrB_init must called before any other GraphBLAS operation. GrB_finalize
     * must be called as the last GraphBLAS operation.
     * <p>
     * GrB_init defines the mode that GraphBLAS will use: blocking or
     * non-blocking. With blocking mode, all operations finish before returning
     * to the user application. With non-blocking mode, operations can be left
     * pending, and are computed only when needed.</p>
     * <p>
     * The GrB_wait ( ) function forces all pending operations to complete.
     * Blocking mode is as if GrB_wait is called whenever a GraphBLAS method or
     * operation returns to the user.</p>
     * <p>
     * The non-blocking mode is unpredictable if user-defined functions have
     * side effects or if they rely on global variables, which are not under the
     * control of GraphBLAS. Suppose the user application creates a user-defined
     * operator that accesses a global variable. That operator is then used in a
     * GraphBLAS operation, which is left pending. If the user application then
     * changes the global variable before pending operations complete, the
     * pending operations will be eventually computed with this different
     * value.</p>
     * <p>
     * The non-blocking mode can have side effects if user-defined functions
     * have side effects or if they rely on global variables, which are not
     * under the control of GraphBLAS. Suppose the user creates a user-defined
     * operator that accesses a global variable. That operator is then used in a
     * GraphBLAS operation, which is left pending. If the user then changes the
     * global variable before pending operations complete, the pending
     * operations will be eventually computed with this different value.</p>
     * <p>
     * Worse yet, a user-defined operator might be freed before it is needed to
     * finish a pending operation. This causes undefined behavior. To avoid
     * this, call GrB_wait before modifying any global variables relied upon by
     * user-defined operators, or before freeing any user-defined types,
     * operators, monoids, or semirings.</p>
     *
     * @param mode on of {@link GrB_Mode}
     *
     * @return see {@link GrB_Info} for possible values
     */
    int GrB_init(int mode);

    /**
     * In non-blocking mode, GraphBLAS operations need not complete until their
     * results are required. GrB_wait ensures all pending operations are
     * finished.
     *
     * @return see {@link GrB_Info} for possible values
     */
    int GrB_wait();

    /**
     * finish GraphBLAS.
     * <p>
     * <p>
     * GrB_finalize does not call GrB_wait; any pending computations are
     * abandoned.</p>
     */
    int GrB_finalize();

    int GrB_Matrix_new(GrB_Matrix_ByReference matrix, GrB_Type type, long nrows, long ncols);

    /**
     * make an exact copy of a matrix
     *
     * @param C handle of output matrix to create
     * @param A input matrix to copy
     *
     * @return
     */
    int GrB_Matrix_dup(GrB_Matrix C, GrB_Matrix A);

    /**
     * clear a matrix of all entries; type and dimensions remain unchanged
     *
     * @param A matrix to clear
     *
     * @return
     */
    int GrB_Matrix_clear(GrB_Matrix A);

    /**
     * get the number of rows of a matrix
     *
     * @param nrows matrix has nrows rows
     * @param A     matrix to query
     *
     * @return
     */
    int GrB_Matrix_nrows(LongByReference nrows, GrB_Matrix A);

    /**
     * get the number of columns of a matrix
     *
     * @param ncols matrix has ncols columns
     * @param A     matrix to query
     */
    int GrB_Matrix_ncols(LongByReference ncols, GrB_Matrix A);

    /**
     * get the number of entries in a matrix
     *
     * @param nvals matrix has nvals entries
     * @param A     matrix to query
     */
    int GrB_Matrix_nvals(LongByReference nvals, GrB_Matrix A);

    /**
     * get the type of a matrix
     *
     * @param type returns the type of the matrix
     * @param A    matrix to query
     */
    int GxB_Matrix_type(GrB_Type_ByReference type, GrB_Matrix A);

    interface GrB_Info {

        /**
         * all is well
         */
        int GrB_SUCCESS = 0;

        //--------------------------------------------------------------------------
        // informational codes, not an error:
        //--------------------------------------------------------------------------
        /**
         * The GraphBLAS spec lists GrB_NO_VALUE as an 'error' code; it means
         * that A(i,j) is not present in the matrix, having been requested by
         * GrB_*_extractElement. The function cannot return the proper value
         * because the value of 'implicit zeros' depends on the semiring. For
         * the conventational plus-times semiring, the implied 'zero' actually
         * has the value of zero. For the max-plus semiring, it has the value
         * -infinity. A matrix does not keep track of its semiring, and the user
         * can change the semiring used to operate on the matrix. How
         * mathematically well-defined that change of semiring is depends the
         * user; GraphBLAS will not change the explicit values in the matrix if
         * the semiring changes. As a result, GraphBLAS needs to return not a
         * value, but an indication that the value of A(i,j) is implicit. The
         * user application can use this indicator (GrB_NO_VALUE) to use the
         * semiring's addititive identity, or it can take other action, as it
         * chooses. In either case, it is safe to ask for values that are not
         * there, which is why this return condition is not really an 'error'
         * code but an informational code.
         */
        /**
         * A(i,j) requested but not there
         */
        int GrB_NO_VALUE = 1;

        //--------------------------------------------------------------------------
        // API errors:
        //--------------------------------------------------------------------------
        // In non-blocking mode, these errors are caught right away.
        /**
         * object has not been initialized
         */
        int GrB_UNINITIALIZED_OBJECT = 2;
        /**
         * object is corrupted
         */
        int GrB_INVALID_OBJECT = 3;
        /**
         * input pointer is NULL
         */
        int GrB_NULL_POINTER = 4;
        /**
         * generic error code; some value is bad
         */
        int GrB_INVALID_VALUE = 5;
        /**
         * a row or column index is out of bounds; used for indices passed as
         * scalars; not in a list.
         */
        int GrB_INVALID_INDEX = 6;
        /**
         * object domains are not compatible
         */
        int GrB_DOMAIN_MISMATCH = 7;
        /**
         * matrix dimensions do not match
         */
        int GrB_DIMENSION_MISMATCH = 8;
        /**
         * output matrix already has values in it
         */
        int GrB_OUTPUT_NOT_EMPTY = 9;

        //--------------------------------------------------------------------------
        // execution errors:
        //--------------------------------------------------------------------------
        // In non-blocking mode; these errors can be deferred.
        /**
         * out of memory
         */
        int GrB_OUT_OF_MEMORY = 10;
        /**
         * output array not large enough
         */
        int GrB_INSUFFICIENT_SPACE = 11;
        /**
         * a row or column index is out of bounds; used for indices in a list of
         * indices.
         */
        int GrB_INDEX_OUT_OF_BOUNDS = 12;
        /**
         * SuiteSparse:GraphBLAS never panics
         */
        int GrB_PANIC = 13;

    };

    interface GrB_Mode {

        /**
         * methods may return with pending computations
         */
        int GrB_NONBLOCKING = 0;
        /**
         * no computations are ever left pending
         */
        int GrB_BLOCKING = 0;
    }

    class GrB_Type extends PointerType {

        public GrB_Type() {
        }

        public GrB_Type(Pointer p) {
            super(p);
        }

    }

    class GrB_Matrix extends PointerType {

        public GrB_Matrix() {
        }

        public GrB_Matrix(Pointer p) {
            super(p);
        }

    }

    class GrB_Matrix_ByReference extends ByReference {

        public GrB_Matrix_ByReference() {
            super(Native.POINTER_SIZE);
        }

        public void setValue(GrB_Matrix value) {
            getPointer().setPointer(0, value.getPointer());
        }

        public GrB_Matrix getValue() {
            return new GrB_Matrix(getPointer().getPointer(0));
        }
    }

    class GrB_Type_ByReference extends ByReference {

        public GrB_Type_ByReference() {
            super(Native.POINTER_SIZE);
        }

        public void setValue(GrB_Type value) {
            getPointer().setPointer(0, value.getPointer());
        }

        public GrB_Type getValue() {
            return new GrB_Type(getPointer().getPointer(0));
        }
    }
}
