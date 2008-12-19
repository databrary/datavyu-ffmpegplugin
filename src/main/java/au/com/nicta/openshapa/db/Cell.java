/*
 * Cell.java
 *
 * Created on December 7, 2006, 3:15 PM
 *
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;

/**
 * Class Cell
 *
 * Abstract class for OpenSHAPA data and reference cells.  Data and reference
 * cells don't have all that much in common, so this class is a bit sparse.
 *
 *                                                  JRM -- 8/24/07j
 *
 *
 * @author FGA
 */
public abstract class Cell extends DBElement
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsColID: ID of the instance of Column within which the cell resides.
     *
     * comment: String intended to allow the user to attach a comment to the
     *      cell.
     *
     * ord: Number of this cell in its host column.  This number should
     *      be 1 + the index of the cell in the column's Vector of cells.
     *
     * selected:  Boolean flag indicating whether the cell is currently
     *      selected.
     */

    /** ID of the column within which the cell resides. */
    protected long itsColID = DBIndex.INVALID_ID;

    /** arbitrary comment associated with the cell */
    protected String comment = null;

    /** ord of cell */
    int ord = -1; /* a convenient invalid value */

    /** whether the cell is selected */
    boolean selected = false;

//    /* TODO -- revisit this field and the associated code */
//    /**
//     * CellChange listener list
//     */
//    Vector<CellChangeListener> cellListeners = new Vector<CellChangeListener>();


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Cell()
     *
     * Constructor for instances of Cell.
     *
     * Only three versions of this constructor, as the management of itsColID
     * varies between the subclasses.
     *
     * The first takes only a reference to a database as its parameter and
     * constructs an undefined instance of Cell (that is, an instance
     * that is not yet associated with some column).
     *
     * The second takes a reference to a database, and a comment.  The comment
     * is copied, and the comment field is used to refer to the copy.
     *
     *  The third takes and instance of Cell as its parameter, and returns
     *  a copy.
     *
     *  Observe that none of the constructor accept a column ID, as this is
     *  best handled by the subclasses.
     *
     *                                              JRM -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public Cell(Database db)
        throws SystemErrorException
    {

        super(db);

    } /* Cell::Cell(db) */


    public Cell(Database db,
                String comment)
        throws SystemErrorException
    {
        super(db);

        if ( comment != null )
        {
            this.comment = new String(comment);
        }
        else
        {
            comment = null;
        }
    } /* Cell::Cell(db, comment) */

    public Cell(Cell c)
        throws SystemErrorException
    {
        super((DBElement)c);

        this.itsColID = c.itsColID;

        this.ord = c.ord;

        if ( c.comment != null )
        {
            this.comment = new String(c.comment);
        }
    } /* Cell::Cell(c) */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsColID()
     *
     * Return the current value of the itsColID field.
     *
     *                                          JRM -- 8/29/07
     *
     * Changes:
     *
     *    - none.
     */

    public long getItsColID()
    {

        return this.itsColID;

    } /* Cell::getItsColID() */


    /**
     * getComment() & setComment()
     *
     * Get and set the comment field.  Observe that a copy is made in both
     * cases.  In both cases,
     *                                          JRM -- 8/24/07
     *
     * Changes:
     *
     *    - None.
     */

    public String getComment()
    {
        if ( this.comment != null )
        {
            return new String(this.comment);
        }
        else
        {
            return null;
        }
    } /* Cell::getComment() */

    public void setComment(String comment)
    {
        if ( comment != null )
        {
            this.comment = new String(comment);
        }
        else
        {
            this.comment = null;
        }

        return;

    } /* /* Cell::getComment() */


    /**
     * getOrd() & setOrd()
     *
     * Get and set the ord of the cell.
     *
     *                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getOrd()
    {

        return this.ord;

    } /* Cell::getOrd() */

    public void setOrd(int newOrd)
        throws SystemErrorException
    {
        final String mName = "Cell::setOrd(newOrd): ";

        if ( newOrd < 1 )
        {
            throw new SystemErrorException(mName + "newOrd < 1");
        }

        this.ord = newOrd;

        return;

    } /* Cell::setOrd(newOrd) */


    /**
     * getSelected() & setSelected()
     *
     * Get and set the value of the selected field.
     *
     *                              JRM -- 2/8/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getSelected()
    {

        return this.selected;

    } /* Cell::getSelected() */

    public void setSelected(boolean selected)
    {

        this.selected = selected;

        return;

    } /* Cell::setSelected() */



//    /**
//     * Adds a cell listener reference to cell
//     * @param cellListener The cell listener to add
//     */
//    public void addCellChangeListener(CellChangeListener cellListener)
//    {
//      if (this.cellListeners != null) {
//        this.cellListeners.add(cellListener);
//      }
//    } //End of addCellChangeListener() method
//
//    /**
//     * Removes a cell listener reference from a cell
//     */
//    public void removeCellChangeListener(CellChangeListener cellListener)
//    {
//    if (this.cellListeners != null) {
//        this.cellListeners.remove(cellListener);
//      }
//    } //End of removeCellChangeListener() method


    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessorMethods()
     *
     * Verify that the accessors supported by the Cell abstract class function
     * correctly when run on the supplied instance of some subclass of Cell.
     *
     *                                              JRM -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */

    public static int TestAccessorMethods(Cell testCell,
                                          Database initDB,
                                          String initComment,
                                          long initItsColID,
                                          int initOrd,
                                          java.io.PrintStream outStream,
                                          boolean verbose,
                                          String desc)
    {
        int failures = 0;
        String newComment = new String("a new comment");
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;

        if ( testCell == null )
        {
            failures++;

            outStream.printf("Cell::TestAccessors(): testCell null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;

            outStream.printf("Cell::TestAccessors(): desc null on entry.\n");
        }

        /* test getDB() */

        if ( testCell.getDB() != initDB )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getDB() != expected value.\n", desc);
            }
        }


        /* test getComment() / setComment() */

        if ( initComment == null )
        {
            if ( testCell.getComment() != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.getComment() = \"%s\", not null as expected.\n",
                            desc, testCell.getComment());
                }
            }
        }
        else
        {
            if ( testCell.getComment() == null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.getComment() = null, not \"%s\" as expected.\n",
                            desc, initComment);
                }
            }
            else if ( testCell.getComment() == initComment )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s.getComment() == initComment.\n", desc);
                }
            }
            else if ( initComment.compareTo(testCell.getComment()) != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                        "%s.getComment() = \"%s\" != \"%s\" = initComment.\n",
                        desc, testCell.getComment(), initComment);
                }
            }
        }

        testCell.setComment(newComment);

        if ( testCell.getComment() == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getComment() = null != \"%s\" = newComment.\n",
                    desc, testCell.getComment(), newComment);
            }
        }
        else if ( testCell.getComment() == newComment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getComment() == newComment.\n", desc);
            }
        }
        else if ( newComment.compareTo(testCell.getComment()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getComment() = \"%s\" != \"%s\" = newComment.\n",
                    desc, testCell.getComment(), newComment);
            }
        }


        /* test getItsColID() */

        if ( testCell.getItsColID() != initItsColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                    "%s.getColID() = %d != %d = initItsColID.\n",
                    desc, testCell.getItsColID(), initItsColID);
            }
        }


        /* test getOrd() / setOrd() */

        if ( testCell.getOrd() != initOrd )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() == %d != %d == expected value.\n",
                        desc, testCell.getOrd(), initOrd);
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOrd(1066);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf(
                            "testCell.setOrd(1066) failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOrd(1066) threw " +
                                      "system error exception: \"%s\".\n",
                                      systemErrorExceptionString);
                }
            }
        }

        if ( testCell.getOrd() != 1066 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() = %d, not 1066 as expected (1).\n",
                        desc, testCell.getOrd());
            }
        }

        completed = false;
        threwSystemErrorException = false;

        try
        {
            testCell.setOrd(0);
            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( completed ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( completed )
                {
                    outStream.printf("testCell.setOrd(0) completed.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("testCell.setOrd(0) failed to throw " +
                                      "system error exception.\n");
                }
            }
        }

        if ( testCell.getOrd() != 1066 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.getOrd() = %d, not 1066 as expected (2).\n",
                        desc, testCell.getOrd());
            }
        }

        return failures;

    } /* Cell::TestAccessorMethods() */


    /**
     * VerifyCellCopy()
     *
     * Verify that the supplied instances of Cell are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     *                                              JRM -- 12/3/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyCellCopy(Cell base,
                                     Cell copy,
                                     java.io.PrintStream outStream,
                                     boolean verbose,
                                     String baseDesc,
                                     String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyCellCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyCellCopy: %s null on entry.\n",
                             copyDesc);
        }
        else if ( base == copy )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }

        if ( base.db != copy.db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }

        if ( base.id != copy.id )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.id == %d != %s.id == %d.\n",
                                 baseDesc, base.id,
                                 copyDesc, copy.id);
            }
        }

        if ( base.itsColID != copy.itsColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsColID == %d != %s.itsColID == %d.\n",
                                 baseDesc, base.itsColID,
                                 copyDesc, copy.itsColID);
            }
        }

        if ( base.ord != copy.ord )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.ord == %d != %s.ord == %d.\n",
                                 baseDesc, base.ord,
                                 copyDesc, copy.ord);
            }
        }

        if ( base.comment == null )
        {
            if ( copy.comment != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "%s.comment == null != %s.comment == \"%s\".\n",
                            baseDesc, copyDesc, copy.comment);
                }
            }
        }
        else if ( copy.comment == null )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment == \"%s\" != %s.comment == null.\n",
                        baseDesc, base.comment, copyDesc);
            }
        }
        else if ( base.comment == copy.comment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment and %s.comment refer to the same string.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( base.comment.compareTo(copy.comment) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.comment == \"%s\" != %s.comment == \"%s\".\n",
                        baseDesc, base.comment, copyDesc, copy.comment);
            }
        }

        return failures;

    } /* Cell::VerifyCellCopy() */


    /**
     * VerifyInitialization()
     *
     * Verify that the supplied instance of Cell has been correctly
     * initialized by a constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */

    public static int VerifyInitialization(Database db,
                                           Cell c,
                                           String desc,
                                           long expectedColID,
                                           String expectedComment,
                                           java.io.PrintStream outStream,
                                           boolean verbose)
    {
        int failures = 0;

        if ( db == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: db null on entry.\n");
        }

        if ( c == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: c null on entry.\n");
        }

        if ( desc == null )
        {
            failures++;
            outStream.printf("Cell::VerifyInitialization: desc null on entry.\n");
        }

        if ( c.db != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.db not initialized correctly.\n",
                                 desc);
            }
        }

        if ( c.id != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.id not initialized corectly: %d.\n",
                                 desc, c.id);
            }
        }

        if ( c.itsColID != expectedColID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s: c.itsColID not initialized correctly: %d (%d).\n",
                        desc, c.itsColID, expectedColID);
            }
        }

        if ( expectedComment == null )
        {
            if ( c.comment != null )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("%s: c.comment == \"%s\" != null.\n",
                                     desc, c.comment);
                }
            }
        }
        else if ( expectedComment == c.comment )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.comment and expectedComment refer " +
                                 "to the same string.\n, desc");
            }
        }
        else if ( expectedComment.compareTo(c.comment) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s: c.comment == \"%s\" != " +
                                 "expectedComment = \"%s\".\n",
                                 desc, c.comment, expectedComment);
            }
        }

        return failures;

    } /* Cell::VerifyInitialization() */

} /* class Cell */
